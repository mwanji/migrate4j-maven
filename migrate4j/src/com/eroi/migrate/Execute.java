package com.eroi.migrate;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.eroi.migrate.generators.Generator;
import com.eroi.migrate.generators.GeneratorFactory;
import com.eroi.migrate.misc.Closer;
import com.eroi.migrate.misc.SchemaMigrationException;
import com.eroi.migrate.schema.Column;
import com.eroi.migrate.schema.ForeignKey;
import com.eroi.migrate.schema.Index;
import com.eroi.migrate.schema.Table;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Execute {
	private static Log log = LogFactory.getLog(Execute.class);
	public static boolean exists(Index index) {
		if (index == null) {
			throw new SchemaMigrationException("Invalid index object");
		}
		
		try {
			Connection connection = Configure.getConnection();
		
			Generator generator = GeneratorFactory.getGenerator(connection);
			
			return generator.exists(index);
			
		} catch (SQLException e) {
            log.error("Unable to check index " + index.getName() + " on table " + index.getTableName(), e);
			throw new SchemaMigrationException("Unable to check index " + index.getName() + " on table " + index.getTableName(), e);
		} 
	}
	
	public static boolean exists(Table table) {
		if (table == null) {
			log.debug("Invalid Table object located in Execute.exists(Table)");
			throw new SchemaMigrationException("Invalid table object");
		}
		
		try {
			Connection connection = Configure.getConnection();
		
			Generator generator = GeneratorFactory.getGenerator(connection);
			
			return generator.exists(table);
			
		} catch (SQLException e) {
			log.error("Unable to create table " + table.getTableName(), e);
			throw new SchemaMigrationException("Unable to check table " + table.getTableName(), e);
		} 
	}
	
	public static boolean exists(Column column, Table table) {
		if (table == null) {
			log.debug("Invalid Table object located in Execute.exists(Column,Table)");
			throw new SchemaMigrationException("Invalid table object");
		}
		
		if (column == null) {
			log.debug("Invalid Column object located in Execute.exists(Column,Table)");
			throw new SchemaMigrationException("Invalid column object");
		}
		
		try {
			Connection connection = Configure.getConnection();
		
			Generator generator = GeneratorFactory.getGenerator(connection);
			
			return generator.exists(column, table);
			
		} catch (SQLException e) {
			log.error("Unable to create table " + table.getTableName(), e);
			throw new SchemaMigrationException("Unable to check column " + column.getColumnName() + " on table " + table.getTableName(), e);
		} 
	}
	
	public static boolean exists(ForeignKey foreignKey) {
		if (foreignKey == null) {
			log.debug("Invalid Foreign Key object in Execute.exists(ForeignKey)");
			throw new SchemaMigrationException("Invalid Foreign Key object");
		}
		
		try {
			Connection connection = Configure.getConnection();
		
			Generator generator = GeneratorFactory.getGenerator(connection);
			
			return generator.exists(foreignKey);
			
		} catch (SQLException e) {
			log.error("Unable to check foreign key " + foreignKey.getName() + " on table " + foreignKey.getParentTable(), e);
			throw new SchemaMigrationException("Unable to check foreign key " + foreignKey.getName() + " on table " + foreignKey.getParentTable(), e);
		} 
	}
	
	public static void createTable(Table table){
		
		if (table == null) {
			log.debug("Invalid table object located in Execute.createTable(Table)");
			throw new SchemaMigrationException("Invalid table object");
		}
		
		if (exists(table)) {
			return;
		}
		
		try {
			Connection connection = Configure.getConnection();
		
			Generator generator = GeneratorFactory.getGenerator(connection);
			
			String query = generator.createTableStatement(table);
			
			executeStatement(connection, query);
			
		} catch (SQLException e) {
			log.error("Unable to create table " + table.getTableName(), e);
			throw new SchemaMigrationException("Unable to create table " + table.getTableName(), e);
		} 
	}
	
	public static void dropTable(Table table) {
		if (table == null) {
			throw new SchemaMigrationException("Invalid Table object");
		}
		
		if (!exists(table)) {
			return;
		}
		
		try {
			Connection connection = Configure.getConnection();
			
			Generator generator = GeneratorFactory.getGenerator(connection);
			
			String query = generator.dropTableStatement(table);
			
			executeStatement(connection, query);
		} catch (SQLException e) {
			log.error("Unable to drop table " + table.getTableName(), e);
			throw new SchemaMigrationException("Unable to drop table " + table.getTableName(), e);
		} 
	}
	
	public static void addColumn(Column column, Table table) {
		if (table == null || column == null) {
			log.error("Either Table name or the Column name is not provided !! Must provide a Table and Column name");
			throw new SchemaMigrationException("Must provide a Table and Column");
		}
		
		if (!exists(table)) {
			log.error("Table "+table.getTableName()+ " does not exsists !!!");
			throw new SchemaMigrationException("Table does not exist");
		}
		
		try {
			Connection connection = Configure.getConnection();
			
			Generator generator = GeneratorFactory.getGenerator(connection);
			
			String query = generator.addColumnStatement(column, table, null);
			
			executeStatement(connection, query);
		} catch (SQLException e) {
			log.error("Unable to alter table " + table.getTableName() + " and add column " + column.getColumnName(), e);
			throw new SchemaMigrationException("Unable to alter table " + table.getTableName() + " and add column " + column.getColumnName(), e);
		}
	}
	
	public static void dropColumn(Column column, Table table) {
		if (table == null || column == null) {
			log.error("Either Table name or the Column name is not provided !! Must provide a Table and Column name");
			throw new SchemaMigrationException("Must provide a Table and Column");
		}
		
		if (!exists(table)) {
			log.error("Table "+table.getTableName()+ " does not exsists !!!");
			throw new SchemaMigrationException("Table does not exist");
		}
		
		if (!exists(column, table)) {
			return;
		}
		
		try {
			Connection connection = Configure.getConnection();
			
			Generator generator = GeneratorFactory.getGenerator(connection);
			
			String query = generator.dropColumnStatement(column, table);
			
			executeStatement(connection, query);
		} catch (SQLException e) {
			log.error("Unable to alter table " + table.getTableName() + " and drop column " + column.getColumnName(), e);
			throw new SchemaMigrationException("Unable to alter table " + table.getTableName() + " and drop column " + column.getColumnName(), e);
		}
		
	}
	
	public static void addIndex(Index index) {
		if (index == null) {
			log.debug("Invalid Index Object located in Execute.addIndex(Index)");
			throw new SchemaMigrationException("Invalid Index Object");
		}
		
		if (exists(index)) {
			return;
		}
		
		try {
			Connection connection = Configure.getConnection();
			
			Generator generator = GeneratorFactory.getGenerator(connection);
			
			String query = generator.addIndex(index);
			
			executeStatement(connection, query);
		} catch (SQLException e) {
			log.error("Unable to add index " + index.getName() + " on table " + index.getTableName(), e);
			throw new SchemaMigrationException("Unable to add index " + index.getName() + " on table " + index.getTableName(), e);
		}
	}
	
	public static void dropIndex(Index index) {
		if (index == null) {
			log.debug("Invalid Index Object located in Execute.dropIndex(Index)");
			throw new SchemaMigrationException("Invalid Index Object");
		}
		
		if (!exists(index)) {
			return;
		}
		
		try {
			Connection connection = Configure.getConnection();
			
			Generator generator = GeneratorFactory.getGenerator(connection);
			
			String query = generator.dropIndex(index);
			
			executeStatement(connection, query);
		} catch (SQLException e) {
			log.error("Unable to drop index " + index.getName() + " from table " + index.getTableName(), e);
			throw new SchemaMigrationException("Unable to drop index " + index.getName() + " from table " + index.getTableName(), e);
		}
	}
	
	public static void addForeignKey(ForeignKey foreignKey) {
		if (foreignKey == null) {
			log.debug("Invalid foreignKey Object located in Execute.addForeignKey(ForeignKey)");
			throw new SchemaMigrationException("Invalid foreignKey Object");
		}
		
		if (exists(foreignKey)) {
			return;
		}
		
		try {
			Connection connection = Configure.getConnection();
			
			Generator generator = GeneratorFactory.getGenerator(connection);
			
			String query = generator.addForeignKey(foreignKey);
			
			executeStatement(connection, query);
		} catch (SQLException e) {
			log.error("Unable to add foreign key " + foreignKey.getName() + " on table " + foreignKey.getParentTable(), e);
			throw new SchemaMigrationException("Unable to add foreign key " + foreignKey.getName() + " on table " + foreignKey.getParentTable(), e);
		}
	}
	
	public static void dropForeignKey(ForeignKey foreignKey) {
		if (foreignKey == null) {
			log.debug("Invalid foreignKey Object located in Execute.dropForeignKey(ForeignKey)");
			throw new SchemaMigrationException("Invalid foreign key Object");
		}
		
		if (!exists(foreignKey)) {
			return;
		}
		
		try {
			Connection connection = Configure.getConnection();
			
			Generator generator = GeneratorFactory.getGenerator(connection);
			
			String query = generator.dropForeignKey(foreignKey);
			
			executeStatement(connection, query);
		} catch (SQLException e) {
			log.error("Unable to drop foreign key " + foreignKey.getName() + " from table " + foreignKey.getParentTable(), e);
			throw new SchemaMigrationException("Unable to drop foreign key " + foreignKey.getName() + " from table " + foreignKey.getParentTable(), e);
		}
	}
	
	public static void statement(Connection connection, String query) throws SQLException {
		executeStatement(connection, query);
	}
	
	private static void executeStatement(Connection connection, String query) throws SQLException {
		
		Statement statement = null;
		
		try {
			statement = connection.createStatement();
			statement.executeUpdate(query);
		} finally {
			Closer.close(statement);
		}
		
	}
}
