package com.wtgroup.abtable.schedule.impl;

import com.wtgroup.abtable.ABTables;
import com.wtgroup.abtable.entity.ABTableMeta;
import com.wtgroup.abtable.schedule.Switcher;
import com.wtgroup.abtable.utils.ABTableJDBCUtilBean;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.List;

/**
 * 每次新建 quartz job 都会新建一个实例
 *
 *
 *
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/2 0:28
 */

@DisallowConcurrentExecution        // 不允许并发执行
@PersistJobDataAfterExecution       // 保留属性数据
@Slf4j
public class PlainSwitcher implements Switcher {

    private final ABTableJDBCUtilBean abTableJDBCUtilBean;
    private final ABTables abTables;

    public PlainSwitcher(ABTableJDBCUtilBean abTableJDBCUtilBean, ABTables abTables) {
        this.abTableJDBCUtilBean = abTableJDBCUtilBean;
        this.abTables = abTables;
    }

    /*
        * 如果配置 ab table 元数据表,
        * 这里就执行简单的切换逻辑,
        * 作为缺省配置
        *
        * 查询 abtable_meta , 获取 所有 的当前 active,
        * 全部切换
        * */

    @Override
    public void switching() throws Exception {
        log.info("start switching");
//        JobDataMap jobDataMap = jec.getJobDetail().getJobDataMap();
//        ABTableJDBCUtilBean jdbcUtil = (ABTableJDBCUtilBean) jobDataMap.get("JDBCUtil");
//        ABTables abTables = (ABTables) jobDataMap.get("abTables");

        List<ABTableMeta> abTableMetaList = abTableJDBCUtilBean.getABTableMetaList(abTables.keySet());
        if (abTableMetaList.size()>0) {
            for (ABTableMeta meta : abTableMetaList) {
                ABTables.ABTable abTable = abTables.get(meta.getPure());

                // 当前最新的表
                String abLabel = meta.getAb();
                if (abLabel != null) {
                    // 时间戳 在 0 点后?
//                    meta.getUpdateTime()

                    try {
                        ABTables.AB ab = ABTables.AB.valueOf(abLabel.toUpperCase());
                        if (log.isDebugEnabled()) {
                            ABTables.AB curLbl = abTable.getActiveLabel();
                            if (curLbl!=ab) {
                                log.info("{} 发生表切换: {} --> {}",
                                        abTable.getPure(), curLbl, ab);
                                log.info("last ab table: {}", abTable);
                            }
                        }
                        abTable.activate(ab);
                    } catch (Exception e) {
                        log.error("切换失败: abtable meta: "+ meta, e);
                        //throw new JobExecutionException(e);
                    }

                }
            }
        }


    }
}
