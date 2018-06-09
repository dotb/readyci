package com.squarepolka.readyci;

import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ReadyCI {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadyCI.class);

    public static void main(String[] arguments) throws Exception {

        ReadyCIConfiguration readyCIConfiguration = ReadyCIConfiguration.instance();
        readyCIConfiguration.handleInputParameters(arguments);

        SpringApplication.run(ReadyCI.class, arguments);

    }

}
