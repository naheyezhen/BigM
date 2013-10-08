package dk.itu.bigm.analysis.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import dk.itu.bigm.analysis.element.Define;

public class DefsParser {
	
	public ArrayList<Define> readDefines(String fileName){
		ArrayList<Define> defineList = new ArrayList<Define>();
		String defines = readtxt(fileName);
		if(defines.contains("}")){
			String[] defs = defines.split("}");
			for(String def : defs){
				if(def.contains("{")){
					String[] defTerms = def.split("[{]");
					if(defTerms.length == 2){
						String term = defTerms[0];
						Define define = new Define(term);
						if(defTerms[1].contains(", ")){
							String[] pathIndexs = defTerms[1].split(", ");
							for(String index : pathIndexs){
								define.addPathIndex(Integer.parseInt(index));
							}
						}
						defineList.add(define);
					}
				}
			}
		}
		return defineList;
	}
	
	private String readtxt(String fileName){
		String result = "";	
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String r;
			r = br.readLine(); 
			while(r != null){
				result += r;
				r = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println("can not open the file:"+e);
		} catch (IOException e) {
			System.out.println("can not read the file:"+e);
		}			
		return result;
	}
	
	public static void main(String[] args){
		String defs = "E:\\bigred-workspace\\big-red-master\\plugins\\dk.itu.big_red\\resources\\doc\\dpath.txt";
		DefsParser ddp = new DefsParser();
		ArrayList<Define> defineList = ddp.readDefines(defs);
		for(Define def : defineList){
			System.out.print("Term: " + def.getTerm() + " Path Index: ");
			for(int i : def.getPathIndex()){
				System.out.print(i + " ");
			}
			System.out.println("");
		}
	}
}
