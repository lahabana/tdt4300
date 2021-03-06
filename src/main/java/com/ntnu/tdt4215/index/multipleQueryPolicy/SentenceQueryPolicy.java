package com.ntnu.tdt4215.index.multipleQueryPolicy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;

import org.apache.lucene.document.Document;

import com.ntnu.tdt4215.index.ScoredDocument;


/**
 * A MultipleQueryPolicy that will split the query in separated sentences
 * and then returns the result according to the chapters that where the most frequent
 * (This code should be optimized)
 *
 */
public class SentenceQueryPolicy implements MultipleQueryPolicy {
	Hashtable<String, ScoredDocument> docs = new Hashtable<String, ScoredDocument>();
	private float limit;

	public SentenceQueryPolicy(float limit) {
		this.limit = limit;
	}
	
	public ArrayList<String> splitQuery(String query) {
		String[] sentences = query.split("[\\.\\!\\?]");
		ArrayList<String> arr = new ArrayList<String>();
		for (int i = 0; i < sentences.length; i++) {
			sentences[i] = sentences[i].trim();
			if (sentences[i].length() > 0) {
				arr.add(sentences[i]);
			}
		}
		return arr;
	}

	public void map(Document doc, float score) {
		String id = doc.get("id");
		ScoredDocument d2 = docs.get(id);
		if (d2 != null) {
			d2.addScore(score);
		} else {
			docs.put(id, new ScoredDocument(doc, score));
		}
	}

	public Collection<ScoredDocument> reduce(int nbHits) {
		HashSet<ScoredDocument> res = new HashSet<ScoredDocument>(docs.size());
		Enumeration<ScoredDocument> enumer = docs.elements();
		while (enumer.hasMoreElements()) {
			ScoredDocument doc = enumer.nextElement();
			if (doc.getNormalizedScore() > limit) {
				res.add(doc);
			}
		}
		docs = new Hashtable<String, ScoredDocument>();
		return res;
	}

}