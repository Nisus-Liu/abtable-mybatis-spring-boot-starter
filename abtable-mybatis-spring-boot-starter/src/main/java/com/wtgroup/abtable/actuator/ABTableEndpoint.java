package com.wtgroup.abtable.actuator;

import com.wtgroup.abtable.ABTables;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/9 15:12
 */
//@Configuration
//@Endpoint(id = "abtable")   // ==> /actuator/abtable
@RestControllerEndpoint(id = "abtable")
@Slf4j
public class ABTableEndpoint {
    @Autowired
    private ABTables abTables;

    public ABTableEndpoint() {
        log.info("'ABTableEndpoint' inited");
    }

    /**
     * @param pure '_all' 返回所有
     * @return
     */
//    @ReadOperation   // 貌似不支持 @RequestParam 等注解
    @GetMapping("/{pure}")
    public Collection<ABTables.ABTable> status(@PathVariable(required = false) String pure) {
        if (StringUtils.isEmpty(pure) || "_all".equalsIgnoreCase(pure)) {
            // 返回所有
            return abTables.values();
        } else {
            return Collections.singletonList(abTables.get(pure));
        }
    }

    //    @WriteOperation
    @PostMapping("/switch/{pure}/{label}")
    public String forceSwitch(@PathVariable("pure")String pure,@PathVariable("label")String label) {

//        String pure = pureLabel.getPure();
//        String label = pureLabel.getLabel();
//        assert pure != null : "'pure' must not null";
//        assert label != null : "'label' must not null";

        ABTables.ABTable abTable = abTables.get(pure);
        if (abTable == null) {
            return pure + " 无对应的 ABTable";
        } else {

            ABTables.AB ab = null;
            String upLabel = label.toUpperCase();
            try {
                ab = ABTables.AB.valueOf(upLabel);
            } catch (IllegalArgumentException e) {
                return pure + " 不合法, 需要: 'a'或'b'";
            }

            ABTables.AB curLbl = abTable.getActiveLabel();
            if (curLbl != ab) {
                // 需要切换
                abTable.activate(ab);
                return "完成切换: " + abTable;
            } else {
                // 已经是预期的 label 忽略
                return "当前AB标签已经是 " + label + ", 忽略";
            }
        }
    }


    @Data
    public static class PureLabel {
        private String pure;
        private String label;
    }

}
