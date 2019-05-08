package com.wtgroup.abtable.config;

import com.wtgroup.abtable.ABTableMybatisInterceptor;
import com.wtgroup.abtable.ABTables;
import com.wtgroup.abtable.schedule.ABTableScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/2 0:21
 */
@Configuration
@EnableConfigurationProperties({ABTablesConfig.class,
        DataSourceConfig.class,MetaTableConfig.class,ScheduleConfig.class})
public class ABTableAutoConfiguration {


    @Autowired
    private ABTableScheduler abTableScheduler;
    // 保证 MetaTableConfig 先处理了
    @Autowired
    private MetaTableConfig metaTableConfig;
    @Autowired
    private ABTables abTables;

    @PostConstruct
    public void init() {
        try {
            abTableScheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @ConditionalOnMissingBean(ABTableMybatisInterceptor.class)
    public ABTableMybatisInterceptor abTableMybatisInterceptor() {
        return new ABTableMybatisInterceptor(abTables);
    }

}
