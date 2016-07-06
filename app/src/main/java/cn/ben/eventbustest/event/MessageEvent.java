package cn.ben.eventbustest.event;

/**
 * 定义事件
 */
public class MessageEvent {
    public final String message;

    public MessageEvent(String message) {
        this.message = message;
    }
}