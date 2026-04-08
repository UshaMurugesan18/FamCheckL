package com.familychecklist.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

/**
 * Reads Railway's MYSQL_URL (format: mysql://user:pass@host:port/db)
 * and builds a proper JDBC DataSource, bypassing unresolved placeholder issues.
 */
@Configuration
public class DataSourceConfig {

    @Value("${MYSQL_URL:}")
    private String mysqlUrl;

    @Bean
    @Primary
    public DataSource dataSource() throws Exception {
        if (mysqlUrl == null || mysqlUrl.isBlank() || mysqlUrl.startsWith("${")) {
            throw new IllegalStateException(
                "MYSQL_URL environment variable is not set. " +
                "In Railway FamCheckL Variables, add: MYSQL_URL = ${{ MySQL.MYSQL_URL }}"
            );
        }

        // Normalize to http:// so java.net.URI can parse it
        String normalized = mysqlUrl
                .replaceFirst("^mysql2://", "http://")
                .replaceFirst("^mysql://", "http://");

        URI uri = new URI(normalized);

        String host = uri.getHost();
        int    port = uri.getPort() > 0 ? uri.getPort() : 3306;
        String db   = uri.getPath().replaceFirst("^/", "");

        String user = null, pass = null;
        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            int colonIdx = userInfo.indexOf(':');
            if (colonIdx >= 0) {
                user = userInfo.substring(0, colonIdx);
                pass = userInfo.substring(colonIdx + 1);
            } else {
                user = userInfo;
            }
        }

        String jdbcUrl = String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            host, port, db
        );

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(jdbcUrl);
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        if (user != null) ds.setUsername(user);
        if (pass != null) ds.setPassword(pass);

        return ds;
    }
}
