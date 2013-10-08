package dk.itu.bigm.action_response;

import interleaving.client.EFSMModelClients;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import model.efsm.EFSMModel;
import model.efsm.State;
import model.efsm.parser.EFSMXMLParser;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import path.efsm.PathSet;

import data.efsm.DataGenerationByRandom;
import dk.itu.bigm.analysis.GenerateAnalysisReport;
import dk.itu.bigm.analysis.element.EFSMUnitAnalysisElement;
import dk.itu.bigm.analysis.element.PathDefAnalysisElement;
import dk.itu.bigm.analysis.element.Define;
import dk.itu.bigm.analysis.parser.DefsParser;
import dk.itu.bigm.analysis.parser.PathsParser;
import dk.itu.bigm.application.plugin.BigMPlugin;

public class TestingAndAnalysisBasedOnConfigDialogBak extends TitleAreaDialog {
	private Text dmFile;
	private Button browseDM;
	private int BROWSE_DM_ID = 1000;
	
	private Text efsmFile;
	private Button browseEFSM;
	private int BROWSE_EFSM_ID = 1001;
	
	private Button[] options = new Button[3];
	private Text[] files = new Text[3];
	private Button[] browse = new Button[3];
	private int[] BROWSE_ID = {3000, 3001, 3002};
	
	private Text[] inputFiles = new Text[4];
	private Button[] browseFiles = new Button[4];
	private int[] BROWSE_FILE_ID = {4000, 4001, 4002, 4003};
	
	private Button runAll;
	private int ALL_TEST_ID = 2004;
	
	int index = 0;
	
	private String systemPath = Platform.getBundle(BigMPlugin.PLUGIN_ID).getLocation().replace("initial@reference:file:", "");
	private String allImageFile = systemPath + "resources/icons/runall.gif";
	
