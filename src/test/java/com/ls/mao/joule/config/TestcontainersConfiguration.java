package com.ls.mao.joule.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestcontainersConfiguration {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:16");
    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:latest");

    @Bean
    public PostgreSQLContainer<?> postgresContainer(Environment env) {
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE)
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");
        postgres.start();
        System.setProperty("DB_PORT", postgres.getMappedPort(5432).toString());
        return postgres;
    }

    @Bean
    @DependsOn("postgresContainer")
    public GenericContainer<?> redisContainer() {
        GenericContainer<?> redis = new GenericContainer<>(REDIS_IMAGE).withExposedPorts(6379);
        redis.start();
        System.setProperty("REDIS_PORT", redis.getFirstMappedPort().toString());
        return redis;
    }
}
