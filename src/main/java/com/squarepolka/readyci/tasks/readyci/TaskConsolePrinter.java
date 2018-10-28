package com.squarepolka.readyci.tasks.readyci;

import org.springframework.stereotype.Component;

@Component
public class TaskConsolePrinter {

    public void consolePrintln(String line) {
        System.out.println(line);
    }

}
