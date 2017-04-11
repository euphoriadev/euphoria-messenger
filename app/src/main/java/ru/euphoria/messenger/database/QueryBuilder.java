package ru.euphoria.messenger.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Builder for raw SQL query.
 */
public class QueryBuilder {
    /**
     * SQL Command buffer
     */
    private StringBuilder buffer;

    // internal use only
    private QueryBuilder() {
        this.buffer = new StringBuilder();

    }

    /**
     * Creates a new Query Builder with empty sql-command
     */
    public static QueryBuilder query() {
        return new QueryBuilder();
    }

    /**
     * Select data by specified column
     *
     * @param column a list of which columns to select
     */
    public QueryBuilder select(String column) {
        this.buffer.append("SELECT ")
                .append(column)
                .append(" ");
        return this;
    }

    /**
     * Sets table name to query.
     *
     * @param table the table name
     */
    public QueryBuilder from(String table) {
        this.buffer.append("FROM ")
                .append(table)
                .append(" ");
        return this;
    }

    /**
     * A WHERE clause specifies that a SQL statement should
     * only affect rows that meet specified criteria.
     *
     * @param clause the where clause
     */
    public QueryBuilder where(String clause) {
        this.buffer.append("WHERE ")
                .append(clause)
                .append(" ");
        return this;
    }

    public QueryBuilder leftJoin(String table) {
        this.buffer.append("LEFT JOIN ")
                .append(table)
                .append(" ");
        return this;
    }

    public QueryBuilder on(String where) {
        this.buffer.append("ON ")
                .append(where)
                .append(" ");
        return this;
    }

    /**
     * A logic operator AND
     */
    public QueryBuilder and() {
        this.buffer.append("AND ");
        return this;
    }

    /**
     * A Logic operator OR
     */
    public QueryBuilder or() {
        this.buffer.append("OR ");
        return this;
    }

    /**
     * Creates new cursor by this query
     */
    public Cursor asCursor(SQLiteDatabase db) {
        return db.rawQuery(toString(), null);
    }

    @Override
    public String toString() {
        return buffer.toString().trim();
    }
}
