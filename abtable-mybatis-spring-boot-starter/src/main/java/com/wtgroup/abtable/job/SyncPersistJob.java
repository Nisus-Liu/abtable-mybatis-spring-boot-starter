package com.wtgroup.abtable.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/8 14:13
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class SyncPersistJob extends ABTableJob {
}
