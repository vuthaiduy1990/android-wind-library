package the.wind.library;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CWAttach {

    // tagged objects
    private Map<String, Object> mTags = new HashMap<>();

    /* ---------------------- GET-SET ------------------------ */

    /**
     * A map of tagged objects with respective key
     *
     * @return all tagged object
     */
    public Map<String, Object> values() {
        return Collections.unmodifiableMap(mTags);
    }

    /**
     * Get tagged object by key
     *
     * @param key tagged key
     * @return tagged object
     */
    public Object get(String key) {
        return mTags.get(key);
    }

    /**
     * Add tagged object
     *
     * @param key       tagged key
     * @param taggedObj tagged object
     */
    public void set(String key, Object taggedObj) {
        mTags.put(key, taggedObj);
    }

    /**
     * Remove attached object
     *
     * @param key key
     */
    public void remove(String key) {
        mTags.remove(key);
    }

    /* ---------------------- METHOD ------------------------- */
}
