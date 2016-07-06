package cn.ben.eventbustest.event;

import java.util.List;

@SuppressWarnings("ALL")
public class EventWithGoodManagement {
    public static class UserListEvent {
        public List<User> users;
    }

    public static class ItemListEvent {
        public List<Item> items;
    }

    // 注意，不是相同类型就一定要作为一个事件封装，具体需要考虑业务情景跟代码情况，比如事件行为不同、事件生命周期不同，如果有必要，写封装成两个Event可能是更好的选择。
    public static class UserListUpdateEventOnCreate {
        public List<User> users;
    }

    public static class UserListUpdateEventOnStart {
        public List<User> users;
    }

    public static class UserListRemoveEventOnStart {
        public List<User> users;
    }

    private static class User {
    }

    private static class Item {
    }
}
