package dk.itu.bigm.editors.formation_rules;

import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bigraph.model.FormRules;
import org.bigraph.model.FormationRule;
import org.bigraph.model.LinkSort;
import org.bigraph.model.ModelObject;
import org.bigraph.model.NamedModelObject;
import org.bigraph.model.PlaceSort;
import org.bigraph.model.SortSet;
import org.bigraph.model.assistants.FileData;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.ChangeGroup;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.descriptors.BoundDescriptor;
import org.bigraph.model.loaders.LoadFailedException;
import org.bigraph.model.resources.IFileWrapper;
import org.bigraph.model.savers.FormRulesXMLSaver;
import org.bigraph.model.savers.SaveFailedException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.FileEditorInput;

import dk.itu.bigm.editors.AbstractNonGEFEditor;
import dk.itu.bigm.utilities.resources.EclipseFileWrapper;
import dk.itu.bigm.utilities.resources.ResourceTreeSelectionDialog;
import dk.itu.bigm.utilities.resources.ResourceTreeSelectionDialog.Mode;
import dk.itu.bigm.utilities.ui.ResourceSelector;
import dk.itu.bigm.utilities.ui.StockButton;
import dk.itu.bigm.utilities.ui.UI;
import dk.itu.bigm.utilities.ui.ResourceSelector.ResourceListener;

