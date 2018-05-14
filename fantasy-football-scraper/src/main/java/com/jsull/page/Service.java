package com.jsull.page;

public class Service {
	
	public String getEspnLinkForPlayer(String first, String last) {
		Player p = new Player();
		p.first = first;
		p.last = last;
		String googleLink = GoogleSearchDocument.generateGoogleSearchLinkForPlayer(p);
		GoogleSearchDocument g = new GoogleSearchDocument(googleLink);
		return g.getEspnLink(p);
	}

}
