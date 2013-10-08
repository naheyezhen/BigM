package dk.itu.bigm.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;

import dk.itu.bigm.analysis.element.EFSMUnitAnalysisElement;
import dk.itu.bigm.analysis.element.PathDefAnalysisElement;
import dk.itu.bigm.analysis.element.Define;

public class GenerateAnalysisReport {
	
	@SuppressWarnings("deprecation")
	public static void buildAnalysisReportExcel(String directory, String fileName, ArrayList<EFSMUnitAnalysisElement> efsmUnitAnalysisEle, ArrayList<Define> defs, ArrayList<PathDefAnalysisElement> pathdefAnalysisEle){
		HSSFWorkbook wb = new HSSFWorkbook();
		
		//font
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		font.setFontHeightInPoints((short)16);
		//head style
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment((short)16);
		style.setBorderBottom((short)16);
		style.setFont(font);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		
		//coverage style
		HSSFCellStyle align_right_style = wb.createCellStyle();
		align_right_style.setAlignment((short)16);
		align_right_style.setBorderBottom((short)16);
		align_right_style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		
		//create excel for detail information of efsm unit test
		ArrayList<String> efsm_head = new ArrayList<String>();
		efsm_head.add("Path");
		efsm_head.add("State_Sequence");
		efsm_head.add("Variables");
		efsm_head.add("Data");
		efsm_head.add("State_Cov_Rate");
		
		HSSFSheet efsm_sheet = wb.createSheet("EFSM_UnitTest_Result");
		
		HSSFRow efsmrow = efsm_sheet.createRow((short)0);
		efsmrow.setHeightInPoints((short)30);
		for(int j = 0; j < efsm_head.size(); j++){
			HSSFCell cell = efsmrow.createCell((short)(j));
			cell.setCellStyle(style);
			cell.setCellValue(efsm_head.get(j));
		}
		
		int offset = 1;
		for (int u = 0; u < efsmUnitAnalysisEle.size(); u++) {
			int varSize = efsmUnitAnalysisEle.get(u).getVarList().size();
			
			for (int i = offset; i < offset + varSize; i++) {
				HSSFRow row = efsm_sheet.createRow((short) i);
				row.setHeightInPoints((short) 20);
				for (int j = 0; j < efsm_head.size(); j++) {
					HSSFCell cell = row.createCell((short) (j));
					switch (j) {
					case 0:
						if (i == offset) {
							cell.setCellValue(efsmUnitAnalysisEle.get(u)
									.getId() + "");
						}
						break;
					case 1:
						if (i == offset) {
							String s = "";
							for(int si = 0; si < efsmUnitAnalysisEle.get(u).getTpath().size(); si++){
								s += efsmUnitAnalysisEle.get(u).getTpath().get(si).getIndex() + "-->";
								if(si / 10 > 0 && si % 10 == 0){
									s += "\r\n";
								}
							}
							cell.setCellValue(s);
						}
						break;
					case 2:
						ArrayList<String> var = efsmUnitAnalysisEle.get(u)
								.getVarList().get(i - offset);
						String s = var.get(0);
						cell.setCellValue(s);
						break;
					case 3:
						ArrayList<String> data = efsmUnitAnalysisEle.get(u)
						.getVarList().get(i - offset);
						String str = "";
						for (int m = 1; m < data.size(); m++) {
							str += data.get(m) + " ";
						}
						cell.setCellValue(str);
						break;
					case 4:
						if (i == offset) {
							cell.setCellStyle(align_right_style);
							cell.setCellValue(efsmUnitAnalysisEle.get(u)
									.getCoverageRate());
						}
						break;
					default:
						cell.setCellValue("");
						break;
					}
				}
			}
			
			efsm_sheet.addMergedRegion(new Region(offset, (short)0,
					(short) (offset + varSize - 1), (short) 0));
			efsm_sheet.addMergedRegion(new Region(offset, (short)1,
					(short) (offset + varSize - 1), (short)1));
			efsm_sheet.addMergedRegion(new Region(offset, (short)4,
					(short) (offset + varSize - 1), (short)4));

			offset += efsmUnitAnalysisEle.get(u).getVarList().size();
		}
		
		//create excel for detail information of defines
		ArrayList<String> def_head = new ArrayList<String>();
		def_head.add("Name");
		def_head.add("Term Description");
		HSSFSheet def_sheet = wb.createSheet("Def_Detail");
		for(int i = 0; i <= defs.size(); i++){
			HSSFRow row = def_sheet.createRow((short)i);
			if(i == 0){
				row.setHeightInPoints((short)30);
			} else {
				row.setHeightInPoints((short)20);
			}
			for(int j = 0; j < def_head.size(); j++){
				HSSFCell cell = row.createCell((short)(j));
				
				if(i == 0){
					cell.setCellStyle(style);
					cell.setCellValue(def_head.get(j));
				} else {	
					switch(j){				
					case 0:
						cell.setCellValue("def " + i);
						break;
					case 1:
						cell.setCellValue(defs.get(i-1).getTerm());
						break;
					default:
						cell.setCellValue("");
					break;
					}
				}
			}
		}
		
		//create excel for information of path/define/rule coverage
		ArrayList<String> path_def_head = new ArrayList<String>();
		path_def_head.add("Path");
		path_def_head.add("Model_Num");
		path_def_head.add("Defs");
		path_def_head.add("Rule_Cov_Rate");
		HSSFSheet path_def_sheet = wb.createSheet("Path_Def_RuleCoverage");
		for(int i = 0; i <= pathdefAnalysisEle.size(); i++){
			HSSFRow row = path_def_sheet.createRow((short)i);
			if(i == 0){
				row.setHeightInPoints((short)30);
			} else {
				row.setHeightInPoints((short)20);
			}
			for(int j = 0; j < path_def_head.size(); j++){
				HSSFCell cell = row.createCell((short)(j));
				
				if(i == 0){
					cell.setCellStyle(style);
					cell.setCellValue(path_def_head.get(j));
				} else {
					
					switch(j){				
					case 0:
						cell.setCellValue(pathdefAnalysisEle.get(i-1).getId()+"");
						break;
					case 1:
						cell.setCellStyle(align_right_style);
						cell.setCellValue(pathdefAnalysisEle.get(i-1).getModelNum());
						break;
					case 2:
						String defines = "";
						for(int di = 0; di < pathdefAnalysisEle.get(i-1).getDefs().size(); di++){
							defines += pathdefAnalysisEle.get(i-1).getDefs().get(di) + " ";
							if(di / 10 > 0 && di % 10 == 0){
								defines += "\r\n";
							}
						}
						cell.setCellValue(defines);
						break;
					case 3:
						cell.setCellStyle(align_right_style);
						cell.setCellValue(pathdefAnalysisEle.get(i-1).getCoverageRate());
						break;
					default:
						cell.setCellValue("");
					break;
					}
				}
			}
		}
		
		if (!(new File(directory).isDirectory())) {
			new File(directory).mkdir();
		}

		try {
			File target = new File(directory + fileName);
			FileOutputStream ft;
			ft = new FileOutputStream(target);
			wb.write(ft);
			ft.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
