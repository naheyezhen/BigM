package dk.itu.bigm.utilities.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dk.itu.bigm.model.LinkSortKey;
import dk.itu.bigm.utilities.CommonFuncUtilities;

import static dk.itu.bigm.preferences.owl.OWLPreferencePage.keySetOWL;
import static dk.itu.bigm.preferences.swrl.SWRLPreferencePage.keySetSWRL;
import static dk.itu.bigm.preferences.xml.XMLPreferencePage.keySetXML;

public class LinkSortPrefs {
	
	public static List<String[]> keySetInUse = new ArrayList<String[]>();
	public static HashMap<String, String> keySetUML = new HashMap<String, String>();
	public static HashMap<String, String> keySetClass = new HashMap<String, String>();
	public static HashMap<String, String> keySetInd = new HashMap<String, String>();
	public static HashMap<String, String> keySetPro = new HashMap<String, String>();
	public static HashMap<String, String> keySetBool = new HashMap<String, String>();
	public static HashMap<String, String> keySetString = new HashMap<String, String>();
	public static HashMap<String, String> keySetDTD = new HashMap<String, String>();
	public static HashMap<String, String> keySetList = new HashMap<String, String>();
	public static HashMap<String, String> keySetCmp = new HashMap<String, String>();
	public static HashMap<String, String> keySetMath = new HashMap<String, String>();
	public static HashMap<String, String> keySetPri = new HashMap<String, String>();
	public static HashMap<String, String> keySetDer = new HashMap<String, String>();
	public static HashMap<String, String> keySetUserDef = new HashMap<String, String>();
	public static HashMap<String, String> keySetUserDefExisted = new HashMap<String, String>();	

	
		
