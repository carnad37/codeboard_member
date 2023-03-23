package com.hhs.codeboard.member.conf.db;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
public class DatabaseConfig {
//    private static Maria
//#spring:
//#  datasource:
//#    driver-class-name: org.mariadb.jdbc.Driver
//#    url: jdbc:mariadb://localhost:33660/codeboard?serverTimezone=UTC
//#    username: root
////#    password: testdb#
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        conf = MariadbConnectionConfiguration.builder()
//                .host("example.skysql.net").port(5009)
//                .username("db_user").password("db_user_password")
//                .database("test").build();
//
//        // Instantiate a Connection Factory
//        connFactory = new MariadbConnectionFactory(conf);
//
//        // Instantiate a Database Client
//        client = DatabaseClient.create(connFactory);
//        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
//                .option(ConnectionFactoryOptions.DRIVER, "org.mariadb.jdbc.Driver")
//                .option(ConnectionFactoryOptions.HOST, host)
//                .option(ConnectionFactoryOptions.PORT, port)
//                .option(ConnectionFactoryOptions.DATABASE, "test")
//                .option(ConnectionFactoryOptions.USER, username)
//                .option(ConnectionFactoryOptions.PASSWORD, password)
//                .build());
//    }


}
