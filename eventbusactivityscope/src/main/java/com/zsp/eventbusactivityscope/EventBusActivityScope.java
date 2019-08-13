package com.zsp.eventbusactivityscope;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

/**
 * @decs: EventBusActivityScope
 * @author: 郑少鹏
 * @date: 2019/6/17 18:07
 */
public class EventBusActivityScope {
    private static AtomicBoolean sInitialized = new AtomicBoolean(false);
    private static volatile EventBus sInvalidEventBus;
    private static final Map<Activity, LazyEventBusInstance> S_ACTIVITY_EVENT_BUS_SCOPE_POOL = new ConcurrentHashMap<>();

    static void init(Context context) {
        if (sInitialized.getAndSet(true)) {
            return;
        }
        ((Application) context.getApplicationContext())
                .registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                    private Handler mainHandler = new Handler(Looper.getMainLooper());

                    @Override
                    public void onActivityCreated(@NonNull Activity activity, Bundle bundle) {
                        S_ACTIVITY_EVENT_BUS_SCOPE_POOL.put(activity, new LazyEventBusInstance());
                    }

                    @Override
                    public void onActivityStarted(@NonNull Activity activity) {

                    }

                    @Override
                    public void onActivityResumed(@NonNull Activity activity) {

                    }

                    @Override
                    public void onActivityPaused(@NonNull Activity activity) {

                    }

                    @Override
                    public void onActivityStopped(@NonNull Activity activity) {

                    }

                    @Override
                    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

                    }

                    @Override
                    public void onActivityDestroyed(@NonNull final Activity activity) {
                        if (!S_ACTIVITY_EVENT_BUS_SCOPE_POOL.containsKey(activity)) {
                            return;
                        }
                        mainHandler.post(new Runnable() {
                            // Make sure Fragment's onDestroy() has been called.
                            @Override
                            public void run() {
                                S_ACTIVITY_EVENT_BUS_SCOPE_POOL.remove(activity);
                            }
                        });
                    }
                });
    }

    /**
     * Get the activity-scope EventBus instance.
     *
     * @param activity Activity
     * @return he activity-scope EventBus instance
     */
    public static EventBus getDefault(Activity activity) {
        if (activity == null) {
            Timber.d("Can't find the Activity, the Activity is null!");
            return invalidEventBus();
        }
        LazyEventBusInstance lazyEventBusInstance = S_ACTIVITY_EVENT_BUS_SCOPE_POOL.get(activity);
        if (lazyEventBusInstance == null) {
            Timber.d("Can't find the Activity, it has been removed!");
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
