package com.tmap.mit.parser.constant;

/**
 * DB 설정 관련 constant
 */
public class DataResourceName {
    public static final String COMMON_DB_CONFIG_NM = "commonHikariConfig";

    public static class PropertiesName {
        public static final String MASTER = "masterJdbcProperties";
        public static final String SLAVE = "slaveJdbcProperties";
    }

    public static class DataSourceName {
        public static final String MASTER = "masterDataSource";
        public static final String SLAVE = "slaveDataSource";

    }

}
