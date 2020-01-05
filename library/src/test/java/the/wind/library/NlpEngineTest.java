package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import the.wind.library.nlp.CWNLPEngine;
import the.wind.library.nlp.INLPText;
import the.wind.library.nlp.NLPMatchResult;
import the.wind.library.nlp.NLPString;
import the.wind.library.utils.CWMathUtils;
import the.wind.library.utils.CWStringUtils;

public final class NlpEngineTest {

    @Test
    public void build() {
        CWNLPEngine<NLPString> engine;

        // Testcase:
        // Text is loaded to queue before building
        {
            engine = new CWNLPEngine<>(null);
            engine.load(new NLPString("color the wind"));
            engine.load(new NLPString("Tô màu cho gió"));
            engine.load(null, null); // not process null value
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
            engine = new CWNLPEngine<>(null, opts);
            engine.load(new NLPString(" color \n  the  WIND %$%^$^6969"));
            engine.load(new NLPString("  風を&*((*彩る。%"));
            engine.build();

            Assert.assertEquals("color the wind 6969", engine.getCookedText(engine.targets().get(0)));
            Assert.assertEquals("風を 彩る。", engine.getCookedText(engine.targets().get(1)));
        }


        // Testcase:
        // option = {strip: false, caseSensitive: true, useSpecialChars: true}
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.strip = false;
            opts.caseSensitive = true;
            opts.useSpecialChars = true;
            engine = new CWNLPEngine<>(null, opts);
            String ip1 = " color   the  Wind %$%^$^6969";
            String ip2 = "  風を&*((*彩る。%";
            engine.load(new NLPString(ip1));
            engine.load(new NLPString(ip2));
            engine.build();

            Assert.assertEquals(ip1, engine.getCookedText(engine.targets().get(0)));
            Assert.assertEquals(ip2, engine.getCookedText(engine.targets().get(1)));
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
            engine = new CWNLPEngine<>(null, opts);
            NLPString target = new NLPString("color the wind");

            engine.load(target);
            engine.build();
            Assert.assertEquals(1, engine.targets().size());
            Assert.assertEquals("color the wind", engine.getCookedText(engine.targets().get(0)));

            // build does not update pre-loaded text
            Field field = target.getClass().getDeclaredField("value");
            field.setAccessible(true);
            field.set(target, "Tô màu cho gió");
            engine.build();
            Assert.assertNull(engine.getCookedText(engine.targets().get(0)));

            // rebuild will update both pre-loaded and new-loaded text
            engine.load(new NLPString("風を彩る。"));
            engine.rebuild();
            Assert.assertEquals(2, engine.targets().size());
            Assert.assertEquals("Tô màu cho gió", engine.getCookedText(engine.targets().get(0)));
            Assert.assertEquals("風を彩る。", engine.getCookedText(engine.targets().get(1)));
        }

