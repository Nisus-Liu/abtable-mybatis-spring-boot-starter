package com.wtgroup.abtable;

import com.wtgroup.abtable.utils.SQLWord;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;

/**
 * AB表容器清单
 * 表名设置格式: 表名+后缀
 * afs_blacklist_a, afs_blacklist_b
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/3/27 16:06
 */
public class ABTables extends HashMap<String, ABTables.ABTable> {
    @Getter
    public enum AB{
        A("_a"),
        B("_b"),
        ;

        private final String abfix;

        AB(String abfix) {
            this.abfix = abfix;
        }
    }

//    /**
//     * A 表名后缀 (a postfix -> afix)
//     */
//    public static final String AFIX = "_a";
//    public static final String BFIX = "_b";

    public static class ABTable {
        private final String pure;
        private final String tableA;
        private final String tableB;
        private String active;
        /**
         * 最近一次切换的时间戳
         */
        private Date lastSwitch;

        /**
         * @param pureName 纯表名, 即不带ab后缀的表名.
         */
        public ABTable(@NotNull String pureName) {
            this(pureName, null);
        }

        public ABTable(@NotNull String pureName, @Nullable AB active) {
            assert pureName != null && "".equals(pureName) : "'pureName' must not be empty";
            checkPureName(pureName);
            pureName = pureName.toLowerCase();

            this.pure = pureName;
            this.tableA = pureName + AB.A.abfix;
            this.tableB = pureName + AB.B.abfix;

            if (active != null && active == AB.B) {
                this.active = tableB;
            }else{
                // 默认激活 tableA
                this.active = tableA;
            }

            lastSwitch = new Date();
        }

        /**pure table name 避开SQL的关键字
         * 待补全
         * @param pureName
         */
        private void checkPureName(@NotNull String pureName) {
            if (SQLWord.isSqlWord(pureName)) {
                throw new IllegalArgumentException("pure table must not be a SQL reserved word");
            }
        }

        /**
         * 翻转
         */
        public synchronized void switchAB() {
            if (tableA.equalsIgnoreCase(active)) {
                // 切换至 tableB
                active = tableB;
            }else {
                // 切换至 tableA
                active = tableA;
            }
            lastSwitch = new Date();
        }

        /**沉默 A, 同时激活 B, 反之亦然
         * @param whoToSilence
         */
        public synchronized void silence(@NotNull AB whoToSilence) {
            if (whoToSilence== AB.A) {
                active = tableB;
            }else{
                active = tableA;
            }
            lastSwitch = new Date();
        }

        /**
         * 激活 A, 同时沉默 B, 返回亦然
         *
         * @return
         */
        public synchronized void activate(@NotNull AB whoToActivate) {
            if (whoToActivate== AB.A) {
                active = tableA;
            }else{
                active = tableB;
            }
            lastSwitch = new Date();
        }




        public String getActive() {
            return active;
        }

        /**
         * @return ab table 标记: a / b
         */
        public AB getActiveLabel() {
            return AB.valueOf(active.substring(pure.length() + 1).toUpperCase());
        }
        public AB getSilenceLabel() {
            return AB.valueOf(getSilence().substring(pure.length() + 1).toUpperCase());
        }
        public String getSilence() {
            if (tableA.equalsIgnoreCase(active)) {
                return tableB;
            }else {
               return tableA;
            }
        }

        public String getPure() {
            return pure;
        }

        public String getTableA() {
            return tableA;
        }
        public String getTableB() {
            return tableB;
        }

        public Date getLastSwitch() {
            return lastSwitch;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ABTable{");
            sb.append("tableA='").append(tableA).append('\'');
            sb.append(", tableB='").append(tableB).append('\'');
            sb.append(", active='").append(active).append('\'');
            sb.append(", lastSwitch='").append(lastSwitch).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }


}
