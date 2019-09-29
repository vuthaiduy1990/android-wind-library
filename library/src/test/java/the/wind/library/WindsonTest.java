package the.wind.library;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import the.wind.library.db.CWTable;
import the.wind.library.db.CWTestTable;

public class WindsonTest {

    @Test
    public void parse() {
        // Testcase: parse empty json
        {
            // empty object
            String json = "{}";
            JsonElement el = CWindson.$.parse(json);
            Assert.assertTrue(el.isJsonObject());
            Assert.assertEquals(json, el.toString());

            // empty array`
            json = "[]";
            el = CWindson.$.parse(json);
            Assert.assertTrue(el.isJsonArray());
            Assert.assertEquals(0, el.getAsJsonArray().size());
            Assert.assertEquals(json, el.toString());
        }

        // Testcase: parse primitive type
        {
            // number
            String json = CWindson.$.serialize(15).toString();
            JsonElement el = CWindson.$.parse(json);
            Assert.assertTrue(el.isJsonPrimitive());
            Assert.assertEquals(15, el.getAsInt());
            Assert.assertEquals(json, el.toString());
            Assert.assertEquals("15", json);

            // string
            json = CWindson.$.serialize("color the wind").toString();
            el = CWindson.$.parse(json);
            Assert.assertTrue(el.isJsonPrimitive());
            Assert.assertEquals("color the wind", el.getAsString());
            Assert.assertEquals(json, el.toString());
            Assert.assertEquals("\"color the wind\"", json);

            // boolean
            json = CWindson.$.serialize(true).toString();
            el = CWindson.$.parse(json);
            Assert.assertTrue(el.isJsonPrimitive());
            Assert.assertTrue(el.getAsBoolean());
            Assert.assertEquals(json, el.toString());
            Assert.assertEquals("true", json);
        }

        // Testcase: parse enum type
        {
            String json = CWindson.$.serialize(CWTable.SyncState.MODIFIED).toString();
            JsonElement el = CWindson.$.parse(json);
            Assert.assertEquals("MODIFIED", el.getAsString());
            Assert.assertEquals(json, el.toString());
            Assert.assertEquals("\"MODIFIED\"", json);
        }

        // Testcase: parse date type
        {
            Date now = new Date();
            String json = CWindson.$.serialize(now).toString();
            JsonElement el = CWindson.$.parse(json);
            Assert.assertEquals(now.getTime(), el.getAsLong());
            Assert.assertEquals(json, el.toString());
        }

        // Testcase: parse array type
        {
            // number array
            double[] dbArray = new double[]{0.56124, 0.3529};
            String json = CWindson.$.serialize(dbArray).toString();
            JsonElement el = CWindson.$.parse(json);
            Assert.assertTrue(el.isJsonArray());
            Assert.assertEquals(2, el.getAsJsonArray().size());
            Assert.assertEquals(json, el.toString());
            Assert.assertEquals("[0.56124,0.3529]", json);

            // string array
            String[] strArray = new String[]{"color the wind", "風を彩る。"};
            json = CWindson.$.serialize(strArray).toString();
            el = CWindson.$.parse(json);
            Assert.assertTrue(el.isJsonArray());
            Assert.assertEquals(2, el.getAsJsonArray().size());
            Assert.assertEquals(json, el.toString());
            Assert.assertEquals("[\"color the wind\",\"風を彩る。\"]", json);
        }

        // Testcase: parse generic type
        {
            // List type
            List<String> list = new LinkedList<>();
            list.add("color the wind");
            list.add("風を彩る。");
            String json = CWindson.$.serialize(list).toString();
            JsonElement el = CWindson.$.parse(json);
            Assert.assertTrue(el.isJsonArray());
            Assert.assertEquals(2, el.getAsJsonArray().size());
            Assert.assertEquals(json, el.toString());
            Assert.assertEquals("[\"color the wind\",\"風を彩る。\"]", json);

            // Set type
            Set<Float> set = new HashSet<>();
            set.add(10.256f);
            set.add(0.6969f);
            json = CWindson.$.serialize(set).toString();
            el = CWindson.$.parse(json);
            Assert.assertTrue(el.isJsonArray());
            Assert.assertEquals(2, el.getAsJsonArray().size());
            Assert.assertEquals(json, el.toString());
            Assert.assertEquals("[10.256,0.6969]", json);
        }

        // Testcase: parse map type
        {
            Map<String, Double> map = new LinkedHashMap<>();
            map.put("風を彩る。", 0.9696);
            map.put("color the wind", 0.6969);
            String json = CWindson.$.serialize(map).toString();
            JsonElement el = CWindson.$.parse(json);
            Assert.assertTrue(el.isJsonObject());
            Assert.assertEquals(2, el.getAsJsonObject().size());
            Assert.assertEquals(json, el.toString());
            Assert.assertEquals("{\"風を彩る。\":0.9696,\"color the wind\":0.6969}", json);
        }

        // Testcase: Parse complex object type
        {
            // test table object
            CWTestTable origin = new CWTestTable();
            String json = CWindson.$.serialize(origin).toString();
            JsonObject el = CWindson.$.parse(json).getAsJsonObject();
            Assert.assertTrue(el.isJsonObject());
            Assert.assertEquals(json, el.toString());
        }

        // Testcase: Parse complex json bytes
        {
            CWTestTable origin = new CWTestTable();
            String json = CWindson.$.serialize(origin).toString();
            JsonObject el = CWindson.$.parse(json.getBytes(StandardCharsets.UTF_8)).getAsJsonObject();
            Assert.assertTrue(el.isJsonObject());
            Assert.assertEquals(json, el.toString());
        }
    }

