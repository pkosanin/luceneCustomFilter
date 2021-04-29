package com.assignments;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;

public final class ConcatFilter extends TokenFilter {

    private final CharTermAttribute charTermAttribute =
            this.addAttribute(CharTermAttribute.class);
    private AttributeSource.State current;
    private boolean doneConcatenating = false;

    public ConcatFilter(final TokenStream input) {
        super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!doneConcatenating) {
            StringBuilder stringBuilder = new StringBuilder();
            while (this.input.incrementToken()) {
                String term = charTermAttribute.toString();
                stringBuilder.append(term).append(" ");
                current = captureState();
            }
            restoreState(current);
            clearAttributes();
            charTermAttribute.append(stringBuilder.toString().trim());
            current = captureState();
            doneConcatenating = true;
            return true;
        }

        return false;
    }
}
