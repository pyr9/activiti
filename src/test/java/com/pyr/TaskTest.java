package com.pyr;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 完成任务
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskTest {

    //=======================================完成任务========================================================

    /**
     * 根据taskId，完成任务
     */
    @Test
    public void testCompleteTask(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取taskService
        TaskService taskService = processEngine.getTaskService();
        // 3. 根据任务id完成任务
        taskService.complete("2505");
    }

    /**
     * 根据流程Key和负责人，完成任务
     */
    @Test
    public void testCompleteTask2(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取taskService
        TaskService taskService = processEngine.getTaskService();
        // 3. 如果可以确定是一个任务，可以直接通过singleResult获得, 依次修改taskAssignee为直线经理，部门经理，财务人员
        Task tasks = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .taskAssignee("赵财务")
                .singleResult();
        // 3. 根据任务id完成任务
        taskService.complete(tasks.getId());
        System.out.println("DONE!");
    }



    //===========================================查询任务====================================================

    /**
     * 查询任务列表
     */
    @Test
    public void testFindPersonalTasks(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取taskService
        TaskService taskService = processEngine.getTaskService();
        // 3. 根据流程key和任务负责人 查询任务
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey("process-variable")
                .taskAssignee("王总经理2")
                .list();
        // 4. 输出
        tasks.forEach(task -> {
            System.out.println("流程实例id="+task.getProcessInstanceId());
            System.out.println("任务id="+task.getId());
            System.out.println("任务负责人="+task.getAssignee());
            System.out.println("任务名称="+task.getName());
        });
    }



}
