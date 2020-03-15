package the.wind.library.db;

import java.io.Serializable;
import java.util.Date;

import androidx.annotation.NonNull;
import the.wind.library.CWBundle;

/**
 * Define structure of table.
 * Note that: all extends should have default constructor
 */
public abstract class CWTable implements Serializable {

    // this constant value should be equal to name of id variable
    // ⚠⚠⚠ Do not modify this field
    public static final String HASH = "hash";

    // use this field to identity an table model.
    // because SQL id field is not sync to server
    // and may be changed each time we re-insert (import) data to database
    // ⚠⚠⚠ Do not modify this field
    @Column(name = "hash")
    private String mHash = "";

    // sync state
    private SyncState mSyncState = SyncState.NEW;

    // table version.
    // Normally it should be the same as app/database version
    private int mVersion;

    // date
    private Date mCreatedDate;
    private Date mUpdatedDate;

    // offline state.
    // true -> Table/model is retrieved from offline database
    // false -> Table/model is retrieved from server (online)
    private transient boolean mOffline = false;

    // attaching
    private transient CWBundle mBundle;

    /**
     * Constructor
     */
    protected CWTable() {
        mBundle = new CWBundle();
        setHash(new Date().getTime() + Math.random() + "");
        Date now = new Date();
        setCreatedDate(now);
        setUpdateDate(now);
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- EVENT -------------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return bundle
     */
    public CWBundle bundle() {
        return mBundle;
    }

    /**
     * Get hash string which identify a table data
     *
     * @return hash string
     */
    public String getHash() {
        return mHash;
    }

    /**
     * Set hash string which identify a table data. This field should be unique
     *
     * @param hash hash string
     */
    protected void setHash(String hash) {
        mHash = hash;
    }

    /**
     * Get synchronization state of data
     *
     * @return state of data
     */
    public SyncState getSyncState() {
        return mSyncState;
    }

    /**
     * Set sync state
     *
     * @param state state of data
     */
    public void setSyncState(@NonNull SyncState state) {
        mSyncState = state;
    }

    /**
     * @return created date
     */
    public Date getCreatedDate() {
        return mCreatedDate;
    }

    /**
     * Set created date
     *
     * @param createdDate created date
     */
    protected void setCreatedDate(Date createdDate) {
        mCreatedDate = createdDate;
    }

    /**
     * @return updated date
     */
    public Date getUpdatedDate() {
        return mUpdatedDate;
    }

    /**
     * Set created date
     *
     * @param updatedDate updated date
     */
    protected void setUpdateDate(Date updatedDate) {
        mUpdatedDate = updatedDate;
    }

    /**
     * Check if sticker is already saved offline to local storage
     *
     * @return true if offline
     */
    public boolean isOffline() {
        return mOffline;
    }

    /**
     * Set model offline
     */
    protected void setOffline() {
        mOffline = true;
    }

    /**
     * @return version
     */
    public int getVersion() {
        return mVersion;
    }

    /**
     * Set version.
     * Normally, it should be the same as app/database version
     *
     * @param version table version.
     */
    protected void setVersion(int version) {
        mVersion = version;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Sync state
     */
    public enum SyncState {
        NEW, // data have not been uploaded to server
        MODIFIED, // data have been uploaded to server but not latest version
        SYNCED,  // data have been uploaded to server and is latest version
        DELETED, // data have been deleted from local but not synced to server
        TEMP    // data is marked as temporary and should be deleted later
    }
}
