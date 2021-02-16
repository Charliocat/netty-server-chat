package org.charliocat.netty.chat.server.chat.executor;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler implements Executor {

    private final ScheduledThreadPoolExecutor executor;

    public Scheduler(String schedulerName, Integer poolSize) {
        executor = new ScheduledThreadPoolExecutor(poolSize, new ThreadFactory() {
            private AtomicInteger counter = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, String.format("%s_%d", schedulerName, counter.incrementAndGet()));
                t.setDaemon(true);

                return t;
            }
        });
    }

    public Future<?> execute(Runnable task) {
        return executor.submit(task);
    }

}
