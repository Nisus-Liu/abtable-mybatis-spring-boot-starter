package com.wtgroup.abtable;

import com.wtgroup.abtable.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;


//拦截StatementHandler类中参数类型为Statement的 prepare 方法
//即拦截 Statement prepare(Connection var1, Integer var2) 方法
@Slf4j
//@Component
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class ABTableMybatisInterceptor implements Interceptor {

    private final ABTables abTables;

    public ABTableMybatisInterceptor(ABTables abTables) {
        this.abTables = abTables;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        //通过MetaObject优雅访问对象的属性，这里是访问statementHandler的属性;：MetaObject是Mybatis提供的一个用于方便、
        //优雅访问对象属性的对象，通过它可以简化代码、不需要try/catch各种reflect异常，同时它支持对JavaBean、Collection、Map三种类型对象的操作。
        MetaObject metaObject = MetaObject
                .forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                        new DefaultReflectorFactory());
        //先拦截到RoutingStatementHandler，里面有个StatementHandler类型的delegate变量，其实现类是BaseStatementHandler，然后就到BaseStatementHandler的成员变量mappedStatement
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        //id为执行的mapper方法的全路径名，如com.uv.dao.UserMapper.insertUser
        String id = mappedStatement.getId();
        //sql语句类型 select、delete、insert、update
        String sqlCommandType = mappedStatement.getSqlCommandType().toString();
        //数据库连接信息
//        Configuration configuration = mappedStatement.getConfiguration();
//        ComboPooledDataSource dataSource = (ComboPooledDataSource)configuration.getEnvironment().getDataSource();
//        dataSource.getJdbcUrl();

        BoundSql boundSql = statementHandler.getBoundSql();
        //获取到原始sql语句
        String sql = boundSql.getSql();
        //String mSql = sql + " limit 2";

        // 动态修改SQL
        // 只关注 abtable: 开头的 SQL, 大小写不敏感
        if ( sql !=null &&
                ( sql.startsWith(Const.ABTABLE_SQL_PREFIX)
                || sql.startsWith(Const.ABTABLE_SQL_PREFIX.toUpperCase())) ) {
            switchSql(boundSql, sql);
        }


        return invocation.proceed();
    }


    /**
     * 原始 SQL 中, 不论是 pure 还是 slience 都被替换为 active.
     *
     * 'example_a'
     * 'example_b'
     * 'example ' 若干空白符
     * 'example('
     *
     * @param boundSql
     * @param sql
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private void switchSql(BoundSql boundSql, String sql) throws NoSuchFieldException, IllegalAccessException {
        // 统一成了小写
        String mSql = sql.substring(Const.ABTABLE_SQL_PREFIX.length()).toLowerCase().trim();
        if (abTables != null) {
            for (String tn : abTables.keySet()) {
                // 只有当 SQL 中用的非激活的表名, 才触发替换
                ABTables.ABTable abTable = abTables.get(tn);

                mSql = mSql.replaceAll(abTable.getSilence(), abTable.getActive())
                        .replaceAll(abTable.getPure() + "\\s", abTable.getActive() + " ")
                        .replaceAll(abTable.getPure() + "\\(", abTable.getActive() + "(");

//                if ( mSql.contains(abTable.getSilence()) ) {
//                    // 遇到 AB table
//                    // 将SQL中的非激活表名全部替换成当前被激活的表名
//                    mSql = mSql.replaceAll(abTable.getSilence(), abTable.getActive());
//                } else if (mSql.contains(abTable.getPure())) {
//                    // 为方便使用, mybatis SQL 可以仅仅写上 pure name
//                    mSql = mSql.replaceAll(abTable.getPure(), abTable.getActive());
//                }

                //通过反射修改sql语句
                Field field = boundSql.getClass().getDeclaredField("sql");
                field.setAccessible(true);
                field.set(boundSql, mSql);
                log.debug("old sql: {}, after switch: {}", sql,mSql);
            }
        }else{
            log.warn("目标SQL为abtable类型, 但 ABTables == null");
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        //此处可以接收到配置文件的property参数
        System.out.println(properties.getProperty("name"));
    }

}