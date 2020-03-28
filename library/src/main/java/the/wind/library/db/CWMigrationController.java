package the.wind.library.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Manage migration
 */
final class CWMigrationController {

    private static CWMigrationController instance = new CWMigrationController();
    private Set<Class<? extends CWBuild>> builds = new LinkedHashSet<>();
    private Set<Class<? extends CWTable>> tables = new LinkedHashSet<>();

    private CWMigrationController() {

    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    protected static CWMigrationController instance() {
        return instance;
    }

    /* ---------------------- ABSTRACT -----------------------*/

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Add builds.
     * setMigrations(GMBuild100.class)
     *
     * @param buildList list of builds
     */
    @SafeVarargs
    final void setMigrations(Class<? extends CWBuild>... buildList) {
        this.builds.addAll(Arrays.asList(buildList));
    }

    /**
     * Add tables
     * setTables(CWTestTable.class)
     *
     * @param tableList list of tables
     */
    @SafeVarargs
    final void setTables(Class<? extends CWTable>... tableList) {
        this.tables.addAll(Arrays.asList(tableList));
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Initialize tables
     */
    void doInitialization() {
        for (Class<? extends CWTable> clazz : tables) {
            CWindDB.$.createTable(clazz);
        }
    }

    /**
     * Do migration
     *
     * @param db         SQLite database
     * @param oldVersion old database version
     * @param newVersion new database version
     */
    void doMigration(SQLiteDatabase db, int oldVersion, int newVersion) {
        // run migration
        db.beginTransactionNonExclusive();
        try {
            for (Class<? extends CWBuild> clazz : builds) {
                CWBuild build = clazz.newInstance();
                if (build.getVersion() > oldVersion) {
                    build.handle(db);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        db.endTransaction();
    }
}
