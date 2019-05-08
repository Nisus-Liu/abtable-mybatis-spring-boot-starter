package com.wtgroup.abtable.config;

import com.wtgroup.abtable.ABTables;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/2 2:10
 */
@Configuration
@ConfigurationProperties(prefix = "abtable")
@Data
@Slf4j
public class ABTablesConfig {
    private List<String> pureNameList;
    private List<String> abfixList;
//    /**
//     * 配置信息是 replace into 元数据表;
//     * 还是 insert ignore.
//     * abfix 优先级: 配置文件(若有) > 库表
//     * */
//    private volatile boolean resetAbfix;
    /**
     * 是否重置 ab table meta
     * pureNameList 有配置时, 就会重置 abtable_meta
     */
    private volatile boolean resetABTableMeta;

    @Bean
    @ConditionalOnMissingBean
    public ABTables abTables() {
        ABTables abTables = new ABTables();

        if (pureNameList ==null || pureNameList.isEmpty()) {
            resetABTableMeta = false;
        }else{
            // abfixList 没有匹配配置 ==> 停止
            if (abfixList == null || abfixList.size() != pureNameList.size()) {
                throw new RuntimeException("'pureNameList' must match with 'abfixList': requires same number. " +
                        "'pureNameList': "+pureNameList +", 'abfixList': "+abfixList);
            }

            for (int i = 0; i < pureNameList.size(); i++) {
                String pure = pureNameList.get(i);

                String ab = abfixList.get(i);
                // 不接受 不正确 ab 标识
                ABTables.AB abEnum = normalize(ab);

                ABTables.ABTable abTable = new ABTables.ABTable(pure, abEnum);
                abTables.put(pure, abTable);
            }

            resetABTableMeta = true;
        }


        return abTables;
    }

    /**a/b,A/B 才会接受, 其他的返回 null
     * @param ab
     * @return
     */
    private ABTables.AB normalize(String ab) {
        if (ABTables.AB.A.name().equalsIgnoreCase(ab)) {
            return ABTables.AB.A;
        } else if (ABTables.AB.B.name().equalsIgnoreCase(ab)) {
            return ABTables.AB.B;
        }else{
            throw new RuntimeException("无效的 abfix : "+ ab);
        }
    }


}
