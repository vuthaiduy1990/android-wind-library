package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import the.wind.library.nlp.CWNLPEngine;
import the.wind.library.nlp.NLPMatchResult;
import the.wind.library.nlp.NLPString;

public final class NlpEngineTest {

    @Test
    public void build() {
        CWNLPEngine<NLPString> engine;

        // Testcase:
        // Text is loaded to queue before building
        {
            engine = new CWNLPEngine<>();
            engine.load(new NLPString("color the wind"));
            engine.load(new NLPString("Tô màu cho gió"));
            Assert.assertEquals(0, engine.targets().size());
            engine.build();

            // add more text then build again
            Assert.assertEquals(2, engine.targets().size());
            engine.load(new NLPString("風を彩る。"));
            Assert.assertEquals(2, engine.targets().size());
            engine.build();
            Assert.assertEquals(3, engine.targets().size());
        }

        // Testcase:
        // option = {strip: true, caseSensitive: false, useSpecialChars: false}
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.strip = true;
            opts.caseSensitive = false;
            opts.useSpecialChars = false;
            engine = new CWNLPEngine<>(opts);
            engine.load(new NLPString(" color \n  the  WIND %$%^$^6969"));
            engine.load(new NLPString("  風を&*((*彩る。%"));
            engine.build();

            Assert.assertEquals("color the wind 6969", engine.targets().get(0).nlpText());
            Assert.assertEquals("風を 彩る。", engine.targets().get(1).nlpText());
        }


        // Testcase:
        // option = {strip: false, caseSensitive: true, useSpecialChars: true}
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.strip = false;
            opts.caseSensitive = true;
            opts.useSpecialChars = true;
            engine = new CWNLPEngine<>(opts);
            String ip1 = " color   the  Wind %$%^$^6969";
            String ip2 = "  風を&*((*彩る。%";
            engine.load(new NLPString(ip1));
            engine.load(new NLPString(ip2));
            engine.build();

