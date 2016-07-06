package cn.ben.eventbustest.factory;

import android.util.SparseArray;

import org.greenrobot.eventbus.EventBus;

/**
 * 方法2
 * 用枚举工厂
 * EventBusFactoryEnum.START.getBus();
 */
@SuppressWarnings("ALL")
public enum EventBusFactoryEnum {
    CREATE(0),
    START(1);

    private int mType;

    EventBusFactoryEnum(int type) {
        mType = type;
    }

    public EventBus getBus() {
        return mBusSparseArray.get(mType);
    }

    private static SparseArray<EventBus> mBusSparseArray = new SparseArray<>(2);

    static {
        mBusSparseArray.put(CREATE.mType, EventBus.builder().build());
        mBusSparseArray.put(START.mType, EventBus.getDefault());
    }
}
