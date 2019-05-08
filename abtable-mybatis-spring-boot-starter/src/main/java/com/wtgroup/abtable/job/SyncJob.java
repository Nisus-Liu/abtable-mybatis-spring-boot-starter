package com.wtgroup.abtable.job;

import org.quartz.DisallowConcurrentExecution;

/**
 * 串行
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/8 14:12
 */
@DisallowConcurrentExecution
public class SyncJob extends ABTableJob{
}
