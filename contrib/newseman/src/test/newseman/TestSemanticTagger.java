package newseman;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import invenio.montysolr.jni.MontySolrVM;
import invenio.montysolr.jni.PythonMessage;
import invenio.montysolr.util.MontySolrAbstractLuceneTestCase;
import invenio.montysolr.util.MontySolrAbstractTestCase;

public class TestSemanticTagger extends MontySolrAbstractLuceneTestCase {
	
	private String url;
	private SemanticTagger tagger;
	
	public void setUp() throws Exception {
		super.setUp();
		
		addToSysPath(getMontySolrHome() + "/contrib/newseman/src/python");
		addTargetsToHandler("monty_newseman.targets", "monty_newseman.tests.targets");
		
		this.url = "sqlite:///:memory:"; 
		
		tagger = new SemanticTagger(this.url);
		
		// fill the db with test data
		PythonMessage message = MontySolrVM.INSTANCE.createMessage(
				"fill_newseman_dictionary")
				.setParam("url", tagger.getName());
		MontySolrVM.INSTANCE.sendMessage(message);
	}

	/**
	   * Must return a fully qualified name of the python module to load, eg:
	   * "montysolr.tests.basic"
	   */
	@Override
	public String getModuleName() {
		return "montysolr.java_bridge.SimpleBridge";
	}

	
	private String[][] createTokens(String[] words) {
		
		String[][] tokens = new String[(words.length + 1)][];
		tokens[0] = new String[]{"token", "id"};
		int i = 1;
		for (String word: words) {
			tokens[i] = new String[]{word, Integer.toString(i)};
			i++;
		}
		return tokens;
	}
	
	private Map<String, List<String>> getResults(String[][] results) {
		HashMap<String, List<String>> out = new HashMap<String, List<String>>();
		
		List<String> header = Arrays.asList(results[0]);
		
		for (String x: new String[]{"token", "id", "sem", "multi-sem", "synonyms", "multi-synonyms", "pos"}) {
			out.put(x, new ArrayList<String>());
		}
		for (String x: header) {
			if (!out.containsKey(x)) {
				out.put(x, new ArrayList<String>());
			}
		}
		
		for (int r=1;r<results.length;r++) {
			String[] row = results[r];
			for (int i=0;i<header.size();i++) {
				if (i < row.length) {
					out.get(header.get(i)).add(row[i]);
				}
				else {
					out.get(header.get(i)).add(null);
				}
			}
		}
		
		return out;
		
	}

	public void testTranslation_add_purge()  {
		
		// configure for fuzzy parsing
		tagger.configureTagger("czech", 2, "add", "purge");
		
		String text = "velká světová revoluce byla velká říjnová revoluce bez velké extra říjnové revoluce"; //12 tokens
	    
		String[] words = text.split(" ");
		
		String[][] tokens = createTokens(words);
		
		String[][] results = tagger.translateTokens(tokens);

		Map<String, List<String>> data = getResults(results);

		List<String> ids = data.get("id");
		List<String> sem = data.get("sem");
		List<String> tok = data.get("token");
		List<String> syn = data.get("synonyms");
		List<String> mulsyn = data.get("multi-synonyms");
		List<String> mulsem = data.get("multi-sem");
		
		
		assertTrue(tok.size() == 10);
		assertTrue(Collections.frequency(tok, null) == 0);
		assertTrue(Collections.frequency(sem, "XXX") == 1);
		assertTrue(Collections.frequency(mulsem, "XXX") == 1);
		assertTrue(Collections.frequency(sem, "r2") == 1);
		assertTrue(Collections.frequency(mulsyn, "velk říjn revol") == 1);
		assertTrue(tok.get(mulsyn.indexOf("velk říjn revol")).equals("velké"));
		assertTrue(Collections.frequency(tok, "velká říjnová revoluce") == 1);
	}


	public void testTranslation_add_nopurge()  {
		
		// configure for fuzzy parsing
		tagger.configureTagger("czech", 2, "add", null);
		
		String text = "velká světová revoluce byla velká říjnová revoluce bez velké extra říjnové revoluce"; //12 tokens
	    
		String[] words = text.split(" ");
		
		String[][] tokens = createTokens(words);
		
		String[][] results = tagger.translateTokens(tokens);

		Map<String, List<String>> data = getResults(results);

		List<String> ids = data.get("id");
		List<String> sem = data.get("sem");
		List<String> tok = data.get("token");
		List<String> syn = data.get("synonyms");
		List<String> mulsyn = data.get("multi-synonyms");
		List<String> mulsem = data.get("multi-sem");
		
		
		assertTrue(tok.size() == 10);
		assertTrue(Collections.frequency(tok, null) == 0);
		assertTrue(Collections.frequency(sem, "XXX") == 1);
		assertTrue(Collections.frequency(mulsem, "XXX") == 1);
		assertTrue(Collections.frequency(sem, "r2") == 2);
		assertTrue(Collections.frequency(mulsyn, "velk říjn revol") == 1);
		assertTrue(Collections.frequency(tok, "velká říjnová revoluce") == 1);
	}	
	
	
	public void testTranslation_rewrite_purge()  {
		
		// configure for fuzzy parsing
		tagger.configureTagger("czech", 2, "rewrite", "purge");
		
		String text = "velká světová revoluce byla velká říjnová revoluce bez velké extra říjnové revoluce";
	    
		String[] words = text.split(" ");
		
		String[][] tokens = createTokens(words);
		
		String[][] results = tagger.translateTokens(tokens);

		Map<String, List<String>> data = getResults(results);
		
		List<String> ids = data.get("id");
		List<String> sem = data.get("sem");
		List<String> tok = data.get("token");
		List<String> syn = data.get("synonyms");
		List<String> mulsyn = data.get("multi-synonyms");
		List<String> mulsem = data.get("multi-sem");
		
		
		assertTrue(tok.size() == 10);
		assertTrue(Collections.frequency(tok, null) == 0);
		assertTrue(Collections.frequency(sem, "XXX") == 2);
		assertTrue(Collections.frequency(mulsem, "XXX") == 0);
		assertTrue(Collections.frequency(sem, "r2") == 1);
		assertTrue(Collections.frequency(mulsyn, "velk říjn revol") == 0);
		assertTrue(Collections.frequency(tok, "velké říjnové revoluce") == 1);
	}
	
}
