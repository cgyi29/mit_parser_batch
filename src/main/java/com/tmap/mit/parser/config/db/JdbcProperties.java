package com.tmap.mit.parser.config.db;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * jdbc properties
 */
@Getter
@Setter
public class JdbcProperties{
    private long connectionTimeout;
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}