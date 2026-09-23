package com.atguigu.exam.entity.postgresql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * AI对话记忆表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_chat_memory")
public class AiChatMemory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    @TableField("conversation_id")
    private String conversationId;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 消息类型
     */
    @TableField("type")
    private MessageType type;

    /**
     * 创建时间
     */
    @TableField("timestamp")
    private LocalDateTime timestamp;

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        USER("USER"),
        ASSISTANT("ASSISTANT"),
        SYSTEM("SYSTEM"),
        TOOL("TOOL");

        private final String value;

        MessageType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static MessageType fromValue(String value) {
            for (MessageType type : MessageType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("未知的消息类型: " + value);
        }
    }
}
