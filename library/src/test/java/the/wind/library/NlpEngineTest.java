package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
            opts.escapeSpecialChars = false;
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
            opts.escapeSpecialChars = true;
            engine = new CWNLPEngine<>(null, opts);
            String ip1 = " color   the  Wind %$%^$^6969";
            String ip2 = "  風を&*((*彩る。%";
            engine.load(new NLPString(ip1));
            engine.load(new NLPString(ip2));
            engine.build();

            Assert.assertEquals(ip1.trim(), engine.getCookedText(engine.targets().get(0)));
            Assert.assertEquals(ip2.trim(), engine.getCookedText(engine.targets().get(1)));
        }
    }

    @Test
    public void rebuild() throws Exception {
        CWNLPEngine<NLPString> engine;
        CWNLPEngine.Options opts = new CWNLPEngine.Options();
        opts.strip = false;
        opts.caseSensitive = true;
        opts.escapeSpecialChars = true;

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
            Assert.assertEquals("color the wind", engine.getCookedText(engine.targets().get(0)));

            // rebuild will update both pre-loaded and new-loaded text
            engine.load(new NLPString("風を彩る。"));
            engine.rebuild();
            Assert.assertEquals(2, engine.targets().size());
            Assert.assertEquals("Tô màu cho gió", engine.getCookedText(engine.targets().get(0)));
            Assert.assertEquals("風を彩る。", engine.getCookedText(engine.targets().get(1)));
        }

        // testcase
        // rebuild specific text which has been processed before
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
            Assert.assertEquals(1, engine.targets().size());
            Assert.assertEquals("風を彩る。", engine.getCookedText(engine.targets().get(0)));
        }

        // Testcase
        // Rebuild new target
        {
            engine = new CWNLPEngine<>(null, opts);
            NLPString target = new NLPString("color the wind");
            engine.rebuild(target);
            engine.build();
            Assert.assertEquals("color the wind", engine.getCookedText(engine.targets().get(0)));
            Assert.assertEquals(1, engine.targets().size());
        }
    }

    @Test
    public void remove() {
        CWNLPEngine.Options opts = new CWNLPEngine.Options();
        opts.cache = 3;

        CWNLPEngine<NLPString> engine = new CWNLPEngine<>(null, opts);
        NLPString target = new NLPString("color the wind");
        engine.load(target);
        engine.build();
        List<NLPMatchResult<NLPString>> results = doMatching(engine, "color");
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(1, engine.targets().size());
        Assert.assertEquals(1, Objects.requireNonNull(engine.getCaches().get("color")).size());

        // remove target
        engine.remove(target);
        Assert.assertEquals(0, engine.targets().size());
        Assert.assertEquals(0, engine.getCaches().size());
        Assert.assertNull(engine.getCookedText(target));
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
            Assert.assertTrue(r.isMatching());
            Assert.assertEquals("color", r.keys.get(0));
        }

        // Testcase
        // Full matching but no result
        // The combined search with multiple inputs
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.fullMatch = true; // partial match
            engine = new CWNLPEngine<>(null, opts);
            NLPString[] inputs = new NLPString[]{
                    new NLPString("color the wind color of the wind"),
                    new NLPString("風を彩る。")
            };
            engine.load(inputs).build();

            List<NLPMatchResult<NLPString>> results = doMatching(engine, "  wind-風color %^color&風\n%*Y(  ");
            Assert.assertEquals(2, results.size());
            Assert.assertFalse(results.get(0).isMatching());
            Assert.assertFalse(results.get(1).isMatching());
        }

        // Testcase
        // Full matching with result
        // The combined search with multiple inputs
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.fullMatch = true; // partial match
            opts.strip = false;
            engine = new CWNLPEngine<>(null, opts);
            NLPString[] inputs = new NLPString[]{
                    new NLPString("color    the wind   %^color&風 color of the wind"),
                    new NLPString("風を彩る。")
            };
            engine.load(inputs).build();

            List<NLPMatchResult<NLPString>> results = doMatching(engine, " color the       wind");
            Assert.assertEquals(2, results.size());
            NLPMatchResult<NLPString> r1 = results.get(0);
            NLPMatchResult<NLPString> r2 = results.get(1);

            Assert.assertTrue(r1.isMatching());
            Assert.assertFalse(r2.isMatching());
            Assert.assertEquals(1, r1.keys.size());
            Assert.assertEquals(0, r1.indexes.get(0).intValue());
            Assert.assertEquals(17, r1.indexes.get(1).intValue());
        }

        // Testcase:
        // Partial matching
        // Testcase
        // The combined search with multiple inputs
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.fullMatch = false; // partial match
            opts.greedy = false; // partial match
            engine = new CWNLPEngine<>(null, opts);
            NLPString[] inputs = new NLPString[]{
                    new NLPString("color the wind color of the wind"),
                    new NLPString("風を彩る。")
            };
            engine.load(inputs).build();

            List<NLPMatchResult<NLPString>> results = doMatching(engine, "  wind-風color %^color&風\n%*Y(  ");
            Assert.assertEquals(2, results.size());
            NLPMatchResult<NLPString> r1 = results.get(0);
            NLPMatchResult<NLPString> r2 = results.get(1);

            Assert.assertTrue(r1.isMatching());
            Assert.assertEquals(2, r1.keys.size());
            Assert.assertEquals("wind", r1.keys.get(0));
            Assert.assertEquals("color", r1.keys.get(1));
            Assert.assertEquals(4, r1.indexes.size());
            Assert.assertEquals(10, r1.indexes.get(0).intValue());
            Assert.assertEquals(14, r1.indexes.get(1).intValue());
            Assert.assertEquals(0, r1.indexes.get(2).intValue());
            Assert.assertEquals(5, r1.indexes.get(3).intValue());

            Assert.assertTrue(r2.isMatching());
            Assert.assertEquals(1, r2.keys.size());
            Assert.assertEquals("風", r2.keys.get(0));
            Assert.assertEquals(0, r2.indexes.get(0).intValue());
            Assert.assertEquals(1, r2.indexes.get(1).intValue());
        }

        // Testcase
        // Not match
        {
            engine = new CWNLPEngine<>(null);
            engine.load(new NLPString("color the wind"));
            engine.build();

            List<NLPMatchResult<NLPString>> results = doMatching(engine, "storm");
            Assert.assertEquals(1, results.size());
            Assert.assertFalse(results.get(0).isMatching());

        }

        // Testcase:
        // Test with Japanese language
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.fullMatch = false; // partial match
            engine = new CWNLPEngine<>(null, opts);
            engine.load(new NLPString("風を彩る。"));
            engine.build();

            // search with 1 key
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "風");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isMatching());

            // search with 2 key
            results = doMatching(engine, "を風");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertEquals(2, results.get(0).keys.size());
        }

        // Testcase:
        // Test with Thai language
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.fullMatch = false; // partial match
            engine = new CWNLPEngine<>(null, opts);
            engine.load(new NLPString("หน่วยเสียงวรรณยุกต์"));
            engine.build();

            // search with 1 key
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "ก");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertEquals(1, results.get(0).keys.size());

            // search with 2 key
            results = doMatching(engine, "กร");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertEquals(2, results.get(0).keys.size());
        }

        // Testcase:
        // Test with Vietnamese language
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.fullMatch = false; // partial match
            engine = new CWNLPEngine<>(null, opts);
            engine.load(new NLPString("Tô màu cho gió"));
            engine.build();

            // search with 1 key
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "Tô");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertEquals(1, results.get(0).keys.size());

            // search with 2 key
            results = doMatching(engine, "Tô cho");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertEquals(2, results.get(0).keys.size());
        }

        // Testcase:
        // Test with both space and non-space breaking language
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.fullMatch = false; // partial match
            engine = new CWNLPEngine<>(null, opts);
            engine.load(new NLPString("一所懸命color 1508 the勉強wind \nหนнаиболее распространённы"));
            engine.build();

            // only English
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "color the");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertEquals(2, results.get(0).keys.size());

            // Only Japanese
            results = doMatching(engine, "命勉");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertEquals(2, results.get(0).keys.size());

            // Only Russian
            results = doMatching(engine, "аиболее");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertEquals(1, results.get(0).keys.size());

            // Only number
            results = doMatching(engine, "1508");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertEquals(1, results.get(0).keys.size());

            // space and non-space breaking languages
            results = doMatching(engine, "命color勉1508zzz");
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertEquals(4, results.get(0).keys.size());
        }
    }

    @Test
    public void greedyMatching() {
        CWNLPEngine<NLPString> engine;

        // non greedy matching
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.greedy = false;
            engine = new CWNLPEngine<>(null, opts);
            engine.load(new NLPString("color the wind with light color"));
            engine.build();
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "color");
            NLPMatchResult<NLPString> r1 = results.get(0);
            Assert.assertTrue(r1.isMatching());
            Assert.assertEquals(1, r1.keys.size());
            Assert.assertEquals(2, r1.indexes.size());
            Assert.assertEquals("color", r1.keys.get(0));
            Assert.assertEquals(0, r1.indexes.get(0).intValue());
            Assert.assertEquals(5, r1.indexes.get(1).intValue());
        }

        // greedy matching
        // non greedy matching
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.greedy = true;
            engine = new CWNLPEngine<>(null, opts);
            engine.load(new NLPString("color the wind with light color"));
            engine.build();
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "color");
            NLPMatchResult<NLPString> r1 = results.get(0);
            Assert.assertTrue(r1.isMatching());
            Assert.assertEquals(2, r1.keys.size());
            Assert.assertEquals(4, r1.indexes.size());
            Assert.assertEquals("color", r1.keys.get(0));
            Assert.assertEquals("color", r1.keys.get(1));
            Assert.assertEquals(0, r1.indexes.get(0).intValue());
            Assert.assertEquals(5, r1.indexes.get(1).intValue());
            Assert.assertEquals(26, r1.indexes.get(2).intValue());
            Assert.assertEquals(31, r1.indexes.get(3).intValue());
        }
    }

    @Test
    public void searchWithLimitedTargets() {
        CWNLPEngine<NLPString> engine = new CWNLPEngine<>(null);
        NLPString text1 = new NLPString("color the wind");
        NLPString text2 = new NLPString("gone with the wind");
        NLPString text3 = new NLPString("color me run");
        engine.load(text1, text2, text3);
        engine.build();

        // search with limited target = null;
        {
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "color", null);
            Assert.assertEquals(3, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertFalse(results.get(1).isMatching());
            Assert.assertTrue(results.get(2).isMatching());
        }

        // search with limited target is empty;
        {
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "color", new HashSet<String>());
            Assert.assertEquals(3, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertFalse(results.get(1).isMatching());
            Assert.assertTrue(results.get(2).isMatching());
        }

        // search with limited targets
        {
            Set<String> limitedTargetIds = new HashSet<>();
            limitedTargetIds.add(text1.nlpTextId(null));
            limitedTargetIds.add(text2.nlpTextId(null));
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "color", limitedTargetIds);

            Assert.assertEquals(2, results.size());
            Assert.assertTrue(results.get(0).isMatching());
            Assert.assertFalse(results.get(1).isMatching());
        }
    }

    @Test
    public void cache() {
        CWNLPEngine.Options opts = new CWNLPEngine.Options();
        opts.cache = 3;
        opts.fullMatch = false; // partial matching
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
        Assert.assertEquals(6, result2.size());
        Assert.assertEquals(5, Objects.requireNonNull(engine.getCaches().get("never")).size());
        Assert.assertEquals(6, result3.size());
        Assert.assertEquals(4, Objects.requireNonNull(engine.getCaches().get("know")).size());
        Assert.assertEquals(6, result4.size());
        Assert.assertEquals(3, Objects.requireNonNull(engine.getCaches().get("what")).size());


        // do matching again.
        // These value should
        result3 = doMatching(engine, "know");
        Assert.assertEquals(4, result3.size());
        Assert.assertEquals(3, engine.getCaches().size());
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

        // if not matching -> does not clear cache size
        result3 = doMatching(engine, "dragon");
        Assert.assertEquals(3, engine.getCaches().size());
        Assert.assertEquals(0, Objects.requireNonNull(engine.getCaches().get("dragon")).size());

        // Data is updated -> build/rebuild will clear the cache
        Assert.assertEquals(3, Objects.requireNonNull(engine.getCaches().get("what")).size());
        engine.load(new NLPString("what the hell?"));
        engine.build();
        Assert.assertEquals(0, engine.getCaches().size());
        result4 = doMatching(engine, "what");
        Assert.assertEquals(7, result4.size());
        Assert.assertEquals(4, Objects.requireNonNull(engine.getCaches().get("what")).size());
        Assert.assertEquals(1, engine.getCaches().size());
    }

    @Test
    public void testCachePerformance() {
        System.out.println("[NLPEngine] Test cache performance with 100000 records");
        CWNLPEngine.Options opts = new CWNLPEngine.Options();
        opts.cache = 3;
        opts.fullMatch = false;
        CWNLPEngine<NLPString> engine;

        // Testcase:
        // Process 100000 text with using cache
        {
            engine = new CWNLPEngine<>(null, opts);
            String[] textBags1 = new String[]{"you", "never", "know", "what", "happens", "next"};
            String[] textBags2 = new String[]{"color", "of", "the", "wind"};
            for (int i = 0; i < 50000; i++) {
                engine.load(new NLPString(CWStringUtils.join(" ", CWMathUtils.shuffle(String.class, textBags1))));
                engine.load(new NLPString(CWStringUtils.join(" ", CWMathUtils.shuffle(String.class, textBags2))));
            }
            engine.build();
            long startTime = System.currentTimeMillis();
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "never know");
            long costWithoutCache = System.currentTimeMillis() - startTime;

            // do matching again with the same search key
            startTime = System.currentTimeMillis();
            results = doMatching(engine, "never know");
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
            for (int i = 0; i < 50000; i++) {
                engine.load(new NLPString(CWStringUtils.join(" ", CWMathUtils.shuffle(String.class, textBags1))));
            }
            for (int i = 0; i < 25000; i++) {
                engine.load(new NLPString(CWStringUtils.join(" ", CWMathUtils.shuffle(String.class, textBags2))));
                engine.load(new NLPString(CWStringUtils.join(" ", CWMathUtils.shuffle(String.class, textBags3))));
            }
            engine.build();

            // do matching with the new search key which wrap the previous one without using cache
            long startTime = System.currentTimeMillis();
            List<NLPMatchResult<NLPString>> results = doMatching(engine, "color");
            long costWithoutCache = System.currentTimeMillis() - startTime;
            engine.clearCache();

            // do matching again with the new search key which wrap the previous one with using cache
            results = doMatching(engine, "col");
            startTime = System.currentTimeMillis();
            results = doMatching(engine, "color");
            long costWithCache = System.currentTimeMillis() - startTime;

            // compare
            System.out.println("[NLPEngine] do matching without pre-result: " + costWithoutCache);
            System.out.println("[NLPEngine] do matching with pre-result: " + costWithCache);
            Assert.assertTrue(costWithCache < costWithoutCache);
        }

    }

    @Test
    public void testRegexSpecialCharacter() {
        CWNLPEngine.Options opts = new CWNLPEngine.Options();
        opts.escapeSpecialChars = true;
        CWNLPEngine<NLPString> engine = new CWNLPEngine<>(null, opts);
        NLPString target = new NLPString("Color the wind " + CWRegex.SPECIAL_CHARS);
        engine.load(target);
        engine.load(new NLPString("Color the wind "));
        engine.build();
        for (Character c : CWRegex.SPECIAL_CHARS.toCharArray()) {
            List<NLPMatchResult<NLPString>> results = doMatching(engine, c.toString());
            Assert.assertEquals(2, results.size());
            for (NLPMatchResult<NLPString> rs : results) {
                if (rs.isMatching()) {
                    Assert.assertEquals(target, rs.target);
                }
            }
        }
    }

    private <T extends INLPText> List<NLPMatchResult<T>> doMatching(CWNLPEngine<T> engine, CharSequence searchKey) {
        final List<NLPMatchResult<T>> list = new LinkedList<>();
        engine.doMatching(searchKey, new CWCallback<NLPMatchResult<T>>() {
            @Override
            public NLPMatchResult<T> onSuccess(NLPMatchResult<T> result) {
                list.add(result);
                return super.onSuccess(result);
            }
        });
        return list;
    }

    private <T extends INLPText> List<NLPMatchResult<T>> doMatching(CWNLPEngine<T> engine, CharSequence searchKey, Set<String> limitedTargetIds) {
        final List<NLPMatchResult<T>> list = new LinkedList<>();
        engine.doMatching(searchKey, limitedTargetIds, new CWCallback<NLPMatchResult<T>>() {
            @Override
            public NLPMatchResult<T> onSuccess(NLPMatchResult<T> result) {
                list.add(result);
                return super.onSuccess(result);
            }
        });
        return list;
    }
}
