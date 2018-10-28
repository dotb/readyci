package com.squarepolka.readyci.tasks.readyci;

import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TaskProxyConfigurationTest {

    @Mock
    private ReadyCIConfiguration readyCIConfiguration;

    @InjectMocks
    private TaskProxyConfiguration subject;

    private ProcessBuilder processBuilder;

    @Before
    public void Setup() {
        processBuilder = new ProcessBuilder();
        Map environment = processBuilder.environment();
        // Ensure we don't pick up existing environment configuration by clearing out proxy settings.
        environment.remove("http_proxy");
        environment.remove("https_proxy");
    }

    @Test
    public void testFullConfigurationWithURLEncoding() {
        readyCIConfiguration.proxyHost = "proxyHost";
        readyCIConfiguration.proxyPort = "proxyPort";
        readyCIConfiguration.proxyUsername = "proxyUsername@!";
        readyCIConfiguration.proxyPassword = "proxyPassword!@";
        subject.configureProxyServer(processBuilder, readyCIConfiguration);
        String httpProxyLine = processBuilder.environment().get("http_proxy");
        String httpsProxyLine = processBuilder.environment().get("https_proxy");
        assertEquals("http proxy line is correct", "http://proxyUsername%40%21:proxyPassword%21%40@proxyHost:proxyPort", httpProxyLine);
        assertEquals("https proxy line is correct", "http://proxyUsername%40%21:proxyPassword%21%40@proxyHost:proxyPort", httpsProxyLine);
    }

    @Test
    public void testConfigurationHostAndPortOnly() {
        readyCIConfiguration.proxyHost = "proxyHost";
        readyCIConfiguration.proxyPort = "proxyPort";
        readyCIConfiguration.proxyUsername = "";
        readyCIConfiguration.proxyPassword = "";
        subject.configureProxyServer(processBuilder, readyCIConfiguration);
        String httpProxyLine = processBuilder.environment().get("http_proxy");
        String httpsProxyLine = processBuilder.environment().get("https_proxy");
        assertEquals("http proxy line is correct", "http://proxyHost:proxyPort", httpProxyLine);
        assertEquals("https proxy line is correct", "http://proxyHost:proxyPort", httpsProxyLine);
    }

    @Test
    public void testEmptyConfiguration() {
        readyCIConfiguration.proxyHost = "";
        readyCIConfiguration.proxyPort = "";
        readyCIConfiguration.proxyUsername = "";
        readyCIConfiguration.proxyPassword = "";
        subject.configureProxyServer(processBuilder, readyCIConfiguration);
        String httpProxyLine = processBuilder.environment().get("http_proxy");
        String httpsProxyLine = processBuilder.environment().get("https_proxy");
        assertEquals("http proxy line is correct", null, httpProxyLine);
        assertEquals("https proxy line is correct", null, httpsProxyLine);
    }

}
