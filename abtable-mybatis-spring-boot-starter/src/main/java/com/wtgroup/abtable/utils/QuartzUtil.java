package com.wtgroup.abtable.utils;

import com.wtgroup.abtable.ABTables;
import com.wtgroup.abtable.constant.JobDataMapKeys;
import com.wtgroup.abtable.job.ABTableJob;
import com.wtgroup.abtable.job.PersistABTableJob;
import com.wtgroup.abtable.job.SyncJob;
import com.wtgroup.abtable.job.SyncPersistJob;
import com.wtgroup.abtable.schedule.Switcher;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.utils.ClassUtils;

public class QuartzUtil {
    public final static String JOB_GROUP_NAME = "ABTABLE_QUARTZ_JOBGROUP_NAME";// 任务组
    public final static String TRIGGER_GROUP_NAME = "ABTABLE_QUARTZ_TRIGGERGROUP_NAME";// 触发器组

    private static SchedulerFactory schedulerFactory;
    static {
        // 创建一个SchedulerFactory工厂实例
        schedulerFactory = new StdSchedulerFactory();

    }


    /**
     * 添加任务的方法
     *
     * @param jobName     任务名
     * @param triggerName 触发器名
     * @param switcher    执行任务的类
     * @param abTables
     * @param abTableJDBCUtilBean
     * @throws SchedulerException
     */
    public static Scheduler buildQuartzSched(String jobName, String triggerName,
                                             Switcher switcher, String cron,
                                             ABTables abTables, ABTableJDBCUtilBean abTableJDBCUtilBean)
            throws SchedulerException {
        /*
        * Switcher 支持 Job 的 两个注解.
        * 根据注解, 选择对应的 Job class.
        * switcher 实例作为单例放入 JobDataMap 实现在 Job 间共享.
        * */

        Class<? extends Job> jobClass = selectJobClass (switcher.getClass());


        // 通过SchedulerFactory构建Scheduler对象
        Scheduler sche = schedulerFactory.getScheduler();
        // 用于描述Job实现类及其他的一些静态信息，构建一个作业实例
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, JOB_GROUP_NAME)
                .build();
        jobDetail.getJobDataMap().put(JobDataMapKeys.switcher, switcher);
        jobDetail.getJobDataMap().put(JobDataMapKeys.abtables, abTables);
        jobDetail.getJobDataMap().put(JobDataMapKeys.abtablejdbcutilbean, abTableJDBCUtilBean);
        // 构建一个触发器，规定触发的规则
//        Trigger trigger = TriggerBuilder.newTrigger()// 创建一个新的TriggerBuilder来规范一个触发器
//                .withIdentity(triggerName, TRIGGER_GROUP_NAME)// 给触发器起一个名字和组名
//                .startNow()// 立即执行
//                .withSchedule(
//                        SimpleScheduleBuilder
//                        .simpleSchedule()
//                        .withIntervalInSeconds(seconds)// 时间间隔                                                              // 单位：秒
//                        .repeatForever()// 一直执行
//                ).build();// 产生触发器

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity(triggerName, TRIGGER_GROUP_NAME)
                .startNow()
                .withSchedule(
                        CronScheduleBuilder
                                .cronSchedule(cron)
                ).build();


        //绑定触发器和任务
        sche.scheduleJob(jobDetail, trigger);
        // 启动
        //sche.start();
        return sche;
    }

    private static Class<? extends Job> selectJobClass(Class<? extends Switcher> aClass) {

        boolean isConc = ClassUtils.isAnnotationPresent(aClass, DisallowConcurrentExecution.class);
        boolean isPersist = ClassUtils.isAnnotationPresent(aClass, PersistJobDataAfterExecution.class);
        if (isConc && isPersist) {
            // PersistABTableJob
            return PersistABTableJob.class;
        } else if (isConc) {
            // 并行 + 非持久化 => 普通的
            return ABTableJob.class;
        } else if (isPersist) {
            // 串行 + 持久化
            return SyncPersistJob.class;
        }else{
            // 串行 + 非持久化
            return SyncJob.class;
        }
    }

}
