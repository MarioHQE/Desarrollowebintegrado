package com.springboot.desarrolloweb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ServerConfig {

    @Value("${server.http.port:3600}")
    private int httpPort;

    @Value("${server.port:8443}")
    private int httpsPort;

    @Bean
    @Profile("dev")
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createStandardConnector());
        return tomcat;
    }

    private org.apache.catalina.connector.Connector createStandardConnector() {
        org.apache.catalina.connector.Connector connector = new org.apache.catalina.connector.Connector(
                "org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(httpPort);
        connector.setSecure(false);
        connector.setRedirectPort(httpsPort);
        return connector;
    }
}