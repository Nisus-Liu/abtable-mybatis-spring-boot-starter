package com.wtgroup.abtable.config;

import com.wtgroup.abtable.ABTables;
import com.wtgroup.abtable.utils.ABTableJDBCUtilBean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/2 9:38
 */
@Configuration
@EnableConfigurationProperties(DataSourceConfig.class)
@ConfigurationProperties(prefix = "abtable")
@Slf4j
@Data
public class MetaTableConfig {
    @Autowired
    private ABTablesConfig abTablesConfig;
    @Autowired
    private ABTableJDBCUtilBean abTableJDBCUtilBean;
    @Autowired
    private ABTables abTables;

    private boolean synchronizing;

    @PostConstruct
    public void init() {
        boolean resetABTableMeta = abTablesConfig.isResetABTableMeta();
        if (synchronizing) {
            // 没有表则自动建表
            // 根据 resetABTableMeta , 选择是否覆盖元数据表的内容 ab table 元数据
            abTableJDBCUtilBean.synchronizeABTableMeta(abTables, resetABTableMeta);
        }
    }
}
