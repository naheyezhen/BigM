package dk.itu.bigm.analysis.element;

import java.util.ArrayList;
import java.util.List;

public class Define {
	private String term;
	private List<Integer> pathIndex;
	
	public Define(String term){
		this.term = term;
		this.pathIndex = new ArrayList<Integer>();
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public List<Integer> getPathIndex() {
		return pathIndex;
	}

	public void setPathIndex(List<Integer> pathIndex) {
		this.pathIndex = pathIndex;
	}
	
	public void addPathIndex(int index){
		this.pathIndex.add(index);
	}
}
