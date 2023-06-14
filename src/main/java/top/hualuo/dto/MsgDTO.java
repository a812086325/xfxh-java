package top.hualuo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 消息对象
 * @author hualuo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MsgDTO {
    /**
     * 角色
     */
    private String role;
    /**
     * 消息内容
     */
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
