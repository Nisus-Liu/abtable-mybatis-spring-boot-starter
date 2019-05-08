package com.wtgroup.abtable.demo.schedule;

import com.wtgroup.abtable.ABTables;
import com.wtgroup.abtable.entity.ABTableMeta;
import com.wtgroup.abtable.schedule.Switcher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

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
    public void switching(ABTables abTables, List<ABTableMeta> abTableMetaList) throws Exception {
        System.out.println("my switcher execute ...");
        System.out.println("last switch: "+ lastSwitch);
        lastSwitch = new Date();
    }
}
