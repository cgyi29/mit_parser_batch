package com.tmap.mit.parser.config.db.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * jdbc properties
 */
@Getter
@Setter
public class JdbcProperties{
    private String driverClassName;
    private String url;
    private String username;
    private String password;
}