            Assert.assertEquals(ip1, engine.targets().get(0).nlpText());
            Assert.assertEquals(ip2, engine.targets().get(1).nlpText());
        }
    }

    @Test
    public void rebuild() throws Exception {
        CWNLPEngine<NLPString> engine;
        CWNLPEngine.Options opts = new CWNLPEngine.Options();
        opts.strip = false;
        opts.caseSensitive = true;
        opts.useSpecialChars = true;

        // Testcase: data is changed
        {
            engine = new CWNLPEngine<>(opts);
            NLPString target = new NLPString("color the wind");

            engine.load(target);
            engine.build();
            Assert.assertEquals(1, engine.targets().size());
            Assert.assertEquals("color the wind", engine.targets().get(0).nlpText());

            // build does not update pre-loaded text
            Field field = target.getClass().getDeclaredField("value");
            field.setAccessible(true);
            field.set(target, "Tô màu cho gió");
            engine.build();
            Assert.assertEquals("color the wind", engine.targets().get(0).nlpText());

            // rebuild will update both pre-loaded and new-loaded text
            engine.load(new NLPString("風を彩る。"));
            engine.rebuild();
            Assert.assertEquals(2, engine.targets().size());
            Assert.assertEquals("Tô màu cho gió", engine.targets().get(0).nlpText());
            Assert.assertEquals("風を彩る。", engine.targets().get(1).nlpText());
        }

        // testcase
        // rebuild specific text
        {
            engine = new CWNLPEngine<>(opts);
            NLPString target = new NLPString("color the wind");
            engine.load(target);
            engine.build();
            Assert.assertEquals("color the wind", engine.targets().get(0).nlpText());

            // change target value then rebuild
            Field field = target.getClass().getDeclaredField("value");
            field.setAccessible(true);
            field.set(target, "風を彩る。");
            engine.rebuild(target);
            Assert.assertEquals("風を彩る。", engine.targets().get(0).nlpText());
        }
    }

    @Test
    public void doMatching() {
        CWNLPEngine<NLPString> engine;

        // Testcase
        // empty/null search input
        {
            engine = new CWNLPEngine<>();
            engine.load(new NLPString("color the wind"));
            engine.build();

            List<NLPMatchResult<NLPString>> results = engine.doMatching("  ");
            Assert.assertEquals(0, results.size());

            results = engine.doMatching("~!@#$%^&*()_+=}{~!@#$%^&*()_+=}{");
            Assert.assertEquals(0, results.size());
        }

        // Testcase
        // Single search
        {
            engine = new CWNLPEngine<>();
            engine.load(new NLPString("color the wind"));
            engine.build();

            List<NLPMatchResult<NLPString>> results = engine.doMatching("color");
            NLPMatchResult<NLPString> r = results.get(0);
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(r.isFullMatched());
            Assert.assertEquals("color", r.keys.get(0));
        }

        // Testcase
        // The combined search with multiple inputs
        {
            engine = new CWNLPEngine<>();
            NLPString[] inputs = new NLPString[]{
                    new NLPString("color the wind color of the wind"),
                    new NLPString("風を彩る。")
            };
            engine.load(inputs).build();

            List<NLPMatchResult<NLPString>> results = engine.doMatching("  wind-風color %^color&風\n%*Y(  ");
            Assert.assertEquals(2, results.size());
            NLPMatchResult<NLPString> r1 = results.get(0);
            NLPMatchResult<NLPString> r2 = results.get(1);

            Assert.assertTrue(r1.isMatched());
            Assert.assertEquals(2, r1.keys.size());
            Assert.assertEquals("wind", r1.keys.get(0));
            Assert.assertEquals("color", r1.keys.get(1));
            Assert.assertEquals(4, r1.indexes.size());
            Assert.assertEquals(10, r1.indexes.get(0).intValue());
            Assert.assertEquals(14, r1.indexes.get(1).intValue());
            Assert.assertEquals(0, r1.indexes.get(2).intValue());
            Assert.assertEquals(5, r1.indexes.get(3).intValue());

            Assert.assertTrue(r2.isMatched());
            Assert.assertEquals(1, r2.keys.size());
            Assert.assertEquals("風", r2.keys.get(0));
            Assert.assertEquals(0, r2.indexes.get(0).intValue());
            Assert.assertEquals(1, r2.indexes.get(1).intValue());
        }

        // Testcase:
        // Partial matching
        {
            engine = new CWNLPEngine<>();
            engine.load(new NLPString("color the wind"));
            engine.build();

            List<NLPMatchResult<NLPString>> results = engine.doMatching("wind color");
            NLPMatchResult<NLPString> r = results.get(0);
            Assert.assertEquals(1, results.size());
            Assert.assertFalse(r.isPartialMatched());
            Assert.assertTrue(r.isFullMatched());
            Assert.assertEquals(2, r.keys.size());
            Assert.assertEquals("wind", r.keys.get(0));
            Assert.assertEquals("color", r.keys.get(1));
            Assert.assertEquals(4, r.indexes.size());
        }

        // Testcase:
        // Full matching
        {
            engine = new CWNLPEngine<>();
            engine.load(new NLPString("color the wind"));
            engine.build();

            List<NLPMatchResult<NLPString>> results = engine.doMatching("red color");
            NLPMatchResult<NLPString> r = results.get(0);
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(r.isPartialMatched());
            Assert.assertFalse(r.isFullMatched());
            Assert.assertEquals(1, r.keys.size());
            Assert.assertEquals("color", r.keys.get(0));
            Assert.assertEquals(2, r.indexes.size());
        }

        // Testcase
        // Not match
        {
            engine = new CWNLPEngine<>();
            engine.load(new NLPString("color the wind"));
            engine.build();

            List<NLPMatchResult<NLPString>> results = engine.doMatching("storm");
            Assert.assertEquals(1, results.size());
            Assert.assertFalse(results.get(0).isMatched());
        }

        // Testcase:
        // Test with Japanese language
        {
            engine = new CWNLPEngine<>();
            engine.load(new NLPString("風を彩る。"));
            engine.build();

            // search with 1 key
            List<NLPMatchResult<NLPString>> results = engine.doMatching("風");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(1, results.get(0).keys.size());

            // search with 2 key
            results = engine.doMatching("を風");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(2, results.get(0).keys.size());
        }

        // Testcase:
        // Test with Thai language
        {
            engine = new CWNLPEngine<>();
            engine.load(new NLPString("หน่วยเสียงวรรณยุกต์"));
            engine.build();

            // search with 1 key
            List<NLPMatchResult<NLPString>> results = engine.doMatching("ก");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(1, results.get(0).keys.size());

            // search with 2 key
            results = engine.doMatching("กร");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(2, results.get(0).keys.size());
        }

        // Testcase:
        // Test with Vietnamese language
        {
            engine = new CWNLPEngine<>();
            engine.load(new NLPString("Tô màu cho gió"));
            engine.build();

            // search with 1 key
            List<NLPMatchResult<NLPString>> results = engine.doMatching("Tô");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(1, results.get(0).keys.size());

            // search with 2 key
            results = engine.doMatching("Tô cho");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(2, results.get(0).keys.size());
        }

        // Testcase:
        // Test with both space and non-space breaking language
        {
            engine = new CWNLPEngine<>();
            engine.load(new NLPString("一所懸命color 1508 the勉強wind \nหนнаиболее распространённы"));
            engine.build();

            // only English
            List<NLPMatchResult<NLPString>> results = engine.doMatching("color the");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(2, results.get(0).keys.size());

            // Only Japanese
            results = engine.doMatching("命勉");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(2, results.get(0).keys.size());

            // Only Russian
            results = engine.doMatching("аиболее");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(1, results.get(0).keys.size());

            // Only number
            results = engine.doMatching("1508");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(1, results.get(0).keys.size());

            // space and non-space breaking languages
            results = engine.doMatching("命color勉1508zzz");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isPartialMatched());
            Assert.assertEquals(4, results.get(0).keys.size());
        }
    }
}
