package com.pyr;

import com.pyr.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestParallelGateway {
    @Test
    public void testDeployment(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3. 使用service进行流程的部署，定义流程名字，把bpmn部署到数据库中
        Deployment deploy = repositoryService.createDeployment()
                .name("测试并行网关")
                .addClasspathResource("bpmn/parallel-gateway.bpmn20.xml")
                .deploy();
        // 4. 输出部署信息
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署名字="+deploy.getName());
    }
    /**
     * 启动流程实例,设置流程变量的值
     */
    @Test
    public void startProcess(){
//        获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        流程定义key
        String key = "parallelGateway";
//       创建变量集合
        Map<String, Object> map = new HashMap<>();
//        创建出差pojo对象
        Evection evection = new Evection();
//        设置出差天数
        evection.setDays(2d);
//      定义流程变量，把出差pojo对象放入map
        map.put("evection",evection);
//        启动流程实例，并设置流程变量的值（把map传入）
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(key, map);
//      输出
        System.out.println("流程实例名称="+processInstance.getName());
        System.out.println("流程定义id=="+processInstance.getProcessDefinitionId());

    }

    @Test
    public void testCompleteTask2(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取taskService
        TaskService taskService = processEngine.getTaskService();
        // 3. 如果可以确定是一个任务，可以直接通过singleResult获得, 依次修改taskAssignee为直线经理，部门经理，财务人员
        Task tasks = taskService.createTaskQuery()
                .processDefinitionKey("parallelGateway")
                .taskAssignee("cat")
                .singleResult();
        // 3. 根据任务id完成任务
        taskService.complete(tasks.getId());
        System.out.println("DONE!");
    }
}
