package com.squarepolka.readyci.tasks.code;

import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskGitCheckout implements Task {
    @Override
    public String taskIdentifier() {
        return "checkout_git";
    }

    @Override
    public String description() {
        return "fetching code from ....";
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
