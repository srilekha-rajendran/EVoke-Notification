package com.amzur

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class NotificationServer {

    public static final Logger logger = LoggerFactory.getLogger(NotificationServer.class);

    public static void main(String[] args) {
        configureLogback();


    }
    private static void configureLogback() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        InputStream configStream = new FileInputStream('conf/logback.xml');
        configurator.setContext(loggerContext);
        configurator.doConfigure(configStream); // loads logback file
        configStream.close();
        logger.debug ("Configured logback")
    }
}
