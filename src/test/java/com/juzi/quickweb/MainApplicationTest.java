package com.juzi.quickweb;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * @author codejuzi
 */
@Slf4j
@SpringBootTest
class MainApplicationTest {

    @Resource
    private DataSource dataSource;

    @Test
    void contextLoads() throws SQLException {
        //看一下默认数据源
        log.info("默认数据源 => {}", dataSource.getClass());
        //获得连接
        Connection connection = dataSource.getConnection();
        log.info("连接池 => {}", connection);

        DruidDataSource druidDataSource = (DruidDataSource) dataSource;
        log.info("druidDataSource 数据源最大连接数：{}", druidDataSource.getMaxActive());
        log.info("druidDataSource 数据源初始化连接数：{}", druidDataSource.getInitialSize());

        //关闭连接
        connection.close();
    }
}