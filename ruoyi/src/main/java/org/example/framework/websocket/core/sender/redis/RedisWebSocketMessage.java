package org.example.framework.websocket.core.sender.redis;

import org.example.framework.mq.redis.core.pubsub.AbstractRedisChannelMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Redis 广播 WebSocket 的消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RedisWebSocketMessage extends AbstractRedisChannelMessage {

    /**
     * Session 编号
     */
    private String sessionId;
    /**
     * 用户类型
     */
    private Integer userType;
    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 消息类型
     */
    private String messageType;
    /**
     * 消息内容
     */
    private String messageContent;

}
