package com.zsp.eventbusactivityscope;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @decs: EventBusActivityScope
 * @author: 郑少鹏
 * @date: 2019/6/17 18:07
 */
public class EventBusActivityScope {
    private static final String TAG = EventBusActivityScope.class.getSimpleName();
    private static final Map<Activity, LazyEventBusInstance> ACTIVITY_EVENTBUS_BUS_SCOPE_POOL = new ConcurrentHashMap<>();
    private static AtomicBoolean sInitialized = new AtomicBoolean(false);
    private static volatile EventBus sInvalidEventBus;

    static void init(Context context) {
        if (sInitialized.getAndSet(true)) {
            return;
        }
        ((Application) context.getApplicationContext())
                .registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    private Handler mainHandler = new Handler(Looper.getMainLooper());

                    @Override
                    public void onActivityCreated(Activity activity, Bundle bundle) {
                        ACTIVITY_EVENTBUS_BUS_SCOPE_POOL.put(activity, new LazyEventBusInstance());
                    }

                    @Override
                    public void onActivityStarted(Activity activity) {
                    }

                    @Override
                    public void onActivityResumed(Activity activity) {
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {
                    }

                    @Override
                    public void onActivityStopped(Activity activity) {

                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                    }

                    @Override
                    public void onActivityDestroyed(final Activity activity) {
                        if (!ACTIVITY_EVENTBUS_BUS_SCOPE_POOL.containsKey(activity)) {
                            return;

                        }
                        mainHandler.post(new Runnable() {
                            // Make sure Fragment's onDestroy() has been called.
                            @Override
                            public void run() {
                                ACTIVITY_EVENTBUS_BUS_SCOPE_POOL.remove(activity);
                            }
                        });
                    }
                });
    }

    /**
     * Get the activity-scope EventBus instance.
     *
     * @param activity Activity
     * @return EventBus
     */
    public static EventBus getDefault(Activity activity) {
        if (activity == null) {
            Log.e(TAG, "Can't find the Activity, the Activity is null!");
            return invalidEventBus();
        }
        LazyEventBusInstance lazyEventBusInstance = ACTIVITY_EVENTBUS_BUS_SCOPE_POOL.get(activity);
        if (lazyEventBusInstance == null) {
            Log.e(TAG, "Can't find the Activity, it has been removed!");
            return invalidEventBus();
        }
        return lazyEventBusInstance.getInstance();
    }

    private static EventBus invalidEventBus() {
        if (sInvalidEventBus == null) {
            synchronized (EventBusActivityScope.class) {
                if (sInvalidEventBus == null) {
                    sInvalidEventBus = new EventBus();

                }
            }
        }
        return sInvalidEventBus;
    }

    static class LazyEventBusInstance {
        private volatile EventBus eventBus;

        EventBus getInstance() {
            if (eventBus == null) {
                synchronized (this) {
                    if (eventBus == null) {
                        eventBus = new EventBus();
                    }
                }
            }
            return eventBus;
        }
    }
}
