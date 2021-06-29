package com.game.gameservermaster.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.cassandra.SessionFactory;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;


@Configuration
@EnableConfigurationProperties({CassandraProperties.class})
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Autowired
    private CassandraProperties cassandraProperties;

    @Override
    protected String getKeyspaceName() {
        return cassandraProperties.getKeyspaceName();
    }

    @Override
    protected String getContactPoints() {
        return String.join(",", cassandraProperties.getContactPoints());
    }

    @Override
    protected int getPort() {
        return cassandraProperties.getPort();
    }

    @Override
    protected String getLocalDataCenter() { return cassandraProperties.getLocalDatacenter(); }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.valueOf(cassandraProperties.getSchemaAction());
    }

    @Override
    protected Resource getDriverConfigurationResource() {
        return new ClassPathResource("aws_ks.conf");
    }

    @Bean
    @Override
    public CqlSessionFactoryBean cassandraSession() {
        CqlSessionFactoryBean cassandraSession = super.cassandraSession();
        cassandraSession.setUsername(cassandraProperties.getUsername());
        cassandraSession.setPassword(cassandraProperties.getPassword());
        return cassandraSession;
    }

    @Bean
    public CassandraOperations cassandraTemplate(SessionFactory sessionFactory, CassandraConverter converter) {
        return new CassandraTemplate(sessionFactory, converter);
    }

}
