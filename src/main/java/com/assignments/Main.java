package com.assignments;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.ExitableDirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.FilterLeafReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        Path indexPath = Files.createTempDirectory("tempIndex");
        FSDirectory directory = FSDirectory.open(indexPath);

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

        Document doc1 = new Document();
        doc1.add(new TextField(
                "text",
                "make a long story short",
                Field.Store.NO)
        );

        Document doc2 = new Document();
        doc2.add(new TextField(
                "text",
                "to be or not to be that is the question",
                Field.Store.NO)
        );

        Document doc3= new Document();
        doc3.add(new TextField(
                "text",
                "see eye to eye",
                Field.Store.NO)
        );

        indexWriter.addDocument(doc3);
        indexWriter.addDocument(doc2);
        indexWriter.addDocument(doc1);
        indexWriter.close();

        IndexSearcher indexSearcher =
                new IndexSearcher(DirectoryReader.open(directory));

        MultiPhraseQuery.Builder queryBuilder = new MultiPhraseQuery.Builder();
        queryBuilder.add(new Term("text", "long") );
        queryBuilder.add(new Term("text", "story"));
        queryBuilder.setSlop(2);

        for (LeafReaderContext leaf :
                (indexSearcher.getIndexReader()).leaves()) {
            System.out.println("num of segs");
            TermsEnum terms = leaf.reader().terms("text").iterator();
            List<Term> termList = new ArrayList<>();
            while(terms.next() != null) {
                String potentialMatch = terms.term().utf8ToString();
                System.out.println(potentialMatch);
                if (potentialMatch.startsWith("sho")) {
                    termList.add(new Term("text", potentialMatch));
                }
            }
            queryBuilder.add(termList.toArray(new Term[0]));

        }
        MultiPhraseQuery phraseQuery = queryBuilder.build();

        ScoreDoc[] docs = indexSearcher.search(phraseQuery, 10).scoreDocs;
        for (ScoreDoc doc : docs) {
            System.out.println(doc.doc);
        }


    }
}
