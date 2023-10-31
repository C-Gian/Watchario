package com.example.rememberme.utils;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;

public final class ReminderWorker extends Worker {
    @NotNull
    private final Context context;
    @NotNull
    private final WorkerParameters params;

    @NotNull
    public Result doWork() {
        new NotificationHelper(getContext()).createNotification(String.valueOf(this.getInputData().getString("Name")), this.getInputData().getString("Type"));
        return Result.success();
    }

    public ReminderWorker(@NotNull Context context, @NotNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        this.params = params;
    }

    @NotNull
    public final Context getContext() {
        return this.context;
    }

    @NotNull
    public final WorkerParameters getParams() {
        return this.params;
    }

}