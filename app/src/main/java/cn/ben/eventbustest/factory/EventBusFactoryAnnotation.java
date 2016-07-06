package cn.ben.eventbustest.factory;

import android.support.annotation.IntDef;
import android.util.SparseArray;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 方法1
 * 用annotation配合使用工厂
 * EventBusFactoryAnnotation.getBus(EventBusFactory.START);
 * EventBusFactoryAnnotation.getBus();
 * 明确事件的生命周期，根据不同生命周期使用不同的EventBus
 */
@SuppressWarnings("ALL")
public class EventBusFactoryAnnotation {
    private static SparseArray<EventBus> mBusSparseArray = new SparseArray<>(2);

    @IntDef({CREATE, START})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BusType {
    }

    public static final int CREATE = 0;
    public static final int START = 1;

    static {
        mBusSparseArray.put(CREATE, EventBus.builder().build());
        mBusSparseArray.put(START, EventBus.getDefault());
    }

    public static EventBus getBus() {
        return getBus(START);
    }

    public static EventBus getBus(@BusType int type) {
        return mBusSparseArray.get(type);
    }

}