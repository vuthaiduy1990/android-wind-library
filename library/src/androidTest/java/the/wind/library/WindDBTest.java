package the.wind.library;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import the.wind.library.db.CWTestTable;
import the.wind.library.db.CWindDB;

public class WindDBTest {

    private static Context context = InstrumentationRegistry.getTargetContext();

    @BeforeClass
    public static void beforeClass() {
        CWindDB.setTables(CWTestTable.class);
        CWindDB.init(context, "test_database", 3);
    }

    @After
    public void afterMethod() {
        // Clear database
        CWindDB.$.clear(CWTestTable.class);
    }

    @Test
    public void find() throws IllegalAccessException {
        CWTestTable origin = CWTestTable.newTestTable();
        Assert.assertTrue(CWindDB.$.insert(origin) >= 0);

        // find by hash
        CWTestTable fromDB = CWindDB.$.findByHash(CWTestTable.class, origin.getHash());
        Assert.assertEquals(
                CWindson.$.serialize(origin).toString(),
                CWindson.$.serialize(fromDB).toString());

        // find by state
        fromDB = CWindDB.$.findByState(CWTestTable.class, origin.getSyncState()).get(0);
        Assert.assertEquals(
                CWindson.$.serialize(origin).toString(),
                CWindson.$.serialize(fromDB).toString());

        // find with where condition
        fromDB = CWindDB.$.find(CWTestTable.class, null, "intPrim = ?", new String[]{"1"}).get(0);
        Assert.assertEquals(
                CWindson.$.serialize(origin).toString(),
                CWindson.$.serialize(fromDB).toString());
        fromDB = CWindDB.$.find(CWTestTable.class, null, "stringObj = ?", new String[]{"Color the wind"}).get(0);
        Assert.assertEquals(
                CWindson.$.serialize(origin).toString(),
                CWindson.$.serialize(fromDB).toString());

        // search with like condition
        fromDB = CWindDB.$.find(CWTestTable.class, null, "stringObj like ?", new String[]{"%wind"}).get(0);
        Assert.assertEquals(
                CWindson.$.serialize(origin).toString(),
                CWindson.$.serialize(fromDB).toString());

        // search with specific column
        fromDB = CWindDB.$.find(
                CWTestTable.class,
                new String[]{"stringObj", "intPrim"},
                "stringObj like ?", new String[]{"%wind"}).get(0);
        for (Field field : CWindDB.$.getColumnFields(CWTestTable.class)) {
            Class<?> type = field.getType();
            field.setAccessible(true);
            if (CWindDB.$.getColumnName(field).equals("stringObj")
                    || CWindDB.$.getColumnName(field).equals("stringObj")) {
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
        CWindDB.$.insert(record1, record2, record3);

        // find many
        List<CWTestTable> result = CWindDB.$.find(
                CWTestTable.class, null,
                "stringObj = ?", new String[]{"Color the wind"});
        Assert.assertEquals(2, result.size());

        // find with limit
        result = CWindDB.$.find(
                CWTestTable.class, null,
                "stringObj = ?", new String[]{"Color the wind"},
                null, "1");
        Assert.assertEquals(1, result.size());

        // find all
        result = CWindDB.$.findAll(CWTestTable.class);
        Assert.assertEquals(3, result.size());
    }

    @Test
    public void insert() {
        // Testcase: insert record with null value
        {
            CWTestTable origin = new CWTestTable();
            long id = CWindDB.$.insert(origin);
            Assert.assertTrue(id >= 0);

            CWTestTable fromDB = CWindDB.$.findByHash(CWTestTable.class, origin.getHash());
            Assert.assertEquals(
                    CWindson.$.serialize(origin).toString(),
                    CWindson.$.serialize(fromDB).toString());
            CWindDB.$.clear(CWTestTable.class);
        }

        // Testcase: insert record with not-null value
        {
            CWTestTable origin = CWTestTable.newTestTable();
            long id = CWindDB.$.insert(origin);
            Assert.assertTrue(id >= 0);

            CWTestTable fromDB = CWindDB.$.findByHash(CWTestTable.class, origin.getHash());
            Assert.assertEquals(
                    CWindson.$.serialize(origin).toString(),
                    CWindson.$.serialize(fromDB).toString());
            CWindDB.$.clear(CWTestTable.class);
        }

        // Testcase: insert many table
        // Testcase: insert record with not-null value
        {
            long id = CWindDB.$.insert(
                    new CWTestTable(),
                    new CWTestTable(),
                    CWTestTable.newTestTable(),
                    CWTestTable.newTestTable());
            Assert.assertTrue(id >= 0);

            List<CWTestTable> list = CWindDB.$.findAll(CWTestTable.class);
            Assert.assertEquals(4, list.size());
            CWindDB.$.clear(CWTestTable.class);
        }

    }

    @Test
    public void update() {
        // Testcase: update non-exist record
        {
            int numberOfRow = CWindDB.$.update(new CWTestTable());
            Assert.assertEquals(0, numberOfRow);
        }

        // Testcase: update exist record
        {
            String hash = "dvs156b&*(U";
            CWTestTable record = new CWTestTable();
            record.setHash(hash);
            Assert.assertTrue(CWindDB.$.insert(record) >= 0);

            // update
            record = CWTestTable.newTestTable();
            record.setHash(hash);
            Assert.assertEquals(1, CWindDB.$.update(record));

            // check
            CWTestTable fromDB = CWindDB.$.findByHash(CWTestTable.class, hash);
            Assert.assertEquals(
                    CWindson.$.serialize(record).toString(),
                    CWindson.$.serialize(fromDB).toString());

            CWindDB.$.clear(CWTestTable.class);
        }

        // Testcase: update many records
        {
            CWTestTable record1 = new CWTestTable();
            CWTestTable record2 = CWTestTable.newTestTable();
            CWTestTable record3 = CWTestTable.newTestTable();
            CWindDB.$.insert(record1, record2, record3);

            // update
            Assert.assertEquals(3, CWindDB.$.update(record1, record2, record3));

            CWindDB.$.clear(CWTestTable.class);
        }
    }

    @Test
    public void upsert() {
        CWTestTable record1 = new CWTestTable();
        CWTestTable record2 = CWTestTable.newTestTable();
        CWindDB.$.insert(record1, record2);

        // upsert exist record
        Assert.assertTrue(CWindDB.$.upsert(record1));

        // update non-exist record
        Assert.assertTrue(CWindDB.$.upsert(record2));
    }

    @Test
    public void delete() {
        // Testcase: delete one record
        {
            CWTestTable record1 = new CWTestTable();
            CWTestTable record2 = CWTestTable.newTestTable();
            CWTestTable record3 = CWTestTable.newTestTable();
            CWindDB.$.insert(record1, record2, record3);

            List<CWTestTable> list = CWindDB.$.findAll(CWTestTable.class);
            Assert.assertEquals(3, list.size());

            int numberOfRow = CWindDB.$.delete(record1);
            Assert.assertEquals(1, numberOfRow);

            list = CWindDB.$.findAll(CWTestTable.class);
            Assert.assertEquals(2, list.size());
            CWindDB.$.clear(CWTestTable.class);
        }

        // Testcase: delete multiple records
        {
            CWTestTable record1 = new CWTestTable();
            CWTestTable record2 = CWTestTable.newTestTable();
            CWTestTable record3 = CWTestTable.newTestTable();
            CWindDB.$.insert(record1, record2, record3);

            List<CWTestTable> list = CWindDB.$.findAll(CWTestTable.class);
            Assert.assertEquals(3, list.size());

            int numberOfRow = CWindDB.$.delete(record1, record2, record3);
            Assert.assertEquals(3, numberOfRow);

            list = CWindDB.$.findAll(CWTestTable.class);
            Assert.assertEquals(0, list.size());
            CWindDB.$.clear(CWTestTable.class);
        }

        // Testcase: delete non-exist record
        {
            int numberOfRow = CWindDB.$.delete(new CWTestTable());
            Assert.assertEquals(0, numberOfRow);
        }
    }

    @Test
    public void clear() {
        long id = CWindDB.$.insert(
                new CWTestTable(),
                new CWTestTable(),
                CWTestTable.newTestTable(),
                CWTestTable.newTestTable());
        Assert.assertTrue(id >= 0);
        List<CWTestTable> list = CWindDB.$.findAll(CWTestTable.class);
        Assert.assertEquals(4, list.size());

        int numberOfRow = CWindDB.$.clear(CWTestTable.class);
        Assert.assertEquals(4, numberOfRow);
        list = CWindDB.$.findAll(CWTestTable.class);
        Assert.assertEquals(0, list.size());
    }
}