    @Test
    public void serialize_deserialize() {
        // Testcase: null object
        {
            JsonElement el = CWindson.$.serialize(null);
            Assert.assertEquals(JsonNull.class, el.getClass());
            Assert.assertEquals("null", el.toString());

            String value = CWindson.$.deserialize(null, String.class);
            Assert.assertNull(value);

            Assert.assertNull(CWindson.$.deserialize(CWindson.$.serialize(null), String.class));
        }

        // Testcase: serialize/deserialize primitive type
        {
            Date now = new Date();
            double dbVal = Math.random();
            String strVal = "color the wind";
            Assert.assertEquals(1, (int) CWindson.$.deserialize(CWindson.$.serialize(1), int.class));
            Assert.assertEquals(1, (int) CWindson.$.deserialize(CWindson.$.serialize(1), Integer.class));
            Assert.assertEquals(now.getTime(), (long) CWindson.$.deserialize(CWindson.$.serialize(now.getTime()), long.class));
            Assert.assertEquals(now.getTime(), (long) CWindson.$.deserialize(CWindson.$.serialize(now.getTime()), Long.class));
            Assert.assertEquals(1.1f, CWindson.$.deserialize(CWindson.$.serialize(1.1f), float.class), 0);
            Assert.assertEquals(1.1f, CWindson.$.deserialize(CWindson.$.serialize(1.1f), Float.class), 0);
            Assert.assertEquals(dbVal, CWindson.$.deserialize(CWindson.$.serialize(dbVal), double.class), 0);
            Assert.assertEquals(dbVal, CWindson.$.deserialize(CWindson.$.serialize(dbVal), Double.class), 0);
            Assert.assertTrue(CWindson.$.deserialize(CWindson.$.serialize(true), boolean.class));
            Assert.assertFalse(CWindson.$.deserialize(CWindson.$.serialize(false), Boolean.class));
            Assert.assertEquals(strVal, CWindson.$.deserialize(CWindson.$.serialize(strVal), String.class));
        }

        // Testcase: serialize/deserialize enum type
        {
            CWTable.SyncState state = CWTable.SyncState.DELETED;
            Assert.assertEquals(state, CWindson.$.deserialize(CWindson.$.serialize(state), CWTable.SyncState.class));
        }

        // Testcase: serialize/deserialize datetime type
        {
            Date now = new Date();
            Assert.assertEquals(now.getTime(), CWindson.$.serialize(now).getAsLong());
            Assert.assertEquals(now, CWindson.$.deserialize(CWindson.$.serialize(now), Date.class));
        }

        // Testcase: serialize/deserialize json object
        {

            JsonObject origin = new JsonObject();
            origin.addProperty("color", "wind");
            JsonObject json = CWindson.$.deserialize(origin, JsonObject.class);
            Assert.assertEquals(origin.toString(), json.toString());
        }

        // Testcase: serialize/deserialize array type
        {
            // double array
            double[] dbArray = new double[]{Math.random(), Math.random()};
            Assert.assertEquals(dbArray.getClass(), double[].class);
            Assert.assertArrayEquals(dbArray, CWindson.$.deserialize(CWindson.$.serialize(dbArray), double[].class), 0);

            // String array
            String[] strArray = new String[]{"color the wind", "風を彩る。"};
            Assert.assertEquals(strArray.getClass(), String[].class);
            Assert.assertArrayEquals(strArray, CWindson.$.deserialize(CWindson.$.serialize(strArray), String[].class));

            // object array
            CWTestTable.SubClass[] objArr = new CWTestTable.SubClass[]{
                    new CWTestTable.SubClass(1, "color"),
                    new CWTestTable.SubClass(2, "wind")
            };
            Assert.assertEquals(objArr.getClass(), CWTestTable.SubClass[].class);
            CWTestTable.SubClass[] obArrActual = CWindson.$.deserialize(CWindson.$.serialize(objArr), objArr.getClass());
            Assert.assertEquals(
                    CWindson.$.serialize(objArr),
                    CWindson.$.serialize(obArrActual));
        }

        // Testcase: serialize/deserialize generic type (collection)
        {
            // list type
            List<String> list = new LinkedList<>();
            list.add("color the wind");
            list.add("風を彩る。");

            Assert.assertEquals(list.getClass(), LinkedList.class);
            Type type = new TypeToken<List<String>>() {}.getType();
            List<String> jsonList = CWindson.$.deserialize(CWindson.$.serialize(list), type);

            Assert.assertEquals(jsonList.get(0).getClass(), String.class);
            Assert.assertEquals(2, jsonList.size());
            Assert.assertEquals("color the wind", jsonList.get(0));
            Assert.assertEquals("風を彩る。", jsonList.get(1));

            // set type
            Set<String> set = new HashSet<>();
            set.add("color the wind");
            set.add("風を彩る。");

            Assert.assertEquals(set.getClass(), HashSet.class);
            type = new TypeToken<HashSet<String>>() {}.getType();
            Set<String> jsonSet = CWindson.$.deserialize(CWindson.$.serialize(set), type);

            Assert.assertEquals(2, jsonSet.size());
            Assert.assertTrue(jsonSet.contains("color the wind"));
            Assert.assertTrue(jsonSet.contains("風を彩る。"));

            // list of object
            // object array
            List<CWTestTable.SubClass> objList = new LinkedList<>();
            objList.add(new CWTestTable.SubClass(1, "color"));
            objList.add(new CWTestTable.SubClass(2, "wind"));

            Assert.assertEquals(objList.getClass(), LinkedList.class);
            type = new TypeToken<List<CWTestTable.SubClass>>() {}.getType();

            List<CWTestTable.SubClass> objListActual = CWindson.$.deserialize(CWindson.$.serialize(objList), type);
            Assert.assertEquals(
                    CWindson.$.serialize(objList).toString(),
                    CWindson.$.serialize(objListActual).toString());
        }

        // Testcase: serialize/deserialize map type
        {
            Map<String, Double> map = new HashMap<>();
            map.put("color the wind", 0.6969);
            map.put("風を彩る。", 0.9696);

            Assert.assertEquals(map.getClass(), HashMap.class);
            Type type = new TypeToken<Map<String, Double>>() {}.getType();
            Map<String, Double> jsonMap = CWindson.$.deserialize(CWindson.$.serialize(map), type);

            Assert.assertEquals(2, jsonMap.size());
            Assert.assertEquals((Double) 0.6969, jsonMap.get("color the wind"));
            Assert.assertEquals((Double) 0.9696, jsonMap.get("風を彩る。"));
        }

        // Testcase: serialize/deserialize complex object type with null value
        {
            CWTestTable origin = new CWTestTable();
            JsonElement el = CWindson.$.serialize(origin);
            CWTestTable json = CWindson.$.deserialize(el, CWTestTable.class);
            Assert.assertEquals(el.toString(), CWindson.$.serialize(json).toString());
        }

        // Testcase: serialize/deserialize complex object type with not-null value
        {
            CWTestTable origin = CWTestTable.newTestTable();
            JsonElement el = CWindson.$.serialize(origin);
            System.out.println(el.toString());
            CWTestTable json = CWindson.$.deserialize(el, CWTestTable.class);
            Assert.assertEquals(el.toString(), CWindson.$.serialize(json).toString());
        }

    }

    @Test
    public void mock() {
        CWindson.mock(new CWindson(true) {
            @Override
            protected void setBuilder(GsonBuilder builder) {
                super.setBuilder(builder);
            }
        });
        CWTestTable origin = new CWTestTable();
        JsonElement el = CWindson.$.serialize(origin);
        System.out.println(el.toString());
        CWTestTable json = CWindson.$.deserialize(el, CWTestTable.class);
        Assert.assertEquals(el.toString(), CWindson.$.serialize(json).toString());
    }

}
