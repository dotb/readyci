package com.squarepolka.readyci.webhook;

import com.squarepolka.readyci.taskrunner.TaskRunnerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebHookView {

    private TaskRunnerFactory taskRunnerFactory;
    private WebHookPresenter webHookPresenter;

    @Autowired
    public WebHookView(WebHookPresenter webHookPresenter, TaskRunnerFactory taskRunnerFactory) {
        this.webHookPresenter = webHookPresenter;
        this.taskRunnerFactory = taskRunnerFactory;

        webHookPresenter.setView(this);
    }

    @RequestMapping(value = "/webhook", method = RequestMethod.GET)
    public WebHookResponse handleWebHook() {

        webHookPresenter.handleWebHook();

        WebHookResponse webHookResponse = new WebHookResponse();
        webHookResponse.message = "Thanks";

        return webHookResponse;
    }
}
