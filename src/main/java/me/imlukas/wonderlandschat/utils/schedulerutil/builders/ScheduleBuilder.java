/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.java.JavaPlugin
 */
package me.imlukas.wonderlandschat.utils.schedulerutil.builders;

import me.imlukas.wonderlandschat.utils.schedulerutil.builders.RepeatableT2;
import me.imlukas.wonderlandschat.utils.schedulerutil.builders.ScheduleBuilderT2;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleBuilderBase;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleData;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleThread;
import me.imlukas.wonderlandschat.utils.schedulerutil.data.ScheduleTimestamp;
import org.bukkit.plugin.java.JavaPlugin;

public class ScheduleBuilder
implements ScheduleBuilderBase {
    private ScheduleData data = new ScheduleData();

    public ScheduleBuilder(JavaPlugin plugin) {
        this.data.setPlugin(plugin);
    }

    public static ScheduleThread runIn1Tick(JavaPlugin plugin, Runnable runnable) {
        return new ScheduleBuilder(plugin).in(1L).ticks().run(runnable);
    }

    public ScheduleTimestamp<RepeatableT2> every(long number) {
        this.data.setRepeating(true);
        return new ScheduleTimestamp<RepeatableT2>(new RepeatableT2(this.data), number, this.data::setTicks);
    }

    public ScheduleTimestamp<ScheduleBuilderT2> in(long number) {
        this.data.setRepeating(false);
        return new ScheduleTimestamp<ScheduleBuilderT2>(new ScheduleBuilderT2(this.data), number, this.data::setTicks);
    }

    void setData(ScheduleData data) {
        this.data = data;
    }

    @Override
    public ScheduleData getData() {
        return this.data;
    }
}

