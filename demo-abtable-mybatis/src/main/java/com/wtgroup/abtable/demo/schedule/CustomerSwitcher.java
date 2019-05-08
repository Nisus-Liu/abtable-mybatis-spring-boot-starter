package com.wtgroup.abtable.demo.schedule;

import com.wtgroup.abtable.schedule.Switcher;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/2 18:45
 */
@Component
public class CustomerSwitcher implements Switcher {

    private Date lastSwitch;

    public CustomerSwitcher() {
        System.out.println("CustomerSwitcher construct...");
    }

    @Override
    public void switching() throws Exception {
        System.out.println("my switcher execute ...");
        System.out.println("last switch: "+ lastSwitch);
        lastSwitch = new Date();
    }
}
