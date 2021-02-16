package org.charliocat.netty.chat.server.chat.executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class SchedulerTest {

    private Scheduler scheduler = new Scheduler("testing", 1);

    @Test
    public void ScheduleTaskOnExecutorAndCheckIsDone() {
        AtomicInteger counter = new AtomicInteger();

        Runnable task = () -> {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter.incrementAndGet();};
        Future<?> future = scheduler.execute(task);
        while (!future.isDone()) {
            try {
                Thread.sleep(100L);
            } catch (Exception ex) {
                fail("Fail");
            }
        }

        assertThat(counter.get()).isEqualTo(1);
    }

}