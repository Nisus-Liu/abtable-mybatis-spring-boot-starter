package com.wtgroup.abtable.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/1 23:24
 */
public class SQLWord {

    private static final Set<String> words;
    static {
        words = new HashSet<>();
        words.add("select");
        words.add("update");
        words.add("delete");
        words.add("insert");
        words.add("into");
        words.add("from");
        words.add("where");
        words.add("and");
        words.add("or");
        words.add("null");
        words.add("set");
        words.add("order");
        words.add("group");
        // ...

    }

    public static boolean isSqlWord(String word) {
        if (word==null) {
            return false;
        }
        return words.contains(word.toLowerCase());
    }

//    public static final String SELECT = "select";
//    public static final String UPDATE = "update";
//    public static final String DELETE = "delete";
//    public static final String INSERT = "insert";
//    public static final String INTO = "into";
//    public static final String FROM = "from";
//    public static final String WHERE = "where";
//    public static final String AND = "and";
//    public static final String OR = "or";
//    public static final String NULL = "null";
//    public static final String SET = "set";
//    public static final String ORDER = "order";
//    public static final String GROUP = "group";
//    // ...
}
