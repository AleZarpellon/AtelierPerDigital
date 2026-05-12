package com.za.testexe.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RateSchemaMigration implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        if (!rateTableExists()) {
            return;
        }

        if (!columnExists("mese")) {
            jdbcTemplate.execute("ALTER TABLE rate ADD COLUMN mese TEXT");
        }

        if (!columnExists("attivo")) {
            jdbcTemplate.execute("ALTER TABLE rate ADD COLUMN attivo BOOLEAN NOT NULL DEFAULT 1");
        }

        jdbcTemplate.execute("UPDATE rate SET attivo = 1 WHERE attivo IS NULL");
    }

    private boolean rateTableExists() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM sqlite_master WHERE type = 'table' AND name = 'rate'",
                Integer.class
        );
        return count != null && count > 0;
    }

    private boolean columnExists(String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM pragma_table_info('rate') WHERE name = ?",
                Integer.class,
                columnName
        );
        return count != null && count > 0;
    }
}

