/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.schedulerutil.data;

import me.imlukas.wonderlandschat.utils.schedulerutil.ScheduledTask;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleBuilderBase;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleData;

public class ScheduleThread
implements ScheduleBuilderBase {
    private final ScheduleData data;

    public ScheduleThread(ScheduleData data) {
        this.data = data;
    }

    public ScheduledTask sync() {
        this.data.setSync(true);
        return new ScheduledTask(this.data.getPlugin(), this.data);
    }

    public ScheduledTask async() {
        this.data.setSync(false);
        return new ScheduledTask(this.data.getPlugin(), this.data);
    }

    @Override
    public ScheduleData getData() {
        return this.data;
    }
}

