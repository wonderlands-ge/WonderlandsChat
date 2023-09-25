/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.schedulerutil.builders;

import me.imlukas.wonderlandschat.utils.schedulerutil.builders.RepeatableBuilder;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleBuilderBase;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleData;

public class RepeatableT2
implements ScheduleBuilderBase {
    private final ScheduleData data;

    RepeatableT2(ScheduleData data) {
        this.data = data;
    }

    public RepeatableBuilder run(Runnable runnable) {
        this.data.setRunnable(runnable);
        return new RepeatableBuilder(this.data);
    }

    @Override
    public ScheduleData getData() {
        return this.data;
    }
}