public class FormationRulesEditor extends AbstractNonGEFEditor
implements PropertyChangeListener {
	public static final String ID = "dk.itu.bigm.FormationRulesEditor";
	public static List<String> sortDataType = new ArrayList<String>();
	public static List<String> constraints = new ArrayList<String>();
	public static String[] typeStrings = {"", "Formation Rule for Place Sorting", "Formation Rule for Link Sorting"};
	
	public FormationRulesEditor() {
	}
	
	@Override
	public void doActualSave(IFile f, OutputStream os)
			throws SaveFailedException {
    	FormRulesXMLSaver r = new FormRulesXMLSaver().setModel(getModel());
		r.setFile(new EclipseFileWrapper(f)).
    		setOutputStream(os).exportObject();
		setSavePoint();
	}

	private FormRules model = null;
	
	@Override
	protected FormRules getModel() {
		return model;
	}
	
	private ResourceSelector sortsetSelector;
	private FormationRule currentRule;
	private TreeViewer rules;
	
	private Button embedFormRules, addRule, remove;
	private Text name;
	private Combo sort1, sort2, constraint, type;
	private String sort1Name, sort2Name, cString, tString;
	
	protected void setFormationRule(FormationRule fr) {
		if (currentRule != null)
			currentRule.removePropertyChangeListener(this);
		currentRule = fr;
		if (setNameAndTypeEnablement(fr != null) & setSortAndConsEnablement(fr != null)) {
			currentRule.addPropertyChangeListener(this);
			rulesToFields();
		}
	}
	private boolean uiUpdateInProgress = false;
	
	private FormationRule getSelectedRule() {
		Object s = ((IStructuredSelection)rules.getSelection()).
				getFirstElement();
		return (s instanceof FormationRule ? (FormationRule)s : null);
	}
	
	protected void rulesToFields() {
		uiUpdateInProgress = true;
		
		try {
			name.setText(currentRule.getName());
			if (setNameAndTypeEnablement(currentRule.getFormRules().equals(getModel()))) {
				name.setFocus();
				name.selectAll();
			}
			tString = currentRule.getType();
			if(setSortAndConsEnablement(!(checkTypeSelection() == 0))){
				setSortAndConsDataType(getModel().getSortSet());
				initSortSetAndConsItems();
				type.select(checkTypeSelection());
				sort1.select(sortDataType.indexOf(currentRule.getSort1()));
				sort2.select(sortDataType.indexOf(currentRule.getSort2()));
				constraint.select(constraints.indexOf(currentRule.getConstraint()));
			}
			if (getSelectedRule() != currentRule)
				rules.setSelection(
						new StructuredSelection(currentRule), true);
			setSortAndConsEnablement(true);
		} finally {
			uiUpdateInProgress = false;
		}
	}
	
	private void lockedTextUpdate(Text t, String newValue) {
		boolean oldUI = uiUpdateInProgress;
		uiUpdateInProgress = true;
		try {
			t.setText(newValue);
		} finally {
			uiUpdateInProgress = oldUI;
		}
	}
	
	/**
	 * Indicates whether or not changes made to the UI should be propagated
	 * to the current {@link Sort}.
	 * @return <code>true</code> if the {@link Sort} is valid and is not
	 * itself currently changing the UI, or <code>false</code> otherwise
	 */
	private boolean shouldPropagateUI() {
		return (!uiUpdateInProgress && currentRule != null);
	}
	
	private static final IChange changeRuleName(FormationRule fr, String str) {
		if (fr != null && str != null) {
			ChangeGroup cg = new ChangeGroup();
			cg.add(new BoundDescriptor(fr.getFormRules(),
					new NamedModelObject.ChangeNameDescriptor(
							fr.getIdentifier(), str)));
			return cg;
		} else return null;
	}
	
	@Override
	public void createEditorControl(Composite parent) {
		setSortAndConsDataType(getModel().getSortSet());
		Composite self = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(3, false);
		gl.marginTop = gl.marginLeft = gl.marginBottom = gl.marginRight = 
			gl.horizontalSpacing = gl.verticalSpacing = 10;
		self.setLayout(gl);
		
		abstract class TextListener implements SelectionListener, FocusListener {
			abstract void go();
			
			@Override
			public void focusGained(FocusEvent e) {
				/* nothing */
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (shouldPropagateUI())
					go();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				/* nothing */
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (shouldPropagateUI())
					go();
			}
			
		}
		
		Label l = new Label(self, SWT.RIGHT);
		l.setText("Sort Set:");
		sortsetSelector = new ResourceSelector(self,
			((FileEditorInput)getEditorInput()).getFile().getProject(),
			Mode.FILE, SortSet.CONTENT_TYPE);
		new Label(self, SWT.NONE);
		sortsetSelector.getButton().setLayoutData(
			new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		sortsetSelector.addListener(new ResourceListener() {
			@Override
			public void resourceChanged(IResource oldValue, IResource newValue) {
				if (uiUpdateInProgress)
					return;
				try {
					SortSet s = (newValue != null ?
						(SortSet)new EclipseFileWrapper((IFile)newValue).load() : null);
					doChange(new BoundDescriptor(getModel(),
							new FormRules.ChangeSetSortSetDescriptor(
									new FormRules.Identifier(),
									getModel().getSortSet(), s)));
					setSortAndConsDataType(s);
				} catch (LoadFailedException ife) {
					ife.printStackTrace();
				}
			}
		});	
		
		/* top_left part of formation rules editor */
		Composite left = new Composite(self, 0);
		left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout leftLayout = new GridLayout(1, false);
		left.setLayout(leftLayout);
		
		rules = new TreeViewer(left);
		rules.setComparator(new ViewerComparator() {
			@Override
			public int category(Object element) {
				return (element instanceof SortSet ? 1 :
						element instanceof PlaceSort ? 2 : 0);
			}
			
			@Override
			public boolean isSorterProperty(Object element, String property) {
				return (element instanceof PlaceSort &&
						PlaceSort.PROPERTY_NAME.equals(property));
			}
		});
		rules.setContentProvider(
				new FormRulesContentProvider(rules));
		rules.setLabelProvider(new FormRulesLabelProvider());
		GridData sortsLayoutData =
			new GridData(SWT.FILL, SWT.FILL, true, true);
		sortsLayoutData.widthHint = 80;
		rules.getTree().setLayoutData(sortsLayoutData);
		rules.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				type.select(0);
				sort1.select(0);
				sort2.select(0);
				constraint.select(0);
				setFormationRule(getSelectedRule());
			}
		});
	
		Composite controlButtons = new Composite(left, SWT.NONE);
		RowLayout controlButtonsLayout = new RowLayout();
		controlButtons.setLayout(controlButtonsLayout);
		controlButtons.setLayoutData(new GridData(SWT.END, SWT.TOP, true, false));
		
		embedFormRules = StockButton.OPEN.create(controlButtons, SWT.NONE);
		embedFormRules.setText("&Import...");
		embedFormRules.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ResourceTreeSelectionDialog rtsd =
						new ResourceTreeSelectionDialog(
								getSite().getShell(),
								getFile().getProject(),
								Mode.FILE, FormRules.CONTENT_TYPE);
				if (rtsd.open() == Dialog.OK) {
					try {
						IFile f = (IFile)rtsd.getFirstResult();
						doChange(new BoundDescriptor(getModel(),
								new FormRules.ChangeAddFormRulesDescriptor(
										new FormRules.Identifier(),
										-1,
										(FormRules)new EclipseFileWrapper(f).
												load())));
					} catch (LoadFailedException ex) {
						return;
					}
				}
			}
		});
		
		addRule = StockButton.ADD.create(controlButtons);
		addRule.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FormationRule fr = new FormationRule();
				type.select(0);
				sort1.select(0);
				sort2.select(0);
				constraint.select(0);
				doChange(getModel().changeAddFormationRule(fr,
						getModel().getNamespace().getNextName(), "", "", "", ""));
				rules.setSelection(new StructuredSelection(fr), true);
			}
		});
		
		remove = StockButton.REMOVE.create(controlButtons);
		remove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Iterator<?> it =
					((IStructuredSelection)rules.getSelection()).iterator();
				ChangeGroup cg = new ChangeGroup();
				PropertyScratchpad context = new PropertyScratchpad();
				while (it.hasNext()) {
					Object i = it.next();
					IChange ch = null;
					if (i instanceof FormationRule) {
						FormationRule fr = (FormationRule)i;
						if (fr.getFormRules().equals(getModel()))
							cg.add(ch = fr.changeRemove());
					} else if (i instanceof FormRules) {
						FormRules s = (FormRules)i;
						if (s.getParent().equals(getModel()))
							cg.add(ch = new BoundDescriptor(getModel(),
									new FormRules.ChangeRemoveFormRulesDescriptor(
											new FormRules.Identifier(),
											getModel().getFormRuless(context).indexOf(s),
											s)));
					}
					context.executeChange(ch);
				}
				
				if (cg.size() > 0) {
					doChange(cg);
					rules.setSelection(StructuredSelection.EMPTY);
					setFormationRule(null);
				}
			}
		});		
		
		/* right part of formation rules editor */
		Composite right = new Composite(self, 0);
		right.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout rightLayout = new GridLayout(2, false);
		right.setLayout(rightLayout);
		
		TextListener nameListener = new TextListener() {
			@Override
			void go() {
				String n = currentRule.getName();
				if (!n.equals(name.getText()))
					if (!doChange(changeRuleName(
							currentRule, name.getText())))
						lockedTextUpdate(name, n);
			}
		};
		
		(new Label(right, SWT.NONE)).setText("Name:");
		name = new Text(right, SWT.BORDER);
		name.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		name.addSelectionListener(nameListener);
		name.addFocusListener(nameListener);
		
		new Label(right, SWT.NONE).setText("Type:");
		type = new Combo(right, SWT.FILL|SWT.BORDER);
		type.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		type.setItems(typeStrings);
		
		new Label(right, SWT.NONE).setText("Sort1:");
		sort1 = new Combo(right, SWT.BORDER);
		sort1.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		sort1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sort1Name = sort1.getItem(sort1.getSelectionIndex());
				doChange(currentRule.changeFormRuleSort1(sort1Name));
			}
				
		});
		
		new Label(right, SWT.NONE).setText("Sort2:");
		sort2 = new Combo(right, SWT.BORDER);
		sort2.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		sort2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				sort2Name = sort2.getItem(sort2.getSelectionIndex());
				doChange(currentRule.changeFormRuleSort2(sort2Name));
			}
				
		});
		
		type.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(type.getSelectionIndex() > 0){
					tString = typeStrings[type.getSelectionIndex()].trim();
					doChange(currentRule.changeFormRuleType(tString));
					setSortAndConsEnablement(true);
					setSortAndConsDataType(getModel().getSortSet());		
					initSortSetAndConsItems();					
				}
			}
		});
		
		new Label(right, SWT.NONE).setText("Constraint:");
		constraint = new Combo(right, SWT.BORDER);
		constraint.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		constraint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cString = constraint.getItem(constraint.getSelectionIndex()).trim();
				doChange(currentRule.changeFormRuleCons(cString));
			}
		});
	}
		
	private boolean setNameAndTypeEnablement(boolean enabled) {
		return UI.setEnabled(enabled, name, type);
	}
	
	private boolean setSortAndConsEnablement(boolean enabled) {
		return UI.setEnabled(enabled, sort1, sort2, constraint);
	}
	
	private int checkTypeSelection() {
		if(tString == null){
			return 0;
		}
		else if(tString.contains("Place Sorting")){
			return 1;
		}else if(tString.contains("Link Sorting")){
			return 2;
		}else{
			return 0;
		}
	}
	
	private void setSortAndConsDataType(SortSet ss){
		if(ss != null){
			if(checkTypeSelection() == 1 && !ss.getPlaceSorts().isEmpty()){
				sortDataType.clear();
				constraints.clear();
				constraints.add("");
				constraints.add("in");
				constraints.add("not in");
				if(ss.getPlaceSorts().size() > 0){
					sortDataType.add("");
					for(PlaceSort ps : ss.getPlaceSorts()){
						sortDataType.add(ps.getName());
					}						
				}
			}else if(checkTypeSelection() == 2 && !ss.getLinkSorts().isEmpty()){
				sortDataType.clear();
				constraints.clear();
				constraints.add("");
				constraints.add("link");
				constraints.add("not link");
				if(ss.getLinkSorts().size() > 0){
					sortDataType.add("");
					for(LinkSort ls : ss.getLinkSorts()){
						sortDataType.add(ls.getName());
					}						
				}
			}
		}	
	}
	
	private void initSortSetAndConsItems(){
		if(sortDataType.size() > 0){
			sort1.setItems(sortDataType.toArray(new String[sortDataType.size()]));			
			sort2.setItems(sortDataType.toArray(new String[sortDataType.size()]));		
		}
		if(constraints.size() > 0){
			constraint.setItems(constraints.toArray(new String[constraints.size()]));
		}
	}
	
	private void modelToControls() {
		uiUpdateInProgress = true;
		
		sortsetSelector.setResource(getFileFrom(model.getSortSet()));
		
		uiUpdateInProgress = false;
	}
	
	private static final IFile getFileFrom(ModelObject m) {
		IFileWrapper fw = FileData.getFile(m);
		return (fw instanceof EclipseFileWrapper ?
				((EclipseFileWrapper)fw).getResource() : null);
	}
	
	@Override
	protected void loadModel() throws LoadFailedException {
		model = (FormRules)loadInput();
	}
	
	@Override
	protected void updateEditorControl() {
		if (getError() != null)
			return;
		
		clearUndo();
		if (currentRule != null)
			currentRule.removePropertyChangeListener(this);
		
		rules.setInput(getModel());
		setFormationRule(null);
		modelToControls();
	}

	@Override
	public void dispose() {
		if (currentRule != null)
			currentRule.removePropertyChangeListener(this);
		model = null;
		super.dispose();
	}
	
	@Override
	public void setFocus() {
		super.setFocus();
		if (rules != null) {
			org.eclipse.swt.widgets.Control s = rules.getControl();
			if (!s.isDisposed())
				s.setFocus();
		}
		if (sortsetSelector != null) {
			Button b = sortsetSelector.getButton();
			if (b != null && !b.isDisposed() && b.isVisible())
				b.setFocus();
		}
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		Object newValue = evt.getNewValue();
		if (evt.getSource().equals(currentRule)) {
			if (uiUpdateInProgress)
				return;
			uiUpdateInProgress = true;
			try {
				if (PlaceSort.PROPERTY_NAME.equals(propertyName)) {
					name.setText((String)newValue);
				}
			} finally {
				uiUpdateInProgress = false;
			}
		}
	}

	@Override
	protected void createActions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void tryApplyChange(IChange c) throws ChangeRejectedException {
		getModel().tryApplyChange(c);
	}
	
	@Override
	public boolean doChange(IChange c) {
		return super.doChange(c);
	}
	
}
