package com.tmap.mit.parser.constant;

/**
 * DB 설정 관련 정의 상수
 *
 * @author 한주희
 */
public class ConstantsDb {


    /**
     * DB 설정 객체 bean 등록시 사용하는 bean name
     **/
    public static final String COMMON_DB_CONFIG_NM = "commonHikariConfig";
    /**
     * 샤딩DB의 이름 구분자: _
     */
    public static final String DB_NAME_DELIMITER = "_";

    /**
     * property bean name 모음
     */
    public static class PropertiesNm {

        public static final String MAIN_PROPERTY = "mainJdbcProperties";
        public static final String MAIN_SLAVE_PROPERTY = "mainSlaveJdbcProperties";

        public static final String BBS_PROPERTY = "bbsJdbcProperties";
        public static final String BBS_SLAVE_PROPERTY = "bbsSlaveJdbcProperties";

        public static final String BBS_USER_PROPERTY = "bbsUserJdbcProperties";
        public static final String BBS_USER_SLAVE_PROPERTY = "bbsUserSlaveJdbcProperties";
    }

    /**
     * sql session bean name 모음
     */
    public static class SqlSessionNm {
        /**
         * 메인 DB Sql세션명
         **/
        public static final String MAIN_SQL_SESSION = "main-sqlSession";

        /**
         * 메인 slave DB Sql세션명
         **/
        public static final String MAIN_SLAVE_SQL_SESSION = "main-slave-sqlSession";

        /**
         * bbs DB Sql세션명(샤딩DB)
         **/
        public static final String BBS_SQL_SESSION = "bbs-sqlSession";

        /**
         * bbs slave DB Sql세션명(샤딩DB)
         **/
        public static final String BBS_SLAVE_SQL_SESSION = "bbs-slave-sqlSession";

        /**
         * bbs DB Sql세션명(샤딩DB)
         **/
        public static final String BBS_USER_SQL_SESSION = "bbs-user-sqlSession";

        /**
         * bbs slave DB Sql세션명(샤딩DB)
         **/
        public static final String BBS_USER_SLAVE_SQL_SESSION = "bbs-user-slave-sqlSession";
    }

    /**
     * sql factory bean name 모음
     */
    public static class SessionFactoryNm {
        /**
         * 메인 DB SessionFactory bean name
         **/
        public static final String MAIN_SESSION_FACTORY_NM = "mainSessionFactory";

        /**
         * 메인 slave DB SessionFactory bean name
         **/
        public static final String MAIN_SLAVE_SESSION_FACTORY_NM = "mainSlaveSessionFactory";
    }

    /**
     * datasource bean name 모음
     */
    public static class DataSourceNm {
        /**
         * 메인 DB Datasource bean name
         **/
        public static final String MAIN_DATA_SOURCE_NM = "mainDataSource";

        /**
         * 메인 slave DB Datasource bean name
         **/
        public static final String MAIN_SLAVE_DATA_SOURCE_NM = "mainSlaveDataSource";

        /**
         * bbs DB Datasource bean name
         **/
        public static final String BBS_DATA_SOURCE_NM = "bbsMainDataSource";

        /**
         * bbs slave DB Datasource bean name
         **/
        public static final String BBS_SLAVE_DATA_SOURCE_NM = "bbsSlaveDataSource";

        /**
         * bbs-user DB Datasource bean name
         **/
        public static final String BBS_USER_DATA_SOURCE_NM = "bbsUserMainDataSource";

        /**
         * bbs-user slave DB Datasource bean name
         **/
        public static final String BBS_USER_SLAVE_DATA_SOURCE_NM = "bbsUserSlaveDataSource";
    }

    public static class TransactionNm {
        /**
         * 메인 DB Transaction bean name
         **/
        public static final String MAIN_TRANSACTION_NM = "transactionOfMainSession";

        /**
         * bbs DB Transaction bean name
         **/
        public static final String BBS_TRANSACTION_NM = "transactionOfBbsSession";

        /**
         * bbs user DB Transaction bean name
         **/
        public static final String BBS_USER_TRANSACTION_NM = "transactionOfBbsUserSession";

        public static final String TRANSACTION_NM = "transactionManager";
    }

}
