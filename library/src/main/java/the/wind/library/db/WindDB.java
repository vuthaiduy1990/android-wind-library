package the.wind.library.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import the.wind.library.Windson;
import the.wind.library.utils.CWAndroidUtils;
import the.wind.library.utils.CWClazzUtils;
import the.wind.library.utils.CWStringUtils;

/**
 * Usage
 * <pre>
 *     WindDB.setTables(CWTestTable.class);
 *     WindDB.setMigrations(CWBuild.class);
 *     WindDB.init(context, db-name, db-version)
 *     WindDB.$.getWritableDatabase();
 *     WindDB.$.find()
 * </pre>
 * - After initializing database, you should call {@link WindDB#$#getWritableDatabase()}
 * to run migration if database version is changed
 * - You also should not init database in main thread b/c sometime the migration process may take long time.
 * So, call it in async task of possible
 *
 * @see SQLiteOpenHelper#getWritableDatabase()
 */
public class WindDB extends SQLiteOpenHelper {

    // singleton instance of windson
    @NonNull
    public static WindDB $;

    // SQLite database
    private SQLiteDatabase _sqLiteDatabase;

    // application context
    private final Context context;

    /**
     * Constructor
     *
     * @param context  android context
     * @param database database name
     * @param version  database version
     */
    private WindDB(Context context, String database, int version) {
        super(context.getApplicationContext(), database, null, version);
        this.context = context.getApplicationContext();
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public SQLiteDatabase getWritableDatabase() {
        if (this._sqLiteDatabase != null) {
            return this._sqLiteDatabase;
        }
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (this._sqLiteDatabase != null) {
            return this._sqLiteDatabase;
        }
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this._sqLiteDatabase = sqLiteDatabase;
        CWMigrationController.instance().doInitialization();
        this._sqLiteDatabase = null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        this._sqLiteDatabase = sqLiteDatabase;
        CWMigrationController.instance().doMigration(sqLiteDatabase, oldVersion, newVersion);
        this._sqLiteDatabase = null;
    }



    /* ---------------------- STATIC ------------------------- */

    /**
     * Init database
     *
     * @param context  android context
     * @param database database name
     * @param version  database version
     */
    public static void init(@NonNull Context context, @NonNull String database, int version) {
        $ = new WindDB(context, database, version);
    }

    /**
     * Add tables
     * setTables(CWTestTable.class)
     *
     * @param tableList list of tables
     */
    @SafeVarargs
    public static void setTables(Class<? extends CWTable>... tableList) {
        CWMigrationController.instance().setTables(tableList);
    }

    /**
     * Add builds.
     * setMigrations(GMBuild100.class)
     *
     * @param buildList list of builds
     */
    @SafeVarargs
    public static void setMigrations(Class<? extends CWBuild>... buildList) {
        CWMigrationController.instance().setMigrations(buildList);
    }

    /**
     * Get table name
     *
     * @param clazz table class
     * @return table name
     */
    public static String getTableName(Class<? extends CWTable> clazz) {
        Entity def = clazz.getAnnotation(Entity.class);
        String name = def != null ? def.name().trim() : "";
        name = name.isEmpty() ? clazz.getSimpleName() : name;
        return name;
    }

    /**
     * Get column's name (in db)
     *
     * @param columnField column field in model class
     * @return column's name
     */
    public static String getColumnName(Field columnField) {
        Column colDef = columnField.getAnnotation(Column.class);
        String name = colDef != null ? colDef.name().trim() : "";
        name = name.isEmpty() ? CWClazzUtils.toCamelCase(columnField) : name;
        return name;
    }

    /**
     * Get field for saving to database. Look fields in all supper parents too.
     * +By default, all field will be store to database except
     * fields are marked as CWColumnIgnore or are transient or final
     *
     * @param clazz class
     * @return list of field
     */
    public static Set<Field> getColumnFields(Class<?> clazz) {
        Set<Field> results = new LinkedHashSet<>();

        Class<?> parentClazz = clazz;
        while (parentClazz != null) {
            for (Field field : parentClazz.getDeclaredFields()) {
                // exclude field which is transient/static or marked as CWColumnIgnore
                Column colDef = field.getAnnotation(Column.class);
                if ((colDef != null && colDef.ignore())
                        || Modifier.isTransient(field.getModifiers())
                        || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                results.add(field);
            }
            // continue to look in supper class
            parentClazz = parentClazz.getSuperclass();
            if (parentClazz != null && parentClazz.equals(Object.class)) parentClazz = null;
        }
        return results;
    }

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /* ------------------- METHOD - DLL ---------------------- */
    /* ------------------- ------------ ---------------------- */

    /**
     * Create table by class
     *
     * @param clazz table class
     */
    public void createTable(Class<? extends CWTable> clazz) {
        SQLiteDatabase db = getWritableDatabase();
        List<String> indexingCols = new ArrayList<>();
        String tableName = getTableName(clazz);

        // Generate table structure script
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        builder.append("id").append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, ");
        for (Field field : getColumnFields(clazz)) {
            Class<?> type = field.getType();
            Column colDef = field.getAnnotation(Column.class);
            String colName = getColumnName(field);

            if (colName.equals(CWTable.HASH)) {
                builder.append(colName).append(" NVARCHAR NOT NULL UNIQUE, ");

            } else if (type.equals(String.class) || type.equals(Character.class) || type.equals(char.class)) {
                builder.append(colName).append(" NVARCHAR, ");

            } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
                builder.append(colName).append(" NVARCHAR, ");

            } else if (type.equals(double.class) || type.equals(Double.class)
                    || type.equals(float.class) || type.equals(Float.class)) {
                builder.append(colName).append(" REAL, ");

            } else if (type.equals(short.class) || type.equals(Short.class)
                    || type.equals(int.class) || type.equals(Integer.class)
                    || type.equals(long.class) || type.equals(Long.class)
                    || type.equals(Date.class)) {
                builder.append(colName).append(" INTEGER, ");

            } else if (type.equals(byte[].class) || type.equals(Byte[].class)) {
                builder.append(colName).append(" BLOB, ");

            } else {
                // other field type which SQLite does not support. Ex, array, collection, date ...
                // -> convert to json then save as long string -> text
                builder.append(colName).append(" TEXT, ");
            }

            // collect indexing columns
            if (colDef != null && colDef.indexing()) {
                indexingCols.add(colName);
            }
        }
        builder.deleteCharAt(builder.lastIndexOf(",")).append(");");
        db.execSQL(builder.toString());

        // create index script
        for (String colName : indexingCols) {
            String idxName = String.format("idx_%s_%s", tableName, colName);
            String sql = String.format("CREATE INDEX %1$s ON %2$s (%3$s);", idxName, tableName, colName);
            db.execSQL(sql);
        }
    }

    /**
     * Drop specific table
     *
     * @param clazz table class
     */
    public void dropTable(Class<? extends CWTable> clazz) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(String.format("DROP TABLE IF EXISTS %s;", getTableName(clazz)));
    }

