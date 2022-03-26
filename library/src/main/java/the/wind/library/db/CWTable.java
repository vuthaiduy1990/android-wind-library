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
    @Column(name = HASH, indexing = true)
    private String hash;

    // sync state
    private SyncState syncState = SyncState.NEW;

    // table version.
    // Normally it should be the same as app/database version
    private int version;

    // date
    private Date createdDate;
    private Date updatedDate;

    // offline state.
    // true -> Table/model is retrieved from offline database
    // false -> Table/model is retrieved from server (online)
    private transient boolean offline = false;
    // record is deleted or not
    private transient boolean deleted = false;

    // attaching
    private final transient CWBundle bundle;

    /**
     * Constructor
     */
    protected CWTable() {
        bundle = new CWBundle();
        Date now = new Date();
        setCreatedDate(now);
        setUpdateDate(now);
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return bundle
     */
    @NonNull
    public CWBundle bundle() {
        return bundle;
    }

    /**
     * Get hash string which identify a table data
     *
     * @return hash string
     */
    public String getHash() {
        return hash;
    }

    /**
     * Set hash string which identify a table data. This field should be unique
     *
     * @param hash hash string
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * Get synchronization state of data
     *
     * @return state of data
     */
    public SyncState getSyncState() {
        return syncState;
    }

    /**
     * Set sync state
     *
     * @param state state of data
     */
    public void setSyncState(@NonNull SyncState state) {
        syncState = state;
    }

    /**
     * @return created date
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * Set created date
     *
     * @param createdDate created date
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return updated date
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * Set created date
     *
     * @param updatedDate updated date
     */
    public void setUpdateDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * Check if data is already saved offline to local storage
     *
     * @return true if offline
     */
    public boolean isOffline() {
        return offline;
    }

    /**
     * Set model offline
     */
    protected void setOffline() {
        offline = true;
    }

    /**
     * Check if record is deleted or not
     *
     * @return deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Mark the record as delete
     */
    protected void setDeleted() {
        this.deleted = true;
    }

    /**
     * @return version
     */
    public int getVersion() {
        return version;
    }

    /**
     * Set version.
     * Normally, it should be the same as app/database version
     *
     * @param version table version.
     */
    protected void setVersion(int version) {
        this.version = version;
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
