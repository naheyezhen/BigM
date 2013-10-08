package dk.itu.bigm.analysis.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PathsParser {
	List<String> ruleCoverages = new ArrayList<String>();
	
	public List<String[]> readContextModels(String fileName){
		List<String[]> contextModels = new ArrayList<String[]>();
		String models = readtxt(fileName);
		if(models.contains("%")){
			String[] cModels = models.split("%");
			for(String cModel : cModels){
				if(cModel.contains("{")){
					String[] indexPathCoverage = cModel.split("[{]");
					if(indexPathCoverage.length == 2){
						if(indexPathCoverage[1].contains("}")){
							String[] pathCoverage = indexPathCoverage[1].split("}");
							if(pathCoverage.length == 2){
								String[] contextM = readContextModel(pathCoverage[0]);
								contextModels.add(contextM);
								ruleCoverages.add(pathCoverage[1] + "%");
							}
						}
					}
				}
			}
		}
		return contextModels;
	}
	
	private String[] readContextModel(String contextModel){
		String[] result = null;
		if(contextModel.contains(";")){
			result = contextModel.split(";");
		}		
		return result;
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

	public List<String> getRuleCoverages() {
		return ruleCoverages;
	}
	
	public static void main(String[] args){
		String paths = "E:\\bigred-workspace\\big-red-master\\plugins\\dk.itu.big_red\\resources\\doc\\paths_new.txt";
		PathsParser bp = new PathsParser();
		bp.readContextModels(paths);
		List<String> ruleCoverages = bp.getRuleCoverages();
		for(int i = 0; i < ruleCoverages.size(); i++){
			String coverage = ruleCoverages.get(i);
			System.out.println("Index: " + i + " Coverage: " + coverage);
		}
	}
}
