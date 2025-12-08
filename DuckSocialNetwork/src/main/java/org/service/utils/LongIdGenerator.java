package org.service.utils;

import java.util.concurrent.atomic.AtomicLong;

public class LongIdGenerator implements IdGenerator<Long> {

    public LongIdGenerator(Long start) {
        this.counter = new AtomicLong(start);
    }

    private final AtomicLong counter;

    @Override
    public Long nextId() {
        return counter.getAndIncrement();
    }

    @Override
    public Long currentID() {
        return counter.get();
    }

    /**
     * Update the counter to be at least minValue.
     * This ensures the generator doesn't produce IDs that already exist.
     * Thread-safe operation.
     */
    public void ensureMinimum(Long minValue) {
        counter.updateAndGet(current -> Math.max(current, minValue));
    }
}
