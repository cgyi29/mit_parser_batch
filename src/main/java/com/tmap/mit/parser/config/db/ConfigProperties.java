package com.biz.config.db;

import com.biz.config.db.properties.JdbcProperties;
import com.biz.constants.ConstantsDb;
import com.biz.constants.ConstantsDb.PropertiesNm;
import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

/**
 * db properties config
 *
 * @author 한주희
 */
@Configuration
@PropertySource(value = {"classpath:properties/jdbc.properties"})
public class ConfigProperties {

    // 공통 hikaricp 설정
    @Bean(name = ConstantsDb.COMMON_DB_CONFIG_NM)
    @Primary
    @ConfigurationProperties(prefix = "hikari")
    public HikariConfig commonHikariConfig() {
        return new HikariConfig();
    }

    /**
     * 메인 master jdbc property
     */
    @Bean(name = PropertiesNm.MAIN_PROPERTY)
    @Primary
    @ConfigurationProperties(prefix = "main")
    public JdbcProperties.MainProperty mainJdbcProperties() {
        return new JdbcProperties.MainProperty();
    }

    /**
     * 메인 slave jdbc property
     */
    @Bean(name = PropertiesNm.MAIN_SLAVE_PROPERTY)
    @ConfigurationProperties(prefix = "main-slave")
    public JdbcProperties.MainProperty mainSlaveJdbcProperties() {
        return new JdbcProperties.MainProperty();
    }

    /**
     * bbs master shard jdbc property
     */
    @Bean(name = PropertiesNm.BBS_PROPERTY)
    @ConfigurationProperties(prefix = "bbs")
    public JdbcProperties.ShardJdbcProperty bbsJdbcProperties() {
        return new JdbcProperties.ShardJdbcProperty();
    }

    /**
     * bbs slave shard jdbc property
     */
    @Bean(name = PropertiesNm.BBS_SLAVE_PROPERTY)
    @ConfigurationProperties(prefix = "bbs-slave")
    public JdbcProperties.ShardJdbcProperty bbsSlaveJdbcProperties() {
        return new JdbcProperties.ShardJdbcProperty();
    }

    /**
     * bbs-user master shard jdbc property
     */
    @Bean(name = PropertiesNm.BBS_USER_PROPERTY)
    @ConfigurationProperties(prefix = "bbs-user")
    public JdbcProperties.ShardJdbcProperty bbsUserJdbcProperties() {
        return new JdbcProperties.ShardJdbcProperty();
    }

    /**
     * bbs-user slave shard jdbc property
     */
    @Bean(name = PropertiesNm.BBS_USER_SLAVE_PROPERTY)
    @ConfigurationProperties(prefix = "bbs-user-slave")
    public JdbcProperties.ShardJdbcProperty bbsUserSlaveJdbcProperties() {
        return new JdbcProperties.ShardJdbcProperty();
    }
}