        // testcase
        // rebuild specific text
        {
            engine = new CWNLPEngine<>(null, opts);
            NLPString target = new NLPString("color the wind");
            engine.load(target);
            engine.build();
            Assert.assertEquals("color the wind", engine.getCookedText(engine.targets().get(0)));

            // change target value then rebuild
            Field field = target.getClass().getDeclaredField("value");
            field.setAccessible(true);
            field.set(target, "風を彩る。");
            engine.rebuild(target);
            Assert.assertEquals("風を彩る。", engine.getCookedText(engine.targets().get(0)));
        }
    }

    @Test
    public void doMatching() {
        CWNLPEngine<NLPString> engine;

        // Testcase
        // empty/null search input
        {
            engine = new CWNLPEngine<>(null);
            engine.load(new NLPString("color the wind"));
            engine.build();

            List<NLPMatchResult<NLPString>> results = doMatching(engine, "  ");
            Assert.assertEquals(0, results.size());

            results = doMatching(engine, "~!@#$%^&*()_+=}{~!@#$%^&*()_+=}{");
            Assert.assertEquals(0, results.size());
        }

        // Testcase
        // Single search
        {
            engine = new CWNLPEngine<>(null);
            engine.load(new NLPString("color the wind"));
            engine.build();

            List<NLPMatchResult<NLPString>> results = doMatching(engine, "color");
            NLPMatchResult<NLPString> r = results.get(0);
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(r.isFullMatched());
            Assert.assertEquals("color", r.keys.get(0));
        }

        // Testcase
        // The combined search with multiple inputs
        {
            engine = new CWNLPEngine<>(null);
            NLPString[] inputs = new NLPString[]{
                    new NLPString("color the wind color of the wind"),
                    new NLPString("風を彩る。")
            };
            engine.load(inputs).build();

            List<NLPMatchResult<NLPString>> results = doMatching(engine, "  wind-風color %^color&風\n%*Y(  ");
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
            engine = new CWNLPEngine<>(null);
            engine.load(new NLPString("color the wind"));
            engine.build();

            List<NLPMatchResult<NLPString>> results = doMatching(engine, "wind color");
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
            engine = new CWNLPEngine<>(null);
            engine.load(new NLPString("color the wind"));
            engine.build();

            List<NLPMatchResult<NLPString>> results = doMatching(engine, "red color");
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
            engine = new CWNLPEngine<>(null);
            engine.load(new NLPString("color the wind"));
            engine.build();

            List<NLPMatchResult<NLPString>> results = doMatching(engine, "storm");
            Assert.assertEquals(0, results.size());
        }
        // Not matched item is include in the result
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.matchOnly = false;
            engine = new CWNLPEngine<>(null, opts);
            engine.load(new NLPString("color the wind"));
            engine.build();

            List<NLPMatchResult<NLPString>> results = doMatching(engine, "storm");
            Assert.assertEquals(1, results.size());
            Assert.assertFalse(results.get(0).isMatched());
        }

        // Testcase:
        // Test with Japanese language
        {
            engine = new CWNLPEngine<>(null);
            engine.load(new NLPString("風を彩る。"));
            engine.build();

            // search with 1 key
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "風");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(1, results.get(0).keys.size());

            // search with 2 key
            results = doMatching(engine, "を風");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(2, results.get(0).keys.size());
        }

        // Testcase:
        // Test with Thai language
        {
            engine = new CWNLPEngine<>(null);
            engine.load(new NLPString("หน่วยเสียงวรรณยุกต์"));
            engine.build();

            // search with 1 key
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "ก");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(1, results.get(0).keys.size());

            // search with 2 key
            results = doMatching(engine, "กร");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(2, results.get(0).keys.size());
        }

        // Testcase:
        // Test with Vietnamese language
        {
            engine = new CWNLPEngine<>(null);
            engine.load(new NLPString("Tô màu cho gió"));
            engine.build();

            // search with 1 key
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "Tô");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(1, results.get(0).keys.size());

            // search with 2 key
            results = doMatching(engine, "Tô cho");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(2, results.get(0).keys.size());
        }

        // Testcase:
        // Test with both space and non-space breaking language
        {
            engine = new CWNLPEngine<>(null);
            engine.load(new NLPString("一所懸命color 1508 the勉強wind \nหนнаиболее распространённы"));
            engine.build();

            // only English
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "color the");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(2, results.get(0).keys.size());

            // Only Japanese
            results = doMatching(engine, "命勉");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(2, results.get(0).keys.size());

            // Only Russian
            results = doMatching(engine, "аиболее");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(1, results.get(0).keys.size());

            // Only number
            results = doMatching(engine, "1508");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isFullMatched());
            Assert.assertEquals(1, results.get(0).keys.size());

            // space and non-space breaking languages
            results = doMatching(engine, "命color勉1508zzz");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isPartialMatched());
            Assert.assertEquals(4, results.get(0).keys.size());
        }
    }

    @Test
    public void cache() {
        CWNLPEngine.Options opts = new CWNLPEngine.Options();
        opts.cache = 3;
        opts.matchOnly = true;
        CWNLPEngine<NLPString> engine = new CWNLPEngine<>(null, opts);
        List<String> textBags = Arrays.asList("you", "never", "know", "what", "happens", "next");
        for (int i = 0; i < textBags.size(); i++) {
            engine.load(new NLPString(CWStringUtils.join(" ", textBags.subList(0, i + 1))));
        }
        engine.build();

        // do matching in the first time
        List<NLPMatchResult<NLPString>> result1 = doMatching(engine, "you");
        List<NLPMatchResult<NLPString>> result2 = doMatching(engine, "never");
        List<NLPMatchResult<NLPString>> result3 = doMatching(engine, "know");
        List<NLPMatchResult<NLPString>> result4 = doMatching(engine, "what");
        Assert.assertEquals(3, engine.getCaches().size());
        Assert.assertEquals(6, result1.size());
        Assert.assertNull(engine.getCaches().get("you"));
        Assert.assertEquals(5, result2.size());
        Assert.assertEquals(5, Objects.requireNonNull(engine.getCaches().get("never")).size());
        Assert.assertEquals(4, result3.size());
        Assert.assertEquals(4, Objects.requireNonNull(engine.getCaches().get("know")).size());
        Assert.assertEquals(3, result4.size());
        Assert.assertEquals(3, Objects.requireNonNull(engine.getCaches().get("what")).size());


        // do matching again.
        // These value should
        result3 = doMatching(engine, "know");
        Assert.assertEquals(3, engine.getCaches().size());
        Assert.assertEquals(4, result3.size());
        Assert.assertNull(engine.getCaches().get("you"));
        Assert.assertEquals(5, Objects.requireNonNull(engine.getCaches().get("never")).size());
        Assert.assertEquals(4, Objects.requireNonNull(engine.getCaches().get("know")).size());
        Assert.assertEquals(3, Objects.requireNonNull(engine.getCaches().get("what")).size());

        // do matching with other difference key
        result3 = doMatching(engine, "know what");
        Assert.assertNull(engine.getCaches().get("never"));
        Assert.assertEquals(4, Objects.requireNonNull(engine.getCaches().get("know")).size());
        Assert.assertEquals(3, Objects.requireNonNull(engine.getCaches().get("what")).size());
        Assert.assertEquals(4, Objects.requireNonNull(engine.getCaches().get("know what")).size());
        Assert.assertEquals(4, result3.size());

        // if no data added -> engine.build() will not clear cache
        engine.build();
        Assert.assertEquals(3, engine.getCaches().size());

        // Data is updated -> build/rebuild will clear the cache
        Assert.assertEquals(3, Objects.requireNonNull(engine.getCaches().get("what")).size());
        engine.load(new NLPString("what the hell?"));
        engine.build();
        Assert.assertEquals(0, engine.getCaches().size());
        result4 = doMatching(engine, "what");
        Assert.assertEquals(4, result4.size());
        Assert.assertEquals(4, Objects.requireNonNull(engine.getCaches().get("what")).size());
        Assert.assertEquals(1, engine.getCaches().size());
    }

    @Test
    public void testCachePerformance() {
        CWNLPEngine.Options opts = new CWNLPEngine.Options();
        opts.cache = 3;
        opts.matchOnly = true;
        CWNLPEngine<NLPString> engine;

        // Testcase:
        // Process 20000 text with using cache
        {
            engine = new CWNLPEngine<>(null, opts);
            String[] textBags1 = new String[]{"you", "never", "know", "what", "happens", "next"};
            String[] textBags2 = new String[]{"color", "of", "the", "wind"};
            for (int i = 0; i < 10000; i++) {
                engine.load(new NLPString(CWStringUtils.join(" ", CWMathUtils.shuffle(String.class, textBags1))));
                engine.load(new NLPString(CWStringUtils.join(" ", CWMathUtils.shuffle(String.class, textBags2))));
            }
            engine.build();
            long startTime = System.currentTimeMillis();
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "never know");
            Assert.assertEquals(10000, results.size());
            long costWithoutCache = System.currentTimeMillis() - startTime;

            // do matching again with the same search key
            startTime = System.currentTimeMillis();
            results = doMatching(engine, "never know");
            Assert.assertEquals(10000, results.size());
            long costWithCache = System.currentTimeMillis() - startTime;

            // compare
            System.out.println("[NLPEngine] do matching without cache: " + costWithoutCache);
            System.out.println("[NLPEngine] do matching with cache: " + costWithCache);
            Assert.assertTrue(costWithCache < costWithoutCache);
        }

        // Testcase:
        // Process 20000 text with using previous result of wrapping condition
        {
            engine = new CWNLPEngine<>(null, opts);
            String[] textBags1 = new String[]{"you", "never", "know", "what", "happens", "next"};
            String[] textBags2 = new String[]{"color", "the", "the", "col"};
            String[] textBags3 = new String[]{"col", "of", "duty"};
            for (int i = 0; i < 10000; i++) {
                engine.load(new NLPString(CWStringUtils.join(" ", CWMathUtils.shuffle(String.class, textBags1))));
            }
            for (int i = 0; i < 5000; i++) {
                engine.load(new NLPString(CWStringUtils.join(" ", CWMathUtils.shuffle(String.class, textBags2))));
                engine.load(new NLPString(CWStringUtils.join(" ", CWMathUtils.shuffle(String.class, textBags3))));
            }
            engine.build();

            // do matching with the new search key which wrap the previous one without using cache
            long startTime = System.currentTimeMillis();
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "color");
            long costWithoutCache = System.currentTimeMillis() - startTime;
            engine.clearCache();
            Assert.assertEquals(5000, results.size());

            // do matching again with the new search key which wrap the previous one with using cache
            results = doMatching(engine, "col");
            Assert.assertEquals(10000, results.size());
            startTime = System.currentTimeMillis();
            results = doMatching(engine, "color");
            long costWithCache = System.currentTimeMillis() - startTime;
            Assert.assertEquals(5000, results.size());

            // compare
            System.out.println("[NLPEngine] do matching without pre-result: " + costWithoutCache);
            System.out.println("[NLPEngine] do matching with pre-result: " + costWithCache);
            Assert.assertTrue(costWithCache < costWithoutCache);
        }

    }

    private <T extends INLPText> List<NLPMatchResult<T>> doMatching(CWNLPEngine<T> engine, CharSequence searchKey) {
        final List<NLPMatchResult<T>> list = new LinkedList<>();
        engine.doMatching(searchKey, new CWCallback<NLPMatchResult<T>>() {
            @Override
            public NLPMatchResult<T> onSuccess(NLPMatchResult<T> result) {
                if (result != null) {
                    list.add(result);
                }
                return super.onSuccess(result);
            }
        });
        return list;
    }
}
