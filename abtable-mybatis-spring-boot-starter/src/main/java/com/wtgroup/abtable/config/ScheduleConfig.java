package com.wtgroup.abtable.config;

import com.wtgroup.abtable.ABTables;
import com.wtgroup.abtable.schedule.ABTableScheduler;
import com.wtgroup.abtable.schedule.Switcher;
import com.wtgroup.abtable.schedule.impl.ABTableSchedulerImpl;
import com.wtgroup.abtable.schedule.impl.PlainSwitcher;
import com.wtgroup.abtable.utils.ABTableJDBCUtilBean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;

import static java.lang.Class.forName;

/**
 *
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/2 0:38
 */
@Configuration
@EnableConfigurationProperties({DataSourceConfig.class,
        ABTablesConfig.class})
@ConfigurationProperties(prefix = "abtable.schedule")
@Slf4j
@Data
public class ScheduleConfig {

    private List<String> crons;
//    /**
//     * 具体执行切换任务的全类名(确保是个无状态的bean, 有无参构造)
//     */
//    private String switcher = "com.wtgroup.abtable.schedule.impl.PlainSwitcher";

    /*
    * 缺省基于 quart 框架,
    * 读取配置, 设置 调度器,
    *
    * */

//    @Bean
//    @ConditionalOnMissingBean
//    public Switcher switcher(/*DataSourceConfig dataSourceConfig,*/
//                             ABTables abTables,
//                             ABTableJDBCUtilBean abTableJDBCUtilBean) {
////        ABTableJDBCUtilBean abTableJDBCUtilBean = new ABTableJDBCUtilBean(dataSourceConfig);
//        if (abTables == null) {
//            log.warn("ABTables is null, return");
//            return null;
//        }
//        if (abTableJDBCUtilBean==null) {
//            log.warn("ABTableJDBCUtilBean is null, return");
//            return null;
//        }
//        return new PlainSwitcher(abTableJDBCUtilBean,abTables);
//    }

    @Bean
    @ConditionalOnMissingBean
    public Switcher switcher( ABTableJDBCUtilBean abTableJDBCUtilBean,
                                 ABTables abTables) {

        Switcher switcherInst = new PlainSwitcher(
                abTableJDBCUtilBean,
                abTables
        );
        return switcherInst;
    }


    //@PostConstruct
    @Bean
    @ConditionalOnMissingBean
    public ABTableScheduler defScheduler(Switcher switcher) {
        if (crons == null) {
            log.warn("'crons' is null, will not config ABTableScheduler, please config it by yourself");
            return null;
        }
//        BeanFactory beanFactory = null;
        /*DefaultListableBeanFactory */
//        beanFactory = (DefaultListableBeanFactory) applicationContext.getParentBeanFactory();
//        if (beanFactory==null) {
//            beanFactory = applicationContext.getAutowireCapableBeanFactory();
//        }
        //ABTableScheduler scheduler = applicationContext.getBean(ABTableScheduler.class);
//        ABTableScheduler scheduler = null;
//        try {
//            scheduler = beanFactory.getBean(ABTableScheduler.class);
//        } catch (Exception e) {
//            log.debug("ABTableScheduler 还未创建, 将注入缺省的");
//        }
//
        // 根据配置创建默认的调度器
//        if (scheduler == null) {
            // 没有才创建
//            if (StringUtils.isEmpty(switcher)) {
//                log.warn("'switcher' is empty, will use default : 'com.jfai.afs.abtable.schedule.impl.PlainSwitcher'");
//                //switcher = "com.jfai.afs.abtable.schedule.impl.PlainSwitcher";
//            }
//            Class<Switcher> switcherClzz = null;
//            try {
//                switcherClzz = (Class<Switcher>) Class.forName(switcher);
//                switcher = switcherClzz.newInstance();
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new RuntimeException("'switcher' 配置错误", e);
//            }

            if (switcher == null) {
                throw new RuntimeException("缺少 'Switcher' 的一个实例");
            }

            ABTableScheduler defScheduler = null;

            if (crons!=null && !crons.isEmpty()) {
                defScheduler = new ABTableSchedulerImpl(
                        crons, switcher);
            }

//        }else{
//            log.info("已经有了定制的 ABTableScheduler ==> 忽略缺省的.");
//        }

//        // 注入到 spring 中
//        if (beanFactory.getBean(ABTableScheduler.class) == null) {
//            if (defScheduler != null) {
//                //根据obj的类型、创建一个新的bean、添加到Spring容器中，
//                //注意BeanDefinition有不同的实现类，注意不同实现类应用的场景
//                BeanDefinition beanDefinition = new GenericBeanDefinition();
//                //beanDefinition.setBeanClassName(defScheduler.getClass().getName());
//                beanDefinition.setBeanClassName(ABTableScheduler.class.getName());
//                beanFactory.registerBeanDefinition(ABTableScheduler.class.getName(), beanDefinition);
//            }
//        }

        return defScheduler;
    }


}
