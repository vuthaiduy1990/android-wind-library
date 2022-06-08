package the.wind.library;

import android.content.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import the.wind.library.db.CWTestTable;
import the.wind.library.db.WindDB;

@RunWith(AndroidJUnit4.class)
public class WindDBTest {

    private static final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @BeforeClass
    public static void beforeClass() {
        WindDB.setTables(CWTestTable.class);
        WindDB.init(context, "test_database", 3);
        WindDB.$.clear(CWTestTable.class);
    }

    @After
    public void afterMethod() {
        // Clear database
        WindDB.$.clear(CWTestTable.class);
    }

    @Test
    public void find() throws IllegalAccessException {
        CWTestTable origin = CWTestTable.newTestTable();
        Assert.assertTrue(WindDB.$.insert(origin) >= 0);

        // find by hash
        CWTestTable fromDB = WindDB.$.findByHash(CWTestTable.class, origin.getHash());
        Assert.assertEquals(
                Windson.$.serialize(origin).toString(),
                Windson.$.serialize(fromDB).toString());

        // find with where condition
        fromDB = WindDB.$.find(CWTestTable.class, null, "intPrim = ?", new String[]{"1"}).get(0);
        Assert.assertEquals(
                Windson.$.serialize(origin).toString(),
                Windson.$.serialize(fromDB).toString());
        fromDB = WindDB.$.find(CWTestTable.class, null, "stringObj = ?", new String[]{"Color the wind"}).get(0);
        Assert.assertEquals(
                Windson.$.serialize(origin).toString(),
                Windson.$.serialize(fromDB).toString());

        // search with like condition
        fromDB = WindDB.$.find(CWTestTable.class, null, "stringObj like ?", new String[]{"%wind"}).get(0);
        Assert.assertEquals(
                Windson.$.serialize(origin).toString(),
                Windson.$.serialize(fromDB).toString());

        // search with specific column
        fromDB = WindDB.$.find(
                CWTestTable.class,
                new String[]{"stringObj", "intPrim"},
                "stringObj like ?", new String[]{"%wind"}).get(0);
        for (Field field : WindDB.getColumnFields(CWTestTable.class)) {
            Class<?> type = field.getType();
            field.setAccessible(true);
            if (WindDB.getColumnName(field).equals("stringObj")
                    || WindDB.getColumnName(field).equals("stringObj")) {
                Assert.assertNotNull(field.get(fromDB));
            } else if (!type.isPrimitive()) {
                Assert.assertNull(field.get(fromDB));
            }
        }

    }


    @Test
    public void find_many() {
        CWTestTable record1 = new CWTestTable();
        CWTestTable record2 = CWTestTable.newTestTable();
        CWTestTable record3 = CWTestTable.newTestTable();
        WindDB.$.insert(record1, record2, record3);

        // find many
        List<CWTestTable> result = WindDB.$.find(
                CWTestTable.class, null,
                "stringObj = ?", new String[]{"Color the wind"});
        Assert.assertEquals(2, result.size());

        // find with limit
        result = WindDB.$.find(
                CWTestTable.class, null,
                "stringObj = ?", new String[]{"Color the wind"},
                null, "1");
        Assert.assertEquals(1, result.size());

        // find by hash
        result = WindDB.$.findByHash(CWTestTable.class, record1.getHash(), record2.getHash());
        Assert.assertEquals(2, result.size());

        // find all
        result = WindDB.$.findAll(CWTestTable.class);
        Assert.assertEquals(3, result.size());
    }

    @Test
    public void insert() {
        // Testcase: insert record with null value
        {
            CWTestTable origin = new CWTestTable();
            long id = WindDB.$.insert(origin);
            Assert.assertTrue(id >= 0);

            CWTestTable fromDB = WindDB.$.findByHash(CWTestTable.class, origin.getHash());
            Assert.assertEquals(
                    Windson.$.serialize(origin).toString(),
                    Windson.$.serialize(fromDB).toString());
            WindDB.$.clear(CWTestTable.class);
        }

        // Testcase: insert record with not-null value
        {
            CWTestTable origin = CWTestTable.newTestTable();
            long id = WindDB.$.insert(origin);
            Assert.assertTrue(id >= 0);

            CWTestTable fromDB = WindDB.$.findByHash(CWTestTable.class, origin.getHash());
            Assert.assertEquals(
                    Windson.$.serialize(origin).toString(),
                    Windson.$.serialize(fromDB).toString());
            WindDB.$.clear(CWTestTable.class);
        }

        // Testcase: insert many table
        // Testcase: insert record with not-null value
        {
            long id = WindDB.$.insert(
                    new CWTestTable(),
                    new CWTestTable(),
                    CWTestTable.newTestTable(),
                    CWTestTable.newTestTable());
            Assert.assertTrue(id >= 0);

            List<CWTestTable> list = WindDB.$.findAll(CWTestTable.class);
            Assert.assertEquals(4, list.size());
            WindDB.$.clear(CWTestTable.class);
        }

    }

