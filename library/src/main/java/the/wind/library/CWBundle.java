package the.wind.library;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CWBundle implements Serializable {

    // tagged objects
    private final Map<String, Object> tags = new HashMap<>();

    /* ---------------------- GET-SET ------------------------ */

    /**
     * A map of tagged objects with respective key
     *
     * @return all tagged object
     */
    public Map<String, Object> values() {
        return Collections.unmodifiableMap(tags);
    }

    /**
     * Get tagged object by key
     *
     * @param key tagged key
     * @return tagged object
     */
    public Object get(String key) {
        return tags.get(key);
    }

    /**
     * Get tagged object by class key
     *
     * @return tagged object
     */
    public Object get() {
        return tags.get(CWBundle.class.getName());
    }

    /**
     * Add tagged object
     *
     * @param key       tagged key
     * @param taggedObj tagged object
     */
    public void set(String key, Object taggedObj) {
        tags.put(key, taggedObj);
    }

    /**
     * Add tagged object
     *
     * @param taggedObj tagged object
     */
    public void set(Object taggedObj) {
        tags.put(CWBundle.class.getName(), taggedObj);
    }

    /**
     * Remove attached object
     *
     * @param key key
     */
    public void remove(String key) {
        tags.remove(key);
    }

    /**
     * Remove attached object
     */
    public void remove() {
        tags.remove(CWBundle.class.getName());
    }

    /* ---------------------- METHOD ------------------------- */
}
