package it.uniud.bigmit.import_export;

/**
 * A collection of bigraph-related XML namespaces.
 * @author alec
 *
 */
public interface XMLNS {
	/**
	 * The XML namespace for {@code <bigraph>} documents.
	 */
	public static final String BIGRAPH =
		"http://www.itu.dk/research/pls/xmlns/2010/bigraph";
	
	public static final String BRS =
			"http://www.itu.dk/research/pls/xmlns/2010/brs";
	
	/**
	 * The XML namespace for {@code <signature>} documents.
	 */
	public static final String SIGNATURE =
		"http://www.itu.dk/research/pls/xmlns/2010/signature";
	
	/**
	 * The XML namespace for BigM's extensions to the other formats.
	 */
	public static final String BIGM =
		"http://www.itu.dk/research/pls/xmlns/2010/bigm";

	public static final String CHANGE =
		"http://www.itu.dk/research/pls/xmlns/2010/change";
	
	public static final String RULE =
		"http://www.itu.dk/research/pls/xmlns/2011/rule";
	
	public static final String SPEC =
		"http://www.itu.dk/research/pls/xmlns/2012/simulation-spec";
}
