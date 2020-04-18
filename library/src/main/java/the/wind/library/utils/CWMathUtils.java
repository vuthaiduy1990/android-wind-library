package the.wind.library.utils;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class CWMathUtils {
    // a random generator
    private static final Random mRand = new Random();

    /**
     * Round a double number.
     * <pre>
     *     round(1.14, 0) = 1
     *     round(1.14, 1) = 1.1
     *     round(1.14, 2) = 1.14
     *     round(1.15, 1) = 1.2
     *     round(-1.15, 1) = -1.1
     *     round(1.16, 1) = 1.2
     *     round(-1.16, 1) = -1.2
     * </pre>
     *
     * @param value  double number
     * @param places number of digit after decimal point
     * @return double value
     */
    public static double round(double value, int places) {
        int round = (int) Math.pow(10, places);
        return (double) Math.round(value * round) / round;
    }

    /**
     * Round a float number
     * <pre>
     *  round(1.14f, 0) = 1.14
     *  round(1.14f, 1) = 1.1
     *  round(1.14f, 2) = 1.14
     *  round(1.15f, 1) = 1.2
     *  round(-1.15f, 1) = -1.1
     *  round(1.16f, 1) = 1.2
     *  round(-1.16f, 1) = -1.2
     * </pre>
     *
     * @param value  float number
     * @param places number of digit after decimal point
     * @return float value
     */
    public static float round(float value, int places) {
        int round = (int) Math.pow(10, places);
        return (float) Math.round(value * round) / round;
    }

    /**
     * Truncate a float value
     * <pre>
     *     truncate(1.14f, 0) = 1
     *     truncate(1.14f, 1) = 1.1
     *     truncate(1.14f, 2) = 1.14
     *     truncate(1.15f, 1) = 1.1
     *     round(-1.15f, 1) = -1.1
     *     round(1.16f, 1) = 1.1
     *     round(-1.16f, 1) = -1.1
     * </pre>
     *
     * @param value  float number
     * @param places number of digits after decimal point
     */
    public static float truncate(float value, int places) {
        int pow = (int) Math.pow(10, places);
        if (value > 0) {
            return (float) Math.floor(value * pow) / pow;
        }
        return (float) Math.ceil(value * pow) / pow;
    }

    /**
     * Generate random number between min and max value
     *
     * @param min lower boundary
     * @param max upper boundary
     * @return random integer number
     */
    public static int random(int min, int max) {
        return (int) Math.floor(mRand.nextDouble() * (max - min + 1)) + min;
    }

    /**
     * Generate random number between min and max value
     *
     * @param min lower boundary
     * @param max upper boundary
     * @return random integer number
     */
    public static float random(float min, float max) {
        return mRand.nextFloat() * (max - min) + min;
    }

    /**
     * Generate random number between min and max value
     *
     * @param min lower boundary
     * @param max upper boundary
     * @return random integer number
     */
    public static double random(double min, double max) {
        return mRand.nextDouble() * (max - min) + min;
    }

    /**
     * Shuffle array of integer numbers from start index to end index
     * Note: the shuffled array will not include the end index
     * <pre>
     *     shuffle(0, 5) -> [4, 3, 1, 0, 2]
     *     shuffle(0, 5) -> [3, 0, 1, 4, 2]
     * </pre>
     *
     * @param start start index
     * @param end   end index
     * @return shuffled index array
     */
    public static int[] shuffle(int start, int end) {
        if (start > end) throw new IllegalArgumentException("start must not be greater than end");
        int length = end - start;

        // initiate value for array
        int[] result = new int[length];
        for (int idx = 0; idx < length; idx++) {
            result[idx] = idx + start;
        }

        // randomize an index
        // then swap the value at random index with the value at current index
        int temp;
        for (int idx = 0; idx < length; idx++) {
            int randomIdx = (int) Math.floor(mRand.nextDouble() * length);
            temp = result[idx];
            result[idx] = result[randomIdx];
            result[randomIdx] = temp;
        }

        // make sure the shuffle array is always different from the origin
        if (length > 1 && result[0] == start) {
            temp = result[0];
            result[0] = result[1];
            result[1] = temp;
        }
        return result;
    }

    /**
     * Shuffle an array
     * <pre>
     *     String[] shuffle = shuffle(String.class, array, true)
     *     Color[] shuffle = shuffle(Color.class, array, false)
     * </pre>
     *
     * @param clazz      type of array's elements
     * @param array      array
     * @param keepOrigin true -> this operation will not change the input array
     * @param <T>        generic type
     * @return shuffled array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] shuffle(Class<T> clazz, T[] array, boolean keepOrigin) {
        int length = array.length;
        int[] shuffleIndexes = shuffle(0, length); // generate shuffle indexes

        T[] result;
        if (keepOrigin) {
            // shuffle array based on the shuffle indexes
            result = (T[]) Array.newInstance(clazz, length);
            for (int idx = 0; idx < length; idx++) {
                result[idx] = array[shuffleIndexes[idx]];
            }

        } else {
            result = array;
            Set<Integer> idxSet = new HashSet<>();
            for (int idx = 0; idx < length; idx++) {
                int shuffleIdx = shuffleIndexes[idx];

                if (idxSet.add(idx) && idxSet.add(shuffleIdx)) {
                    // have not swapped items at given index -> do swap
                    T temp = result[idx];
                    result[idx] = result[shuffleIdx];
                    result[shuffleIdx] = temp;
                }

                // finish if all items are swapped
                if (idxSet.size() == shuffleIndexes.length) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Shuffle an array without changing the input array
     * <pre>
     *     String[] shuffle = shuffle(String.class, array)
     *     Color[] shuffle = shuffle(Color.class, array)
     * </pre>
     *
     * @param clazz type of array's elements
     * @param array array
     * @param <T>   generic type
     * @return shuffled array
     */
    public static <T> T[] shuffle(Class<T> clazz, T[] array) {
        return shuffle(clazz, array, true);
    }

    /**
     * Shuffle a list
     * <pre>
     *     List<String> shuffle = shuffle(list of string, true)
     *     List<Color> shuffle = shuffle(list of color, false)
     * </pre>
     *
     * @param list       list
     * @param keepOrigin true -> this operation will not change the input list
     * @param <T>        generic type
     * @return shuffled list
     */
    public static <T> List<T> shuffle(List<T> list, boolean keepOrigin) {
        int length = list.size();
        int[] shuffleIndexes = shuffle(0, length); // generate shuffle indexes
        List<T> result;
        if (keepOrigin) {
            // shuffle array based on the shuffle indexes
            result = new LinkedList<>();
            for (int idx = 0; idx < length; idx++) {
                result.add(list.get(shuffleIndexes[idx]));
            }

        } else {
            result = list;
            Set<Integer> idxSet = new HashSet<>();
            for (int idx = 0; idx < length; idx++) {
                int shuffleIdx = shuffleIndexes[idx];

                if (idxSet.add(idx) && idxSet.add(shuffleIdx)) {
                    // have not swapped items at given index -> do swap
                    T temp = result.get(idx);
                    result.set(idx, result.get(shuffleIdx));
                    result.set(shuffleIdx, temp);
                }

                // finish if all items are swapped
                if (idxSet.size() == shuffleIndexes.length) {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Shuffle a list without changing the input list
     * <pre>
     *     List<String> shuffle = shuffle(list of string)
     *     List<Color> shuffle = shuffle(list of color)
     * </pre>
     *
     * @param list a list
     * @param <T>  generic type
     * @return shuffled list
     */
    public static <T> List<T> shuffle(List<T> list) {
        return shuffle(list, true);
    }
}
