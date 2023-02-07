package com.pyr;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitiDemo {

    @Test
    public void testDeployment(){
      // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
      // 2. 获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
      // 3. 使用service进行流程的部署，定义流程名字，把bpmn部署到数据库中
        Deployment deploy = repositoryService.createDeployment()
                .name("话费报销")
                .addClasspathResource("bpmn/phoneExpense.bpmn20.xml")
                .deploy();
      // 4. 输出部署信息
        System.out.println("流程部署id="+deploy.getId());
        System.out.println("流程部署名字="+deploy.getName());
    }

    @Test
    public void testStartProcess(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 3. 根据流程ID启动流程
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("myProcess");
        // 4. 输出
        System.out.println("流程定义id="+instance.getProcessDefinitionId());
        System.out.println("流程实例id="+instance.getId());
        System.out.println("当前活动的id="+instance.getActivityId());
    }


    @Test
    public void testFindPersonalTasks(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取taskService
        TaskService taskService = processEngine.getTaskService();
        // 3. 根据流程key和任务负责人 查询任务
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .taskAssignee("Lisa1")
                .list();
        // 4. 输出
        tasks.forEach(task -> {
            System.out.println("流程实例id="+task.getProcessInstanceId());
            System.out.println("任务id="+task.getId());
            System.out.println("任务负责人="+task.getAssignee());
            System.out.println("任务名称="+task.getName());
        });
    }

    @Test
    public void testCompleteTask(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取taskService
        TaskService taskService = processEngine.getTaskService();
        // 3. 根据任务id完成任务
        taskService.complete("2505");
    }

    @Test
    public void testCompleteTask2(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取taskService
        TaskService taskService = processEngine.getTaskService();
        // 3. 如果可以确定是一个任务，可以直接通过singleResult获得, 依次修改taskAssignee为直线经理，部门经理，财务人员
        Task tasks = taskService.createTaskQuery()
                .processDefinitionKey("myProcess")
                .taskAssignee("张三")
                .singleResult();
        // 3. 根据任务id完成任务
        taskService.complete(tasks.getId());
        System.out.println("DONE!");
    }

    //ACT_RE_PROCDEF
    @Test
    public void queryProcessDefinition(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        List<ProcessDefinition> definitionList = processDefinitionQuery.processDefinitionKey("myProcess")
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        for (ProcessDefinition processDefinition : definitionList) {
            System.out.println("流程定义 id="+processDefinition.getId());
            System.out.println("流程定义 name="+processDefinition.getName());
            System.out.println("流程定义 key="+processDefinition.getKey());
            System.out.println("流程定义 Version="+processDefinition.getVersion());
            System.out.println("流程部署ID ="+processDefinition.getDeploymentId());
        }
    }

    @Test
    public void deleteDeployment() {
        // 流程部署id
        String deploymentId = "1";

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 通过流程引擎获取repositoryService
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        //删除流程定义，如果该流程定义已有流程实例启动则删除时出错
        //repositoryService.deleteDeployment(deploymentId);
        //设置true 级联删除流程定义，即使该流程有流程实例启动也可以删除，设置为false非级别删除方式，如果流程
        repositoryService.deleteDeployment(deploymentId, true);
    }

    /**
     * 查看历史信息
     */
    @Test
    public void findHistoryInfo(){
//      获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        获取HistoryService
        HistoryService historyService = processEngine.getHistoryService();
//        获取 actinst表的查询对象
        HistoricActivityInstanceQuery instanceQuery = historyService.createHistoricActivityInstanceQuery();
//        查询 actinst表，条件：根据 InstanceId 查询
//        instanceQuery.processInstanceId("2501");
//        查询 actinst表，条件：根据 DefinitionId 查询
        instanceQuery.processDefinitionId("myProcess:2:12503");
//        增加排序操作,orderByHistoricActivityInstanceStartTime 根据开始时间排序 asc 升序
        instanceQuery.orderByHistoricActivityInstanceStartTime().asc();
//        查询所有内容
        List<HistoricActivityInstance> activityInstanceList = instanceQuery.list();
//        输出
        for (HistoricActivityInstance hi : activityInstanceList) {
            System.out.println(hi.getActivityId());
            System.out.println(hi.getActivityName());
            System.out.println(hi.getProcessDefinitionId());
            System.out.println(hi.getProcessInstanceId());
            System.out.println("<==========================>");
        }
    }

    /**
     * 启动流程实例，添加businessKey
     */
    @Test
    public void addBusinessKey(){
//        1、得到ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        2、得到RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        3、启动流程实例，同时还要指定业务标识businessKey，也就是出差申请单id，这里是1001
        ProcessInstance processInstance = runtimeService.
                startProcessInstanceByKey("myProcess","0001");
//        4、输出processInstance相关属性
        System.out.println("业务id=="+processInstance.getBusinessKey());

    }


    /**
     * 查询流程实例
     */
    @Test
    public void queryProcessInstance() {
        // 流程定义key
        String processDefinitionKey = "myProcess";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> list = runtimeService
                .createProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)//
                .list();

        for (ProcessInstance processInstance : list) {
            System.out.println("----------------------------");
            System.out.println("流程实例id："
                    + processInstance.getProcessInstanceId());
            System.out.println("所属流程定义id："
                    + processInstance.getProcessDefinitionId());
            System.out.println("是否执行完成：" + processInstance.isEnded());
            System.out.println("是否暂停：" + processInstance.isSuspended());
            System.out.println("当前活动标识：" + processInstance.getActivityId());
        }
    }

    /**
     * 设置流程负责人
     */
    @Test
    public void assigneeUEL(){
//      获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        获取 RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        设置assignee的取值，用户可以在界面上设置流程的执行
        Map<String,Object> assigneeMap = new HashMap<>();
        assigneeMap.put("assignee0","王武");
        assigneeMap.put("assignee1","李经理3.0");
        assigneeMap.put("assignee2","王总经理.0");
        assigneeMap.put("assignee3","赵财务");
//        启动流程实例，同时还要设置流程定义的assignee的值
        runtimeService.startProcessInstanceByKey("myProcess",assigneeMap);
//       输出
        System.out.println(processEngine.getName());
    }

    /**
     * 全部流程实例挂起与激活
     * 流程定义为挂起状态时：
     *  1.该流程定义将不允许启动新的流程实例，
     *  2.该流程定义下所有的流程实例将全部挂起暂停执行。
     */
    @Test
    public void SuspendAllProcessInstance(){
//        获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
//        查询流程定义的对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().
                processDefinitionKey("myProcess").
                singleResult();
//        得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processDefinition.isSuspended();
//        流程定义id
        String processDefinitionId = processDefinition.getId();
//        判断是否为暂停
        if(suspended){
//         如果是暂停，可以执行激活操作 ,参数1 ：流程定义id ，参数2：是否激活，参数3：激活时间
            repositoryService.activateProcessDefinitionById(processDefinitionId,
                    true,
                    null
            );
            System.out.println("流程定义："+processDefinitionId+",已激活");
        }else{
//          如果是激活状态，可以暂停，参数1 ：流程定义id ，参数2：是否暂停，参数3：暂停时间
            repositoryService.suspendProcessDefinitionById(processDefinitionId,
                    true,
                    null);
            System.out.println("流程定义："+processDefinitionId+",已挂起");
        }

    }
}
