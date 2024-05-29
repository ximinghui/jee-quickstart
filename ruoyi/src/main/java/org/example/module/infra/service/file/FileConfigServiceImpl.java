package org.example.module.infra.service.file;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import org.example.framework.common.pojo.PageResult;
import org.example.framework.common.util.json.JsonUtils;
import org.example.framework.common.util.validation.ValidationUtils;
import org.example.module.infra.framework.file.core.client.FileClient;
import org.example.module.infra.framework.file.core.client.FileClientConfig;
import org.example.module.infra.framework.file.core.client.FileClientFactory;
import org.example.module.infra.framework.file.core.enums.FileStorageEnum;
import org.example.module.infra.controller.admin.file.vo.config.FileConfigPageReqVO;
import org.example.module.infra.controller.admin.file.vo.config.FileConfigSaveReqVO;
import org.example.module.infra.convert.file.FileConfigConvert;
import org.example.module.infra.dal.dataobject.file.FileConfigDO;
import org.example.module.infra.dal.mysql.file.FileConfigMapper;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.framework.common.util.cache.CacheUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import jakarta.validation.Validator;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

import static org.example.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.example.module.infra.enums.ErrorCodeConstants.FILE_CONFIG_DELETE_FAIL_MASTER;
import static org.example.module.infra.enums.ErrorCodeConstants.FILE_CONFIG_NOT_EXISTS;

/**
 * 文件配置 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class FileConfigServiceImpl implements FileConfigService {

    private static final Long CACHE_MASTER_ID = 0L;

    /**
     * {@link FileClient} 缓存，通过它异步刷新 fileClientFactory
     */
    @Getter
    private final LoadingCache<Long, FileClient> clientCache = CacheUtils.buildAsyncReloadingCache(Duration.ofSeconds(10L),
            new CacheLoader<>() {

                @Override
                public FileClient load(Long id) {
                    FileConfigDO config = Objects.equals(CACHE_MASTER_ID, id) ?
                            fileConfigMapper.selectByMaster() : fileConfigMapper.selectById(id);
                    if (config != null) {
                        fileClientFactory.createOrUpdateFileClient(config.getId(), config.getStorage(), config.getConfig());
                    }
                    return fileClientFactory.getFileClient(null == config ? id : config.getId());
                }

            });

    @Resource
    private FileClientFactory fileClientFactory;

    @Resource
    private FileConfigMapper fileConfigMapper;

    @Resource
    private Validator validator;

    @Override
    public Long createFileConfig(FileConfigSaveReqVO createReqVO) {
        FileConfigDO fileConfig = FileConfigConvert.INSTANCE.convert(createReqVO);
        fileConfig.setConfig(parseClientConfig(createReqVO.getStorage(), createReqVO.getConfig()));
        fileConfig.setMaster(false); // 默认非 master
        fileConfigMapper.insert(fileConfig);
        return fileConfig.getId();
    }

    @Override
    public void updateFileConfig(FileConfigSaveReqVO updateReqVO) {
        // 校验存在
        FileConfigDO config = validateFileConfigExists(updateReqVO.getId());
        // 更新
        FileConfigDO updateObj = FileConfigConvert.INSTANCE.convert(updateReqVO);
        updateObj.setConfig(parseClientConfig(config.getStorage(), updateReqVO.getConfig()));
        fileConfigMapper.updateById(updateObj);

        // 清空缓存
        clearCache(config.getId(), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFileConfigMaster(Long id) {
        // 校验存在
        validateFileConfigExists(id);

        // 更新其它为非 master
        FileConfigDO configDO1 = new FileConfigDO();
        configDO1.setMaster(false);
        fileConfigMapper.updateBatch(configDO1);

        // 更新
        FileConfigDO configDO2 = new FileConfigDO();
        configDO2.setId(id);
        configDO2.setMaster(true);
        fileConfigMapper.updateById(configDO2);

        // 清空缓存
        clearCache(null, true);
    }

    private FileClientConfig parseClientConfig(Integer storage, Map<String, Object> config) {
        // 获取配置类
        Class<? extends FileClientConfig> configClass = FileStorageEnum.getByStorage(storage)
                .getConfigClass();
        FileClientConfig clientConfig = JsonUtils.parseObject2(JsonUtils.toJsonString(config), configClass);
        // 参数校验
        ValidationUtils.validate(validator, clientConfig);
        // 设置参数
        return clientConfig;
    }

    @Override
    public void deleteFileConfig(Long id) {
        // 校验存在
        FileConfigDO config = validateFileConfigExists(id);
        if (Boolean.TRUE.equals(config.getMaster())) {
            throw exception(FILE_CONFIG_DELETE_FAIL_MASTER);
        }
        // 删除
        fileConfigMapper.deleteById(id);

        // 清空缓存
        clearCache(id, null);
    }

    /**
     * 清空指定文件配置
     *
     * @param id     配置编号
     * @param master 是否主配置
     */
    private void clearCache(Long id, Boolean master) {
        if (id != null) {
            clientCache.invalidate(id);
        }
        if (Boolean.TRUE.equals(master)) {
            clientCache.invalidate(CACHE_MASTER_ID);
        }
    }

    private FileConfigDO validateFileConfigExists(Long id) {
        FileConfigDO config = fileConfigMapper.selectById(id);
        if (config == null) {
            throw exception(FILE_CONFIG_NOT_EXISTS);
        }
        return config;
    }

    @Override
    public FileConfigDO getFileConfig(Long id) {
        return fileConfigMapper.selectById(id);
    }

    @Override
    public PageResult<FileConfigDO> getFileConfigPage(FileConfigPageReqVO pageReqVO) {
        return fileConfigMapper.selectPage(pageReqVO);
    }

    @Override
    public String testFileConfig(Long id) throws Exception {
        // 校验存在
        validateFileConfigExists(id);
        // 上传文件
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        return getFileClient(id).upload(content, IdUtil.fastSimpleUUID() + ".jpg", "image/jpeg");
    }

    @Override
    public FileClient getFileClient(Long id) {
        return clientCache.getUnchecked(id);
    }

    @Override
    public FileClient getMasterFileClient() {
        return clientCache.getUnchecked(CACHE_MASTER_ID);
    }

}
