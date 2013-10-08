package dk.itu.bigm.analysis.element;

import java.util.ArrayList;

public class PathDefAnalysisElement {
	private int id;
	private String modelNum;
	private ArrayList<String> defs;
	private String coverageRate;
	
	public PathDefAnalysisElement(int id, String modelNum, ArrayList<String> defs, String coverageRate){
		this.id = id;
		this.modelNum = modelNum;
		this.defs = defs;
		this.coverageRate = coverageRate;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getModelNum() {
		return modelNum;
	}
	public void setModelNum(String modelNum) {
		this.modelNum = modelNum;
	}
	public ArrayList<String> getDefs() {
		return defs;
	}
	public void setDefs(ArrayList<String> defs) {
		this.defs = defs;
	}
	public String getCoverageRate() {
		return coverageRate;
	}
	public void setCoverageRate(String coverageRate) {
		this.coverageRate = coverageRate;
	}

}
