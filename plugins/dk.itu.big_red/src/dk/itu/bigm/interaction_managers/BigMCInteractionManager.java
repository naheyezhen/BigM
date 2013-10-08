package dk.itu.bigm.interaction_managers;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.interaction_managers.InteractionManager;

public class BigMCInteractionManager extends InteractionManager {
	private Text stepText;
	private int stepCount;
	private Button sortingButton;
	private Button patternsButton;
	private Button allDefineButton;
	private Button noneButton;
	private boolean sorting = false;
	private boolean patterns = false;
	private boolean allDefine = false;
	private boolean none = true;
	
	private class StepCountListener extends SelectionAdapter
	implements FocusListener {
		@Override
		public void focusGained(FocusEvent e) {
			update();
		}

		@Override
		public void focusLost(FocusEvent e) {
			update();
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			update();
		}
		
		private void update() {
			try {
				stepCount = Integer.parseInt(stepText.getText());
			} catch (NumberFormatException e) {
				stepCount = 1000;
			}
			stepText.setText(Integer.toString(stepCount));
		}
	}
	
	private class OptionsDialog extends Dialog {
		protected OptionsDialog(Shell parentShell) {
			super(parentShell);
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			Composite c = new Composite(parent, SWT.NONE);
			c.setLayout(new GridLayout(2, false));
			c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			new Label(c, SWT.NONE).setText("Number of steps:");
			
			StepCountListener scl = new StepCountListener();
			stepText = new Text(c, SWT.BORDER);
			stepText.addFocusListener(scl);
			stepText.addSelectionListener(scl);
			new Label(c, SWT.NONE).setText("Check Strategy:");
			
			Group g = new Group(c, SWT.NONE);
			g.setLayout(new RowLayout());
			
			sortingButton = new Button(g, SWT.RADIO);
			sortingButton.setText("sorting");
			sortingButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(sortingButton.getSelection()){
						sorting = true;
					}else{
						sorting = false;
					}
				}
			});
			patternsButton = new Button(g, SWT.RADIO);
			patternsButton.setText("patterns");
			patternsButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(patternsButton.getSelection()){
						patterns = true;
					}else{
						patterns = false;
					}
				}
			});
			allDefineButton = new Button(g, SWT.RADIO);
			allDefineButton.setText("all define");
			allDefineButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(allDefineButton.getSelection()){
						allDefine = true;
					}else{
						allDefine = false;
					}
				}
			});
			noneButton = new Button(g, SWT.RADIO);
			noneButton.setText("none");
			noneButton.setSelection(true);
			noneButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(noneButton.getSelection()){
						none = true;
					}else{
						none = false;
					}
				}
			});
			
			return c;
		}
		
		@Override
		protected void buttonPressed(int buttonId) {
			try {
				stepCount = Integer.parseInt(stepText.getText());
			} catch (NumberFormatException nfe) {
				stepCount = 1000;
			}
			super.buttonPressed(buttonId);
		}
	}
	
	@Override
	public void run(Shell parent) {
		if (new OptionsDialog(parent).open() == Dialog.OK) {
			System.out.println("" + none + sorting + patterns);
			String systemPath = Platform.getBundle(BigMPlugin.PLUGIN_ID).getLocation().replace("initial@reference:file:", "");
			if(none){
				bigMCFunctionSupport(2, new String[]{"-m", stepCount + "",
						"-G", systemPath + "resources/doc/Pathes.txt",
						systemPath + "resources/doc/decoction.bgm"});
				MessageDialog.openInformation(parent, "BigMC", "Pathes.txt generated success!");
			}else if(sorting){
				bigMCFunctionSupport(1, new String[]{"-m", stepCount + "",
						"-G", systemPath + "resources/doc/testPathByBigMC.dot", 
						"-sf", systemPath + "resources/doc/SortingConstraint.xml", 
						systemPath + "resources/doc/decoction.bgm"});
				MessageDialog.openInformation(parent, "BigMC", "Pathes.txt generated success!");
			}else if(patterns){
				bigMCFunctionSupport(2, new String[]{"-m", stepCount + "",
						"-PF", systemPath + "resources/doc/Patterns.xml", 
						systemPath + "resources/doc/PatternsPathes.txt", 
						systemPath + "resources/doc/decoction.bgm"});
				MessageDialog.openInformation(parent, "BigMC", "Pathes.txt generated success!");
			}else if(allDefine){
				bigMCFunctionSupport(2, new String[]{"-D", "alldefs", 
						systemPath + "resources/doc/AllDefinePathes.txt", 
						systemPath + "resources/doc/definePath.txt", 
						systemPath + "resources/doc/decoction.bgm"});
			}
		}
	}
	
	public void bigMCFunctionSupport(int categoray, String... parameters){
		if(categoray == 1){
			int length = parameters.length;
			if(length == 0){
				System.out.println("no parameter(s)");
			} else {
				org.bigraph.bigmc.BigMC.main(parameters);
			}		
		} else{
			org.bigraph.bigmcDF.BigMC.main(parameters);
		}
	}
}