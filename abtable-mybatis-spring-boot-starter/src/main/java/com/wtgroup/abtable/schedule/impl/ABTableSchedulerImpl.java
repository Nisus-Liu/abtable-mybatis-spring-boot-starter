package com.wtgroup.abtable.schedule.impl;

import com.wtgroup.abtable.ABTables;
import com.wtgroup.abtable.schedule.ABTableScheduler;
import com.wtgroup.abtable.schedule.Switcher;
import com.wtgroup.abtable.utils.ABTableJDBCUtilBean;
import com.wtgroup.abtable.utils.QuartzUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/3/27 17:53
 */
//@Component
@Slf4j
public class ABTableSchedulerImpl implements ABTableScheduler {

//    @Autowired
//    private ABTables abTables;


    //    @Scheduled(cron = "0/3 * * * * ?") // Test: 每分钟执行一次
//    public void switchAB() {
//        ABTables.ABTable table = abTables.get("table");
//        log.trace("切换前: {}",table);
//        if (table != null) {
//            table.switchAB();
//            log.trace("切换后: {}", table);
//        }
//    }
    private List<Scheduler> quartzSchedulerList = new ArrayList<>();

    private static final String JOB_NAME = "ABTABLE_QUARTZ_JOB";
    private static final String TRIGGER_NAME = "ABTABLE_QUARTZ_TRIGGER";

    public ABTableSchedulerImpl(List<String> crons, Switcher switcher,
                                ABTables abTables,
                                ABTableJDBCUtilBean abTableJDBCUtilBean) {

        for (int i = 0; i < crons.size(); i++) {
            String cron = crons.get(i);
            try {
                Scheduler scheduler = QuartzUtil.buildQuartzSched(
                        JOB_NAME + "-" + i,
                        TRIGGER_NAME + "-" + i,
                        switcher,
                        cron,
                        abTables,
                        abTableJDBCUtilBean
                        );
                quartzSchedulerList.add(scheduler);
            } catch (SchedulerException e) {
                throw new RuntimeException("定时任务创建失败: " + cron, e);
            }
        }


    }

    @Override
    public void start() throws Exception {

        // 等一切配置就绪后, 统一启动
        for (Scheduler scheduler : quartzSchedulerList) {
            scheduler.start();
            log.trace("{} 启动", scheduler.toString());
        }

    }
}
