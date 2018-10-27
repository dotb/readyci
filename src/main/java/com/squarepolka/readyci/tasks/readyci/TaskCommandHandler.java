package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class TaskCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCommandHandler.class);

    public InputStream executeCommand(String[] command, String workingDirectory) {
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
            processInputStream.mark(5120);
            handleProcessOutput(process);
            checkProcessSuccess(process);
            resetInputStream(processInputStream);
            return processInputStream;
        } catch (Exception e) {
            TaskExecuteException taskExecuteException = new TaskExecuteException(String.format("Exception %s. Tried to run command %s", e.toString(), arrayToString(command)));
            taskExecuteException.setStackTrace(e.getStackTrace());
            throw taskExecuteException;
        }
    }

    private String arrayToString(String[] stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringArray) {
            stringBuilder.append(string);
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    private void configureProxyServer(ProcessBuilder processBuilder, ReadyCIConfiguration configuration) {
        String proxyConfString = getProxyConfString(configuration);
        if (!proxyConfString.isEmpty()) {
            Map<String, String> environment = processBuilder.environment();
            environment.put("http_proxy", proxyConfString);
            environment.put("https_proxy", proxyConfString);
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

    private void checkProcessSuccess(Process process) {
        int exitValue = process.exitValue();
        if (exitValue != 0) {
            InputStream processInputStream = process.getInputStream();
            resetInputStream(processInputStream);
            printProcessOutput(processInputStream);
            throw new TaskExecuteException(String.format("Exited with %d", exitValue));
        }
    }

    private void resetInputStream(InputStream inputStream) {
        try {
            inputStream.reset();
        } catch(IOException e) {
            // An exception is expected when really long input streams are reset.
            LOGGER.debug(String.format("Ignoring an exception while attempting to reset an input stream %s", e.toString()));
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









    private String getProxyConfString(ReadyCIConfiguration configuration) {
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

}
