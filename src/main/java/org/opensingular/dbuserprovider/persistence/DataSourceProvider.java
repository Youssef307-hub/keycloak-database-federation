package org.opensingular.dbuserprovider.persistence;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@JBossLog
public class DataSourceProvider implements Closeable {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-YYYY HH:mm:ss");
    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    private HikariDataSource hikariDataSource;

    public DataSourceProvider() {
    }

    synchronized Optional<DataSource> getDataSource() {
        return Optional.ofNullable(hikariDataSource);
    }


    public void configure(String name) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("root");
        hikariConfig.setPoolName(StringUtils.capitalize("USER-PROVIDER-" + name + SIMPLE_DATE_FORMAT.format(new Date())));
        hikariConfig.setJdbcUrl("jdbc:postgresql://host.docker.internal:5432/testkc");
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setDriverClassName(org.postgresql.Driver.class.getName());
        HikariDataSource newDS = new HikariDataSource(hikariConfig);
        newDS.validate();
        HikariDataSource old = this.hikariDataSource;
        this.hikariDataSource = newDS;
        disposeOldDataSource(old);
    }

    private void disposeOldDataSource(HikariDataSource old) {
        executor.submit(() -> {
            try {
                if (old != null) {
                    old.close();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public void close() {
        executor.shutdownNow();
        if (hikariDataSource != null) {
            hikariDataSource.close();
        }
    }
}
