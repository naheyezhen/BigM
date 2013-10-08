package dk.itu.bigm.model;

import java.util.HashMap;

import org.eclipse.core.runtime.Platform;

import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.utilities.CommonFuncUtilities;

public class LinkSortKey {
	
	/**
	 * keys for UML
	 */
	public static final String FILEPATH_UML = Platform.getBundle(BigMPlugin.PLUGIN_ID).getLocation().replace("initial@reference:file:", "") + "resources/doc/excel/uml.xlsx";
	public static final String SHEET_UML = "UML";
	public static final HashMap<String, String> keysForUML = new HashMap<String, String>();
	static{
		CommonFuncUtilities.readExcelFile(FILEPATH_UML, SHEET_UML, keysForUML);
	}
	
	/**
	 * keys for OWL
	 */
	public static final String FILEPATH_OWL = Platform.getBundle(BigMPlugin.PLUGIN_ID).getLocation().replace("initial@reference:file:", "") + "resources/doc/excel/owl.xlsx";
	public static final String SHEET_OWLCLASS = "Class";
	public static final String SHEET_OWLIND = "Individual";
	public static final String SHEET_OWLPRO = "Property";
	
	public static final HashMap<String, String> keysForOWLClass = new HashMap<String, String>();
	static{
		CommonFuncUtilities.readExcelFile(FILEPATH_OWL, SHEET_OWLCLASS, keysForOWLClass);
	}
	
	public static final HashMap<String, String> keysForOWLInd = new HashMap<String, String>();
	static{
		CommonFuncUtilities.readExcelFile(FILEPATH_OWL, SHEET_OWLIND, keysForOWLInd);
	}
	
	public static final HashMap<String, String> keysForOWLPro = new HashMap<String, String>();
	static{
		CommonFuncUtilities.readExcelFile(FILEPATH_OWL, SHEET_OWLPRO, keysForOWLPro);
	}
	
	/**
	 * keys for XML
	 */
	public static final String FILEPATH_XML = Platform.getBundle(BigMPlugin.PLUGIN_ID).getLocation().replace("initial@reference:file:", "") + "resources/doc/excel/xml.xlsx";
	public static final String SHEET_XMLPRI = "Primitive";
	public static final String SHEET_XMLDER = "Derived";
	
	public static final HashMap<String, String> keysForXMLPri = new HashMap<String, String>();
	static{
		CommonFuncUtilities.readExcelFile(FILEPATH_XML, SHEET_XMLPRI, keysForXMLPri);
	}
	
	public static final HashMap<String, String> keysForXMLDer = new HashMap<String, String>();
	static{
		CommonFuncUtilities.readExcelFile(FILEPATH_XML, SHEET_XMLDER, keysForXMLDer);
	}
	
	/**
	 * keys for SWRL
	 */
	public static final String FILEPATH_SWRL = Platform.getBundle(BigMPlugin.PLUGIN_ID).getLocation().replace("initial@reference:file:", "") + "resources/doc/excel/swrl.xlsx";
	public static final String SHEET_SWRLBOOL = "Booleans";
	public static final String SHEET_SWRLSTRING = "Strings";
	public static final String SHEET_SWRLLIST = "Lists";
	public static final String SHEET_SWRLDTD = "Date_Time_Duration";
	public static final String SHEET_SWRLCMP= "Comparisons";
	public static final String SHEET_SWRLMATH = "Math";
	
	public static final HashMap<String, String> keysForSWRLBool = new HashMap<String, String>();
	static{
		CommonFuncUtilities.readExcelFile(FILEPATH_SWRL, SHEET_SWRLBOOL, keysForSWRLBool);
	}
	
	public static final HashMap<String, String> keysForSWRLString = new HashMap<String, String>();
	static{
		CommonFuncUtilities.readExcelFile(FILEPATH_SWRL, SHEET_SWRLSTRING, keysForSWRLString);
	}
	
	public static final HashMap<String, String> keysForSWRLList = new HashMap<String, String>();
	static{
		CommonFuncUtilities.readExcelFile(FILEPATH_SWRL, SHEET_SWRLLIST, keysForSWRLList);
	}
	
	public static final HashMap<String, String> keysForSWRLDTD = new HashMap<String, String>();
	static{
		CommonFuncUtilities.readExcelFile(FILEPATH_SWRL, SHEET_SWRLDTD, keysForSWRLDTD);
	}
	
	public static final HashMap<String, String> keysForSWRLCmp = new HashMap<String, String>();
	static{
		CommonFuncUtilities.readExcelFile(FILEPATH_SWRL, SHEET_SWRLCMP, keysForSWRLCmp);
	}
	
	public static final HashMap<String, String> keysForSWRLMath = new HashMap<String, String>();
	static{
		CommonFuncUtilities.readExcelFile(FILEPATH_SWRL, SHEET_SWRLMATH, keysForSWRLMath);
	}
	
}
