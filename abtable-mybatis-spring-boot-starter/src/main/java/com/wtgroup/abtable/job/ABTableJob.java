package com.wtgroup.abtable.job;

import com.wtgroup.abtable.ABTables;
import com.wtgroup.abtable.constant.JobDataMapKeys;
import com.wtgroup.abtable.entity.ABTableMeta;
import com.wtgroup.abtable.schedule.Switcher;
import com.wtgroup.abtable.utils.ABTableJDBCUtilBean;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

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
        ABTables abTables = null;
        ABTableJDBCUtilBean jdbcUtil = null;
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();
        if (dataMap !=null) {
            switcher = (Switcher) dataMap.get(JobDataMapKeys.switcher);
            abTables = (ABTables) dataMap.get(JobDataMapKeys.abTables);
            jdbcUtil = (ABTableJDBCUtilBean) dataMap.get(JobDataMapKeys.abTableJDBCUtilBean);
        }

        if (switcher == null) {
            throw new RuntimeException("'swither' must not null");
        }
        assert abTables != null : "'abTables' from JobDataMap must not null";
        assert jdbcUtil != null : "'abTableJDBCUtilBean' from JobDataMap must not null";

        List<ABTableMeta> abTableMetaList = jdbcUtil.getABTableMetaList(abTables.keySet());


        try {
            switcher.switching(abTables, abTableMetaList);
        } catch (Exception e) {
            log.error("switching fail", e);
        }

    }
}
