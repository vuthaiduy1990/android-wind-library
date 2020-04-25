package the.wind.library;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Date;

import androidx.annotation.NonNull;
import the.wind.library.utils.CWClazzUtils;
import the.wind.library.utils.CWStreamUtils;

/**
 * A Gson wrapper.
 * But not wrapper only, we can customize windson and mock it to Windson class
 * <p>
 * Usage - Call methods
 * <pre>
 *     Windson.$.serialize();  // we don't need to initialize new object
 *     Windson.$.deserialize();
 * </pre>
 * Usage - create a custom windson
 * <pre>
 *     Windson windson = new Windson(debug) {
 *          @Override
 *          protected void setBuilder(GsonBuilder builder) {
 *              super.setBuilder(builder);
 *              // customize the builder here
 *          }
 *     }
 *     // mock custom instance to class
 *     Windson.mock(windson);
 *     // then call method
 *     Windson.$.serialize();  // we can access new custom instance everywhere
 * </pre>
 */

public class Windson {

    // singleton instance of windson
    @NonNull
    public static Windson $ = new Windson(false);

    // gson and parser
    private Gson Gson;

    /**
     * Constructor
     *
     * @param debug true -> debug mode
     */
    protected Windson(boolean debug) {
        GsonBuilder builder = new GsonBuilder();
        setBuilder(builder);

        // Set pretty printing with indent in debug mode
        if (debug) {
            builder.setPrettyPrinting();
        }

        // create G-SON
        Gson = builder.create();
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /**
     * Mock custom windson instance to singleton instance of this class
     *
     * @param windson windson
     */
    public static void mock(@NonNull Windson windson) {
        $ = windson;
    }

    /* ---------------------- ABSTRACT -----------------------*/

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return G-Son object
     */
    public Gson Gson() {
        return Gson;
    }

    /**
     * Configure the builder
     *
     * @param builder Gson builder
     */
    protected void setBuilder(GsonBuilder builder) {
        // set name rule
        builder.setFieldNamingStrategy(new FieldNamingStrategy() {
            @Override
            public String translateName(Field f) {
                return CWClazzUtils.toCamelCase(f);
            }
        });

        // register adapter for customizing json of given types
        registerDateAdapter(builder);
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Parse string to json element
     *
     * @param jsonString json string
     * @return json element
     */
    public JsonElement parse(@NonNull String jsonString) {
        return JsonParser.parseString(jsonString);
    }

    /**
     * Parse string to json element
     *
     * @param jsonString json string
     * @param clazz      model class
     * @param <T>        class type
     * @return mapped object
     */
    public <T> T parse(@NonNull String jsonString, Class<T> clazz) {
        return deserialize(parse(jsonString), clazz);
    }

    /**
     * Parse file to json element
     *
     * @param jsonFile json file
     * @return json element
     */
    public JsonElement parse(@NonNull File jsonFile) throws FileNotFoundException {
        return JsonParser.parseReader(new FileReader(jsonFile));
    }

    /**
     * Parse file to json element
     *
     * @param jsonFile json file
     * @param clazz    model class
     * @param <T>      class type
     * @return mapped object
     *
     * @throws FileNotFoundException exception
     */
    public <T> T parse(@NonNull File jsonFile, Class<T> clazz) throws FileNotFoundException {
        return deserialize(parse(jsonFile), clazz);
    }

    /**
     * Parse json bytes to json element
     *
     * @param jsonBytes json bytes
     * @return json element
     */
    public JsonElement parse(@NonNull byte[] jsonBytes) {
        return parse(CWStreamUtils.bytesToString(jsonBytes));
    }

    /**
     * Parse bytes to json element
     *
     * @param jsonBytes json bytes
     * @param clazz     model class
     * @param <T>       class type
     * @return mapped object
     */
    public <T> T parse(@NonNull byte[] jsonBytes, Class<T> clazz) {
        return parse(CWStreamUtils.bytesToString(jsonBytes), clazz);
    }

    /**
     * Convert object to json then write it to file.
     *
     * @param obj object
     */
    public void write(Object obj, @NonNull File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        Gson.toJson(obj, writer);
        writer.flush();
        writer.close();
    }

    /**
     * Convert object to Json.
     * JSON format:
     * <pre>
     *     {
     *         field1: "value"
     *         field2: {
     *             clazz: ""
     *             field: value
     *         }
     *     }
     * </pre>
     *
     * @param obj object
     * @return json object
     */
    public JsonElement serialize(Object obj) {
        return Gson.toJsonTree(obj);
    }

    /**
     * Convert Json to object
     *
     * @param json  json
     * @param clazz clazz
     * @return instance of clazz
     */
    public <T> T deserialize(JsonElement json, Class<T> clazz) {
        return Gson.fromJson(json, clazz);
    }

    /**
     * Convert Json to object
     *
     * @param json json
     * @param type For example generic type
     * @return instance of clazz
     */
    public <T> T deserialize(JsonElement json, Type type) {
        return Gson.fromJson(json, type);
    }

    /**
     * Register adapter for customizing date json
     * Use UTC datetime string
     *
     * @param builder Gson builder.
     */
    private void registerDateAdapter(GsonBuilder builder) {
        builder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.getTime());
            }
        });

        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsLong());
            }
        });
    }

    /* ---------------------- INNER CLASS -------------------- */
}
