package com.example.vehicle_backend.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class TimescaleHypertableCreator {

    private final DataSource dataSource;

    public TimescaleHypertableCreator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void createHypertables() {
        String[] tables = {"vehicle_health_status", "vehicle_telemetry"};

        try (Connection conn = dataSource.getConnection()) {
            for (String table : tables) {
                if (!isHypertable(conn, table)) {
                    String sql = String.format(
                            "SELECT create_hypertable('%s', 'timestamp', migrate_data => true);",
                            table);
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql);
                        System.out.println("Hypertable created for table: " + table);
                    }
                } else {
                    System.out.println("Hypertable already exists for table: " + table);
                }
            }
        } catch (SQLException e) {
            System.err.println("Could not create hypertable: " + e.getMessage());
        }
    }

    private boolean isHypertable(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM timescaledb_information.hypertables WHERE hypertable_name = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tableName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
                return false;
            }
        }
    }
}