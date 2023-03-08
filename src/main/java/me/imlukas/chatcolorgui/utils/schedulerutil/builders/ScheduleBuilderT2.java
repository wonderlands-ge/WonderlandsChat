package me.imlukas.chatcolorgui.utils.schedulerutil.builders;

import lombok.Getter;
import me.imlukas.chatcolorgui.utils.schedulerutil.data.ScheduleBuilderBase;
import me.imlukas.chatcolorgui.utils.schedulerutil.data.ScheduleData;
import me.imlukas.chatcolorgui.utils.schedulerutil.data.ScheduleThread;

public class ScheduleBuilderT2 implements ScheduleBuilderBase {

    @Getter
    private final ScheduleData data;

    ScheduleBuilderT2(ScheduleData data) {
        this.data = data;
    }

    public ScheduleThread run(Runnable runnable) {
        data.setRunnable(runnable);
        return new ScheduleThread(data);
    }

}
