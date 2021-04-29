package com.assignments;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.io.StringReader;

public class WhitespaceAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(final String s) {
        WhitespaceTokenizer whitespaceTokenizer = new WhitespaceTokenizer();
        StopFilter stopFilter = new StopFilter(whitespaceTokenizer,
                EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        ConcatFilter concatFilter = new ConcatFilter(stopFilter);
        return new TokenStreamComponents(whitespaceTokenizer, concatFilter);
    }

    public static void main(String[] args) {
        String text = "this is a demo of the TokenStream API";
        WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("field",
                new StringReader(text));

        CharTermAttribute charTermAttribute =
                tokenStream.addAttribute(CharTermAttribute.class);
        PositionIncrementAttribute positionIncrementAttribute =
                tokenStream.addAttribute(PositionIncrementAttribute.class);
        try {
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                System.out.println(charTermAttribute.toString());
            }
            tokenStream.end();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                tokenStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
