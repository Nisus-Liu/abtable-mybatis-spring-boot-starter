package com.wtgroup.abtable.schedule;

import org.junit.Test;
import org.quartz.SchedulerException;

import static org.junit.Assert.*;

public class SwitcherTest {


    @Test
    public void fun1() throws SchedulerException, InterruptedException {
        /*
        * 目标:
        *   测试定时任务是否能够生效*/

//        ABTableJDBCUtilBean abTableJDBCUtilBean = new ABTableJDBCUtilBean(new DataSourceConfig());
//        ABTables abTables = new ABTables();
//        PlainSwitcher plainSwitcher = new PlainSwitcher();
//        QuartzUtil.addJob("xxx","yyy",plainSwitcher.getClass(),
//                "0/3 * * * * ?",
//                abTableJDBCUtilBean,
//                abTables);
//        Thread.sleep(60000);
//        System.out.println("end");
    }

}