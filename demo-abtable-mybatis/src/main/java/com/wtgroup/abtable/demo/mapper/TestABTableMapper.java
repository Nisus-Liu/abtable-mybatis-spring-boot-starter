package com.wtgroup.abtable.demo.mapper;

import com.wtgroup.abtable.demo.entity.TestABTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/2 10:58
 */
@Mapper
public interface TestABTableMapper {

    @Select("ABTABLE: select * from test_abtable_a")
    List<TestABTable> getAll();
}
