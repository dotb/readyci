package com.squarepolka.readyci.tasks.app.ios;

import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskCompileIOSApp implements Task {
    @Override
    public String taskIdentifier() {
        return "compile_ios";
    }

    @Override
    public String description() {
        return "Compiling iOS app";
    }

    @Override
    public boolean shouldStopOnFailure() {
        return false;
    }

    @Override
    public boolean performTask() {
        return true;
    }
}
