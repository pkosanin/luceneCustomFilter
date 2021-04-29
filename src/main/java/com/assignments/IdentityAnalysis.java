package com.assignments;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;

import java.io.IOException;
import java.io.StringReader;

public class IdentityAnalysis {
    public static void main(String[] args) {
        Analyzer analyzer = new StandardAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream(
                "fieldname",
                new StringReader("some text goes here"));
        OffsetAttribute offsetAttribute =
                tokenStream.addAttribute(OffsetAttribute.class);
        CharTermAttribute charTermAttribute =
                tokenStream.addAttribute(CharTermAttribute.class);

        try
        {
            tokenStream.reset();
            StringBuilder stringBuilder = new StringBuilder();
            CharTermAttribute tmp = null;
            while (tokenStream.incrementToken()) {
                stringBuilder.append(charTermAttribute.toString()).append(" ");
            }

            tokenStream.end();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                tokenStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
