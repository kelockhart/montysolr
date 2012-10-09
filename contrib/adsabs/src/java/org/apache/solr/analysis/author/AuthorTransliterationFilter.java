/**
 * 
 */
package org.apache.solr.analysis.author;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jluker
 * 
 * This class creates different spellings and variations of the
 * author names as they are indexed (it was initially called
 * AuthorAutoSynonymFilter)
 *
 */
public final class AuthorTransliterationFilter extends TokenFilter {

    public static final Logger log = LoggerFactory.getLogger(AuthorNameVariantsCollectorFilter.class);
    
	public AuthorTransliterationFilter(TokenStream input) {
		super(input);
		this.termAtt = addAttribute(CharTermAttribute.class);
		this.posIncrAtt = addAttribute(PositionIncrementAttribute.class);
		this.synonymStack = new Stack<String>();
		this.typeAtt = addAttribute(TypeAttribute.class);
	}
	
	private Stack<String> synonymStack;
	private AttributeSource.State current;
	
	private final CharTermAttribute termAtt;
	private final PositionIncrementAttribute posIncrAtt;
    private final TypeAttribute typeAtt;
	
	/* (non-Javadoc)
	 * @see org.apache.lucene.analysis.TokenStream#incrementToken()
	 */
	@Override
	public boolean incrementToken() throws IOException {
		
		if (this.synonymStack.size() > 0) {
			String syn = this.synonymStack.pop();
			this.restoreState(this.current);
			this.termAtt.setEmpty();
			this.termAtt.append(syn);
			this.posIncrAtt.setPositionIncrement(0);
			this.typeAtt.setType(AuthorUtils.TOKEN_TYPE_AUTHOR_GENERATED_VARIANT);
			return true;
		}
		
	    if (!input.incrementToken()) return false;
	    
	    if (typeAtt.type().equals(AuthorUtils.TOKEN_TYPE_AUTHOR) && this.genVariants()) {
	    	this.current = this.captureState();
	    }
	    
    	return true;
	}
	
	private boolean genVariants() {
	    String authorName = termAtt.toString();
    	//log.debug("generating name variants for: " + authorName);
	    ArrayList<String> synonyms = AuthorUtils.getAsciiTransliteratedVariants(authorName);
	    if (synonyms.size() > 0) {
		    //log.debug("variants: " + synonyms);
	    	synonymStack.addAll(synonyms);
	        return true;
	    }
	    
	    return false;
	}

}
