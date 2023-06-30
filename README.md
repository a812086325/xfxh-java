# 简介
讯飞星火大模型流式输出，对接星火官方API，对接简单，只需增加配置即可完成对接，
支持Tokens计算。

# 使用方式
## 1、引入该依赖

```java
<dependency>
    <groupId>io.github.a812086325</groupId>
    <artifactId>xfxh-java</artifactId>
    <version>1.0</version>
</dependency>
```

## 2、添加yml配置
```yml
xfxh:
  apiHost: spark-api.xf-yun.com
  apiPath: /v1.1/chat
  appId: xxx
  apiKey: xxx
  apiSecret: xxx
```

## 3、编写配置类
```java
@Configuration
@ConfigurationProperties(prefix = "xfxh")
@Data
public class XfXhConfig {
    private String apiHost;
    private String apiPath;
    private String appId;
    private String apiKey;
    private String apiSecret;
}
```

## 4、在启动类或配置类注册一个bean
```java
@SpringBootApplication
public class DevScaffoldApplication {
    @Autowired
    private XfXhConfig xfXhConfig;

    public static void main(String[] args) {
        SpringApplication.run(DevScaffoldApplication.class, args);
    }

    @Bean
    public XhStreamClient xhStreamClient (){
        return XhStreamClient.builder()
                .apiHost(xfXhConfig.getApiHost())
                .apiPath(xfXhConfig.getApiPath())
                .appId(xfXhConfig.getAppId())
                .apiKey(xfXhConfig.getApiKey())
                .apiSecret(xfXhConfig.getApiSecret())

                .build();
    }
}
```

## 5、自定义一个Listener类继承WebSocketListener
```java
public class XfXhListener extends WebSocketListener {

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);

    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        super.onMessage(webSocket, text);
        System.out.println("text:\n" + text);
        ResponseDTO responseData = JSONObject.parseObject(text,ResponseDTO.class);
        if(0 == responseData.getHeader().getCode()){
            ResponseDTO.PayloadDTO pl = responseData.getPayload();
            List<MsgDTO> tests = pl.getChoices().getText();
            MsgDTO textDTO = tests.stream().findFirst().orElse(new MsgDTO());

            System.out.println(textDTO.toString());

            if(2 == responseData.getHeader().getStatus()){
                ResponseDTO.PayloadDTO.UsageDTO.TextDTO testDto = pl.getUsage().getText();
                Integer totalTokens = testDto.getTotalTokens();
                System.out.println("本次花费："+totalTokens + " tokens");


                webSocket.close(3,"客户端主动断开链接");
            }


        }else{
            System.out.println("返回结果错误：\n" + responseData.getHeader().getCode()+  responseData.getHeader().getMessage() );
        }
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        super.onClosed(webSocket, code, reason);
    }
}

```

## 6、在需要发送的地方，注入xhStreamClient就可以发送消息啦
1. 第一个参数为uid，用户标识
2. 第二个参数为消息对象，是一个集合，需要上下文回答，需要把历史消息也传入
3. 第三个参数为刚才自定义的Listener类
```java
@Autowired
private XhStreamClient xhStreamClient;

MsgDTO dto = MsgDTO.builder().role(MsgDTO.Role.USER.getName()).content("请介绍一下你自己").build();

xhStreamClient.sendMsg("123", Arrays.asList(dto),new XfXhListener());
```
