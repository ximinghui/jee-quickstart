package org.example.module.infra.service.db;

import org.example.framework.common.util.object.BeanUtils;
import org.example.framework.mybatis.core.util.JdbcUtils;
import org.example.module.infra.controller.admin.db.vo.DataSourceConfigSaveReqVO;
import org.example.module.infra.dal.dataobject.db.DataSourceConfigDO;
import org.example.module.infra.dal.mysql.db.DataSourceConfigMapper;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

import java.util.List;
import java.util.Objects;

import static org.example.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.example.module.infra.enums.ErrorCodeConstants.DATA_SOURCE_CONFIG_NOT_EXISTS;
import static org.example.module.infra.enums.ErrorCodeConstants.DATA_SOURCE_CONFIG_NOT_OK;

/**
 * 数据源配置 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DataSourceConfigServiceImpl implements DataSourceConfigService {

    @Resource
    private DataSourceConfigMapper dataSourceConfigMapper;

    @Resource
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Override
    public Long createDataSourceConfig(DataSourceConfigSaveReqVO createReqVO) {
        DataSourceConfigDO config = BeanUtils.toBean(createReqVO, DataSourceConfigDO.class);
        validateConnectionOK(config);

        // 插入
        dataSourceConfigMapper.insert(config);
        // 返回
        return config.getId();
    }

    @Override
    public void updateDataSourceConfig(DataSourceConfigSaveReqVO updateReqVO) {
        // 校验存在
        validateDataSourceConfigExists(updateReqVO.getId());
        DataSourceConfigDO updateObj = BeanUtils.toBean(updateReqVO, DataSourceConfigDO.class);
        validateConnectionOK(updateObj);

        // 更新
        dataSourceConfigMapper.updateById(updateObj);
    }

    @Override
    public void deleteDataSourceConfig(Long id) {
        // 校验存在
        validateDataSourceConfigExists(id);
        // 删除
        dataSourceConfigMapper.deleteById(id);
    }

    private void validateDataSourceConfigExists(Long id) {
        if (dataSourceConfigMapper.selectById(id) == null) {
            throw exception(DATA_SOURCE_CONFIG_NOT_EXISTS);
        }
    }

    @Override
    public DataSourceConfigDO getDataSourceConfig(Long id) {
        // 如果 id 为 0，默认为 master 的数据源
        if (Objects.equals(id, DataSourceConfigDO.ID_MASTER)) {
            return buildMasterDataSourceConfig();
        }
        // 从 DB 中读取
        return dataSourceConfigMapper.selectById(id);
    }

    @Override
    public List<DataSourceConfigDO> getDataSourceConfigList() {
        List<DataSourceConfigDO> result = dataSourceConfigMapper.selectList();
        // 补充 master 数据源
        result.add(0, buildMasterDataSourceConfig());
        return result;
    }

    private void validateConnectionOK(DataSourceConfigDO config) {
        boolean success = JdbcUtils.isConnectionOK(config.getUrl(), config.getUsername(), config.getPassword());
        if (!success) {
            throw exception(DATA_SOURCE_CONFIG_NOT_OK);
        }
    }

    private DataSourceConfigDO buildMasterDataSourceConfig() {
        String primary = dynamicDataSourceProperties.getPrimary();
        DataSourceProperty dataSourceProperty = dynamicDataSourceProperties.getDatasource().get(primary);
        DataSourceConfigDO configDO = new DataSourceConfigDO();
        configDO.setId(DataSourceConfigDO.ID_MASTER);
        configDO.setName(primary);
        configDO.setUrl(dataSourceProperty.getUrl());
        configDO.setUsername(dataSourceProperty.getUsername());
        configDO.setPassword(dataSourceProperty.getPassword());
        return configDO;
    }

}