	public static void buildLinkSortPrefsXMLDoc(List<List<String>> linksortLists, HashMap<String, String> existedUserDef, String filePath){
		Document linksortDoc = null;
		try {
			linksortDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			linksortDoc.setDocumentURI("http://www.itu.dk/research/pls/xmlns/2010/linksort");
			linksortDoc.setXmlVersion("1.0");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Element root = linksortDoc.createElement("linksort");
		linksortDoc.appendChild(root);
		
		createSubCategory(linksortDoc, root, "UserDefExisted", existedUserDef);
		for(int i = 0; i < linksortLists.size(); i++){
			List<String> linksorts = linksortLists.get(i);
			String category = linksorts.get(0);
			linksorts.remove(0);
			createSubCategory(linksortDoc, root, category, linksorts);
		}
		
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer trans = null;
		try {
			trans = transFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		
		DOMSource domSource = new DOMSource(linksortDoc);
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		StreamResult xmlResult = new StreamResult(out);
		try {
			trans.transform(domSource, xmlResult);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	public static void createSubCategory(Document doc, Element root, String category, List<String> linksorts){
		Element ctgElement = doc.createElement("category");
		root.appendChild(ctgElement);
		ctgElement.setAttribute("name", category);
		for(String str : linksorts){
			Element ltElement = doc.createElement("sort");
			ltElement.setAttribute("name", str);
			ctgElement.appendChild(ltElement);
		}
	}
	
	public static void createSubCategory(Document doc, Element root, String category, HashMap<String, String> map){
		Element ctgElement = doc.createElement("category");
		root.appendChild(ctgElement);
		ctgElement.setAttribute("name", category);
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			String des = map.get(key);
			Element ltElement = doc.createElement("sort");
			ltElement.setAttribute("key", key);
			ltElement.setAttribute("des", des);
			ctgElement.appendChild(ltElement);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void loadLinkSort(String filePath) throws DocumentException, ParserConfigurationException{
		SAXReader reader = new SAXReader();
		org.dom4j.Document linksortDoc;
		File file = new File(filePath);
		if(!file.exists()){
			return;
		}
		linksortDoc = reader.read(file);
		
		List all = linksortDoc.selectNodes("/linksort/category/@name");
		Iterator it = all.iterator();
		while(it.hasNext()){
			Attribute upperTypeAttri = (Attribute)it.next();
			if(upperTypeAttri.getValue().equals("UML")){
				List list = linksortDoc.selectNodes("/linksort/category[@name='UML']/sort/@name");
				Iterator iterator = list.iterator();
				while(iterator.hasNext()){
					Attribute typeAttri = (Attribute)iterator.next();
					String key = typeAttri.getValue().toString();
					String des = LinkSortKey.keysForUML.get(key);
					keySetUML.put(key, des);
				}
			}
			if(upperTypeAttri.getValue().equals("OWL")){
				List list = linksortDoc.selectNodes("/linksort/category[@name='OWL']/sort/@name");
				Iterator iterator = list.iterator();
				while(iterator.hasNext()){
					Attribute typeAttri = (Attribute)iterator.next();
					String subCategory = typeAttri.getValue().toString().split("_")[0];
					String key = typeAttri.getValue().toString().split("_")[1];
					if(subCategory.equals("OWLClass")){
						String des = LinkSortKey.keysForOWLClass.get(key);
						keySetClass.put(key, des);
					}
					else if(subCategory.equals("OWLIndividual")){
						String des = LinkSortKey.keysForOWLInd.get(key);
						keySetInd.put(key, des);
					}
					else if(subCategory.equals("OWLProperty")){
						String des = LinkSortKey.keysForOWLPro.get(key);
						keySetPro.put(key, des);
					}
				}
				CommonFuncUtilities.refreshPrefsContent(new Object[]{keySetClass, keySetInd, keySetPro}, 
						new String[]{"OWLClass_", "OWLIndividual_", "OWLProperty_"}, keySetOWL);
			}
			if(upperTypeAttri.getValue().equals("XML")){
				List list = linksortDoc.selectNodes("/linksort/category[@name='XML']/sort/@name");
				Iterator iterator = list.iterator();
				while(iterator.hasNext()){
					Attribute typeAttri = (Attribute)iterator.next();
					String subCategory = typeAttri.getValue().toString().split("_")[0];
					String key = typeAttri.getValue().toString().split("_")[1];
					if(subCategory.equals("XMLPrimitive")){
						String des = LinkSortKey.keysForXMLPri.get(key);
						keySetPri.put(key, des);
					}
					else if(subCategory.equals("XMLDerived")){
						String des = LinkSortKey.keysForXMLDer.get(key);
						keySetDer.put(key, des);
					}
				}
				CommonFuncUtilities.refreshPrefsContent(new Object[]{keySetPri, keySetDer}, 
						new String[]{"XMLPrimitive_", "XMLDerived_"}, keySetXML);
			}
			if(upperTypeAttri.getValue().equals("SWRL")){
				List list = linksortDoc.selectNodes("/linksort/category[@name='SWRL']/sort/@name");
				Iterator iterator = list.iterator();
				while(iterator.hasNext()){
					Attribute typeAttri = (Attribute)iterator.next();
					String subCategory = typeAttri.getValue().toString().split("_")[0];
					String key = typeAttri.getValue().toString().split("_")[1];
					if(subCategory.equals("SWRLBool")){
						String des = LinkSortKey.keysForSWRLBool.get(key);
						keySetBool.put(key, des);
					}
					else if(subCategory.equals("SWRLString")){
						String des = LinkSortKey.keysForSWRLString.get(key);
						keySetString.put(key, des);
					}
					else if(subCategory.equals("SWRLList")){
						String des = LinkSortKey.keysForSWRLList.get(key);
						keySetList.put(key, des);
					}
					else if(subCategory.equals("SWRLCmp")){
						String des = LinkSortKey.keysForSWRLCmp.get(key);
						keySetCmp.put(key, des);
					}
					else if(subCategory.equals("SWRLMath")){
						String des = LinkSortKey.keysForSWRLMath.get(key);
						keySetMath.put(key, des);
					}
					else if(subCategory.equals("SWRLDTD")){
						String des = LinkSortKey.keysForSWRLDTD.get(key);
						keySetDTD.put(key, des);
					}
				}
				CommonFuncUtilities.refreshPrefsContent(new Object[]{keySetBool, keySetString, keySetList, keySetCmp, keySetMath, keySetDTD}, 
						new String[]{"SWRLBool_", "SWRLString_", "SWRLList_", "SWRLCmp_", "SWRLMath_", "SWRLDTD_"}, keySetSWRL);
			}
			if(upperTypeAttri.getValue().equals("UserDefExisted")){
				List list = linksortDoc.selectNodes("/linksort/category[@name='UserDefExisted']/sort");
				Iterator iterator = list.iterator();
				while(iterator.hasNext()){
					org.dom4j.Element e = (org.dom4j.Element)iterator.next();
					Attribute keyAttri = (Attribute) e.selectSingleNode("./@key");
					String key = keyAttri.getValue().toString();
					Attribute desAttri = (Attribute) e.selectSingleNode("./@des");
					String des = desAttri.getValue().toString();
					keySetUserDefExisted.put(key, des);
				}
			}
			if(upperTypeAttri.getValue().equals("UserDef")){
				List list = linksortDoc.selectNodes("/linksort/category[@name='UserDef']/sort/@name");
				Iterator iterator = list.iterator();
				while(iterator.hasNext()){
					Attribute typeAttri = (Attribute)iterator.next();
					String key = typeAttri.getValue().toString();
					String des = keySetUserDefExisted.get(key);
					keySetUserDef.put(key, des);
				}
			}
			
		}	
	}

}
