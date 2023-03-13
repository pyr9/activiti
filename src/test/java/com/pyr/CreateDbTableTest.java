package com.pyr;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CreateDbTableTest {
    /**
     * 生成 activiti的数据库表
     */
    @Test
    public void testCreateDbTable() {
        /**
         * 使用classpath下的activiti.cfg.xml中的配置创建processEngine
         * getDefaultProcessEngine会默认从resources下读取名字为activiti.cfg.xml文件
         * 创建processEngine时，就会创建mysql表
         */
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(processEngine);
    }
}
