package com.wtgroup.abtable.utils;

import com.wtgroup.abtable.ABTables;
import com.wtgroup.abtable.config.DataSourceConfig;
import com.wtgroup.abtable.entity.ABTableMeta;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/2 0:33
 */
@Slf4j
public class ABTableJDBCUtilBean {
    private static final String META_TABLE_DDL_TPL = "CREATE TABLE IF NOT EXISTS `{meta_table_name}` (\n" +
            "`id` bigint(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID, 无意义',\n" +
            "`pure` varchar(200) DEFAULT NULL COMMENT 'abtable纯表名(无ab后缀)',\n" +
            "`ab` varchar(4) DEFAULT NULL COMMENT '当前被激活的表标识: ''a''或''b''',\n" +
            "`update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近一次切换的时间',\n" +
            "PRIMARY KEY (`id`),\n" +
            "UNIQUE KEY `uk_pure` (`pure`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8";


    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String metaTable;

    public ABTableJDBCUtilBean(DataSourceConfig conf) {
        driverClassName = conf.getDriverClassName();
        url = conf.getUrl();
        username = conf.getUsername();
        password = conf.getPassword();
        metaTable = conf.getMetaTable();
        // 检查 driver
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("加载数据库驱动失败");
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("建立数据库连接失败");
        }
    }

    public static void closeConnection(ResultSet resultSet, Statement Statement, Connection connection) {
        try {
            if (resultSet != null) resultSet.close();

            if (Statement != null) Statement.close();

            if (connection != null) connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("建立数据库连接失败");
        }
    }


    @NotNull
    public List<ABTableMeta> getABTableMetaList(Set<String> pureNames) {
        // 一开始初始化, 保证返回值 NotNull
        List<ABTableMeta> abTableMetaList = new ArrayList<>();
        Connection connection = this.getConnection();
        PreparedStatement statement = null;


        StringBuilder inSql = new StringBuilder();
        for (String pureName : pureNames) {
            if(inSql.length()<1) {
                inSql.append("( ");
            }else{
                inSql.append(", ");
            }
            inSql.append("\"").append(pureName).append("\"");
        }
        inSql.append(" )");

        String sql = "select * from " + this.metaTable + " where pure in "+inSql;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ABTableMeta meta = new ABTableMeta();
                meta.setId( resultSet.getLong("id") );
                meta.setPure( resultSet.getString("pure") );
                meta.setAb( resultSet.getString("ab") );
                meta.setUpdateTime( resultSet.getDate( "update_time" ) );
                abTableMetaList.add(meta);
            }
        } catch (SQLException e) {
            log.info("##出错的SQL: {}",sql);
            throw new RuntimeException("从 " + metaTable + " 获取 AB table 元数据失败",e);
        }finally {
            closeConnection(resultSet,
                    statement,
                    connection);
        }

        return abTableMetaList;
    }

    /**
     * 测试是否能够正常连接到 abtable_meta 表.
     */
    public boolean testConnect() {
        boolean flag = false;
        String sql = "select * from " + this.metaTable + " limit 1";
        Connection connection = this.getConnection();
            ResultSet resultSet=null;
            PreparedStatement statement=null;
        try {
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            boolean hasMeta = resultSet.next();
            flag = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(resultSet,
                    statement,
                    connection);
        }

        return flag;
    }

    /**
     * 遇到重复的 pure 更新 ab
     * @param abTables
     * @param resetABTableMeta
     */
    public void synchronizeABTableMeta(ABTables abTables, boolean resetABTableMeta) {
        Connection connection = getConnection();
        Statement statement=null;
        String logSql = null;
        try {
            statement = connection.createStatement();

            String sql = buildCreateSql(metaTable);
            logSql = sql;
            log.debug("自动创建abtable元数据表SQL: \n{}",sql);

            statement.executeUpdate(sql);

            // if 重置配置 ==> replace into
            // else do nothing
            if (resetABTableMeta) {
                log.info("将覆盖元数据表已有配置");
                String insertMeta = buildInsertMeta(abTables);
                logSql = insertMeta;
                int i = statement.executeUpdate(insertMeta);
                log.debug("新增 {} 条 abtable meta", i);
            }

        } catch (SQLException e) {
//            log.error("自动创建abtable元数据表异常",e);
            log.error("##失败SQL: \n{}", logSql);
            log.error("自动同步 abtable meta 配置失败",e);
        } finally {
            closeConnection(
                    null,
                    statement,
                    connection
            );
        }

    }

    private String buildInsertMeta(ABTables abTables) {
        StringBuilder sql = new StringBuilder();
        sql.append("REPLACE INTO ").append(metaTable)
            .append(" ( ")
                .append(ABTableMeta.COLUMNS)
                .append(" ) VALUES ");
        // (?, ?);
        StringBuilder values = new StringBuilder();
        for (Map.Entry<String, ABTables.ABTable> e : abTables.entrySet()) {
            if (values.length()>0) {
                values.append(", ");
            }
            values.append("( ");
            values.append("\"").
                    append(e.getKey())
                    .append("\"").append(", ")
                    .append("\"")
                    .append(e.getValue().getActiveLabel().name().toLowerCase())
                    .append("\"")
                    .append(" )");
        }

        sql.append(values);
        // 设置 on duplicate key update set ab (Del)

        log.debug("构建新增 ab table 元数据SQL: \n{}",sql.toString());

        return sql.toString();
    }

    private String buildCreateSql(String metaTable) {
        String ddl = META_TABLE_DDL_TPL.replace("{meta_table_name}", metaTable);
        return ddl;
    }
}
