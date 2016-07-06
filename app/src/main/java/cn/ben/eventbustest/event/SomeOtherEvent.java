package cn.ben.eventbustest.event;

@SuppressWarnings("unused")
public class SomeOtherEvent {
    @SuppressWarnings("FieldCanBeLocal")
    private final String message;

    public SomeOtherEvent(String message) {
        this.message = message;
    }
}