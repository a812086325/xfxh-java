package top.hualuo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MsgDTO {
    private String role;
    private String content;
    private Integer index;

    @Getter
    public static enum Role {
        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant");

        private String name;

        private Role(String name) {
            this.name = name;
        }
    }

}
