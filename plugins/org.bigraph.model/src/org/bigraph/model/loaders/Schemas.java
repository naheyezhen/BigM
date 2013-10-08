package org.bigraph.model.loaders;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import org.bigraph.model.loaders.internal.SchemaResolver;
import org.bigraph.model.resources.ResourceOpenable;
import org.xml.sax.SAXException;

public abstract class Schemas {
	private Schemas() {}
	
	private static Schema tryOpenRegister(String ns, String path) {
		ResourceOpenable file = new ResourceOpenable(path);
		try {
			Schema s = XMLLoader.getSharedSchemaFactory().newSchema(
					new StreamSource(file.open()));
			if (ns != null)
				SchemaResolver.getInstance().registerSchema(ns, file);
			return s;
		} catch (SAXException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static final Schema
		bigraph, sortset, formationrules, signature, rule, spec, edit, sorting;
	static {
		sortset = tryOpenRegister(BigmNamespaceConstants.SORTSET,
				"/resources/schema/sortset.xsd");
		formationrules = tryOpenRegister(BigmNamespaceConstants.FORMATIONRULES,
				"/resources/schema/formationrules.xsd");
		signature = tryOpenRegister(BigmNamespaceConstants.SIGNATURE,
				"/resources/schema/signature.xsd");
		bigraph = tryOpenRegister(BigmNamespaceConstants.BIGRAPH,
				"/resources/schema/bigraph.xsd");
		edit = tryOpenRegister(BigmNamespaceConstants.EDIT,
				"/resources/schema/edit.xsd");
		rule = tryOpenRegister(BigmNamespaceConstants.RULE,
				"/resources/schema/rule.xsd");
		spec = tryOpenRegister(BigmNamespaceConstants.SPEC,
				"/resources/schema/spec.xsd");
		sorting = tryOpenRegister(BigmNamespaceConstants.SORTING,
				"/resources/schema/sorting.xsd");
	}
	
	/**
	 * Returns the shared {@link Schema} suitable for validating
	 * <code>bigraph</code> documents.
	 * @return a {@link Schema}
	 */
	public static Schema getBigraphSchema() {
		return bigraph;
	}
	
	/**
	 * Returns the shared {@link Schema} suitable for validating
	 * <code>sortset</code> documents.
	 * @return a {@link Schema}
	 */
	public static Schema getSortSetSchema() {
		return sortset;
	}

	/**
	 * Returns the shared {@link Schema} suitable for validating
	 * <code>signature</code> documents.
	 * @return a {@link Schema}
	 */
	public static Schema getSignatureSchema() {
		return signature;
	}

	/**
	 * Returns the shared {@link Schema} suitable for validating
	 * <code>formationrules</code> documents.
	 * @return a {@link Schema}
	 */
	public static Schema getFormRulesSchema() {
		return formationrules;
	}

	/**
	 * Returns the shared {@link Schema} suitable for validating
	 * <code>rule</code> documents.
	 * @return a {@link Schema}
	 */
	public static Schema getRuleSchema() {
		return rule;
	}

	/**
	 * Returns the shared {@link Schema} suitable for validating
	 * <code>spec</code> documents.
	 * @return a {@link Schema}
	 */
	public static Schema getSpecSchema() {
		return spec;
	}

	/**
	 * Returns the shared {@link Schema} suitable for validating
	 * <code>edit</code> documents.
	 * @return a {@link Schema}
	 */
	public static Schema getEditSchema() {
		return edit;
	}
	
	/**
	 * Returns the shared {@link Schema} suitable for validating
	 * <code>sorting</code> documents.
	 * @return a {@link Schema}
	 */
	public static Schema getSortingSchema() {
		return sorting;
	}

}
