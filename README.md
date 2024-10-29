# MSFRPC
## MSF的介绍
    MSF是Metasploit Framework（简称MSF）是由Rapid7公司开发的一个开源渗透测试平台，旨在帮助安全专业人员识别、利用和管理应用程序和网络中的安全漏洞。
用途：MSF主要用于渗透测试、漏洞评估和安全研究。它提供了丰富的工具和模块，使得安全专家可以模拟攻击、测试防御机制的有效性，并验证系统的安全性。
## 本SDK主要的工作
是在kali靶机上开启MSF的RPC模式，让我去进行工具的运行。
其实说是一种RPC模式，其实是通过Restful的风格去调用接口。用户可以编写脚本或应用程序来调用 MSF 的功能。
最后开发成一个Start提供给开发者使用。
代码实现JAVA
使用的插件：
1. msppack:msgpack-core 是一个用于处理 MessagePack 格式的 Java 库。MessagePack 是一种高效的二进制序列化格式，类似于 JSON，但更紧凑，更适合在网络传输中使用。msgpack-core 提供了序列化和反序列化数据的功能。
2. msgpack-core 的作用  

   1. 序列化：将 Java 对象转换为 MessagePack 格式的数据。
   2. 反序列化：将 MessagePack 格式的数据转换回 Java 对象。
   3. 高效传输：由于 MessagePack 格式更紧凑，因此在网络传输中可以减少带宽占用，提高传输效率。
```java
<dependency>
        <groupId>org.msgpack</groupId>
        <artifactId>msgpack-core</artifactId>
        <version>0.8.24</version>
    </dependency>
```  

  2.我的序列化方式：  
```java
public static byte[] packOptions(Object... options) throws IOException {
        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            //写入MAp头部
            packer.packArrayHeader(options.length);
            for (Object option : options) {
                if (option instanceof String) {
                    packer.packString((String) option);
                } else if (option instanceof Integer) {
                    packer.packInt((Integer) option);
                } else {
                    throw new IllegalArgumentException("Unsupported type: " + option.getClass().getName());
                }
            }
            return packer.toByteArray();
        }
    }
```   
3. 实现 RESTful 风格的客户端请求的方式。通过Java 的 HttpURLConnection 发送一个 POST 请求，请求体中包含 MessagePack 格式的数据。下面是对这段代码的详细说明：  
   1.创建URL对象
   2.打开HTTP链接
   3. 设置请求方式
   4. 设置请求头
   5. 设置连接属性
   6. 写入请求体
```java
URL url = new URL("http://" + server + ":" + port + "/api");
connection = (HttpURLConnection) url.openConnection();
connection.setRequestMethod("POST");
connection.setRequestProperty("Content-Type", "binary/message-pack");
if (token != null) {
connection.setRequestProperty("Authorization", "Bearer " + token);
}
connection.setDoOutput(true);
try (OutputStream os = connection.getOutputStream()) {
    os.write(data);
}
```  
具体代码请看源代码  

如有疑问请issue


