package com.pyr;

import com.pyr.pojo.Evection;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pyr.BasicTest.PROCESS_DEFINITION_KEY;
import static com.pyr.BasicTest.TASK_ASSIGNEE2;

/**
 * 流程实例的操作方法
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessInstanceTest {
    //===========================================启动流程实例====================================================

    /**
     * 1. 普通启动一个流程实例
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
     * 2. 设置流程负责人,并启动一个流程实例
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
        assigneeMap.put("assignee1","李经理");
        assigneeMap.put("assignee2","王总经理");
        assigneeMap.put("assignee3","赵财务");
//        启动流程实例，同时还要设置流程定义的assignee的值
        runtimeService.startProcessInstanceByKey("myProcess",assigneeMap);
//       输出
        System.out.println(processEngine.getName());
    }

    /**
     * 3.添加businessKey,并启动一个流程实例
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
     * 4. 设置流程变量的值, 并启动流程实例
     */
    @Test
    public void startProcess(){
//        获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        流程定义key
        String key = "process-variable";
//       创建变量集合
        Map<String, Object> map = new HashMap<>();
//        创建出差pojo对象
        Evection evection = new Evection();
//        设置出差天数
        evection.setDays(5d);
//      定义流程变量，把出差pojo对象放入map
        map.put("evection",evection);
//      设置assignee的取值，用户可以在界面上设置流程的执行
        map.put("assignee0","小明2");
        map.put("assignee1","李经理2");
        map.put("assignee2","王总经理2");
        map.put("assignee3","赵财务2");
//        启动流程实例，并设置流程变量的值（把map传入）
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(key, map);
//      输出
        System.out.println("流程实例名称="+processInstance.getName());
        System.out.println("流程定义id=="+processInstance.getProcessDefinitionId());

    }

    //===========================================正在运行的流程实例信息====================================================


    /**
     * 查询流程定义key，查询正在运行的流程实例信息
     */
    @Test
    public void queryProcessInstance() {
        // 流程定义key
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> list = runtimeService
                .createProcessInstanceQuery()
                .processDefinitionKey(PROCESS_DEFINITION_KEY)//
                .list();

        for (ProcessInstance processInstance : list) {
            System.out.println("----------------------------");
            System.out.println("流程实例id：" + processInstance.getProcessInstanceId());
            System.out.println("所属流程定义id：" + processInstance.getProcessDefinitionId());
            System.out.println("是否执行完成：" + processInstance.isEnded());
            System.out.println("是否暂停：" + processInstance.isSuspended());
            System.out.println("当前活动标识：" + processInstance.getActivityId());
            System.out.println("StartUserId：" + processInstance.getStartUserId());

        }
    }

    //===========================================流程实例历史执行信息====================================================

    /**
     * 获取流程实例历史执行信息，可以根据EndActivityId，判断该流程是否结束
     */
    @Test
    public void findProcessInstanceHistoryInFo(){
//      获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

//      获取task
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_DEFINITION_KEY)
                .taskAssignee(TASK_ASSIGNEE2)
                .singleResult();

//      获取HistoryService
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();

        System.out.println("ProcessDefinitionId： "+historicProcessInstance.getProcessDefinitionId());
        System.out.println("Id："+historicProcessInstance.getId());
        System.out.println("name: "+historicProcessInstance.getName());
//      endActivityId为空，说明流程还未结束；endActivityId不为空，说明当前流程已经结束
        System.out.println("EndActivityId: "+historicProcessInstance.getEndActivityId());
        System.out.println("EndTime: "+historicProcessInstance.getEndTime());
    }


    /**
     * 查看流程实例运转的各个节点信息（包含开始、结束等非任务节点）
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
        instanceQuery.processDefinitionId("myProcess:1:c0a9b4c3-be58-11ed-a9bd-b68da16e99d1");
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
            //System.out.println(hi.getEndActivityId());
            System.out.println("<==========================>");
        }
    }
    //===========================================流程实例挂起与激活====================================================

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

    /**
     * 单个流程实例挂起
     */
    @Test
    public void SuspendSingleProcessInstance(){
//        获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
//        查询流程定义的对象
        ProcessInstance processInstance = runtimeService.
                createProcessInstanceQuery().
                processInstanceId("15001").
                singleResult();
//        得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processInstance.isSuspended();
//        流程定义id
        String processDefinitionId = processInstance.getId();
//        判断是否为暂停
        if(suspended){
//         如果是暂停，可以执行激活操作 ,参数：流程定义id
            runtimeService.activateProcessInstanceById(processDefinitionId);
            System.out.println("流程定义："+processDefinitionId+",已激活");
        }else{
//          如果是激活状态，可以暂停，参数：流程定义id
            runtimeService.suspendProcessInstanceById( processDefinitionId);
            System.out.println("流程定义："+processDefinitionId+",已挂起");
        }

    }
}
