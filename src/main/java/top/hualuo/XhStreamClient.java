package top.hualuo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import top.hualuo.dto.MsgDTO;
import top.hualuo.dto.RequestDTO;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

public class XhStreamClient {
    private String apiHost;
    private String apiPath;
    private String appId;
    private String apiKey;
    private String apiSecret;

    public static Builder builder() {
        return new Builder();
    }

    private XhStreamClient(Builder builder) {
        if (StrUtil.isBlank(builder.apiHost)) {
            builder.apiHost = "spark-api.xf-yun.com";
        }
        this.apiHost = builder.apiHost;

        this.apiPath = builder.apiPath;

        this.appId = builder.appId;

        this.apiKey = builder.apiKey;

        this.apiSecret = builder.apiSecret;
    }

    public static final class Builder {
        private String apiHost;
        private String apiPath;
        private String appId;
        private String apiKey;
        private String apiSecret;

        public Builder() {
        }

        public Builder apiHost(String val) {
            this.apiHost = val;
            return this;
        }

        public Builder apiPath(String val) {
            this.apiPath = val;
            return this;
        }

        public Builder appId(String val) {
            this.appId = val;
            return this;
        }

        public Builder apiKey(String val) {
            this.apiKey = val;
            return this;
        }

        public Builder apiSecret(String val) {
            this.apiSecret = val;
            return this;
        }

        public XhStreamClient build() {
            return new XhStreamClient(this);
        }
    }



    public String getAuthorizationUrl(){
        try {
            // 获取鉴权时间 date
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            String date = format.format(new Date());

            // 获取signature_origin字段
            StringBuilder builder = new StringBuilder("host: ").append(this.apiHost).append("\n").
                    append("date: ").append(date).append("\n").
                    append("GET ").append(this.apiPath).append(" HTTP/1.1");

            // 获得signatue
            Charset charset = Charset.forName("UTF-8");
            Mac mac = Mac.getInstance("hmacsha256");
            SecretKeySpec sp = new SecretKeySpec(this.apiSecret.getBytes(charset),"hmacsha256");
            mac.init(sp);
            byte[] basebefore = mac.doFinal(builder.toString().getBytes(charset));
            String signature = Base64.getEncoder().encodeToString(basebefore);
            //获得 authorization_origin
            String authorization_origin = String.format("api_key=\"%s\",algorithm=\"%s\",headers=\"%s\",signature=\"%s\"",this.apiKey,"hmac-sha256","host date request-line",signature);
            //获得authorization
            String authorization = Base64.getEncoder().encodeToString(authorization_origin.getBytes(charset));
            // 获取httpUrl
            Map<String,Object> param = new HashMap<>();
            param.put("authorization",authorization);
            param.put("date",date);
            param.put("host",this.apiHost);

            String toParams = HttpUtil.toParams(param);

            return "wss://" + this.apiHost + this.apiPath + "?" + toParams;
        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 获取请求参数
     * @param uid
     * @param msgList
     * @return
     */
    public RequestDTO getRequestParam(String uid, List<MsgDTO> msgList) {
        RequestDTO dto = new RequestDTO();
        dto.setHeader(new RequestDTO.HeaderDTO(this.appId,uid));
        dto.setParameter(new RequestDTO.ParameterDTO(new RequestDTO.ParameterDTO.ChatDTO()));
        dto.setPayload(new RequestDTO.PayloadDTO(new RequestDTO.PayloadDTO.MessageDTO(msgList)));
        return dto;
    }

    /**
     * 发送消息
     * @param uid
     * @param msgList
     * @return
     */
    public WebSocket sendMsg(String uid, List<MsgDTO> msgList, WebSocketListener listener) {
        // 获取鉴权url
        String authorizationUrl = this.getAuthorizationUrl();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(authorizationUrl).build();
        WebSocket webSocket = okHttpClient.newWebSocket(request,listener);


        RequestDTO requestDTO = this.getRequestParam(uid,msgList);
        System.out.println("param==============");
        System.out.println(JSONObject.toJSONString(requestDTO));

        webSocket.send(JSONObject.toJSONString(requestDTO));

        return webSocket;
    }
}
