package top.hualuo.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class RequestDTO {

    @JsonProperty("header")
    private HeaderDTO header;
    @JsonProperty("parameter")
    private ParameterDTO parameter;
    @JsonProperty("payload")
    private PayloadDTO payload;

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class HeaderDTO {
        @JSONField(name = "app_id")
        private String appId;
        @JSONField(name = "uid")
        private String uid;
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class ParameterDTO {
        private ChatDTO chat;

        @NoArgsConstructor
        @Data
        public static class ChatDTO {
            @JsonProperty("domain")
            private String domain = "general";
            @JsonProperty("temperature")
            private Double temperature = 0.5;
            @JSONField(name = "max_tokens")
            private Integer maxTokens = 2048;
        }
    }

    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    public static class PayloadDTO {
        @JsonProperty("message")
        private MessageDTO message;

        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        public static class MessageDTO {
            @JsonProperty("text")
            private List<MsgDTO> text;


        }
    }
}
