package com.wtgroup.abtable.job;

import com.wtgroup.abtable.schedule.Switcher;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 不持久化数据;
 * 允许多个触发器并行执行;
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/8 14:02
 */
@Slf4j
public class ABTableJob implements Job {


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("job execute");

        Switcher switcher=null;
        if (jobExecutionContext.getMergedJobDataMap()!=null) {
            switcher = (Switcher) jobExecutionContext.getMergedJobDataMap().get("switcher");
        }

        if (switcher == null) {
            throw new RuntimeException("'swither' must not null");
        }

        try {
            switcher.switching();
        } catch (Exception e) {
            log.error("switching fail", e);
        }

    }
}
