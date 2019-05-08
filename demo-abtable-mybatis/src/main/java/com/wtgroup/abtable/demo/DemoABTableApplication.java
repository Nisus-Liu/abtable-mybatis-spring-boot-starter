package com.wtgroup.abtable.demo;

import com.wtgroup.abtable.demo.entity.TestABTable;
import com.wtgroup.abtable.demo.mapper.TestABTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

import java.util.List;

/**
 * @author Nisus Liu
 * @version 0.0.1
 * @email liuhejun108@163.com
 * @date 2019/5/2 9:31
 */

@SpringBootApplication
public class DemoABTableApplication implements ApplicationRunner {

    public static void main(String[] args) {
        //SpringApplication.run(Application.class, args);
        SpringApplication sa = new SpringApplication(DemoABTableApplication.class);
        // 留下 pid , 结束进程: kill `cat application.pid`
        sa.addListeners(new ApplicationPidFileWriter());
        sa.setBannerMode(Banner.Mode.OFF);
        sa.run(args);
    }

    @Autowired
    private TestABTableMapper testABTableMapper;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (true) {
            List<TestABTable> all = testABTableMapper.getAll();
            System.out.println(all);

            Thread.sleep(3000);
        }
    }
}
