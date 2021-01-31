package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import the.wind.library.utils.CWClazzUtils;

public class ClazzUtilsTest {
    private final List<String> linkedList = new LinkedList<>();

    @Test
    public void toCamelCase() {
        for (Field field : GetterSetterTest.class.getDeclaredFields()) {
            if (field.getType().equals(ClazzUtilsTest.class)) continue;
            if (Modifier.isTransient(field.getModifiers())) continue;

            Assert.assertTrue(GetterSetterTest.hasField(CWClazzUtils.toCamelCase(field)));
        }
    }

    @Test
    public void getterSetterMethodOf() {
        for (Field field : GetterSetterTest.class.getDeclaredFields()) {
            if (field.getType().equals(ClazzUtilsTest.class)) continue;
            if (Modifier.isTransient(field.getModifiers())) continue;

            Assert.assertNotNull(CWClazzUtils.getterMethodOf(field));
            Assert.assertNotNull(CWClazzUtils.setterMethodOf(field));
        }
    }

    @Test
    public void forName() {
        System.out.println(GetterSetterTest.class.getName());
        Assert.assertEquals(CWClazzUtils.forName("float"), float.class);
        Assert.assertEquals(CWClazzUtils.forName("java.lang.Float"), Float.class);
        Assert.assertEquals(CWClazzUtils.forName("java.lang.Enum"), Enum.class);
        Assert.assertEquals(CWClazzUtils.forName("the.wind.library.ClazzUtilsTest$GetterSetterTest"), GetterSetterTest.class);
    }

    @Test
    public void getGenericFieldType() throws NoSuchFieldException {
        Assert.assertEquals(LinkedList.class, linkedList.getClass());
        Field genericField = ClazzUtilsTest.class.getDeclaredField("linkedList");
        Assert.assertEquals(String.class, CWClazzUtils.getGenericFieldType(genericField));
    }

    @Test
    public void isNumberType() {
        Assert.assertTrue(CWClazzUtils.isNumberType(short.class));
        Assert.assertTrue(CWClazzUtils.isNumberType(Short.class));
        Assert.assertTrue(CWClazzUtils.isNumberType(int.class));
        Assert.assertTrue(CWClazzUtils.isNumberType(Integer.class));
        Assert.assertTrue(CWClazzUtils.isNumberType(long.class));
        Assert.assertTrue(CWClazzUtils.isNumberType(Long.class));
        Assert.assertTrue(CWClazzUtils.isNumberType(float.class));
        Assert.assertTrue(CWClazzUtils.isNumberType(Float.class));
        Assert.assertTrue(CWClazzUtils.isNumberType(double.class));
        Assert.assertTrue(CWClazzUtils.isNumberType(Double.class));
        Assert.assertTrue(CWClazzUtils.isNumberType(Number.class));
    }

    @Test
    public void isPrimitive() {
        Assert.assertTrue(CWClazzUtils.isPrimitive(short.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(Short.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(int.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(Integer.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(long.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(Long.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(float.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(Float.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(double.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(Double.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(Number.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(char.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(Character.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(String.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(boolean.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(Boolean.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(byte.class));
        Assert.assertTrue(CWClazzUtils.isPrimitive(Byte.class));
    }

    @Test
    public void wrapperOf() {
        Assert.assertEquals(Boolean.class, CWClazzUtils.wrapperOf(boolean.class));
        Assert.assertEquals(Character.class, CWClazzUtils.wrapperOf(char.class));
        Assert.assertEquals(Byte.class, CWClazzUtils.wrapperOf(byte.class));
        Assert.assertEquals(Short.class, CWClazzUtils.wrapperOf(short.class));
        Assert.assertEquals(Integer.class, CWClazzUtils.wrapperOf(int.class));
        Assert.assertEquals(Long.class, CWClazzUtils.wrapperOf(long.class));
        Assert.assertEquals(Float.class, CWClazzUtils.wrapperOf(float.class));
        Assert.assertEquals(Double.class, CWClazzUtils.wrapperOf(double.class));
    }

    private static class GetterSetterTest {
        public transient static final String[] FIELDS = new String[]{
                "color", "gameStatus", "deleted", "x",
                "y", "id", "count", "bodyName", "draggable",
                "isMovable", "motor", "PI"
        };

        // field with 'm' character
        private float mColor;
        private String mGameStatus;
        private boolean mDeleted;
        private int mX;

        // field without 'm' character
        private int y;
        private int id;
        private int count;
        private String bodyName;
        private boolean draggable;
        private boolean isMovable;
        private float mMotor;
        private float motor;
        private float PI = 4.13f;

        public static boolean hasField(String name) {
            for (String field : GetterSetterTest.FIELDS) {
                if (field.equals(name)) return true;
            }
            return false;
        }

        public float getColor() {
            return mColor;
        }

        public void setColor(float color) {
            this.mColor = color;
        }

        public String getGameStatus() {
            return mGameStatus;
        }

        public void setGameStatus(String gameStatus) {
            this.mGameStatus = gameStatus;
        }

        public boolean isDeleted() {
            return mDeleted;
        }

        public void setDeleted(boolean deleted) {
            mDeleted = deleted;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getBodyName() {
            return bodyName;
        }

        public void setBodyName(String bodyName) {
            this.bodyName = bodyName;
        }

        public boolean isDraggable() {
            return draggable;
        }

        public void setDraggable(boolean draggable) {
            this.draggable = draggable;
        }

        public boolean isMovable() {
            return isMovable;
        }

        public void setMovable(boolean movable) {
            isMovable = movable;
        }

        public float getMotor() {
            return mMotor;
        }

        public void setMotor(float motor) {
            mMotor = motor;
        }

        public int getX() {
            return mX;
        }

        public void setX(int x) {
            mX = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public float getPI() {
            return PI;
        }

        public void setPI(float pi) {
            PI = pi;
        }
    }

}
