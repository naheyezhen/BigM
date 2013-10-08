package dk.itu.bigm.editors.signature;

import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bigraph.extensions.param.ParameterUtilities;
import org.bigraph.model.Control;
import org.bigraph.model.Control.Kind;
import org.bigraph.model.Control.PlaceSortKey;
import org.bigraph.model.ModelObject;
import org.bigraph.model.NamedModelObject;
import org.bigraph.model.PlaceSort;
import org.bigraph.model.Signature;
import org.bigraph.model.FormRules;
import org.bigraph.model.assistants.FileData;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.ChangeGroup;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.descriptors.BoundDescriptor;
import org.bigraph.model.loaders.LoadFailedException;
import org.bigraph.model.names.policies.BooleanNamePolicy;
import org.bigraph.model.names.policies.INamePolicy;
import org.bigraph.model.names.policies.LongNamePolicy;
import org.bigraph.model.names.policies.StringNamePolicy;
import org.bigraph.model.resources.IFileWrapper;
import org.bigraph.model.savers.SaveFailedException;
import org.bigraph.model.savers.SignatureXMLSaver;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.FileEditorInput;

import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.editors.AbstractNonGEFEditor;
import dk.itu.bigm.editors.assistants.IFactory;
import dk.itu.bigm.model.Colour;
import dk.itu.bigm.model.ColourUtilities;
import dk.itu.bigm.model.ControlUtilities;
import dk.itu.bigm.model.Ellipse;
import dk.itu.bigm.utilities.CommonFuncUtilities;
import dk.itu.bigm.utilities.resources.EclipseFileWrapper;
import dk.itu.bigm.utilities.resources.ResourceTreeSelectionDialog;
import dk.itu.bigm.utilities.resources.ResourceTreeSelectionDialog.Mode;
import dk.itu.bigm.utilities.ui.ResourceSelector;
import dk.itu.bigm.utilities.ui.StockButton;
import dk.itu.bigm.utilities.ui.UI;
import dk.itu.bigm.utilities.ui.ResourceSelector.ResourceListener;

