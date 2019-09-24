package the.wind.library;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import the.wind.library.nlp.CWNLPEngine;
import the.wind.library.nlp.NLPMatchResult;
import the.wind.library.nlp.NLPText;

public final class NlpEngineTest {

    @Test
    public void build() {
        // Testcase:
        // Text is loaded to queue before building
        {
            CWNLPEngine engine = new CWNLPEngine();
            engine.loadText(new NLPText("color the wind"));
            engine.loadText(new NLPText("Tô màu cho gió"));
            Assert.assertEquals(0, engine.nlpTexts().size());
            engine.build();

            // add more text then build again
            Assert.assertEquals(2, engine.nlpTexts().size());
            engine.loadText(new NLPText("風を彩る。"));
            Assert.assertEquals(2, engine.nlpTexts().size());
            engine.build();
            Assert.assertEquals(3, engine.nlpTexts().size());
        }

        // Testcase:
        // option = {strip: true, caseSensitive: false, useSpecialChars: false}
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.strip = true;
            opts.caseSensitive = false;
            opts.useSpecialChars = false;
            CWNLPEngine engine = new CWNLPEngine(opts);
            engine.loadText(new NLPText(" color \n  the  WIND %$%^$^6969"));
            engine.loadText(new NLPText("  風を&*((*彩る。%"));
            engine.build();

            Assert.assertEquals("color the wind 6969", engine.nlpTexts().get(0).getText());
            Assert.assertEquals("風を 彩る。", engine.nlpTexts().get(1).getText());
        }


        // Testcase:
        // option = {strip: false, caseSensitive: true, useSpecialChars: true}
        {
            CWNLPEngine.Options opts = new CWNLPEngine.Options();
            opts.strip = false;
            opts.caseSensitive = true;
            opts.useSpecialChars = true;
            CWNLPEngine engine = new CWNLPEngine(opts);
            String ip1 = " color   the  Wind %$%^$^6969";
            String ip2 = "  風を&*((*彩る。%";
            engine.loadText(new NLPText(ip1));
            engine.loadText(new NLPText(ip2));
            engine.build();

            Assert.assertEquals(ip1, engine.nlpTexts().get(0).getText());
            Assert.assertEquals(ip2, engine.nlpTexts().get(1).getText());
        }
    }

    @Test
    public void rebuild() {
        CWNLPEngine.Options opts = new CWNLPEngine.Options();
        opts.strip = false;
        opts.caseSensitive = true;
        opts.useSpecialChars = true;

        // Testcase: data is changed
        {
            CWNLPEngine engine = new CWNLPEngine(opts);
            NLPText nlpText = new NLPText(new TextSample("color the wind"));

            engine.loadText(nlpText);
            engine.build();
            Assert.assertEquals(1, engine.nlpTexts().size());
            Assert.assertEquals("color the wind", engine.nlpTexts().get(0).getText());

            // build does not update pre-loaded text
            ((TextSample) nlpText.target()).setText("Tô màu cho gió");
            engine.build();
            Assert.assertEquals("color the wind", engine.nlpTexts().get(0).getText());

            // rebuild will update both pre-loaded and new-loaded text
            engine.loadText(new NLPText("風を彩る。"));
            engine.rebuild();
            Assert.assertEquals(2, engine.nlpTexts().size());
            Assert.assertEquals("Tô màu cho gió", engine.nlpTexts().get(0).getText());
            Assert.assertEquals("風を彩る。", engine.nlpTexts().get(1).getText());
        }

        // testcase
        // rebuild specific text
        {
            CWNLPEngine engine = new CWNLPEngine(opts);
            NLPText nlpText = new NLPText(new TextSample("color the wind"));
            engine.loadText(nlpText);
            engine.build();
            Assert.assertEquals("color the wind", engine.nlpTexts().get(0).getText());

            // change target value then rebuild
            ((TextSample) nlpText.target()).setText("風を彩る。");
            engine.rebuild(nlpText);
            Assert.assertEquals("風を彩る。", engine.nlpTexts().get(0).getText());
        }
    }

    @Test
    public void doMatching() {

        // Testcase
        // empty/null search input
        {
            CWNLPEngine engine = new CWNLPEngine();
            engine.loadText(new NLPText("color the wind"));
            engine.build();

            List<NLPMatchResult> results = engine.doMatching("  ");
            Assert.assertEquals(0, results.size());

            results = engine.doMatching("~!@#$%^&*()_+=}{~!@#$%^&*()_+=}{");
            Assert.assertEquals(0, results.size());
        }

        // Testcase
        // Single search
        {
            CWNLPEngine engine = new CWNLPEngine();
            engine.loadText(new NLPText("color the wind"));
            engine.build();

            List<NLPMatchResult> results = engine.doMatching("color");
            NLPMatchResult r = results.get(0);
            Assert.assertEquals(1, results.size());
            Assert.assertTrue(r.isFullMatched());
            Assert.assertEquals("color", r.keys.get(0));
        }

        // Testcase
        // The combined search with multiple inputs
        {
            CWNLPEngine engine = new CWNLPEngine();
            NLPText[] inputs = new NLPText[]{
                    new NLPText("color the wind color of the wind"),
                    new NLPText("風を彩る。")
            };
            engine.loadText(inputs).build();

            List<NLPMatchResult> results = engine.doMatching("  wind-風color %^color&風\n%*Y(  ");
            Assert.assertEquals(2, results.size());
            NLPMatchResult r1 = results.get(0);
            NLPMatchResult r2 = results.get(1);

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
            CWNLPEngine engine = new CWNLPEngine();
            engine.loadText(new NLPText("color the wind"));
            engine.build();

            List<NLPMatchResult> results = engine.doMatching("wind color");
            NLPMatchResult r = results.get(0);
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
            CWNLPEngine engine = new CWNLPEngine();
            engine.loadText(new NLPText("color the wind"));
            engine.build();

            List<NLPMatchResult> results = engine.doMatching("red color");
            NLPMatchResult r = results.get(0);
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
            CWNLPEngine engine = new CWNLPEngine();
            engine.loadText(new NLPText("color the wind"));
            engine.build();

            List<NLPMatchResult> results = engine.doMatching("storm");
            Assert.assertEquals(1, results.size());
            Assert.assertFalse(results.get(0).isMatched());
        }

        // Testcase:
        // Test with Japanese language
        {
            CWNLPEngine engine = new CWNLPEngine();
            engine.loadText(new NLPText("風を彩る。"));
            engine.build();

            // search with 1 key
            List<NLPMatchResult> results = engine.doMatching("風");
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
            CWNLPEngine engine = new CWNLPEngine();
            engine.loadText(new NLPText("หน่วยเสียงวรรณยุกต์"));
            engine.build();

            // search with 1 key
            List<NLPMatchResult> results = engine.doMatching("ก");
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
            CWNLPEngine engine = new CWNLPEngine();
            engine.loadText(new NLPText("Tô màu cho gió"));
            engine.build();

            // search with 1 key
            List<NLPMatchResult> results = engine.doMatching("Tô");
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
            CWNLPEngine engine = new CWNLPEngine();
            engine.loadText(new NLPText("一所懸命color 1508 the勉強wind \nหนнаиболее распространённы"));
            engine.build();

            // only English
            List<NLPMatchResult> results = engine.doMatching("color the");
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

    private class TextSample implements NLPText.ITextType {

        private String text;

        private TextSample(String text) {
            this.text = text;
        }

        @Override
        public String toTextValue() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
