package dk.itu.bigm.utilities;

import java.io.File;
import java.io.FileInputStream;
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

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bigraph.model.Control;
import org.bigraph.model.FormRules;
import org.bigraph.model.FormationRule;
import org.bigraph.model.PlaceSort;
import org.bigraph.model.Signature;
import org.bigraph.model.SortSet;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CommonFuncUtilities {
	
	public static void copyHashMapToHashMap(HashMap<String, String> source, HashMap<String, String> target){
		target.clear();
		Iterator<String> iterator = source.keySet().iterator();
		while(iterator.hasNext()){
			String keyStr = iterator.next();
			String desStr = source.get(keyStr);
			target.put(keyStr, desStr);
		}
	}
	
	public static void copyHashMapToList(HashMap<String, String> source, List<String[]> target, String category){
		Iterator<String> iterator = source.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			String value = source.get(key);
			target.add(new String[]{category + key, value});
		}
	}
	
	public static void copyHashMapKeyToList(HashMap<String, String> map, List<String> list){
		Iterator<String> iterator = map.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			list.add(key);
		}
	}
	
	public static void copyListKeyToList(List<String[]> keyList, List<String> list){
		for(int i = 0; i < keyList.size(); i++){
			list.add(keyList.get(i)[0]);
		}
	}
	
	public static void copyListToList(List<String[]> source, List<String[]> target){
		for(int i = 0; i < source.size(); i++){
			target.add(new String[]{source.get(i)[0], source.get(i)[1]});
		}
	}
	
	public static void copyListToList(List<String[]> source, List<String[]> target, String category){
		for(int i = 0; i < source.size(); i++){
			target.add(new String[]{category + source.get(i)[0], source.get(i)[1]});
		}
	}
	
	public static void copyListToArray(List<String> list, String[] array){
		for(int i = 0; i < list.size(); i++){
			array[i + 1] = list.get(i);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<List<String>> transformDataStructure(Object[] objs, String[] seq){
		List<List<String>> lists = new ArrayList<List<String>>();
		for(int i = 0; i < objs.length; i++){
			if(objs[i] instanceof List){
				List<String> list = new ArrayList<String>();
				list.add(seq[i]);
				copyListKeyToList((List<String[]>)objs[i], list);
				lists.add(list);
			}
			if(objs[i] instanceof HashMap){
				List<String> list = new ArrayList<String>();
				list.add(seq[i]);
				copyHashMapKeyToList((HashMap<String, String>)objs[i], list);
				lists.add(list);
			}
		}
		return lists;	
	}
	
	@SuppressWarnings("unchecked")
	public static void refreshListContent(Object[] objs, List<String[]> list){
		list.clear();
		for(int i = 0; i < objs.length; i++){
			if(objs[i] instanceof List){
				copyListToList((List<String[]>)objs[i], list);
			}
			if(objs[i] instanceof HashMap){
				copyHashMapToList((HashMap<String, String>)objs[i], list, "");
			}
		}	
	}
	
	@SuppressWarnings("unchecked")
	public static void refreshPrefsContent(Object[] objs, String[] strs, List<String[]> list){
		list.clear();
		for(int i = 0; i < objs.length; i++){
			if(objs[i] instanceof List){
				copyListToList((List<String[]>)objs[i], list, strs[i]);
			}
			if(objs[i] instanceof HashMap){
				copyHashMapToList((HashMap<String, String>)objs[i], list, strs[i]);
			}
		}	
	}
	
	@SuppressWarnings("unchecked")
	public static void readExcelFile(String filePath, String sheetName, Object obj){
		try {
			if(obj instanceof HashMap){
				CommonFuncUtilities.readConfigFileToHashMap(filePath, sheetName, (HashMap<String, String>)obj);	
			}
			else if(obj instanceof List){
				CommonFuncUtilities.readConfigFileToList(filePath, sheetName, (List<String>)obj);	
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readConfigFileToHashMap(String fileName, String sheetName, HashMap<String, String> map) throws FileNotFoundException, IOException{
		XSSFWorkbook  wb = new XSSFWorkbook(new FileInputStream(new File(fileName)));
		XSSFSheet sheet = wb.getSheet(sheetName);
		for(int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++){
			XSSFRow row = sheet.getRow(i);
			XSSFCell key = row.getCell(0);
			XSSFCell value = row.getCell(1);
			map.put(key.getStringCellValue().trim(), value.getStringCellValue().trim());
		}
	}
	
	public static void readConfigFileToList(String fileName, String sheetName, List<String> list) throws FileNotFoundException, IOException{
		XSSFWorkbook  wb = new XSSFWorkbook(new FileInputStream(new File(fileName)));
		XSSFSheet sheet = wb.getSheet(sheetName);
		for(int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++){
			XSSFRow row = sheet.getRow(i);
			XSSFCell key = row.getCell(0);
			list.add(key.getStringCellValue().trim());
		}
	}
	
	public static void drawTableWithCheck(Composite parent, Table keyTable, HashMap<String, String> currentMap, HashMap<String, String> upperMap){
		keyTable.setHeaderVisible(true);
		keyTable.setLinesVisible(true);
		TableLayout tbLayout = new TableLayout();
		keyTable.setLayout(tbLayout);
		keyTable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//set table header
		tbLayout.addColumnData(new ColumnWeightData(10));
		TableColumn id = new TableColumn(keyTable, SWT.NONE);
		id.setText("");
		tbLayout.addColumnData(new ColumnWeightData(60));
		TableColumn key = new TableColumn(keyTable, SWT.NONE);
		key.setText("Key");
		tbLayout.addColumnData(new ColumnWeightData(80));
		TableColumn des = new TableColumn(keyTable, SWT.NONE);
		des.setText("Description");
		
		Iterator<String> currentIt = currentMap.keySet().iterator();
		while(currentIt.hasNext()){
			final TableItem tbItem = new TableItem(keyTable, 0);
			String entryKey = currentIt.next();
			if(upperMap.containsKey(entryKey)){
				tbItem.setChecked(true);
			}
			tbItem.setText(new String[]{"", entryKey, currentMap.get(entryKey)});	
		}
	}
	
	public static void drawTableWithoutCheck(Composite parent, Table keyTable){
		keyTable.setHeaderVisible(true);
		keyTable.setLinesVisible(true);
		TableLayout tbLayout = new TableLayout();
		keyTable.setLayout(tbLayout);
		keyTable.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		//set table header
		tbLayout.addColumnData(new ColumnWeightData(60));
		TableColumn key = new TableColumn(keyTable, SWT.NONE);
		key.setText("Key");
		tbLayout.addColumnData(new ColumnWeightData(80));
		TableColumn des = new TableColumn(keyTable, SWT.NONE);
		des.setText("Description");
	}
	
	public static void drawTableWithoutCheck(Composite parent, Table keyTable, List<String[]> keyList){
		keyTable = new Table(parent, SWT.MULTI|SWT.BORDER|SWT.FULL_SELECTION);
		drawTableWithoutCheck(parent, keyTable);

		for(int i = 0; i < keyList.size(); i++) {
			String keyEntry = keyList.get(i)[0];
			String value = keyList.get(i)[1];
			final TableItem tbItem = new TableItem(keyTable, 0);
			tbItem.setText(new String[]{keyEntry, value});
		}		
	}
	
	public static void buildSortingConfigXMLDoc(String filePath, SortSet sortSet, Signature signature, FormRules formRules){
		Document sortingConfigXML = null;
		try {
			sortingConfigXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			sortingConfigXML.setDocumentURI("http://www.itu.dk/research/pls/xmlns/2010/sorting");
			sortingConfigXML.setXmlVersion("1.0");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Element root = sortingConfigXML.createElement("Sorting");
		sortingConfigXML.appendChild(root);
		
		createPlaceSortElement(sortingConfigXML, root, sortSet, signature);
		createFormationRuleElement(sortingConfigXML, root, formRules);
		
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer trans = null;
		try {
			trans = transFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		
		DOMSource domSource = new DOMSource(sortingConfigXML);
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
	
	public static void createPlaceSortElement(Document doc, Element root, SortSet sortSet, Signature signature){
		for(PlaceSort ps : sortSet.getPlaceSorts()){
			Element psElement = doc.createElement("PlaceSort");
			root.appendChild(psElement);
			psElement.setAttribute("name", ps.getName());
			for(Control c : signature.getControls()){
				if(c.getPlaceSort().equals(ps.getName())){
					Element cElement = doc.createElement("Control");
					cElement.setAttribute("name", c.getName());
					psElement.appendChild(cElement);				
				}
			}
		}
	}
	
	public static void createFormationRuleElement(Document doc, Element root, FormRules formRules){
		Element cstElement = doc.createElement("Constraints");
		root.appendChild(cstElement);
		for(FormationRule fr : formRules.getFormationRules()){
			if(fr.getType().contains("Place Sorting")){
				Element pscElement = doc.createElement("Place Constraint");
				cstElement.appendChild(pscElement);
				if(fr.getConstraint().contains("Child")){
					pscElement.setAttribute("name", "in");
					pscElement.setAttribute("parent", fr.getSort2());
					pscElement.setAttribute("child", fr.getSort1());				
				} else if(fr.getConstraint().contains("Parent")){
					pscElement.setAttribute("name", "in");
					pscElement.setAttribute("parent", fr.getSort1());
					pscElement.setAttribute("child", fr.getSort2());				
				}		
			}
		}
	}
	
}
