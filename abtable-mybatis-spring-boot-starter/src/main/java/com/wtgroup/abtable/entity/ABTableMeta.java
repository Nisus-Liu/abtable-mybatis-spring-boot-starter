package com.wtgroup.abtable.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/2 2:06
 */
@Data
@ToString
public class ABTableMeta {
    public static final String COLUMNS = "pure, ab";

    private long id;
    private String pure;
    private String ab;
    private Date updateTime;

}
