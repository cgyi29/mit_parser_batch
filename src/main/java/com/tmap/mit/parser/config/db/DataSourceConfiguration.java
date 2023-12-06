package com.tmap.mit.parser.config.db;

import com.tmap.mit.parser.constant.DataResourceName;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * hikari cp를 사용한 Datasource setting
 */
@Configuration
public class DataSourceConfiguration {
    @Primary
    @Bean(name = DataResourceName.DataSourceName.MASTER)
    public HikariDataSource masterDataSource(@Qualifier(value = DataResourceName.COMMON_DB_CONFIG_NM) HikariConfig hikariConfig,
                                           @Qualifier(value = DataResourceName.PropertiesName.MASTER) JdbcProperties jdbcProperties) {
        hikariConfig.setConnectionTimeout(jdbcProperties.getConnectionTimeout());
        hikariConfig.setJdbcUrl(jdbcProperties.getUrl());
        hikariConfig.setUsername(jdbcProperties.getUsername());
        hikariConfig.setPassword(jdbcProperties.getPassword());
        hikariConfig.setDriverClassName(jdbcProperties.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = DataResourceName.DataSourceName.SLAVE)
    public HikariDataSource slaveDataSource(@Qualifier(value = DataResourceName.COMMON_DB_CONFIG_NM) HikariConfig hikariConfig,
                                            @Qualifier(value = DataResourceName.PropertiesName.SLAVE) JdbcProperties jdbcProperties) {
        hikariConfig.setConnectionTimeout(jdbcProperties.getConnectionTimeout());
        hikariConfig.setJdbcUrl(jdbcProperties.getUrl());
        hikariConfig.setUsername(jdbcProperties.getUsername());
        hikariConfig.setPassword(jdbcProperties.getPassword());
        hikariConfig.setDriverClassName(jdbcProperties.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }

}