public class SignatureEditor extends AbstractNonGEFEditor
implements PropertyChangeListener {
	public static final String ID = "dk.itu.bigm.SignatureEditor";
	public static final String DATA_FP = Platform.getBundle(BigMPlugin.PLUGIN_ID).getLocation().replace("initial@reference:file:", "") + "resources/doc/excel/datatype.xlsx";
	public static List<String> umlDataType = new ArrayList<String>();
	public static List<String> owlDataType = new ArrayList<String>();
	public static List<String> xmlDataType = new ArrayList<String>();
	public static List<String> udDataType = new ArrayList<String>();
	
	public SignatureEditor() {
	}
	
	@Override
	public void doActualSave(IFile f, OutputStream os)
			throws SaveFailedException {
		if(getModel().getFormRules() != null){
			SignatureXMLSaver r = new SignatureXMLSaver().setModel(getModel());
			r.setFile(new EclipseFileWrapper(f)).
			setOutputStream(os).exportObject();
			setSavePoint();
		} else{
			MessageDialog.openError(null, "No Formation Rules", "Save Failed. No Formation Rules Selected");
		}
		
	}

	private Signature model = null;
	
	@Override
	protected Signature getModel() {
		return model;
	}
	
	private ResourceSelector formRulesSelector;
	
	private org.bigraph.model.Control currentControl;
	
	private TreeViewer controls;
	private Button embedSignature, addControl, remove;
	
	private Text name, label;
	private SignatureEditorPolygonCanvas appearance;
	private Button ovalMode, polygonMode;
	private Button activeKind, atomicKind, passiveKind;
	private Button umlClass, umlAttribute, umlInstance;
	private Combo umlAttriType, umlInstType;
	private Button owlClass, owlIndividual, owlObjectProperty, owlDataProperty;
	private Combo proType;
	private Button xmlElement, xmlAttribute, xmlEntity, xmlPCDATA, xmlCDATA;
	private Combo xmlAttriType;
	private Button udSort;
	private Combo udNewSort;
	
	private Label
		sortLabel, appearanceDescription, kindLabel, labelLabel,
		outlineLabel, fillLabel, nameLabel, appearanceLabel;
	private ColorSelector outline, fill;
	
	protected void setControl(Control c) {
		if (currentControl != null)
			currentControl.removePropertyChangeListener(this);
		currentControl = c;
		if (setEnablement(c != null)) {
			currentControl.addPropertyChangeListener(this);
			controlToFields();
		}
	}
	
	private boolean uiUpdateInProgress = false;
	
	private Control getSelectedControl() {
		Object o = ((IStructuredSelection)controls.getSelection()).
				getFirstElement();
		return (o instanceof Control ? (Control)o : null);
	}
	
	protected void controlToFields() {
		uiUpdateInProgress = true;
		
		try {
			Object shape = ControlUtilities.getShape(currentControl);
			boolean polygon = (shape instanceof PointList);
			
			label.setText(ControlUtilities.getLabel(currentControl));
			name.setText(currentControl.getName());
			/* Don't allow controls from nested signatures to be edited */
			if (setEnablement(
					currentControl.getSignature().equals(getModel()))) {
				name.setFocus();
				name.selectAll();
			}
			appearance.setModel(currentControl);
			
			if (getSelectedControl() != currentControl)
				controls.setSelection(
						new StructuredSelection(currentControl), true);
			
			ovalMode.setSelection(!polygon);
			polygonMode.setSelection(polygon);
			
			outline.setColorValue(
					ColourUtilities.getOutline(currentControl).getRGB());
			fill.setColorValue(
					ColourUtilities.getFill(currentControl).getRGB());
			
			activeKind.setSelection(currentControl.getKind() == Kind.ACTIVE);
			atomicKind.setSelection(currentControl.getKind() == Kind.ATOMIC);
			passiveKind.setSelection(currentControl.getKind() == Kind.PASSIVE);
			
			deselectAllPlaceSorts();

			String currentPlaceSort = currentControl.getPlaceSort();
			
			umlClass.setSelection(currentPlaceSort.equals(PlaceSortKey.UML_CLASS));
			boolean isUmlAttri = (currentPlaceSort.contains(":"))? currentPlaceSort.split(":")[0]
					.equals(PlaceSortKey.UML_ATTRIBUTE) : false;
			umlAttribute.setSelection(isUmlAttri);
			if(isUmlAttri){
				umlAttriType.setEnabled(true);
				umlAttriType.select(umlDataType.indexOf(currentPlaceSort.split(":")[1]) + 1);
			}
			boolean isUmlInst = (currentPlaceSort.contains(":"))? currentPlaceSort.split(":")[0]
					.equals(PlaceSortKey.UML_INSTANCE) : false;
			umlInstance.setSelection(isUmlInst);
			if(isUmlInst){
				umlInstType.setEnabled(true);
				umlInstType.select(umlDataType.indexOf(currentPlaceSort.split(":")[1]) + 1);
			}
			
			owlClass.setSelection(currentPlaceSort.equals(PlaceSortKey.OWL_CLASS));
			owlIndividual.setSelection(currentPlaceSort.equals(PlaceSortKey.OWL_INDIVIDUAL));
			owlObjectProperty.setSelection(currentPlaceSort.equals(PlaceSortKey.OWL_OBJPROPERTY));
			boolean isDataPro = (currentPlaceSort.contains(":"))? currentPlaceSort.split(":")[0]
					.equals(PlaceSortKey.OWL_DATAPROPERTY) : false;
			owlDataProperty.setSelection(isDataPro);
			if(isDataPro){
				proType.setEnabled(true);
				proType.select(owlDataType.indexOf(currentPlaceSort.split(":")[1]) + 1);
			}
			
			xmlElement.setSelection(currentPlaceSort.equals(PlaceSortKey.XML_ELEMENT));
			xmlAttribute.setSelection(currentPlaceSort.equals(PlaceSortKey.XML_ATTRIBUTE));
			boolean isXmlAttri = (currentPlaceSort.contains(":"))? currentPlaceSort.split(":")[0]
					.equals(PlaceSortKey.XML_ATTRIBUTE) : false;
			xmlAttribute.setSelection(isXmlAttri);
			if(isXmlAttri){
				xmlAttriType.setEnabled(true);
				xmlAttriType.select(xmlDataType.indexOf(currentPlaceSort.split(":")[1]) + 1);
			}
			
			xmlEntity.setSelection(currentPlaceSort.equals(PlaceSortKey.XML_ENTITY));
			xmlPCDATA.setSelection(currentPlaceSort.equals(PlaceSortKey.XML_PCDATA));
			xmlCDATA.setSelection(currentPlaceSort.equals(PlaceSortKey.XML_CDATA));
			
			boolean isUserDef = (currentPlaceSort.contains(":"))?currentPlaceSort.split(":")[0]
					.equals(PlaceSortKey.USER_DEF) : false;
			udSort.setSelection(isUserDef);
			if(isUserDef){
				udNewSort.setEnabled(true);
				udNewSort.select(udDataType.indexOf(currentPlaceSort.split(":")[1]));
			}		
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
	
	private static class CNPF implements IFactory<INamePolicy> {
		public Class<? extends INamePolicy> klass;
		public CNPF(Class<? extends INamePolicy> klass) {
			this.klass = klass;
		}
		
		@Override
		public String getName() {
			return klass.getSimpleName();
		}
		
		@Override
		public INamePolicy newInstance() {
			try {
				return klass.newInstance();
			} catch (Exception e) {
				return null;
			}
		}
	}
	
	private static final IFile getFileFrom(ModelObject m) {
		IFileWrapper fw = FileData.getFile(m);
		return (fw instanceof EclipseFileWrapper ?
				((EclipseFileWrapper)fw).getResource() : null);
	}
	
	private static ArrayList<CNPF> getNamePolicies() {
		ArrayList<CNPF> r = new ArrayList<CNPF>();
		r.add(new CNPF(LongNamePolicy.class));
		r.add(new CNPF(StringNamePolicy.class));
		r.add(new CNPF(BooleanNamePolicy.class));
		return r;
	}
	
	/**
	 * Indicates whether or not changes made to the UI should be propagated
	 * to the current {@link Control}.
	 * @return <code>true</code> if the {@link Control} is valid and is not
	 * itself currently changing the UI, or <code>false</code> otherwise
	 */
	private boolean shouldPropagateUI() {
		return (!uiUpdateInProgress && currentControl != null);
	}
	
	private static Font smiff;
	
	private static final PointList POINTS_QUAD = new PointList(new int[] {
		0, 0,
		0, 40,
		-40, 40,
		-40, 0
	});
	
	private static final IChange changeControlName(Control c, String s) {
		if (c != null && s != null) {
			ChangeGroup cg = new ChangeGroup();
			cg.add(new BoundDescriptor(c.getSignature(),
					new NamedModelObject.ChangeNameDescriptor(
							c.getIdentifier(), s)));
			cg.add(ControlUtilities.changeLabel(c,
					ControlUtilities.labelFor(s)));
			return cg;
		} else return null;
	}
	
	@Override
	public void createEditorControl(Composite parent) {	
		CommonFuncUtilities.readExcelFile(DATA_FP, "UML", umlDataType);
		CommonFuncUtilities.readExcelFile(DATA_FP, "OWL", owlDataType);
		CommonFuncUtilities.readExcelFile(DATA_FP, "XML", xmlDataType);
		
		setUdDataType(getModel().getFormRules());
		
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
		l.setText("Formation Rules:");
		formRulesSelector = new ResourceSelector(self,
			((FileEditorInput)getEditorInput()).getFile().getProject(),
			Mode.FILE, FormRules.CONTENT_TYPE);
		new Label(self, SWT.NONE);
		formRulesSelector.getButton().setLayoutData(
			new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		formRulesSelector.addListener(new ResourceListener() {
			@Override
			public void resourceChanged(IResource oldValue, IResource newValue) {
				if (uiUpdateInProgress)
					return;
				try {
					FormRules s = (newValue != null ?
						(FormRules)new EclipseFileWrapper((IFile)newValue).load() : null);
					doChange(new BoundDescriptor(getModel(),
							new Signature.ChangeSetFormRulesDescriptor(
									new Signature.Identifier(),
									getModel().getFormRules(), s)));
					setUdDataType(s);
				} catch (LoadFailedException ife) {
					ife.printStackTrace();
				}
			}
		});	
		
		/* top_left part of signature editor */
		Composite left = new Composite(self, 0);
		left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout leftLayout = new GridLayout(1, false);
		left.setLayout(leftLayout);
		
		controls = new TreeViewer(left);
		controls.setComparator(new ViewerComparator() {
			@Override
			public int category(Object element) {
				return (element instanceof Signature ? 1 :
						element instanceof Control ? 2 : 0);
			}
			
			@Override
			public boolean isSorterProperty(Object element, String property) {
				return (element instanceof Control &&
						Control.PROPERTY_NAME.equals(property));
			}
		});
		controls.setContentProvider(
				new SignatureControlsContentProvider(controls));
		controls.setLabelProvider(new SignatureControlsLabelProvider());
		GridData controlsLayoutData =
			new GridData(SWT.FILL, SWT.FILL, true, true);
		controlsLayoutData.widthHint = 80;
		controls.getTree().setLayoutData(controlsLayoutData);
		controls.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setControl(getSelectedControl());
			}
		});
		final Menu menu = new Menu(controls.getTree());
		menu.addMenuListener(new MenuListener() {
			private Control currentControl;
			private INamePolicy currentPolicy;
			
			private void createPolicyMenuItem(Menu parent, final CNPF p) {
				final MenuItem i = new MenuItem(parent, SWT.RADIO);
				i.setText(p.getName());
				i.setSelection(p.klass.isInstance(currentPolicy));
				i.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (i.getSelection())
							doChange(ParameterUtilities.changeParameterPolicy(
									currentControl, p.newInstance()));
					}
				});
			}
			
			@Override
			public void menuShown(MenuEvent e) {
				for (MenuItem i : menu.getItems())
					i.dispose();
				
				currentControl = getSelectedControl();
				if (currentControl == null) {
					menu.setVisible(false);
					return;
				}
				
				boolean nested =
						(currentControl.getSignature().getParent() != null);
				
				currentPolicy =
					ParameterUtilities.getParameterPolicy(currentControl);
				
				MenuItem paramItem = new MenuItem(menu, SWT.CASCADE);
				paramItem.setText("&Parameter");
				
				Menu paramMenu = new Menu(paramItem);
				final MenuItem n = new MenuItem(paramMenu, SWT.RADIO);
				n.setText("(none)");
				n.setSelection(currentPolicy == null);
				n.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (n.getSelection())
							doChange(
								ParameterUtilities.changeParameterPolicy(
										currentControl, null));
					}
				});
				if (!nested) {
					for (CNPF i : getNamePolicies())
						createPolicyMenuItem(paramMenu, i);
				} else paramItem.setEnabled(false);
				paramItem.setMenu(paramMenu);
			}
			
			@Override
			public void menuHidden(MenuEvent e) {
			}
		});
		controls.getTree().setMenu(menu);
		
		Composite controlButtons = new Composite(left, SWT.NONE);
		RowLayout controlButtonsLayout = new RowLayout();
		controlButtons.setLayout(controlButtonsLayout);
		controlButtons.setLayoutData(new GridData(SWT.END, SWT.TOP, true, false));
		
		embedSignature = StockButton.OPEN.create(controlButtons, SWT.NONE);
		embedSignature.setText("&Import...");
		embedSignature.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ResourceTreeSelectionDialog rtsd =
						new ResourceTreeSelectionDialog(
								getSite().getShell(),
								getFile().getProject(),
								Mode.FILE, Signature.CONTENT_TYPE);
				if (rtsd.open() == Dialog.OK) {
					try {
						IFile f = (IFile)rtsd.getFirstResult();
						doChange(new BoundDescriptor(getModel(),
								new Signature.ChangeAddSignatureDescriptor(
										new Signature.Identifier(),
										-1,
										(Signature)new EclipseFileWrapper(f).
												load())));
					} catch (LoadFailedException ex) {
						return;
					}
				}
			}
		});
		
		addControl = StockButton.ADD.create(controlButtons);
		addControl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Control c = new Control();
				doChange(getModel().changeAddControl(c,
						getModel().getNamespace().getNextName()));
				controls.setSelection(new StructuredSelection(c), true);
			}
		});
		
		remove = StockButton.REMOVE.create(controlButtons);
		remove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Iterator<?> it =
					((IStructuredSelection)controls.getSelection()).iterator();
				ChangeGroup cg = new ChangeGroup();
				PropertyScratchpad context = new PropertyScratchpad();
				while (it.hasNext()) {
					Object i = it.next();
					IChange ch = null;
					if (i instanceof Control) {
						Control c = (Control)i;
						if (c.getSignature().equals(getModel()))
							cg.add(ch = c.changeRemove());
					} else if (i instanceof Signature) {
						Signature s = (Signature)i;
						if (s.getParent().equals(getModel()))
							cg.add(ch = new BoundDescriptor(getModel(),
									new Signature.ChangeRemoveSignatureDescriptor(
											new Signature.Identifier(),
											getModel().getSignatures(context).indexOf(s),
											s)));
					}
					context.executeChange(ch);
				}
				
				if (cg.size() > 0) {
					doChange(cg);
					controls.setSelection(StructuredSelection.EMPTY);
					setControl(null);
				}
			}
		});
		
		new Label(left, SWT.NONE).setText("Place Sort Configurations");
		
		/* bottom_left part of signature editor */
		Composite middle = new Composite(left, SWT.BORDER);
		middle.setLayout(new GridLayout());
		middle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group umlGroup = new Group(middle, SWT.NONE);		
		umlGroup.setText("UML");
		umlGroup.setLayout(new GridLayout(5, true));
		umlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		umlClass = new Button(umlGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO|SWT.SELECTED);
		umlClass.setText("Class");
		umlClass.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(umlClass.getSelection()){
					deselectAllPlaceSorts();
					umlClass.setSelection(true);
					doChange(currentControl.changePlaceSort(PlaceSortKey.UML_CLASS));
				}
			}
		});
		
		umlAttribute = new Button(umlGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO);
		umlAttribute.setText("Attribute");	
		umlAttribute.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(umlAttribute.getSelection()){
					deselectAllPlaceSorts();
					umlAttribute.setSelection(true);
					umlAttriType.setEnabled(true);
				}				
			}
		});
		
		umlAttriType = new Combo(umlGroup, SWT.NONE);
		umlAttriType.add("");
		for(String str : umlDataType){
			umlAttriType.add(str);
		}
		umlAttriType.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				doChange(currentControl.changePlaceSort(PlaceSortKey.UML_ATTRIBUTE + ":" + umlAttriType.getItem(umlAttriType.getSelectionIndex())));
			}
			
		});
		
		umlInstance = new Button(umlGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO);
		umlInstance.setText("Instance");
		umlInstance.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(umlInstance.getSelection()){
					deselectAllPlaceSorts();
					umlInstance.setSelection(true);
					umlInstType.setEnabled(true);
				}
				else{
					umlInstType.setEnabled(false);
				}
			}
		});
		
		umlInstType = new Combo(umlGroup, SWT.NONE);
		umlInstType.add("");
		for(String str : umlDataType){
			umlInstType.add(str);
		}
		umlInstType.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				doChange(currentControl.changePlaceSort(PlaceSortKey.UML_INSTANCE + ":" + umlInstType.getItem(umlInstType.getSelectionIndex())));
			}
			
		});

		Group owlGroup = new Group(middle, SWT.NONE);		
		owlGroup.setText("OWL");
		owlGroup.setLayout(new GridLayout(3, true));
		owlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		owlClass = new Button(owlGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO);
		owlClass.setText("Class");
		owlClass.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(owlClass.getSelection()){
					deselectAllPlaceSorts();
					owlClass.setSelection(true);
					doChange(currentControl.changePlaceSort(PlaceSortKey.OWL_CLASS));
				}
			}
		});
		
		owlIndividual = new Button(owlGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO);
		owlIndividual.setText("Individual");
		owlIndividual.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(owlIndividual.getSelection()){
					deselectAllPlaceSorts();
					owlIndividual.setSelection(true);
					doChange(currentControl.changePlaceSort(PlaceSortKey.OWL_INDIVIDUAL));
				}
			}
		});
		
		new Label(owlGroup, SWT.NONE);
			
		owlObjectProperty = new Button(owlGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO);
		owlObjectProperty.setText("Object Property");
		owlObjectProperty.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(owlObjectProperty.getSelection()){
					deselectAllPlaceSorts();
					owlObjectProperty.setSelection(true);
					doChange(currentControl.changePlaceSort(PlaceSortKey.OWL_OBJPROPERTY));
				}
			}
		});
		
		owlDataProperty = new Button(owlGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO);
		owlDataProperty.setText("Data Property");
		owlDataProperty.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(owlDataProperty.getSelection()){
					deselectAllPlaceSorts();
					owlDataProperty.setSelection(true);
					proType.setEnabled(true);
				}
				else{
					proType.setEnabled(false);
				}
			}
		});
		
		proType = new Combo(owlGroup, SWT.NONE);
		proType.add("");
		for(String str : owlDataType){
			proType.add(str);
		}
		proType.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				doChange(currentControl.changePlaceSort(PlaceSortKey.OWL_DATAPROPERTY + ":" + proType.getItem(proType.getSelectionIndex())));
			}
			
		});
		
		Group xmlGroup = new Group(middle, SWT.NONE);		
		xmlGroup.setText("XML");
		xmlGroup.setLayout(new GridLayout(4, false));
		xmlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		xmlElement = new Button(xmlGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO);
		xmlElement.setText("Element");
		xmlElement.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(xmlElement.getSelection()){
					deselectAllPlaceSorts();
					xmlElement.setSelection(true);
					doChange(currentControl.changePlaceSort(PlaceSortKey.XML_ELEMENT));
				}
			}
		});
		
		xmlEntity = new Button(xmlGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO);
		xmlEntity.setText("Entity");
		xmlEntity.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(xmlEntity.getSelection()){
					deselectAllPlaceSorts();
					xmlEntity.setSelection(true);
					doChange(currentControl.changePlaceSort(PlaceSortKey.XML_ENTITY));
				}
			}
		});
		
		xmlAttribute = new Button(xmlGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO);
		xmlAttribute.setText("Attribute");	
		xmlAttribute.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(xmlAttribute.getSelection()){
					deselectAllPlaceSorts();
					xmlAttribute.setSelection(true);
					xmlAttriType.setEnabled(true);
				}
				else{
					xmlAttriType.setEnabled(false);
				}
			}
		});
		
		xmlAttriType = new Combo(xmlGroup, SWT.NONE);
		xmlAttriType.add("");
		for(String str : xmlDataType){
			xmlAttriType.add(str);
		}
		xmlAttriType.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				doChange(currentControl.changePlaceSort(PlaceSortKey.XML_ATTRIBUTE + ":" + xmlAttriType.getItem(xmlAttriType.getSelectionIndex())));
			}	
		});
		
		xmlPCDATA = new Button(xmlGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO);
		xmlPCDATA.setText("PCDATA");
		xmlPCDATA.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(xmlPCDATA.getSelection()){
					deselectAllPlaceSorts();
					xmlPCDATA.setSelection(true);
					doChange(currentControl.changePlaceSort(PlaceSortKey.XML_PCDATA));
				}
			}
		});
		
		xmlCDATA = new Button(xmlGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO);
		xmlCDATA.setText("CDATA");
		xmlCDATA.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(xmlCDATA.getSelection()){
					deselectAllPlaceSorts();
					xmlCDATA.setSelection(true);
					doChange(currentControl.changePlaceSort(PlaceSortKey.XML_CDATA));
				}
			}
		});
		
		Group userDefGroup = new Group(middle, SWT.NONE);		
		userDefGroup.setText("User Defined");
		userDefGroup.setLayout(new GridLayout(3, false));
		userDefGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		udSort = new Button(userDefGroup, SWT.FLAT|SWT.LEFT|SWT.RADIO);
		udSort.setText("UserDef Sort");
		udSort.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				if(udSort.getSelection()){
					deselectAllPlaceSorts();
					udSort.setSelection(true);
					udNewSort.setEnabled(true);
				}
			}
		});
		
		(sortLabel = new Label(userDefGroup, SWT.NONE)).setText("Name:");
		udNewSort = new Combo(userDefGroup, SWT.NONE);
		udNewSort.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		if(udDataType.size() > 0){
			udNewSort.setItems(udDataType.toArray(new String[udDataType.size()]));			
		}
		udNewSort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doChange(currentControl.changePlaceSort(PlaceSortKey.USER_DEF + ":" + 
						udNewSort.getItem(udNewSort.getSelectionIndex())));
			}
		});
		
		/* right part of signature editor */
		Composite right = new Composite(self, 0);
		right.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout rightLayout = new GridLayout(2, false);
		right.setLayout(rightLayout);
		
		TextListener nameListener = new TextListener() {
			@Override
			void go() {
				String n = currentControl.getName();
				if (!n.equals(name.getText()))
					if (!doChange(changeControlName(
							currentControl, name.getText())))
						lockedTextUpdate(name, n);
			}
		};
		
		(nameLabel = new Label(right, SWT.NONE)).setText("Name:");
		name = new Text(right, SWT.BORDER);
		name.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		name.addSelectionListener(nameListener);
		name.addFocusListener(nameListener);
		
		TextListener labelListener = new TextListener() {
			@Override
			void go() {
				String l = ControlUtilities.getLabel(currentControl);
				if (!l.equals(label.getText()))
					if (!doChange(ControlUtilities.changeLabel(currentControl, label.getText())))
						lockedTextUpdate(label, l);
			}
		};
		
		(labelLabel = new Label(right, SWT.NONE)).setText("Label:");
		label = new Text(right, SWT.BORDER);
		label.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		label.addSelectionListener(labelListener);
		label.addFocusListener(labelListener);
		
		(kindLabel = new Label(right, SWT.NONE)).setText("Kind:");
		
		Composite kindGroup = new Composite(right, SWT.NONE);
		kindGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		kindGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		(atomicKind = new Button(kindGroup, SWT.RADIO)).setText("Atomic");
		atomicKind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (shouldPropagateUI() &&
						!currentControl.getKind().equals(Kind.ATOMIC))
					doChange(currentControl.changeKind(Kind.ATOMIC));
			}
		});
		
		(activeKind = new Button(kindGroup, SWT.RADIO)).setText("Active");
		activeKind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (shouldPropagateUI() &&
						!currentControl.getKind().equals(Kind.ACTIVE))
					doChange(currentControl.changeKind(Kind.ACTIVE));
			}
		});
		
		(passiveKind = new Button(kindGroup, SWT.RADIO)).setText("Passive");
		passiveKind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (shouldPropagateUI() &&
						!currentControl.getKind().equals(Kind.PASSIVE))
					doChange(currentControl.changeKind(Kind.PASSIVE));
			}
		});
				
		(appearanceLabel = new Label(right, SWT.NONE)).setText("Appearance:");
		GridData appearanceLabelLayoutData = new GridData(SWT.FILL, SWT.FILL, false, true);
		appearanceLabel.setLayoutData(appearanceLabelLayoutData);
		
		Composite appearanceGroup = new Composite(right, SWT.NONE);
		GridData appearanceGroupLayoutData = new GridData(SWT.FILL, SWT.FILL, false, true);
		appearanceGroup.setLayoutData(appearanceGroupLayoutData);
		GridLayout appearanceGroupLayout = new GridLayout(1, false);
		appearanceGroup.setLayout(appearanceGroupLayout);		
		
		/* XXX: the addition of this row leads to really weird oversizing of
		 * the polygon canvas! */
		Composite firstLine = new Composite(appearanceGroup, SWT.NONE);
		firstLine.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		firstLine.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		(ovalMode = new Button(firstLine, SWT.RADIO)).setText("Oval");
		ovalMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (shouldPropagateUI())
					doChange(ControlUtilities.changeShape(
							currentControl, Ellipse.SINGLETON));
			}
		});
		
		(polygonMode = new Button(firstLine, SWT.RADIO)).setText("Polygon");
		polygonMode.setSelection(true);
		polygonMode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (shouldPropagateUI())
					doChange(ControlUtilities.changeShape(
							currentControl, POINTS_QUAD));
			}
		});
		
		appearance = new SignatureEditorPolygonCanvas(this,
				appearanceGroup, SWT.BORDER);
		GridData appearanceLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		appearanceLayoutData.widthHint = 100;
		appearanceLayoutData.heightHint = 100;
		appearance.setLayoutData(appearanceLayoutData);
		appearance.setBackground(ColorConstants.listBackground);
		
		if (smiff == null)
			smiff = UI.tweakFont(appearanceLabel.getFont(), 8, SWT.ITALIC);
		
		appearanceDescription = new Label(appearanceGroup, SWT.CENTER | SWT.WRAP);
		appearanceDescription.setText(
				"Click to add a new point. Double-click a point to delete " +
				"it. Move elements by clicking and dragging. Right-click for" +
				"more options.");
		GridData appearanceDescriptionData = new GridData();
		appearanceDescriptionData.verticalAlignment = SWT.TOP;
		appearanceDescriptionData.horizontalAlignment = SWT.FILL;
		appearanceDescriptionData.widthHint = 0;
		appearanceDescription.setLayoutData(appearanceDescriptionData);
		appearanceDescription.setFont(smiff);
		
		(outlineLabel = new Label(right, SWT.NONE)).setText("Outline:");
		outline = new ColorSelector(right);
		outline.getButton().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		outline.addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (!shouldPropagateUI())
					return;
				Colour newColour = new Colour(outline.getColorValue());
				if (!ColourUtilities.getOutline(currentControl).equals(newColour))
					doChange(ColourUtilities.changeOutline(currentControl, newColour));
			}
		});
		
		(fillLabel = new Label(right, SWT.NONE)).setText("Fill:");
		fill = new ColorSelector(right);
		fill.getButton().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fill.addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (!shouldPropagateUI())
					return;
				Colour newColour = new Colour(fill.getColorValue());
				if (!ColourUtilities.getFill(currentControl).equals(newColour))
					doChange(ColourUtilities.changeFill(currentControl, newColour));
			}
		});
		
		setEnablement(false);
	}
	
	private void setUdDataType(FormRules ss){
		if(ss != null && ss.getSortSet() != null && !ss.getSortSet().getPlaceSorts().isEmpty()){
			udDataType.clear();
			if(ss.getSortSet().getPlaceSorts().size() > 0){
				for(PlaceSort ps : ss.getSortSet().getPlaceSorts()){
					udDataType.add(ps.getName());
				}						
			}
		}
	}

	private boolean setEnablement(boolean enabled) {
		return UI.setEnabled(enabled,
			name, label, appearance, appearanceDescription,
			kindLabel, atomicKind, activeKind, passiveKind,
			umlClass, umlAttribute, umlAttriType, umlInstance, umlInstType,
			owlClass, owlIndividual, owlObjectProperty, owlDataProperty, proType,
			xmlElement, xmlAttribute, xmlEntity, xmlPCDATA, xmlCDATA, xmlAttriType, udSort, udNewSort,
			outline.getButton(), outlineLabel, fill.getButton(), fillLabel, 
			ovalMode, polygonMode, nameLabel, sortLabel, appearanceLabel, labelLabel
			);
	}
	
	private void deselectAllPlaceSorts(){
		for(Button btn : new Button[]{umlClass, umlAttribute, umlInstance,
				owlClass, owlIndividual, owlObjectProperty, owlDataProperty,
				xmlElement, xmlAttribute, xmlEntity, xmlPCDATA, xmlCDATA, udSort}){
			btn.setSelection(false);
		}

		for(Combo c : new Combo[]{umlAttriType, umlInstType, proType, xmlAttriType}){
			c.select(0);
			c.setEnabled(false);
		}
		udNewSort.setEnabled(false);
	}
	
	@Override
	protected void loadModel() throws LoadFailedException {
		model = (Signature)loadInput();
		for(int i = 0; i < 6; i++){
			model.getControls().remove(0);
		}
	}
	
	@Override
	protected void updateEditorControl() {
		if (getError() != null)
			return;
		
		clearUndo();
		if (currentControl != null)
			currentControl.removePropertyChangeListener(this);
		
		controls.setInput(getModel());
		setControl(null);
		modelToControls();
	}
	
	private void modelToControls() {
		uiUpdateInProgress = true;
		
		formRulesSelector.setResource(getFileFrom(model.getFormRules()));
		
		uiUpdateInProgress = false;
	}

	@Override
	public void dispose() {
		if (currentControl != null)
			currentControl.removePropertyChangeListener(this);
		model = null;
		
		appearance.dispose();
		appearance = null;
		
		super.dispose();
	}
	
	@Override
	public void setFocus() {
		super.setFocus();
		if (controls != null) {
			org.eclipse.swt.widgets.Control c = controls.getControl();
			if (!c.isDisposed())
				c.setFocus();
		}
		if (formRulesSelector != null) {
			Button b = formRulesSelector.getButton();
			if (b != null && !b.isDisposed() && b.isVisible())
				b.setFocus();
		}
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		Object newValue = evt.getNewValue();
		if (evt.getSource().equals(currentControl)) {
			if (uiUpdateInProgress)
				return;
			uiUpdateInProgress = true;
			try {
				if (Signature.PROPERTY_FORMRULES.equals(propertyName)) {
					FormRules s = (FormRules)newValue;
					formRulesSelector.setResource(getFileFrom(s));
				} else if (ControlUtilities.LABEL.equals(propertyName)) {
					label.setText((String)newValue);
				} else if (Control.PROPERTY_NAME.equals(propertyName)) {
					name.setText((String)newValue);
				} else if (ControlUtilities.SHAPE.equals(propertyName)) {
					ovalMode.setSelection(newValue instanceof Ellipse);
					polygonMode.setSelection(newValue instanceof PointList);
				} else if (ColourUtilities.FILL.equals(propertyName)) {
					Colour c = (Colour)newValue;
					if (c == null)
						c = ColourUtilities.getDefaultFill(currentControl);
					fill.setColorValue(c.getRGB());
				} else if (ColourUtilities.OUTLINE.equals(propertyName)) {
					Colour c = (Colour)newValue;
					if (c == null)
						c = ColourUtilities.getDefaultOutline(currentControl);
					outline.setColorValue(c.getRGB());
				} else if (Control.PROPERTY_KIND.equals(propertyName)) {
					activeKind.setSelection(Kind.ACTIVE.equals(newValue));
					atomicKind.setSelection(Kind.ATOMIC.equals(newValue));
					passiveKind.setSelection(Kind.PASSIVE.equals(newValue));
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
