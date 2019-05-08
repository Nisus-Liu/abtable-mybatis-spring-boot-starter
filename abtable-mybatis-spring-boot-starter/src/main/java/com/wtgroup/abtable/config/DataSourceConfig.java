package com.wtgroup.abtable.config;

import com.wtgroup.abtable.utils.ABTableJDBCUtilBean;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/2 0:22
 */
@ConfigurationProperties(prefix = "abtable.datasource")
@Data
public class DataSourceConfig {
    private String driverClassName = "com.mysql.jdbc.Driver";
    private String url;
    private String username;
    private String password;
    private String metaTable = "abtable_meta";

    @Bean
    public ABTableJDBCUtilBean abTableJDBCUtilBean() {
        return new ABTableJDBCUtilBean(this);
    }

}