	private String efsm = systemPath + "resources/doc/EFSMModel.smd.xml";
	private String paths = systemPath + "resources/doc/paths_new.txt";
	private String defs = systemPath + "resources/doc/dpath.txt";
	private String bgm = systemPath + "resources/doc/decoction.bgm";
	private String resDir = systemPath + "resources/doc/";	
	private String fileName = "AnalysisReport.xls";	
			
	
	public TestingAndAnalysisBasedOnConfigDialogBak(Shell shell) {
		// TODO Auto-generated constructor stub
		super(shell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.getShell().setText("Testing and Analysis");
		setTitle("Testing and Analysis Configuration");
		setMessage("Performing Certain Testing and Analysis Works based on Configuration");
		
		Label separation1 = new Label(parent, SWT.SEPARATOR|SWT.HORIZONTAL);
		separation1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridData threeGrid = new GridData(GridData.FILL_HORIZONTAL);
		threeGrid.horizontalSpan = 3;
		GridData fourGrid = new GridData(GridData.FILL_HORIZONTAL);
		fourGrid.horizontalSpan = 4;
		GridData fiveGrid = new GridData(GridData.FILL_HORIZONTAL);
		fiveGrid.horizontalSpan = 5;
		GridData sevenGrid = new GridData(GridData.FILL_HORIZONTAL);
		sevenGrid.horizontalSpan = 7;
		GridData eightGrid = new GridData(GridData.FILL_HORIZONTAL);
		eightGrid.horizontalSpan = 8;
		
		
		GridData buttonGrid = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonGrid.horizontalSpan = 1;
		
		GridData checkboxGrid = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		checkboxGrid.horizontalSpan = 1;
	
		Composite topComp = new Composite(parent, SWT.NONE);
		topComp.setLayout(new GridLayout());
		topComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group bigDataGen = new Group(topComp, SWT.NONE);		
		bigDataGen.setText("Generate Test Data for Bigraph Data Model");
		bigDataGen.setLayout(new GridLayout(4, false));
		bigDataGen.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(bigDataGen, SWT.NONE).setText("Bigraph Data Model File: ");
		dmFile = new Text(bigDataGen, SWT.BORDER|SWT.READ_ONLY);
		dmFile.setLayoutData(threeGrid);
		browseDM = createButton(bigDataGen, BROWSE_DM_ID, "Browse...", false);

		new Label(topComp, SWT.SEPARATOR|SWT.HORIZONTAL).
			setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
		Group efsmUnit = new Group(topComp, SWT.NONE);		
		efsmUnit.setText("Unit Test for EFSM Model");
		efsmUnit.setLayout(new GridLayout(4, false));
		efsmUnit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(efsmUnit, SWT.NONE).setText("EFSM Model File: ");
		efsmFile = new Text(efsmUnit, SWT.BORDER|SWT.READ_ONLY);
		efsmFile.setLayoutData(threeGrid);
		browseEFSM = createButton(efsmUnit, BROWSE_EFSM_ID, "Browse...", false);
		
		new Label(topComp, SWT.SEPARATOR|SWT.HORIZONTAL).
		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group bigMCforBigM = new Group(topComp, SWT.NONE);		
		bigMCforBigM.setText("Run BigMC for BigM");
		bigMCforBigM.setLayout(new GridLayout(5, false));
		bigMCforBigM.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		options[0] = new Button(bigMCforBigM, SWT.CHECK);
		options[0].setLayoutData(checkboxGrid);
		options[0].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				if(options[0].getSelection()){
					files[0].setEnabled(true);
					browse[0].setEnabled(true);
				} else{
					files[0].setEnabled(false);
					browse[0].setEnabled(false);
				}
			}
		});
		new Label(bigMCforBigM, SWT.NONE).setText("BGM File: ");
		files[0] = new Text(bigMCforBigM, SWT.BORDER|SWT.READ_ONLY);
		files[0].setLayoutData(fiveGrid);
		files[0].setEnabled(false);
		browse[0] = createButton(bigMCforBigM, BROWSE_ID[0], "Browse...", false);
		browse[0].setEnabled(false);
		
		options[1] = new Button(bigMCforBigM, SWT.CHECK);
		options[1].setLayoutData(checkboxGrid);
		options[1].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				if(options[1].getSelection()){
					files[1].setEnabled(true);
					browse[1].setEnabled(true);
				} else{
					files[1].setEnabled(false);
					browse[1].setEnabled(false);
				}
			}
		});
		new Label(bigMCforBigM, SWT.NONE).setText("Sorting Constraint File: ");
		files[1] = new Text(bigMCforBigM, SWT.BORDER|SWT.READ_ONLY);
		files[1].setLayoutData(fiveGrid);
		files[1].setEnabled(false);
		browse[1] = createButton(bigMCforBigM, BROWSE_ID[1], "Browse...", false);
		browse[1].setEnabled(false);
		
		options[2] = new Button(bigMCforBigM, SWT.CHECK);
		options[2].setLayoutData(checkboxGrid);
		options[2].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				if(options[2].getSelection()){
					files[2].setEnabled(true);
					browse[2].setEnabled(true);
				} else{
					files[2].setEnabled(false);
					browse[2].setEnabled(false);
				}
			}
		});
		new Label(bigMCforBigM, SWT.NONE).setText("EFSM Model for Patterns: ");
		files[2] = new Text(bigMCforBigM, SWT.BORDER|SWT.READ_ONLY);
		files[2].setLayoutData(fiveGrid);
		files[2].setEnabled(false);
		browse[2] = createButton(bigMCforBigM, BROWSE_ID[2], "Browse...", false);
		browse[2].setEnabled(false);
		
		new Label(bigMCforBigM, SWT.NONE).setLayoutData(sevenGrid);
		
		new Label(topComp, SWT.SEPARATOR|SWT.HORIZONTAL).
		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group modelInterleaving = new Group(topComp, SWT.NONE);		
		modelInterleaving.setText("Run Model Interleaving");
		modelInterleaving.setLayout(new GridLayout(5, false));
		modelInterleaving.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(modelInterleaving, SWT.NONE).setText("Bigraph Path File: ");
		inputFiles[0] = new Text(modelInterleaving, SWT.BORDER|SWT.READ_ONLY);
		inputFiles[0].setLayoutData(sevenGrid);
		browseFiles[0] = createButton(modelInterleaving, BROWSE_FILE_ID[0], "Browse...", false);
		
		new Label(modelInterleaving, SWT.NONE).setText("EFSM Model File: ");
		inputFiles[1] = new Text(modelInterleaving, SWT.BORDER|SWT.READ_ONLY);
		inputFiles[1].setLayoutData(sevenGrid);
		browseFiles[1] = createButton(modelInterleaving, BROWSE_FILE_ID[1], "Browse...", false);
		
		new Label(modelInterleaving, SWT.NONE).setText("Service Model File: ");
		inputFiles[2] = new Text(modelInterleaving, SWT.BORDER|SWT.READ_ONLY);
		inputFiles[2].setLayoutData(sevenGrid);
		browseFiles[2] = createButton(modelInterleaving, BROWSE_FILE_ID[2], "Browse...", false);
		
		new Label(modelInterleaving, SWT.NONE).setText("Process Service Petrinet Model: ");
		inputFiles[3] = new Text(modelInterleaving, SWT.BORDER|SWT.READ_ONLY);
		inputFiles[3].setLayoutData(sevenGrid);
		browseFiles[3] = createButton(modelInterleaving, BROWSE_FILE_ID[3], "Browse...", false);
		
		new Label(modelInterleaving, SWT.NONE).setLayoutData(eightGrid);

		new Label(topComp, SWT.SEPARATOR|SWT.HORIZONTAL).
		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group runAllGroup = new Group(topComp, SWT.NONE);		
		runAllGroup.setText("");
		runAllGroup.setLayout(new GridLayout(4, false));
		runAllGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(runAllGroup, SWT.NONE).setLayoutData(fourGrid);
		runAll = createButton(runAllGroup, ALL_TEST_ID, "", true);
		runAll.setLayoutData(buttonGrid);
		runAll.setImage(new Image(null, allImageFile));
		
		return parent;
	}
	
	public void buttonPressed(int buttonId){
		 if(buttonId == BROWSE_EFSM_ID){
			 browseEFSM.addSelectionListener(new SelectionAdapter(){
					@SuppressWarnings("deprecation")
					public void widgetSelected(SelectionEvent e){
						FileDialog file = new FileDialog(new Frame());	
						file.setTitle("please select target file");
						file.show();
						String path = file.getDirectory() + file.getFile();
						efsmFile.setText(path);
					}
			 });
		 } else if(buttonId == BROWSE_DM_ID){
			 browseDM.addSelectionListener(new SelectionAdapter(){
					@SuppressWarnings("deprecation")
					public void widgetSelected(SelectionEvent e){
						FileDialog file = new FileDialog(new Frame());	
						file.setTitle("please select target file");
						file.show();
						String path = file.getDirectory() + file.getFile();
						dmFile.setText(path);
					}
			 });
		 } else if(buttonId == 3000 || buttonId == 3001 || buttonId == 3002){
			 index = buttonId % 3000;
			 browse[index].addSelectionListener(new SelectionAdapter(){
					@SuppressWarnings("deprecation")
					public void widgetSelected(SelectionEvent e){
						FileDialog file = new FileDialog(new Frame());	
						file.setTitle("please select target file");
						file.show();
						String path = file.getDirectory() + file.getFile();
						files[index].setText(path);
					}
			 });		 
		 } else if(buttonId == 4000 || buttonId == 4001 || buttonId == 4002 || buttonId == 4003){
			 index = buttonId % 4000;
			 browseFiles[index].addSelectionListener(new SelectionAdapter(){
					@SuppressWarnings("deprecation")
					public void widgetSelected(SelectionEvent e){
						FileDialog file = new FileDialog(new Frame());	
						file.setTitle("please select target file");
						file.show();
						String path = file.getDirectory() + file.getFile();
						inputFiles[index].setText(path);
					}
			 });		 
		 } else if(buttonId == ALL_TEST_ID){
			 org.bigraph.bigmcDF.BigMC.main(new String[]{"-D", "alldefs", paths, defs, files[0].getText()});
			 buildWorkBook(resDir, fileName, efsm, paths, defs);
			 try {
					Runtime.getRuntime().exec("cmd.exe   /C   startup.bat ",
												null, 
												new File("D:\\Program Files\\tomcat\\bin"));
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				EFSMModelClients EFSMModelClients = new EFSMModelClients();
				EFSMModelClients.EFSMModelClientsRun();
				
				MessageDialog.openInformation(getParentShell(), "Model Interleaving Completed", "Interleaving Finished! Please check " + "C:\\Users\\chenjing\\Desktop\\modelinterleaving.txt" + " for details.");
				MessageDialog.openInformation(getParentShell(), "Analysis Completed", "Analysis Finished! Please check " + resDir + fileName + " for details.");
			 buttonPressed(OK);
		 } else if(buttonId == OK){
			 super.okPressed();
		 } else if(buttonId == CANCEL){
			 super.cancelPressed();
		 }
	}
	
	// resDir filename 用来保存分析结果
	// efsm "resources/doc/EFSMModel.smd.xml";
	// paths "resources/doc/paths_new.txt";
	// defs "resources/doc/dpath.txt";
	public void buildWorkBook(String resDir, String fileName, String efsm, String paths, String defs){
		//EFSM Unit Analysis Result
		ArrayList<EFSMUnitAnalysisElement> efsmUnitAnalysisEle = new ArrayList<EFSMUnitAnalysisElement>();
		EFSMXMLParser efsmParser = new EFSMXMLParser();
		EFSMModel efsmModel = efsmParser.parserXml(efsm);// 解析模型文件
		efsmModel.initStates();
		PathSet pathSet = new PathSet(efsmModel);// 路径集
		int basePathNum = pathSet.getPathList().size();
		DataGenerationByRandom dg = new DataGenerationByRandom();
		Random r = new Random();
		int pathsNum = r.nextInt(10);
		for(int i = 0; i < pathsNum; i++){
			int basePathIndex = r.nextInt(basePathNum);
			ArrayList<State> basePath = pathSet.getPathList().get(basePathIndex);
			LinkedList<State> tPath = pathSet.createRandomPathWithCycleByBase(pathSet.getPathList().get(basePathIndex));
			ArrayList<ArrayList<String>> varList = dg.dataGenerationForPath(efsmModel, pathSet, basePathIndex, tPath);
			double covRate = (double)basePath.size()/efsmModel.getStates().length*100;
			BigDecimal big =   new BigDecimal(covRate);  
			String coverageRate = big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() +"%";
			EFSMUnitAnalysisElement efsmUnit = new EFSMUnitAnalysisElement(i, basePath, tPath, varList, coverageRate);
			efsmUnitAnalysisEle.add(efsmUnit);
		}	
		
		//BigMC Path_Def Analysis Result
		ArrayList<PathDefAnalysisElement> analysisEle = new ArrayList<PathDefAnalysisElement>();
		PathsParser bp = new PathsParser();
		List<String[]> contextModels = bp.readContextModels(paths);
		List<String> ruleCoverages = bp.getRuleCoverages();
		DefsParser ddp = new DefsParser();
		ArrayList<Define> defineList = ddp.readDefines(defs);
		int pathNum = ruleCoverages.size();
		for(int i = 0; i < pathNum; i++){
			ArrayList<String> defines = new ArrayList<String>();
			for(int j = 0; j < defineList.size(); j++){
				if(defineList.get(j).getPathIndex().indexOf(i) != -1){
					defines.add("def " + j + ";");
				}
			}
			String modelNum = contextModels.get(i).length + "";
			analysisEle.add(new PathDefAnalysisElement(i, modelNum, defines, ruleCoverages.get(i)));
		}
		
		//generate analysis report
		GenerateAnalysisReport.buildAnalysisReportExcel(resDir, fileName, efsmUnitAnalysisEle, defineList, analysisEle);	
	}

}
