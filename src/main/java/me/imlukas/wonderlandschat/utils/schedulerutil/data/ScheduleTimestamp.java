/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.schedulerutil.data;

import java.util.function.Consumer;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleBuilderBase;

public class ScheduleTimestamp<T extends ScheduleBuilderBase> {
    private final T builder;
    private final long time;
    private final Consumer<Long> execute;

    public ScheduleTimestamp(T builder, long time, Consumer<Long> execute) {
        this.builder = builder;
        this.time = time;
        this.execute = execute;
    }

    public T ticks() {
        this.execute.accept(this.time);
        return this.builder;
    }

    public T seconds() {
        this.execute.accept(this.time * 20L);
        return this.builder;
    }

    public T minutes() {
        this.execute.accept(this.time * 60L * 20L);
        return this.builder;
    }

    public T hours() {
        this.execute.accept(this.time * 60L * 60L * 20L);
        return this.builder;
    }
}

