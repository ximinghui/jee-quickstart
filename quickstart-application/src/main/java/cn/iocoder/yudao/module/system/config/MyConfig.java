package cn.iocoder.yudao.module.system.config;

import com.alibaba.druid.spring.boot3.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Spring配置注解：标识该类为一个配置类，Spring将会把该类作为配置源来处理
@Configuration

// 启用事务管理注解：启用后Spring会用动态代理技术在“@Transactional”注解的方法调用前后自动管理事务
// Spring已自动配置事务管理是否启用，据Stack Overflow显示，当类路径中存在spring-tx、spring-jdbc时该开关自动开启
//@EnableTransactionManagement

public class MyConfig {

    /**
     * 移除Druid监控页面的底部广告
     */
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.druid.web-stat-filter.enabled", havingValue = "true")
    public FilterRegistrationBean<OncePerRequestFilter> removeDruidAds(DruidStatProperties druidConfig) {
        // 获取 druid web 监控页面的参数
        var statViewServlet = druidConfig.getStatViewServlet();
        // 提取 common.js 的配置路径
        String pattern = StringUtils.defaultIfBlank(statViewServlet.getUrlPattern(), "/druid/*");
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
        // 创建 DruidAdRemoveFilter Bean
        FilterRegistrationBean<OncePerRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
                filterChain.doFilter(request, response);
                // 重置缓冲区，响应头不会被重置
                response.resetBuffer();
                // 获取 common.js
                String text = Utils.readFromResource("support/http/resources/js/common.js");
                // 正则替换 banner, 除去底部的广告信息
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");
                response.getWriter().write(text);
            }
        });
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }

}
