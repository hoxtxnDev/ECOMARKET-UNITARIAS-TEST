package com.horacio.ecomarket.usuarios.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DatabaseSchemaFixer")
class DatabaseSchemaFixerTest {

    @Mock
    JdbcTemplate jdbc;
    @Mock
    DataSource dataSource;
    @Mock
    Connection connection;
    @Mock
    DatabaseMetaData meta;
    @Mock
    ResultSet rs;

    @InjectMocks
    DatabaseSchemaFixer fixer;

    private void setupMeta() throws Exception {
        when(jdbc.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(meta);
        when(meta.getColumns(null, null, "perfil_usuario", "password")).thenReturn(rs);
    }

    @Test
    @DisplayName("run() no lanza excepción cuando columna existe y se elimina")
    void columnaExisteSeElimina() throws Exception {
        setupMeta();
        when(rs.next()).thenReturn(true);
        doNothing().when(jdbc).execute(anyString());

        assertThatCode(() -> fixer.run()).doesNotThrowAnyException();
        verify(jdbc).execute("ALTER TABLE perfil_usuario DROP COLUMN password");
    }

    @Test
    @DisplayName("run() absorbe excepción si getConnection() falla")
    void excepcionAbsorbidaEnGetConnection() throws Exception {
        when(jdbc.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenThrow(new java.sql.SQLException("Connection timeout"));
        assertThatCode(() -> fixer.run()).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("run() no lanza excepción cuando columna no existe")
    void columnaNoExiste() throws Exception {
        setupMeta();
        when(rs.next()).thenReturn(false);

        assertThatCode(() -> fixer.run()).doesNotThrowAnyException();
        verify(jdbc, never()).execute(anyString());
    }

    @Test
    @DisplayName("run() absorbe excepción si getDataSource() falla")
    void excepcionAbsorbida() {
        when(jdbc.getDataSource()).thenThrow(new RuntimeException("Sin datasource"));

        assertThatCode(() -> fixer.run()).doesNotThrowAnyException();
    }
    
}