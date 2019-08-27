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

public final class CWMathUtils {

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
        return (int) Math.floor(Math.random() * (max - min + 1)) + min;
    }
}
