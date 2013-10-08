package dk.itu.bigm.analysis.element;

import java.util.ArrayList;
import java.util.LinkedList;

import model.efsm.State;

public class EFSMUnitAnalysisElement {
	private int id;
	private ArrayList<State> basePath;
	private LinkedList<State> tpath;
	private ArrayList<ArrayList<String>> varList;
	private String coverageRate;
	
	public EFSMUnitAnalysisElement(int id, ArrayList<State> basePath, LinkedList<State> tpath, ArrayList<ArrayList<String>> varList, String coverageRate){
		this.id = id;
		this.basePath = basePath;
		this.tpath = tpath;
		this.varList = varList;
		this.coverageRate = coverageRate;	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<State> getBasePath() {
		return basePath;
	}

	public void setBasePath(ArrayList<State> basePath) {
		this.basePath = basePath;
	}

	public LinkedList<State> getTpath() {
		return tpath;
	}

	public void setTpath(LinkedList<State> tpath) {
		this.tpath = tpath;
	}

	public ArrayList<ArrayList<String>> getVarList() {
		return varList;
	}

	public void setVarList(ArrayList<ArrayList<String>> varList) {
		this.varList = varList;
	}

	public String getCoverageRate() {
		return coverageRate;
	}

	public void setCoverageRate(String coverageRate) {
		this.coverageRate = coverageRate;
	}
}