    /**
     * Add column to table
     *
     * @param clazz   table class
     * @param colName column's name
     * @param type    column's type
     */
    public void addColumn(Class<? extends CWTable> clazz, String colName, String type) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = String.format("ALTER TABLE %s$1 ADD COLUMN %2$s %3$s;", getTableName(clazz), colName, type);
        db.execSQL(sql);
    }

    /**
     * Create index for given column of table
     *
     * @param clazz   table class
     * @param colName column name
     */
    public void createIndex(Class<? extends CWTable> clazz, String colName) {
        SQLiteDatabase db = getWritableDatabase();
        String tableName = getTableName(clazz);
        String idxName = String.format("idx_%s_%s", tableName, colName);
        String sql = String.format("CREATE INDEX %1$s ON %2$s (%3$s);", idxName, tableName, colName);
        db.execSQL(sql);
    }

    /* ------------------- METHOD - DQL ---------------------- */
    /* ------------------- ------------ ---------------------- */

    /**
     * Query database
     *
     * @param tableClazz model class
     * @param columns    selected columns
     * @param where      where statement. For example: "gender = ? and userName like ?"
     * @param args       where arguments
     * @param groupBy    group clause. For example: "gender"
     * @param having     having clause. For example: "length(category) > 10"
     * @param orderBy    order clause. For example: "userName DESC"
     * @param limit      Limits the number of rows. For example: "5"
     * @return list of entity model object
     */
    public <T extends CWTable> List<T> find(Class<T> tableClazz, String[] columns, String where, String[] args,
                                            String groupBy, String having, String orderBy, String limit) {

        List<T> result = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cur = db.query(getTableName(tableClazz),
                                  columns, where, args,
                                  groupBy, having, orderBy, limit);

            if (cur.moveToFirst()) {
                do {
                    // Parse cursor to model object
                    T obj = parseRowCursor(cur, tableClazz);
                    obj.setOffline();
                    result.add(obj);
                } while (cur.moveToNext());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // According to issue #27, no need to close the database connection.
        // https://stackoverflow.com/questions/14002022/android-sqlite-closed-exception
        // if (db != null) db.close();

        return result;
    }

    /**
     * Query database
     *
     * @param type    model class
     * @param columns selected columns
     * @param where   where statement. For example: "gender = ? and userName like ? and old IN (?,?,?)"
     * @param args    where arguments
     * @param orderBy order clause. For example: "userName DESC"
     * @param limit   Limits the number of rows. For example: "5"
     * @return list of entity model object
     */
    public <T extends CWTable> List<T> find(Class<T> type, String[] columns, String where, String[] args,
                                            String orderBy, String limit) {
        return find(type, columns, where, args, null, null, orderBy, limit);
    }

    /**
     * Query database
     *
     * @param type    model class
     * @param columns selected columns
     * @param where   where statement. For example: "gender = ? and userName like ? and old IN (?,?,?)"
     * @param args    where arguments
     * @return list of entity model object
     */
    public <T extends CWTable> List<T> find(Class<T> type, String[] columns, String where, String[] args) {
        return find(type, columns, where, args, null, null);
    }

    /**
     * Find all record
     *
     * @param type model class
     * @return list of records
     */
    public <T extends CWTable> List<T> findAll(Class<T> type) {
        return find(type, null, null, null);
    }

    /**
     * Find record by table and hash string
     *
     * @param type model class
     * @param hash unique ID of data
     * @param <T>  class
     * @return record
     */
    public <T extends CWTable> T findByHash(Class<T> type, String hash) {
        List<T> result = findByCol(type, CWTable.HASH, hash);
        return (result.isEmpty()) ? null : result.get(0);
    }

    /**
     * Find record by table and hash string
     *
     * @param type   model class
     * @param hashes unique ID of data
     * @param <T>    class
     * @return record
     */
    public <T extends CWTable> List<T> findByHash(Class<T> type, Collection<String> hashes) {
        return findByCol(type, CWTable.HASH, hashes);
    }

    /**
     * Find record by table and hash string
     *
     * @param type   model class
     * @param hashes unique ID of data
     * @param <T>    class
     * @return record
     */
    public <T extends CWTable> List<T> findByHash(Class<T> type, String... hashes) {
        return findByCol(type, CWTable.HASH, hashes);
    }

    /**
     * Find record by column
     *
     * @param type  model class
     * @param col   column's name
     * @param value search value
     * @param <T>   class
     * @return record
     */
    public <T extends CWTable> List<T> findByCol(Class<T> type, String col, String value) {
        return find(type, null, col + " = ?", new String[]{value});
    }

    /**
     * Find record by given column value
     *
     * @param type   model class
     * @param col    column's name
     * @param values search value
     * @param <T>    class
     * @return record
     */
    public <T extends CWTable> List<T> findByCol(Class<T> type, String col, Collection<String> values) {
        String[] args = values.toArray(new String[0]);
        return findByCol(type, col, args);
    }

    /**
     * Find record by given column value
     *
     * @param type   model class
     * @param col    column's name
     * @param values search value
     * @param <T>    class
     * @return record
     */
    public <T extends CWTable> List<T> findByCol(Class<T> type, String col, String... values) {
        String where = String.format("%s IN (%s)", col, CWStringUtils.join(",", "?", values.length));
        return find(type, null, where, values);
    }

    /**
     * Find record by state
     *
     * @param type  model class
     * @param state record state
     * @param <T>   class
     * @return list of records
     */
    public <T extends CWTable> List<T> findByState(Class<T> type, CWTable.SyncState state) {
        return find(type, null, "syncState = ?", new String[]{"\"" + state.name() + "\""});
    }

    /* ------------------- METHOD - DML ---------------------- */
    /* ------------------- ------------ ---------------------- */

    /**
     * Insert data to database
     *
     * @param entities model objects
     * @return id of lasted inserted row or -1 if error occurs
     */
    public final <T extends CWTable> long insert(Iterable<T> entities) {
        long result = -1;

        // use transaction to ensure the integrity of data
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            for (T entity : entities) {
                entity.setVersion(db.getVersion());
                Date date = new Date();
                entity.setCreatedDate(date);
                entity.setUpdateDate(date);
                if (entity.getHash() == null) {
                    entity.setHash(CWAndroidUtils.randomHash(context, entity.getClass().getName()));
                }

                // insert
                ContentValues values = toRowValue(entity);
                result = db.insert(getTableName(entity.getClass()), null, values);
                entity.setOffline(); // save successfully -> mark as offline
            }
            db.setTransactionSuccessful();

        } catch (Exception ex) {
            result = -1;
            ex.printStackTrace();
        }

        db.endTransaction();

        // According to issue #27, no need to close the database connection.
        // if (db != null) db.close();

        return result;
    }

    /**
     * Insert data to database
     *
     * @param entities model objects
     * @return id of lasted inserted row or -1 if error occurs
     */
    @SafeVarargs
    public final <T extends CWTable> long insert(T... entities) {
        if (entities == null || entities.length == 0) return 0;
        return insert(Arrays.asList(entities));
    }

    /**
     * Update table
     *
     * @param entities model objects
     * @return the number of row affected
     */
    public final <T extends CWTable> int update(Iterable<T> entities) {
        int result = 0;

        // use transaction to ensure the integrity of data
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            for (T entity : entities) {
                entity.setOffline();
                entity.setVersion(db.getVersion());
                Date date = new Date();
                entity.setUpdateDate(date);
                ContentValues values = toRowValue(entity);
                result += db.update(getTableName(entity.getClass()), values, CWTable.HASH + "=?", new String[]{entity.getHash()});
            }
            db.setTransactionSuccessful();

        } catch (Exception ex) {
            result = 0;
            ex.printStackTrace();
        }

        db.endTransaction();

        // According to issue #27, no need to close the database connection.
        // if (db != null) db.close();

        return result;
    }

    /**
     * Update table
     *
     * @param entities model objects
     * @return the number of row affected
     */
    @SafeVarargs
    public final <T extends CWTable> int update(T... entities) {
        if (entities == null || entities.length == 0) return 0;
        return update(Arrays.asList(entities));
    }

    /**
     * Insert record if not exist, otherwise update record
     *
     * @param entity table object
     * @param <T>    class which extend CWTable
     * @return value !=1 -> success else fail
     */
    public <T extends CWTable> boolean upsert(T entity) {
        if (findByHash(entity.getClass(), entity.getHash()) == null) {
            // insert
            return insert(entity) != -1;
        }

        // update
        return update(entity) > 0;
    }

    /**
     * Remove data from table
     *
     * @param entities model objects
     * @return the number of row affected
     */
    public final <T extends CWTable> int delete(Iterable<T> entities) {
        int result = 0;

        // use transaction to ensure the integrity of data
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            for (T entity : entities) {
                String hash = entity.getHash();
                if (hash == null) continue;
                entity.setDeleted();
                result += db.delete(getTableName(entity.getClass()), CWTable.HASH + "=?", new String[]{hash});
            }
            db.setTransactionSuccessful();

        } catch (Exception ex) {
            result = 0;
            ex.printStackTrace();
        }

        db.endTransaction();

        // According to issue #27, no need to close the database connection.
        // if (db != null) db.close();

        return result;
    }

    /**
     * Remove data from table
     *
     * @param entities model objects
     * @return the number of row affected
     */
    @SafeVarargs
    public final <T extends CWTable> int delete(T... entities) {
        if (entities == null || entities.length == 0) return 0;
        return delete(Arrays.asList(entities));
    }

    /**
     * Remove data from table
     *
     * @param type  model class
     * @param where where statement. For example: "gender = ? and userName like ? and old IN (?,?,?)"
     * @param args  where arguments
     * @return the number of row affected
     */
    public final <T extends CWTable> int delete(Class<T> type, String where, String[] args) {
        int result = 0;

        // use transaction to ensure the integrity of data
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            result += db.delete(getTableName(type), where, args);
            db.setTransactionSuccessful();

        } catch (Exception ex) {
            result = 0;
            ex.printStackTrace();
        }

        db.endTransaction();

        // According to issue #27, no need to close the database connection.
        // if (db != null) db.close();

        return result;
    }

    /**
     * Delete record by table and hash string
     *
     * @param type model class
     * @param hash hash string which identify a table
     * @param <T>  class
     * @return the number of row affected
     */
    public <T extends CWTable> int deleteByHash(Class<T> type, String hash) {
        return deleteByCol(type, CWTable.HASH, hash);
    }

    /**
     * Delete record by table and hash string
     *
     * @param type   model class
     * @param hashes hash string which identify a table
     * @param <T>    class
     * @return the number of row affected
     */
    public <T extends CWTable> int deleteByHash(Class<T> type, Collection<String> hashes) {
        return deleteByCol(type, CWTable.HASH, hashes);
    }

    /**
     * Delete record by table and hash string
     *
     * @param type   model class
     * @param hashes hash string which identify a table
     * @param <T>    class
     * @return the number of row affected
     */
    public <T extends CWTable> int deleteByHash(Class<T> type, String... hashes) {
        return deleteByCol(type, CWTable.HASH, hashes);
    }

    /**
     * Delete record by given column value
     *
     * @param type  model class
     * @param col   column's name
     * @param value search value
     * @param <T>   class
     * @return the number of row affected
     */
    public <T extends CWTable> int deleteByCol(Class<T> type, String col, String value) {
        return delete(type, col + " = ?", new String[]{value});
    }

    /**
     * Delete record by give column value
     *
     * @param type   model class
     * @param col    column's name
     * @param values search value
     * @param <T>    class
     * @return the number of row affected
     */
    public <T extends CWTable> int deleteByCol(Class<T> type, String col, Collection<String> values) {
        String[] args = values.toArray(new String[0]);
        return deleteByCol(type, col, args);
    }

    /**
     * Delete record by give column value
     *
     * @param type   model class
     * @param col    column's name
     * @param values search value
     * @param <T>    class
     * @return the number of row affected
     */
    public <T extends CWTable> int deleteByCol(Class<T> type, String col, String... values) {
        String where = String.format("%s IN (%s)", col, CWStringUtils.join(",", "?", values.length));
        return delete(type, where, values);
    }

    /**
     * Clear all data from table
     *
     * @param table table class
     * @return the number of row affected
     */
    public <T extends CWTable> int clear(Class<T> table) {
        int result = 0;

        // use transaction to ensure the integrity of data
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            result += db.delete(getTableName(table), "", new String[]{});
            db.setTransactionSuccessful();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        db.endTransaction();

        // According to issue #27, no need to close the database connection.
        // if (db != null) db.close();

        return result;
    }

    /* ---------------------- METHOD ------------------------- */
    /* ------------------- ------------ ---------------------- */

    /**
     * Convert entity to row value
     * By default, all field will be store to database except
     * fields are marked as CWColumnIgnore or are transient
     *
     * @param entity table entity
     */
    private <T extends CWTable> ContentValues toRowValue(T entity) {
        ContentValues values = new ContentValues();

        for (Field field : getColumnFields(entity.getClass())) {
            Class<?> type = field.getType();
            String colName = getColumnName(field);

            // get value by field
            Object val;
            try {
                field.setAccessible(true);
                val = field.get(entity);
            } catch (Exception ex) {
                continue;
            }

            // exclude null value
            // if (val == null) continue;

            if (type.equals(String.class) || type.equals(Character.class) || type.equals(char.class)) {
                values.put(colName, (String) val);
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                values.put(colName, (Double) val);
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                values.put(colName, (Float) val);
            } else if (type.equals(long.class) || type.equals(Long.class)) {
                values.put(colName, (Long) val);
            } else if (type.equals(int.class) || type.equals(Integer.class)) {
                values.put(colName, (Integer) val);
            } else if (type.equals(short.class) || type.equals(Short.class)) {
                values.put(colName, (Short) val);
            } else if (type.equals(byte[].class) || type.equals(Byte[].class)) {
                values.put(colName, (byte[]) val);
            } else if (type.equals(Date.class)) {
                if (val != null) {
                    values.put(colName, ((Date) val).getTime());
                } else {
                    values.put(colName, (Long) null);
                }
            } else {
                // other field type which SQLite does not support. Ex, enum, array, collection, date ...
                // -> convert to json string -> store as string
                if (val != null) {
                    values.put(colName, Windson.$.serialize(val).toString());
                } else {
                    values.put(colName, (String) null);
                }
            }
        }

        return values;
    }

    /**
     * Parse row value to entity
     *
     * @param cursor database cursor
     * @param clazz  table class
     */
    private <T extends CWTable> T parseRowCursor(Cursor cursor, Class<T> clazz) throws Exception {
        // create model object
        Constructor<T> constructor = clazz.getDeclaredConstructor(); // private constructor is OK
        constructor.setAccessible(true);
        T table = constructor.newInstance();

        for (Field field : getColumnFields(clazz)) {
            field.setAccessible(true);
            Object val;
            Class<?> type = field.getType();
            String colName = getColumnName(field);
            int colIndex = cursor.getColumnIndex(colName);

            // does not exist column or null value
            if (colIndex == -1 || cursor.isNull(colIndex)) {
                if (!type.isPrimitive()) {
                    field.set(table, null);
                }
                continue;
            }

            // get value of column by index
            if (type.equals(String.class) || type.equals(Character.class) || type.equals(char.class)) {
                val = cursor.getString(colIndex);
            } else if (type.equals(double.class) || type.equals(Double.class)) {
                val = cursor.getDouble(colIndex);
            } else if (type.equals(float.class) || type.equals(Float.class)) {
                val = cursor.getFloat(colIndex);
            } else if (type.equals(long.class) || type.equals(Long.class)) {
                val = cursor.getLong(colIndex);
            } else if (type.equals(int.class) || type.equals(Integer.class)) {
                val = cursor.getInt(colIndex);
            } else if (type.equals(short.class) || type.equals(Short.class)) {
                val = cursor.getInt(colIndex);
            } else if (type.equals(byte[].class) || type.equals(Byte[].class)) {
                val = cursor.getBlob(colIndex);
            } else if (type.equals(Date.class)) {
                val = new Date(cursor.getLong(colIndex));
            } else {
                // other field type which SQLite does not support. Ex, array, collection, date ...
                // -> parse json string to object
                val = cursor.getString(colIndex);
                if (field.getGenericType() instanceof ParameterizedType) {
                    val = Windson.$.deserialize(Windson.$.parse((String) val), field.getGenericType());
                } else {
                    val = Windson.$.deserialize(Windson.$.parse((String) val), type);
                }

            }

            // set value to field directly
            field.set(table, val);
        }

        return table;
    }
}
