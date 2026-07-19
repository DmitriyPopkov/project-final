package com.example.LessonRest.repository;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.Locale;

/**
 * Custom Naming Strategy to automatically prefix all table names with "tbl_".
 */
public class TblPhysicalNamingStrategy implements PhysicalNamingStrategy {

    private static final String TABLE_PREFIX = "tbl_";

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        if (name == null) {
            return null;
        }
        String prefixedName = TABLE_PREFIX + name.getText().toLowerCase(Locale.ROOT);
        return Identifier.toIdentifier(prefixedName, name.isQuoted());
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        if (name == null) {
            return null;
        }
        // Преобразуем camelCase в lowercase для совместимости с PostgreSQL
        return Identifier.toIdentifier(name.getText().toLowerCase(Locale.ROOT), name.isQuoted());
    }
    
    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
        return name;
    }
    
    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
        return name;
    }
    
    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
        return name;
    }
}