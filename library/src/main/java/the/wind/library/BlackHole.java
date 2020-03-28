package the.wind.library;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * The center of universe.
 * You can throw everything into blackhole and also pull everything from it
 */
public final class BlackHole {

    // singleton instance of blackhole
    public static final BlackHole $ = new BlackHole();

    // garbage of all universal
    private Map<Class<?>, Object> mGarbages = new HashMap<>();

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT -----------------------*/

    /* ---------------------- GET-SET ------------------------ */

    /* ---------------------- METHOD ------------------------- */

    /**
     * Pull garbage from black hole
     *
     * @param clazz type of garbage
     * @param <T>   template type
     * @return garbage
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T pull(@NonNull Class<T> clazz) {
        return (T) mGarbages.get(clazz);
    }

    /**
     * Swallow garbage
     * You can throw everything into blackhole but don't throw yourself
     *
     * @param garbage garbage
     */
    public void swallow(Object garbage) {
        if (garbage == null) return;
        mGarbages.put(garbage.getClass(), garbage);
    }

}
