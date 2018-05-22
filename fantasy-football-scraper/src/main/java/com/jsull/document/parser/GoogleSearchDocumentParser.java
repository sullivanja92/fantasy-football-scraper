package com.jsull.document.parser;

import com.jsull.document.extractor.GoogleSearchDocumentExtractor;

public class GoogleSearchDocumentParser {

	private GoogleSearchDocumentExtractor extractor;
	
	public GoogleSearchDocumentParser(String url) {
		this.extractor = new GoogleSearchDocumentExtractor(url);
	}
}
