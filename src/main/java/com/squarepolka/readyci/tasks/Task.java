package com.squarepolka.readyci.tasks;

import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.configuration.TaskConfiguration;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.taskrunner.TaskRunner;
import com.squarepolka.readyci.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.util.Map;

public abstract class Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

    public TaskRunner taskRunner;

    public void configure(TaskConfiguration taskConfiguration) {

    }

    public boolean shouldStopOnFailure() {
        return true;
    }

    protected InputStream executeCommand(String command) {
        return executeCommand(new String[] {command});
    }

    protected InputStream executeCommand(String command, String workingDirectory) {
        return executeCommand(new String[] {command}, workingDirectory);
    }

    protected InputStream executeCommand(String[] command) {
        return executeCommand(command, "/tmp/");
    }

    protected InputStream executeCommand(String[] command, String workingDirectory) {
        LOGGER.debug(String.format("Executing command: %s", arrayToString(command)));
        try {
            File workingDirectoryFile = new File(workingDirectory);
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            ReadyCIConfiguration configuration = ReadyCIConfiguration.instance();
            processBuilder.directory(workingDirectoryFile);
            processBuilder.redirectErrorStream(true);
            configureProxyServer(processBuilder, configuration);
            Process process = processBuilder.start();
            InputStream processInputStream = process.getInputStream();
            int sizeOfFiveMeg = 5000000;
            processInputStream.mark(sizeOfFiveMeg);
            handleProcessOutput(process);
            checkProcessSuccess(process);
            resetInputStream(processInputStream);
            return processInputStream;
        } catch (Exception e) {
            TaskExecuteException taskExecuteException = new TaskExecuteException(String.format("Exception while executing task %s: %s. Tried to run %s", taskIdentifier(), e.toString(), arrayToString(command)));
            taskExecuteException.setStackTrace(e.getStackTrace());
            throw taskExecuteException;
        }
    }

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
    private void handleProcessOutput(Process process) throws IOException {
        while (process.isAlive()) {
            InputStream processInputStream = process.getInputStream();
            if (LOGGER.isDebugEnabled()) {
                printProcessOutput(processInputStream);
            } else {
                Util.skipHalfOfStream(processInputStream);
            }
        }
    }

    private void printProcessOutput(InputStream processInputStream) {
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

    private BufferedReader getProcessOutputStream(InputStream processInputStream) {
        InputStreamReader processStreamReader = new InputStreamReader(processInputStream);
        return new BufferedReader(processStreamReader);
    }

    protected void checkProcessSuccess(Process process) {
        int exitValue = process.exitValue();
        if (exitValue != 0) {
            InputStream processInputStream = process.getInputStream();
            resetInputStream(processInputStream);
            printProcessOutput(processInputStream);
            throw new TaskExecuteException(String.format("Exited with %d", exitValue));
        }
    }

    protected String arrayToString(String[] stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringArray) {
            stringBuilder.append(string);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    protected void resetInputStream(InputStream inputStream) {
        try {
            inputStream.reset();
        } catch(IOException e) {
            // An exception is expected when really long input streams are reset.
            LOGGER.debug(String.format("Ignoring an exception while attempting to reset an input stream %s", e.toString()));
        }
    }

    protected void configureProxyServer(ProcessBuilder processBuilder, ReadyCIConfiguration configuration) {
        String proxyConfString = getProxyConfString(configuration);
        if (!proxyConfString.isEmpty()) {
            Map<String, String> environment = processBuilder.environment();
            environment.put("http_proxy", proxyConfString);
            environment.put("https_proxy", proxyConfString);
        }
    }

    protected String getProxyConfString(ReadyCIConfiguration configuration) {
        String proxyHost = configuration.proxyHost;
        String proxyPort = configuration.proxyPort;
        LOGGER.debug(String.format("Configuring proxy %s %s", proxyHost, proxyPort));
        try {
            String proxyUsername = URLEncoder.encode(configuration.proxyUsername, "UTF-8");
            String proxyPassword = URLEncoder.encode(configuration.proxyPassword, "UTF-8");

            if (!proxyHost.isEmpty() && !proxyPort.isEmpty() && !proxyUsername.isEmpty() && !proxyPassword.isEmpty()) {
                return String.format("http://%s:%s@%s:%s", proxyUsername, proxyPassword, proxyHost, proxyPort);
            } else if (!proxyHost.isEmpty() && !proxyPort.isEmpty()) {
                return String.format("http://%s:%s", proxyHost, proxyPort);
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(String.format("Error while configure the proxy server %s %s", proxyHost, e.toString()));
        }
        return "";

    }

    // Methods that must be implemented by subclasses
    public abstract String taskIdentifier();
    public abstract void performTask(BuildEnvironment buildEnvironment) throws Exception;

}
