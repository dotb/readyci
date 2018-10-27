package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class TaskOutputHandler {


    private static final Logger LOGGER = LoggerFactory.getLogger(TaskOutputHandler.class);

    /**
     * Handle the process output stream in one of two ways
     *
     * 1 - Log the output to the console if the LOGGER is in debug mode
     * 2 - Drain half of the output to ensure long running processes don't hang,
     *  while leaving some output in the buffer in case an error is thrown
     *  and the output is needed to debug
     * @param process
     * @throws IOException
     */
    public void handleProcessOutput(Process process) throws IOException {
        while (process.isAlive()) {
            InputStream processInputStream = process.getInputStream();
            if (LOGGER.isDebugEnabled()) {
                printProcessOutput(processInputStream);
            } else {
                Util.skipHalfOfStream(processInputStream);
            }
        }
    }

    public void resetInputStream(InputStream inputStream) {
        try {
            inputStream.reset();
        } catch(IOException e) {
            // An exception is expected when really long input streams are reset.
            LOGGER.debug(String.format("Ignoring an exception while attempting to reset an input stream %s", e.toString()));
        }
    }

    public void printProcessOutput(InputStream processInputStream) {
        try {
            BufferedReader processOutputStream = getProcessOutputStream(processInputStream);
            String processOutputLine;
            while (processOutputStream.ready() &&
                    (processOutputLine = processOutputStream.readLine()) != null) {
                System.out.println(processOutputLine);
            }
        } catch (IOException e) {
            LOGGER.error(String.format("Error while reading and printing process output %s", e.toString()));
        }
    }

    public BufferedReader getProcessOutputStream(InputStream processInputStream) {
        InputStreamReader processStreamReader = new InputStreamReader(processInputStream);
        return new BufferedReader(processStreamReader);
    }

}
