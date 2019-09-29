package the.wind.library.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Handle migration for each build.
 */
public abstract class CWBuild {

    /* ---------------------- OVERRIDE ----------------------- */

    /**
     * Return build version.
     * We should change BUILD_VERSION in GMGlobal to the same too.
     */
    public abstract int getVersion();

    /**
     * Handle migration
     *
     * @param db SQLite database
     */
    public abstract void handle(SQLiteDatabase db);

}
