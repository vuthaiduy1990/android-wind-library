package the.wind.library.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity(name = "TestTable")
public final class CWTestTable extends CWTable {

    // Primitive type
    public int intPrim;
    public Integer mIntObj;
    public float floatPrim;
    public Float mFloatObj;
    public double doublePrim;
    public Double mDoubleObj;
    public boolean boolPrim;
    public Boolean mBoolObj;
    public String mStringObj;

    // enum
    public EnumType mEnumType;

    // date
    @Column(name = "updated_date")
    public Date mDateObj;

    @Column(ignore = true)
    public Date mIgnoreField;

    // Object
    public SubClass mSubObject;

    // Array
    public String[] mStringArr;
    public Float[] mFloatArr;
    public SubClass[] mSubClassArr;

    // List
    public List<String> mStringList;
    public List<Float> mFloatList;
    public List<SubClass> mSubClassList;

    // Set
    public Set<String> mStringSet;
    public Set<Float> mFloatSet;
    public Set<SubClass> mSubClassSet;

    // Map
    public Map<Double, Double> mDoubleMap;
    public Map<String, String> mStringMap;
    public Map<String, SubClass> mSubClassMap;

    // json object
    public JsonObject mJsonObj;
    public JsonArray mJsonArr;

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    public static CWTestTable newTestTable() {
        CWTestTable table = new CWTestTable();
        table.intPrim = 1;
        table.mIntObj = 1;
        table.floatPrim = 1.5f;
        table.mFloatObj = 1.5f;
        table.doublePrim = 1.5;
        table.mDoubleObj = 1.5;
        table.boolPrim = true;
        table.mBoolObj = true;
        table.mStringObj = "Color the wind";
        table.mEnumType = EnumType.HIGH;
        table.mDateObj = new Date();

        // object
        table.mSubObject = new SubClass(1, "風を彩る。");

        // array
        table.mFloatArr = new Float[]{1.1f, 2.2f, 3.3f};
        table.mStringArr = new String[]{"color", "the", "wind"};
        table.mSubClassArr = new SubClass[]{
                new SubClass(1, "color"),
                new SubClass(2, "the"),
                new SubClass(3, "wind"),
        };

        // List
        table.mFloatList = new LinkedList<>();
        table.mStringList = new LinkedList<>();
        table.mStringList.add("color");
        table.mStringList.add("the");
        table.mSubClassList = new LinkedList<>();
        table.mSubClassList.add(new SubClass(1, "color"));
        table.mSubClassList.add(new SubClass(2, "wind"));

        // set
        table.mFloatSet = new LinkedHashSet<>();
        table.mFloatSet.add(1.1f);
        table.mFloatSet.add(1.2f);
        table.mStringSet = new LinkedHashSet<>();
        table.mSubClassSet = new LinkedHashSet<>();
        table.mSubClassSet.add(new SubClass(1, "color"));
        table.mSubClassSet.add(new SubClass(2, "wind"));

        // map
        table.mDoubleMap = new LinkedHashMap<>();
        table.mDoubleMap.put(1.1, 1.1);
        table.mDoubleMap.put(1.2, 1.1);
        table.mStringMap = new LinkedHashMap<>();
        table.mSubClassMap = new LinkedHashMap<>();
        table.mSubClassMap.put("color", new SubClass(1, "color"));
        table.mSubClassMap.put("wind", new SubClass(2, "wind"));

        // JsonObject
        table.mJsonObj = new JsonObject();
        table.mJsonObj.addProperty("color", "the wind");
        table.mJsonArr = new JsonArray();
        table.mJsonArr.add("color");
        table.mJsonArr.add("the");

        return table;
    }

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    // Enum class
    public enum EnumType {
        HIGH,
        MEDIUM,
        LOW
    }

    // Inner class
    public static class SubClass {
        public int mInVal;
        public String mStringVal;

        public SubClass(int intVal, String stringVal) {
            mInVal = intVal;
            mStringVal = stringVal;
        }
    }

}
