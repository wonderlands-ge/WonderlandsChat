/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.schedulerutil.builders;

import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleBuilderBase;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleData;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleThread;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleTimestamp;

public class RepeatableBuilder
extends ScheduleThread
implements ScheduleBuilderBase {
    private final ScheduleData data;

    RepeatableBuilder(ScheduleData data) {
        super(data);
        this.data = data;
    }

    public ScheduleTimestamp<ScheduleThread> during(long amount) {
        return new ScheduleTimestamp<ScheduleThread>(new ScheduleThread(this.data), amount, this.data::setCancelIn);
    }

    @Override
    public ScheduleData getData() {
        return this.data;
    }
}

