package com.pyr;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 测试基本流程操作
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BasicTest {

    public static final String PROCESS_DEFINITION_KEY = "test-basic";
    public static final String TASK_ASSIGNEE1 = "张三";
    public static final String TASK_ASSIGNEE2 = "王经理";


    /**
     *  部署话费报销流程，只有两个节点：创建话费报销（张三），直线经理审批（王经理）
     */
    @Test
    public void testDeployment(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3. 使用service进行流程的部署，定义流程名字，把bpmn部署到数据库中
        Deployment deploy = repositoryService.createDeployment()
                .name("话费报销")
                .addClasspathResource("bpmn/basic.bpmn20.xml")
                .deploy();
        // 4. 输出部署信息
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署名字="+deploy.getName());
        System.out.println("流程部署key="+deploy.getKey());
    }

    /**
     * 启动一个流程实例
     */
    @Test
    public void testStartProcess(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 3. 根据流程ID启动流程
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY);
        // 4. 输出
        System.out.println("流程定义id="+instance.getProcessDefinitionId());
        System.out.println("流程实例id="+instance.getId());
        System.out.println("当前实例的id="+instance.getActivityId());
        System.out.println("当前实例的name="+instance.getName());
        System.out.println("ProcessDefinitionKey="+instance.getProcessDefinitionKey());
        System.out.println("ProcessDefinitionName ="+instance.getProcessDefinitionName());
    }

    /**
     * 根据流程定义key和人名，查询当前用户的任务
     */
    @Test
    public void testFindPersonalTasks(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取taskService
        TaskService taskService = processEngine.getTaskService();
        // 3. 根据流程key和任务负责人 查询任务
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_DEFINITION_KEY)
                .taskAssignee(TASK_ASSIGNEE1)
                .list();
        // 4. 输出
        tasks.forEach(task -> {
            System.out.println("流程定义id ="+task.getProcessDefinitionId());
            System.out.println("流程实例id="+task.getProcessInstanceId());
            System.out.println("运行时流程执行实例Id="+task.getExecutionId());
            System.out.println("任务id="+task.getId());
            System.out.println("任务key="+task.getTaskDefinitionKey());
            System.out.println("任务名称="+task.getName());
            System.out.println("任务负责人="+task.getAssignee());
        });
    }

    /**
     * 完成任务
     */
    @Test
    public void testCompleteTask(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取taskService
        TaskService taskService = processEngine.getTaskService();
        // 3. 如果可以确定是一个任务，可以直接通过singleResult获得, 依次修改taskAssignee为直线经理，部门经理，财务人员
        Task tasks = taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_DEFINITION_KEY)
                .taskAssignee(TASK_ASSIGNEE2)
                .singleResult();
        // 3. 根据任务id完成任务
        taskService.complete(tasks.getId());
        System.out.println("DONE!");
    }
}
