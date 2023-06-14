package top.hualuo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class ResponseDTO {

    @JsonProperty("header")
    private HeaderDTO header;
    @JsonProperty("payload")
    private PayloadDTO payload;

    @NoArgsConstructor
    @Data
    public static class HeaderDTO {
        @JsonProperty("code")
        private Integer code;
        @JsonProperty("message")
        private String message;
        @JsonProperty("sid")
        private String sid;
        @JsonProperty("status")
        private Integer status;
    }

    @NoArgsConstructor
    @Data
    public static class PayloadDTO {
        @JsonProperty("choices")
        private ChoicesDTO choices;
        @JsonProperty("usage")
        private UsageDTO usage;

        @NoArgsConstructor
        @Data
        public static class ChoicesDTO {
            @JsonProperty("status")
            private Integer status;
            @JsonProperty("seq")
            private Integer seq;
            @JsonProperty("text")
            private List<MsgDTO> text;

        }

        @NoArgsConstructor
        @Data
        public static class UsageDTO {
            @JsonProperty("text")
            private TextDTO text;

            @NoArgsConstructor
            @Data
            public static class TextDTO {
                @JsonProperty("question_tokens")
                private Integer questionTokens;
                @JsonProperty("prompt_tokens")
                private Integer promptTokens;
                @JsonProperty("completion_tokens")
                private Integer completionTokens;
                @JsonProperty("total_tokens")
                private Integer totalTokens;
            }
        }
    }
}
