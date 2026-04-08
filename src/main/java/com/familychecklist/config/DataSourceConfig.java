package com.familychecklist.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

/**
 * Builds a JDBC DataSource from Railway MySQL environment variables.
 * Strategy (in order):
 *   1. MYSQL_URL or DATABASE_URL  — full URL: mysql://user:pass@host:port/db
 *   2. Individual vars: MYSQLHOST, MYSQLPORT, MYSQLDATABASE, MYSQLUSER, MYSQLPASSWORD
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() throws Exception {
        // --- Strategy 1: full URL ---
        String rawUrl = env("MYSQL_URL", env("DATABASE_URL", null));
        if (rawUrl != null && !rawUrl.isBlank()) {
            return buildFromUrl(rawUrl);
        }

        // --- Strategy 2: individual Railway vars (auto-injected for linked services) ---
        String host = env("MYSQLHOST", env("MYSQL_HOST", null));
        String port = env("MYSQLPORT", env("MYSQL_PORT", "3306"));
        String db   = env("MYSQLDATABASE", env("MYSQL_DATABASE", null));
        String user = env("MYSQLUSER", env("MYSQL_USER", null));
        String pass = env("MYSQLPASSWORD", env("MYSQL_PASSWORD", null));

        if (host != null && db != null && user != null) {
            String jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                host, port, db
            );
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(jdbcUrl);
            ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
            ds.setUsername(user);
            if (pass != null) ds.setPassword(pass);
            return ds;
        }

        throw new IllegalStateException(
            "No MySQL connection info found. In Railway FamCheckL Variables, add: " +
            "MYSQL_URL = ${{ MySQL.MYSQL_URL }}"
        );
    }

    private DataSource buildFromUrl(String rawUrl) throws Exception {
        String normalized = rawUrl
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

    private static String env(String name, String defaultValue) {
        String val = System.getenv(name);
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }
}
