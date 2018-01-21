package com.duke.dbuslib.core;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author duke
 * @dateTime 2018-01-21 10:31
 * @description 线程池
 */
public class DExecutor {
    private static volatile DExecutor instance = null;
    private static volatile ExecutorService executor = null;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread #" + this.mCount.getAndIncrement());
        }
    };

    private DExecutor() {
    }

    public static DExecutor get() {
        if (instance == null) {
            synchronized (DExecutor.class) {
                if (instance == null) {
                    instance = new DExecutor();
                    //可变线程的线程池
                    executor = Executors.newCachedThreadPool(sThreadFactory);
                    //单线程的线程池
                    //Executors.newSingleThreadExecutor().execute(this);
                }
            }
        }
        return instance;
    }

    public Executor getInternalExecutor() {
        return executor;
    }

    public void execute(Runnable command) {
        executor.execute(command);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
    }

    public void shutdownNow() {
        executor.shutdownNow();
    }
}