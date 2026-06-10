package com.cqq._3dobject.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

@Component
@Order(0)
public class DatabaseMigrationRunner implements CommandLineRunner {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigrationRunner(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        String databaseProduct;
        try (Connection connection = dataSource.getConnection()) {
            databaseProduct = connection.getMetaData().getDatabaseProductName();
            if (databaseProduct == null || !databaseProduct.toLowerCase().contains("mysql")) {
                return;
            }

            jdbcTemplate.execute("ALTER TABLE model_files MODIFY COLUMN file_path TEXT NOT NULL");
            addColumnIfMissing(connection, "scene_schemes", "description",
                    "ALTER TABLE scene_schemes ADD COLUMN description TEXT NULL");
            addColumnIfMissing(connection, "scene_schemes", "cover_image_url",
                    "ALTER TABLE scene_schemes ADD COLUMN cover_image_url VARCHAR(500) NULL");
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS scene_schemes (
                        scheme_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        uid BIGINT NOT NULL,
                        title VARCHAR(200) NULL,
                        description TEXT NULL,
                        cover_image_url VARCHAR(500) NULL,
                        room_display_name VARCHAR(100) NULL,
                        thumb VARCHAR(32) NULL,
                        item_count INT NOT NULL DEFAULT 0,
                        room_id VARCHAR(32) NOT NULL,
                        room_name VARCHAR(100) NULL,
                        shape VARCHAR(32) NULL,
                        area VARCHAR(20) NULL,
                        len_m DECIMAL(10,2) NULL,
                        wid_m DECIMAL(10,2) NULL,
                        height_m DECIMAL(10,2) NULL,
                        kind VARCHAR(20) NULL,
                        eave_m DECIMAL(10,2) NULL,
                        dim_line VARCHAR(500) NULL,
                        room_data JSON NULL,
                        placed_snapshot JSON NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_scene_schemes_uid FOREIGN KEY (uid) REFERENCES users(uid) ON DELETE CASCADE,
                        INDEX idx_scene_schemes_uid_created (uid, created_at),
                        INDEX idx_scene_schemes_room_id (room_id)
                    )
                    """);
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS scene_placements (
                        placement_uid VARCHAR(64) PRIMARY KEY,
                        scheme_id BIGINT NOT NULL,
                        mid BIGINT NOT NULL,
                        title VARCHAR(100) NULL,
                        icon VARCHAR(32) NULL,
                        model_url TEXT NULL,
                        norm_x DECIMAL(5,2) NOT NULL,
                        norm_y DECIMAL(5,2) NOT NULL,
                        rotation_y DECIMAL(10,4) NULL,
                        scale DECIMAL(10,4) NULL,
                        pos_y DECIMAL(10,4) NULL,
                        sort_order INT NOT NULL DEFAULT 0,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_scene_placements_scheme FOREIGN KEY (scheme_id) REFERENCES scene_schemes(scheme_id) ON DELETE CASCADE,
                        CONSTRAINT fk_scene_placements_model FOREIGN KEY (mid) REFERENCES models(mid) ON DELETE RESTRICT,
                        INDEX idx_scene_placements_scheme_sort (scheme_id, sort_order),
                        INDEX idx_scene_placements_mid (mid)
                    )
                    """);
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS scene_scheme_comments (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        scheme_id BIGINT NOT NULL,
                        uid BIGINT NOT NULL,
                        content TEXT NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_scene_scheme_comments_scheme FOREIGN KEY (scheme_id) REFERENCES scene_schemes(scheme_id) ON DELETE CASCADE,
                        CONSTRAINT fk_scene_scheme_comments_uid FOREIGN KEY (uid) REFERENCES users(uid) ON DELETE CASCADE,
                        INDEX idx_scene_scheme_comments_scheme_created (scheme_id, created_at),
                        INDEX idx_scene_scheme_comments_uid (uid)
                    )
                    """);
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS scene_scheme_social_actions (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        uid BIGINT NOT NULL,
                        scheme_id BIGINT NOT NULL,
                        action_type VARCHAR(20) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_scene_scheme_social_actions_uid FOREIGN KEY (uid) REFERENCES users(uid) ON DELETE CASCADE,
                        CONSTRAINT fk_scene_scheme_social_actions_scheme FOREIGN KEY (scheme_id) REFERENCES scene_schemes(scheme_id) ON DELETE CASCADE,
                        UNIQUE KEY uk_scene_scheme_social_actions_uid_scheme_type (uid, scheme_id, action_type),
                        INDEX idx_scene_scheme_social_actions_scheme_type (scheme_id, action_type)
                    )
                    """);
        }
    }

    private void addColumnIfMissing(Connection connection, String tableName, String columnName, String alterSql) throws Exception {
        try (ResultSet columns = connection.getMetaData().getColumns(connection.getCatalog(), null, tableName, columnName)) {
            if (!columns.next()) {
                jdbcTemplate.execute(alterSql);
            }
        }
    }
}
