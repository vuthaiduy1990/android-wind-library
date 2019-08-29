/**
 * Copyright (c) 2014 Google, Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package the.wind.library.utils;

import java.lang.reflect.Array;
import java.util.Random;

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
     *     String[] shuffle = shuffle(String.class, array)
     *     Color[] shuffle = shuffle(Color.class, array)
     * </pre>
     *
     * @param clazz type of array's elements
     * @param array array
     * @param <T>   generic type
     * @return shuffled array
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] shuffle(Class<T> clazz, T[] array) {
        int length = array.length;
        int[] shuffleIndexes = shuffle(0, length); // generate shuffle indexes

        // shuffle array based on the shuffle indexes
        T[] result = (T[]) Array.newInstance(clazz, length);
        for (int idx = 0; idx < length; idx++) {
            result[idx] = array[shuffleIndexes[idx]];
        }
        return result;
    }
}
