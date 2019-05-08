package com.wtgroup.abtable.schedule;

import org.quartz.Job;

/**无状态bean, 有无参构造
 *
 * void execute(JobExecutionContext var1) throws JobExecutionException;
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/2 0:27
 */
public interface Switcher /*extends Job*/ {

    public void switching() throws Exception;

}
