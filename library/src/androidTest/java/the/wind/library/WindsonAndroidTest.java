package the.wind.library;

import android.content.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import the.wind.library.db.CWTestTable;
import the.wind.library.utils.CWFileUtils;

@RunWith(AndroidJUnit4.class)
public class WindsonAndroidTest {

    private static final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @BeforeClass
    public static void beforeClass() {
    }

    @After
    public void afterMethod() {
    }

    @Test
    public void write() throws IOException {
        File file = new File(context.getExternalFilesDir(null), "databook.txt");
        CWTestTable origin = CWTestTable.newTestTable();
        // CWFileUtils.write(CWStreamUtils.stringToBytes(Windson.$.serialize(origin).toString()), file);
        Windson.$.write(origin, file);
        CWTestTable jsonTable = Windson.$.parse(file, CWTestTable.class);
        Assert.assertEquals(
                Windson.$.serialize(origin).toString(),
                Windson.$.serialize(jsonTable).toString());
        CWFileUtils.deleteFile(file);
    }
}