    @Test
    public void update() {
        // Testcase: update non-exist record
        {
            int numberOfRow = WindDB.$.update(new CWTestTable());
            Assert.assertEquals(0, numberOfRow);
        }

        // Testcase: update exist record
        {
            String hash = "dvs156b&*(U";
            CWTestTable record = new CWTestTable();
            record.setHash(hash);
            Assert.assertTrue(WindDB.$.insert(record) >= 0);

            // update
            record = CWTestTable.newTestTable();
            record.setHash(hash);
            Assert.assertEquals(1, WindDB.$.update(record));

            // check
            CWTestTable fromDB = WindDB.$.findByHash(CWTestTable.class, hash);
            Assert.assertEquals(
                    Windson.$.serialize(record).toString(),
                    Windson.$.serialize(fromDB).toString());

            WindDB.$.clear(CWTestTable.class);
        }

        // Testcase: update many records
        {
            CWTestTable record1 = new CWTestTable();
            CWTestTable record2 = CWTestTable.newTestTable();
            CWTestTable record3 = CWTestTable.newTestTable();
            WindDB.$.insert(record1, record2, record3);

            // update
            Assert.assertEquals(3, WindDB.$.update(record1, record2, record3));

            WindDB.$.clear(CWTestTable.class);
        }
    }

    @Test
    public void upsert() {
        CWTestTable record1 = new CWTestTable();
        CWTestTable record2 = CWTestTable.newTestTable();
        WindDB.$.insert(record1, record2);

        // upsert exist record
        Assert.assertTrue(WindDB.$.upsert(record1));

        // update non-exist record
        Assert.assertTrue(WindDB.$.upsert(record2));
    }

    @Test
    public void delete() {
        // Testcase: delete one record
        {
            CWTestTable record1 = new CWTestTable();
            CWTestTable record2 = CWTestTable.newTestTable();
            CWTestTable record3 = CWTestTable.newTestTable();
            WindDB.$.insert(record1, record2, record3);

            List<CWTestTable> list = WindDB.$.findAll(CWTestTable.class);
            Assert.assertEquals(3, list.size());

            int numberOfRow = WindDB.$.delete(record1);
            Assert.assertEquals(1, numberOfRow);

            list = WindDB.$.findAll(CWTestTable.class);
            Assert.assertEquals(2, list.size());
            WindDB.$.clear(CWTestTable.class);
        }

        // Testcase: delete multiple records
        {
            CWTestTable record1 = new CWTestTable();
            CWTestTable record2 = CWTestTable.newTestTable();
            CWTestTable record3 = CWTestTable.newTestTable();
            WindDB.$.insert(record1, record2, record3);

            List<CWTestTable> list = WindDB.$.findAll(CWTestTable.class);
            Assert.assertEquals(3, list.size());

            int numberOfRow = WindDB.$.delete(record1, record2, record3);
            Assert.assertEquals(3, numberOfRow);

            list = WindDB.$.findAll(CWTestTable.class);
            Assert.assertEquals(0, list.size());
            WindDB.$.clear(CWTestTable.class);
        }

        // Testcase: delete non-exist record
        {
            int numberOfRow = WindDB.$.delete(new CWTestTable());
            Assert.assertEquals(0, numberOfRow);
        }

        // Testcase: delete empty
        {
            Assert.assertEquals(0, WindDB.$.delete());
        }

        // Testcase: Delete by hash
        {
            CWTestTable record1 = new CWTestTable();
            CWTestTable record2 = CWTestTable.newTestTable();
            CWTestTable record3 = CWTestTable.newTestTable();
            WindDB.$.insert(record1, record2, record3);

            int numberOfRow = WindDB.$.deleteByHash(CWTestTable.class, record1.getHash(), record2.getHash(), record3.getHash());
            Assert.assertEquals(3, numberOfRow);
            Assert.assertEquals(0, WindDB.$.findAll(CWTestTable.class).size());
        }
    }

    @Test
    public void clear() {
        long id = WindDB.$.insert(
                new CWTestTable(),
                new CWTestTable(),
                CWTestTable.newTestTable(),
                CWTestTable.newTestTable());
        Assert.assertTrue(id >= 0);
        List<CWTestTable> list = WindDB.$.findAll(CWTestTable.class);
        Assert.assertEquals(4, list.size());

        int numberOfRow = WindDB.$.clear(CWTestTable.class);
        Assert.assertEquals(4, numberOfRow);
        list = WindDB.$.findAll(CWTestTable.class);
        Assert.assertEquals(0, list.size());
    }
}
