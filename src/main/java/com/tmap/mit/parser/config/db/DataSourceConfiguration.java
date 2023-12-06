package com.biz.config.db;

import com.biz.config.db.common.DbConfigValidation;
import com.biz.config.db.properties.JdbcProperties;
import com.biz.constants.ConstantsDb;
import com.biz.constants.ConstantsDb.PropertiesNm;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.context.support.GenericWebApplicationContext;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.biz.constants.ConstantsDb.COMMON_DB_CONFIG_NM;

/**
 * hikari cp를 사용한 Datasource 설정
 *
 * @author 한주희
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class DataSourceConfiguration {

    private final GenericWebApplicationContext context;
    private final DbConfigValidation dbConfigValidation;

    private final String mainSqlMapperPath = "classpath:/sqlmapper/main/**/*.xml";
    private final String bbsSqlMapperPath = "classpath:/sqlmapper/bbs/**/*.xml";
    private final String userSqlMapperPath = "classpath:/sqlmapper/user/**/*.xml";

    /**
     * 메인 datasource - master
     */
    @Primary
    @Bean(name = ConstantsDb.DataSourceNm.MAIN_DATA_SOURCE_NM)
    public HikariDataSource mainDataSource(@Qualifier(value = COMMON_DB_CONFIG_NM) HikariConfig hikariConfig,
                                           @Qualifier(value = PropertiesNm.MAIN_PROPERTY) JdbcProperties.MainProperty mainProperty) {
        hikariConfig.setJdbcUrl(mainProperty.getUrl());
        hikariConfig.setUsername(mainProperty.getUsername());
        hikariConfig.setPassword(mainProperty.getPassword());
        hikariConfig.setDriverClassName(mainProperty.getDriverClassName());

        return new HikariDataSource(hikariConfig);
    }

    /**
     * 메인 datasource - slave
     */
    @Bean(name = ConstantsDb.DataSourceNm.MAIN_SLAVE_DATA_SOURCE_NM)
    public HikariDataSource mainSlaveDataSource(@Qualifier(value = COMMON_DB_CONFIG_NM) HikariConfig hikariConfig,
                                                @Qualifier(value = PropertiesNm.MAIN_SLAVE_PROPERTY) JdbcProperties.MainProperty mainSlaveProperty) {
        hikariConfig.setJdbcUrl(mainSlaveProperty.getUrl());
        hikariConfig.setUsername(mainSlaveProperty.getUsername());
        hikariConfig.setPassword(mainSlaveProperty.getPassword());
        hikariConfig.setDriverClassName(mainSlaveProperty.getDriverClassName());

        return new HikariDataSource(hikariConfig);
    }


    /**
     * 메인 sessionFactory-master
     */
    @Bean(name = ConstantsDb.SessionFactoryNm.MAIN_SESSION_FACTORY_NM)
    @DependsOn(value = ConstantsDb.DataSourceNm.MAIN_DATA_SOURCE_NM)
    public SqlSessionFactory mainSessionFactory(@Qualifier(value = ConstantsDb.DataSourceNm.MAIN_DATA_SOURCE_NM) DataSource dataSource) throws Exception {
        return this.getSqlSessionFactory(dataSource, mainSqlMapperPath);
    }

    /**
     * 메인 sessionFactory-slave
     */
    @Bean(name = ConstantsDb.SessionFactoryNm.MAIN_SLAVE_SESSION_FACTORY_NM)
    @DependsOn(value = ConstantsDb.DataSourceNm.MAIN_SLAVE_DATA_SOURCE_NM)
    public SqlSessionFactory mainSlaveSessionFactory(@Qualifier(value = ConstantsDb.DataSourceNm.MAIN_SLAVE_DATA_SOURCE_NM) DataSource dataSource) throws Exception {
        return this.getSqlSessionFactory(dataSource, mainSqlMapperPath);
    }

    /**
     * 메인 sqlSessin-master
     */
    @Bean(name = ConstantsDb.SqlSessionNm.MAIN_SQL_SESSION)
    @DependsOn(value = ConstantsDb.SessionFactoryNm.MAIN_SESSION_FACTORY_NM)
    public SqlSessionTemplate mainSessionTemplate(@Qualifier(value = ConstantsDb.SessionFactoryNm.MAIN_SESSION_FACTORY_NM) SqlSessionFactory sqlSessionFactory) {
        return this.getSessionTemplate(sqlSessionFactory);
    }

    /**
     * 메인 sqlSessin-slave
     */
    @Bean(name = ConstantsDb.SqlSessionNm.MAIN_SLAVE_SQL_SESSION)
    @DependsOn(value = ConstantsDb.SessionFactoryNm.MAIN_SLAVE_SESSION_FACTORY_NM)
    public SqlSessionTemplate mainSlaveSessionTemplate(@Qualifier(value = ConstantsDb.SessionFactoryNm.MAIN_SLAVE_SESSION_FACTORY_NM) SqlSessionFactory sqlSessionFactory) {
        return this.getSessionTemplate(sqlSessionFactory);
    }

    /**
     * 게시판 샤딩 - master
     * sqlSessionFactory, sqlSession, transaction 빈 등록
     */
    @Bean(name = "bbsMasterShardMap")
    public Map<Integer, SqlSessionTemplate> bbsShardDBSessionMaster(@Qualifier(value = COMMON_DB_CONFIG_NM) HikariConfig hikariConfig,
                                                                    @Qualifier(value = PropertiesNm.BBS_PROPERTY) JdbcProperties.ShardJdbcProperty bbsJdbcProperty) throws Exception {

        //샤드 물리 DB 정보 가져옴
        List<JdbcProperties.ShardMachine> shardMachineList = bbsJdbcProperty.getShard();
        dbConfigValidation.bbsDbCountCheck(shardMachineList.size()); // validation check

        Map<Integer, SqlSessionTemplate> sqlSessionTemplateMap = new HashMap<>();

        int machineIdx = 0;
        for (JdbcProperties.ShardMachine shardMachine : shardMachineList) {

            //물리 디비 갯수만큼 datasource 생성
            HikariDataSource dataSource = this.makeShardDataSource(hikariConfig, bbsJdbcProperty, shardMachine);

            //dataSource 빈 등록
            context.registerBean(BbsShardDB.BBS_MASTER.getDataSourceName(machineIdx), DataSource.class, () -> dataSource);
            //transaction 빈 등록
            context.registerBean(BbsShardDB.BBS_MASTER.getTransactionName(machineIdx), PlatformTransactionManager.class, () -> dataSourceTxManager(dataSource));

            SqlSessionTemplate sessionTemplate = this.getSessionTemplate(this.getSqlSessionFactory(dataSource, bbsSqlMapperPath));

            //각 물리 DB에 속해 있는 논리디비 샤드 번호 가져옴
            String[] shardDbs = shardMachine.getShardNumber().split(",");

            //샤딩디비번호별 SqlSessionTemplate Map
            for (String shardNumber : shardDbs) {
                sqlSessionTemplateMap.put(Integer.valueOf(shardNumber), sessionTemplate);
            }

            machineIdx++;
        }

        return sqlSessionTemplateMap;
    }

    /**
     * 게시판 샤딩 - slave
     * sqlSessionFactory, sqlSession, transaction 빈 등록
     */
    @Bean(name = "bbsSlaveShardMap")
    public Map<Integer, SqlSessionTemplate> bbsShardDBSessionSlave(@Qualifier(value = COMMON_DB_CONFIG_NM) HikariConfig hikariConfig,
                                                                   @Qualifier(value = PropertiesNm.BBS_SLAVE_PROPERTY) JdbcProperties.ShardJdbcProperty bbsJdbcProperty) throws Exception {

        //샤드 물리 DB 정보 가져옴
        List<JdbcProperties.ShardMachine> shardMachineList = bbsJdbcProperty.getShard();
        dbConfigValidation.bbsDbCountCheck(shardMachineList.size()); // validation check

        Map<Integer, SqlSessionTemplate> sqlSessionTemplateMap = new HashMap<>();

        int machineIdx = 0;
        for (JdbcProperties.ShardMachine shardMachine : shardMachineList) {

            //물리 디비 갯수만큼 datasource 생성
            HikariDataSource dataSource = this.makeShardDataSource(hikariConfig, bbsJdbcProperty, shardMachine);

            //dataSource 빈 등록
            context.registerBean(BbsShardDB.BBS_SLAVE.getDataSourceName(machineIdx), DataSource.class, () -> dataSource);

            SqlSessionTemplate sessionTemplate = this.getSessionTemplate(this.getSqlSessionFactory(dataSource, bbsSqlMapperPath));

            //각 물리 DB에 속해 있는 논리디비 샤드 번호 가져옴
            String[] shardDbs = shardMachine.getShardNumber().split(",");

            //샤딩디비번호별 SqlSessionTemplate Map
            for (String shardNumber : shardDbs) {
                sqlSessionTemplateMap.put(Integer.valueOf(shardNumber), sessionTemplate);
            }

            machineIdx++;
        }

        return sqlSessionTemplateMap;
    }

    /**
     * 게시판 사용자 샤딩 - master
     * sqlSessionFactory, sqlSession, transaction 빈 등록
     */
    @Bean(name = "bbsUserMasterShardMap")
    public Map<Integer, SqlSessionTemplate> bbsUserShardDBSessionMaster(@Qualifier(value = COMMON_DB_CONFIG_NM) HikariConfig hikariConfig,
                                                                        @Qualifier(value = PropertiesNm.BBS_USER_PROPERTY) JdbcProperties.ShardJdbcProperty bbsUserJdbcProperty) throws Exception {

        //샤드 물리 DB 정보 가져옴
        List<JdbcProperties.ShardMachine> shardMachineList = bbsUserJdbcProperty.getShard();
        dbConfigValidation.userDbCountCheck(shardMachineList.size()); // validation check

        Map<Integer, SqlSessionTemplate> sqlSessionTemplateMap = new HashMap<>();

        int machineIdx = 0;
        for (JdbcProperties.ShardMachine shardMachine : shardMachineList) {

            //물리 디비 갯수만큼 datasource 생성
            HikariDataSource dataSource = this.makeShardDataSource(hikariConfig, bbsUserJdbcProperty, shardMachine);

            //dataSource 빈 등록
            context.registerBean(BbsShardDB.BBS_UER_MASTER.getDataSourceName(machineIdx), DataSource.class, () -> dataSource);
            //transaction 빈 등록
            context.registerBean(BbsShardDB.BBS_UER_MASTER.getTransactionName(machineIdx), PlatformTransactionManager.class, () -> dataSourceTxManager(dataSource));

            SqlSessionTemplate sessionTemplate = this.getSessionTemplate(this.getSqlSessionFactory(dataSource, userSqlMapperPath));

            //각 물리 DB에 속해 있는 논리디비 샤드 번호 가져옴
            String[] shardDbs = shardMachine.getShardNumber().split(",");

            //샤딩디비번호별 SqlSessionTemplate Map
            for (String shardNumber : shardDbs) {
                sqlSessionTemplateMap.put(Integer.valueOf(shardNumber), sessionTemplate);
            }

            machineIdx++;
        }

        return sqlSessionTemplateMap;
    }

    /**
     * 게시판 사용자 샤딩 - slave
     * sqlSessionFactory, sqlSession, transaction 빈 등록
     */
    @Bean(name = "bbsUserSlaveShardMap")
    public Map<Integer, SqlSessionTemplate> bbsUserShardDBSessionSlave(@Qualifier(value = COMMON_DB_CONFIG_NM) HikariConfig hikariConfig,
                                                                       @Qualifier(value = PropertiesNm.BBS_USER_SLAVE_PROPERTY) JdbcProperties.ShardJdbcProperty bbsUserJdbcProperty) throws Exception {

        //샤드 물리 DB 정보 가져옴
        List<JdbcProperties.ShardMachine> shardMachineList = bbsUserJdbcProperty.getShard();
        dbConfigValidation.userDbCountCheck(shardMachineList.size()); // validation check

        Map<Integer, SqlSessionTemplate> sqlSessionTemplateMap = new HashMap<>();

        int machineIdx = 0;
        for (JdbcProperties.ShardMachine shardMachine : shardMachineList) {

            //물리 디비 갯수만큼 datasource 생성
            HikariDataSource dataSource = this.makeShardDataSource(hikariConfig, bbsUserJdbcProperty, shardMachine);

            //dataSource 빈 등록
            context.registerBean(BbsShardDB.BBS_UER_MASTER.getDataSourceName(machineIdx), DataSource.class, () -> dataSource);

            SqlSessionTemplate sessionTemplate = this.getSessionTemplate(this.getSqlSessionFactory(dataSource, userSqlMapperPath));

            //각 물리 DB에 속해 있는 논리디비 샤드 번호 가져옴
            String[] shardDbs = shardMachine.getShardNumber().split(",");

            //샤딩디비번호별 SqlSessionTemplate Map
            for (String shardNumber : shardDbs) {
                sqlSessionTemplateMap.put(Integer.valueOf(shardNumber), sessionTemplate);
            }

            machineIdx++;
        }

        return sqlSessionTemplateMap;
    }

    /**
     * 물리 디비별 dataSource 생성
     */
    private HikariDataSource makeShardDataSource(HikariConfig hikariConfig, JdbcProperties.ShardJdbcProperty shardJdbcProperty, JdbcProperties.ShardMachine dbInfo) {

        hikariConfig.setUsername(shardJdbcProperty.getUsername());
        hikariConfig.setPassword(shardJdbcProperty.getPassword());
        hikariConfig.setDriverClassName(shardJdbcProperty.getDriverClassName());

        hikariConfig.setJdbcUrl(dbInfo.getUrl());

        return new HikariDataSource(hikariConfig);
    }

    /**
     * 전달 받은 Datasource로 SqlSessionFactory 객체 전달
     */
    private SqlSessionFactory getSqlSessionFactory(DataSource dataSource, String mapperPath) throws Exception {

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(mapperPath));

        //select 필드명 카멜케이스로 자동 전환해줌
        Objects.requireNonNull(sqlSessionFactoryBean.getObject()).getConfiguration().setMapUnderscoreToCamelCase(true);

        return sqlSessionFactoryBean.getObject();
    }

    /**
     * 전달 받은 sqlseeionFactory로 session template 전달
     */
    private SqlSessionTemplate getSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * transaction manager
     */
    private PlatformTransactionManager dataSourceTxManager(DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        dataSourceTransactionManager.setNestedTransactionAllowed(true);

        return dataSourceTransactionManager;
    }
}
