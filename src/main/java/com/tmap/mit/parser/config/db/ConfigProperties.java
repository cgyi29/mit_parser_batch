package com.tmap.mit.parser.config.db;

import com.tmap.mit.parser.constant.DataResourceName;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

/**
 * db properties config
 */
@Configuration
@PropertySource(value = {"classpath:properties/jdbc-local.properties"})
public class ConfigProperties {
    @Bean(name = DataResourceName.COMMON_DB_CONFIG_NM)
    @Primary
    @ConfigurationProperties(prefix = "hikari")
    public HikariConfig commonHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = DataResourceName.PropertiesName.MASTER)
    @Primary
    @ConfigurationProperties(prefix = "master")
    public JdbcProperties masterJdbcProperties() {
        return new JdbcProperties();
    }

    @Bean(name = DataResourceName.PropertiesName.SLAVE)
    @ConfigurationProperties(prefix = "slave")
    public JdbcProperties slaveJdbcProperties() {
        return new JdbcProperties();
    }

}
