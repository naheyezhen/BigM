package dk.itu.bigm.editors.sortset;

import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.util.Iterator;

import org.bigraph.model.LinkSort;
import org.bigraph.model.NamedModelObject;
import org.bigraph.model.PlaceSort;
import org.bigraph.model.SortSet;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.ChangeGroup;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.descriptors.BoundDescriptor;
import org.bigraph.model.loaders.LoadFailedException;
import org.bigraph.model.savers.SaveFailedException;
import org.bigraph.model.savers.SortSetXMLSaver;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import dk.itu.bigm.editors.AbstractNonGEFEditor;
import dk.itu.bigm.utilities.resources.EclipseFileWrapper;
import dk.itu.bigm.utilities.resources.ResourceTreeSelectionDialog;
import dk.itu.bigm.utilities.resources.ResourceTreeSelectionDialog.Mode;
import dk.itu.bigm.utilities.ui.StockButton;
import dk.itu.bigm.utilities.ui.UI;

public class SortSetEditor extends AbstractNonGEFEditor
implements PropertyChangeListener {
	public static final String ID = "dk.itu.bigm.SortSetEditor";

	public SortSetEditor() {
	}
	
	@Override
	public void doActualSave(IFile f, OutputStream os)
			throws SaveFailedException {
    	SortSetXMLSaver r = new SortSetXMLSaver().setModel(getModel());
		r.setFile(new EclipseFileWrapper(f)).
    		setOutputStream(os).exportObject();
		setSavePoint();
	}

	private SortSet model = null;
	
	@Override
	protected SortSet getModel() {
		return model;
	}
	
	private PlaceSort currentPlaceSort;
	private LinkSort currentLinkSort;
	private TreeViewer placeSorts, linkSorts;
	
	private Button embedPlaceSortSet, addPlaceSort, removePlaceSort;
	private Text namePlaceSort;
	
	private Button embedLinkSortSet, addLinkSort, removeLinkSort;
	private Text nameLinkSort;
	
	protected void setPlaceSort(PlaceSort s) {
		if (currentPlaceSort != null)
			currentPlaceSort.removePropertyChangeListener(this);
		currentPlaceSort = s;
		if (setEnablementPlace(s != null)) {
			currentPlaceSort.addPropertyChangeListener(this);
			placeSortsToFields();
		}
	}
	
	protected void setLinkSort(LinkSort s) {
		if (currentLinkSort != null)
			currentLinkSort.removePropertyChangeListener(this);
		currentLinkSort = s;
		if (setEnablementLink(s != null)) {
			currentLinkSort.addPropertyChangeListener(this);
			linkSortsToFields();
		}
	}
	
	private boolean uiUpdateInProgress = false;
	
	private PlaceSort getSelectedPlaceSort() {
		Object s = ((IStructuredSelection)placeSorts.getSelection()).
				getFirstElement();
		return (s instanceof PlaceSort ? (PlaceSort)s : null);
	}
	
	private LinkSort getSelectedLinkSort() {
		Object s = ((IStructuredSelection)linkSorts.getSelection()).
				getFirstElement();
		return (s instanceof LinkSort ? (LinkSort)s : null);
	}
	
	protected void placeSortsToFields() {
		uiUpdateInProgress = true;
		
		try {
			namePlaceSort.setText(currentPlaceSort.getName());
			if (setEnablementPlace(
					currentPlaceSort.getSortSet().equals(getModel()))) {
				namePlaceSort.setFocus();
				namePlaceSort.selectAll();
			}
			
			if (getSelectedPlaceSort() != currentPlaceSort)
				placeSorts.setSelection(
						new StructuredSelection(currentPlaceSort), true);
		} finally {
			uiUpdateInProgress = false;
		}
	}
	
	protected void linkSortsToFields() {
		uiUpdateInProgress = true;
		
		try {
			nameLinkSort.setText(currentLinkSort.getName());
			if (setEnablementLink(
					currentLinkSort.getSortSet().equals(getModel()))) {
				nameLinkSort.setFocus();
				nameLinkSort.selectAll();
			}
			
			if (getSelectedLinkSort() != currentLinkSort)
				linkSorts.setSelection(
						new StructuredSelection(currentLinkSort), true);
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
		return (!uiUpdateInProgress && currentPlaceSort != null && currentLinkSort != null);
	}
	
	private static final IChange changePlaceSortName(PlaceSort s, String str) {
		if (s != null && str != null) {
			ChangeGroup cg = new ChangeGroup();
			cg.add(new BoundDescriptor(s.getSortSet(),
					new NamedModelObject.ChangeNameDescriptor(
							s.getIdentifier(), str)));
			return cg;
		} else return null;
	}
	
	private static final IChange changeLinkSortName(LinkSort s, String str) {
		if (s != null && str != null) {
			ChangeGroup cg = new ChangeGroup();
			cg.add(new BoundDescriptor(s.getSortSet(),
					new NamedModelObject.ChangeNameDescriptor(
							s.getIdentifier(), str)));
			return cg;
		} else return null;
	}
	
	@Override
	public void createEditorControl(Composite parent) {
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
		
		/* top_left part of sortset editor */
		Composite left = new Composite(self, 0);
		left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout leftLayout = new GridLayout(1, false);
		left.setLayout(leftLayout);
		
		GridData sortsLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		sortsLayoutData.widthHint = 80;
		
		TextListener namePlaceListener = new TextListener() {
			@Override
			void go() {
				String n = currentPlaceSort.getName();
				if (!n.equals(namePlaceSort.getText()))
					if (!doChange(changePlaceSortName(
							currentPlaceSort, namePlaceSort.getText())))
						lockedTextUpdate(namePlaceSort, n);
			}
		};
		
		(new Label(left, SWT.NONE)).setText("Place Sort:");
		Composite place = new Composite(left, SWT.NONE);
		place.setLayout(new GridLayout(2, false));
		place.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		new Label(place, SWT.NONE).setText("Name");
		namePlaceSort = new Text(place, SWT.BORDER);
		namePlaceSort.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		namePlaceSort.addSelectionListener(namePlaceListener);
		namePlaceSort.addFocusListener(namePlaceListener);
		
		placeSorts = new TreeViewer(left);
		placeSorts.setComparator(new ViewerComparator() {
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
		placeSorts.setContentProvider(
				new SortSetPlaceSortsContentProvider(placeSorts));
		placeSorts.setLabelProvider(new SortSetSortsLabelProvider());
		placeSorts.getTree().setLayoutData(sortsLayoutData);
		placeSorts.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setPlaceSort(getSelectedPlaceSort());
			}
		});
		
		RowLayout controlButtonsLayout = new RowLayout();
	
		Composite controlPlaceButtons = new Composite(left, SWT.NONE);
		controlPlaceButtons.setLayout(controlButtonsLayout);
		controlPlaceButtons.setLayoutData(new GridData(SWT.END, SWT.TOP, true, false));
		
		embedPlaceSortSet = StockButton.OPEN.create(controlPlaceButtons, SWT.NONE);
		embedPlaceSortSet.setText("&Import...");
		embedPlaceSortSet.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ResourceTreeSelectionDialog rtsd =
						new ResourceTreeSelectionDialog(
								getSite().getShell(),
								getFile().getProject(),
								Mode.FILE, SortSet.CONTENT_TYPE);
				if (rtsd.open() == Dialog.OK) {
					try {
						IFile f = (IFile)rtsd.getFirstResult();
						doChange(new BoundDescriptor(getModel(),
								new SortSet.ChangeAddSortSetDescriptor(
										new SortSet.Identifier(),
										-1,
										(SortSet)new EclipseFileWrapper(f).
												load())));
					} catch (LoadFailedException ex) {
						return;
					}
				}
			}
		});
		
		addPlaceSort = StockButton.ADD.create(controlPlaceButtons);
		addPlaceSort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PlaceSort s = new PlaceSort();
				doChange(getModel().changeAddPlaceSort(s,
						getModel().getNamespacePlaceSort().getNextName()));
				placeSorts.setSelection(new StructuredSelection(s), true);
			}
		});
		
		removePlaceSort = StockButton.REMOVE.create(controlPlaceButtons);
		removePlaceSort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Iterator<?> it =
					((IStructuredSelection)placeSorts.getSelection()).iterator();
				ChangeGroup cg = new ChangeGroup();
				PropertyScratchpad context = new PropertyScratchpad();
				while (it.hasNext()) {
					Object i = it.next();
					IChange ch = null;
					if (i instanceof PlaceSort) {
						PlaceSort s = (PlaceSort)i;
						if (s.getSortSet().equals(getModel()))
							cg.add(ch = s.changeRemovePlaceSort());
					} else if (i instanceof SortSet) {
						SortSet s = (SortSet)i;
						if (s.getParent().equals(getModel()))
							cg.add(ch = new BoundDescriptor(getModel(),
									new SortSet.ChangeRemoveSortSetDescriptor(
											new SortSet.Identifier(),
											getModel().getSortSets(context).indexOf(s),
											s)));
					}
					context.executeChange(ch);
				}
				
				if (cg.size() > 0) {
					doChange(cg);
					placeSorts.setSelection(StructuredSelection.EMPTY);
					setPlaceSort(null);
				}
			}
		});
		
		/* right part of sortset editor */
		Composite right = new Composite(self, 0);
		right.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout rightLayout = new GridLayout(1, false);
		right.setLayout(rightLayout);
		
		TextListener nameLinkListener = new TextListener() {
			@Override
			void go() {
				String n = currentLinkSort.getName();
				if (!n.equals(nameLinkSort.getText()))
					if (!doChange(changeLinkSortName(
							currentLinkSort, nameLinkSort.getText())))
						lockedTextUpdate(nameLinkSort, n);
			}
		};
		
		
		(new Label(right, SWT.NONE)).setText("Link Sort:");
		Composite link = new Composite(right, SWT.NONE);
		link.setLayout(new GridLayout(2, false));
		link.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		new Label(link, SWT.NONE).setText("Name");
		nameLinkSort = new Text(link, SWT.BORDER);
		nameLinkSort.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		nameLinkSort.addSelectionListener(nameLinkListener);
		nameLinkSort.addFocusListener(nameLinkListener);
		
		linkSorts = new TreeViewer(right);
		linkSorts.setComparator(new ViewerComparator() {
			@Override
			public int category(Object element) {
				return (element instanceof SortSet ? 1 :
						element instanceof LinkSort ? 2 : 0);
			}
			
			@Override
			public boolean isSorterProperty(Object element, String property) {
				return (element instanceof LinkSort &&
						LinkSort.PROPERTY_NAME.equals(property));
			}
		});
		linkSorts.setContentProvider(
				new SortSetLinkSortsContentProvider(linkSorts));
		linkSorts.setLabelProvider(new SortSetSortsLabelProvider());
		linkSorts.getTree().setLayoutData(sortsLayoutData);
		linkSorts.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setLinkSort(getSelectedLinkSort());
			}
		});
	
		Composite controlLinkButtons = new Composite(right, SWT.NONE);
		controlLinkButtons.setLayout(controlButtonsLayout);
		controlLinkButtons.setLayoutData(new GridData(SWT.END, SWT.TOP, true, false));
		
		embedLinkSortSet = StockButton.OPEN.create(controlLinkButtons, SWT.NONE);
		embedLinkSortSet.setText("&Import...");
		embedLinkSortSet.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ResourceTreeSelectionDialog rtsd =
						new ResourceTreeSelectionDialog(
								getSite().getShell(),
								getFile().getProject(),
								Mode.FILE, SortSet.CONTENT_TYPE);
				if (rtsd.open() == Dialog.OK) {
					try {
						IFile f = (IFile)rtsd.getFirstResult();
						doChange(new BoundDescriptor(getModel(),
								new SortSet.ChangeAddSortSetDescriptor(
										new SortSet.Identifier(),
										-1,
										(SortSet)new EclipseFileWrapper(f).
												load())));
					} catch (LoadFailedException ex) {
						return;
					}
				}
			}
		});
		
		addLinkSort = StockButton.ADD.create(controlLinkButtons);
		addLinkSort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LinkSort s = new LinkSort();
				doChange(getModel().changeAddLinkSort(s,
						getModel().getNamespaceLinkSort().getNextName()));
				linkSorts.setSelection(new StructuredSelection(s), true);
			}
		});
		
		removeLinkSort = StockButton.REMOVE.create(controlLinkButtons);
		removeLinkSort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Iterator<?> it =
					((IStructuredSelection)linkSorts.getSelection()).iterator();
				ChangeGroup cg = new ChangeGroup();
				PropertyScratchpad context = new PropertyScratchpad();
				while (it.hasNext()) {
					Object i = it.next();
					IChange ch = null;
					if (i instanceof LinkSort) {
						LinkSort s = (LinkSort)i;
						if (s.getSortSet().equals(getModel()))
							cg.add(ch = s.changeRemoveLinkSort());
					} else if (i instanceof SortSet) {
						SortSet s = (SortSet)i;
						if (s.getParent().equals(getModel()))
							cg.add(ch = new BoundDescriptor(getModel(),
									new SortSet.ChangeRemoveSortSetDescriptor(
											new SortSet.Identifier(),
											getModel().getSortSets(context).indexOf(s),
											s)));
					}
					context.executeChange(ch);
				}
				
				if (cg.size() > 0) {
					doChange(cg);
					linkSorts.setSelection(StructuredSelection.EMPTY);
					setLinkSort(null);
				}
			}
		});
			
	}
		
	private boolean setEnablementPlace(boolean enabled) {
		return UI.setEnabled(enabled, namePlaceSort);
	}
	
	private boolean setEnablementLink(boolean enabled) {
		return UI.setEnabled(enabled, nameLinkSort);
	}
	
	@Override
	protected void loadModel() throws LoadFailedException {
		model = (SortSet)loadInput();
	}
	
	@Override
	protected void updateEditorControl() {
		if (getError() != null)
			return;
		
		clearUndo();
		if (currentPlaceSort != null)
			currentPlaceSort.removePropertyChangeListener(this);
		
		placeSorts.setInput(getModel());
		setPlaceSort(null);
		
		if (currentLinkSort != null)
			currentLinkSort.removePropertyChangeListener(this);
		
		linkSorts.setInput(getModel());
		setLinkSort(null);
	}

	@Override
	public void dispose() {
		if (currentPlaceSort != null)
			currentPlaceSort.removePropertyChangeListener(this);
		if (currentLinkSort != null)
			currentLinkSort.removePropertyChangeListener(this);
		model = null;
		super.dispose();
	}
	
	@Override
	public void setFocus() {
		super.setFocus();
		if (placeSorts != null) {
			org.eclipse.swt.widgets.Control s = placeSorts.getControl();
			if (!s.isDisposed())
				s.setFocus();
		} else if (linkSorts != null) {
			org.eclipse.swt.widgets.Control s = linkSorts.getControl();
			if (!s.isDisposed())
				s.setFocus();
		}
	}

	@Override
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		Object newValue = evt.getNewValue();
		if (evt.getSource().equals(currentPlaceSort)) {
			if (uiUpdateInProgress)
				return;
			uiUpdateInProgress = true;
			try {
				if (PlaceSort.PROPERTY_NAME.equals(propertyName)) {
					namePlaceSort.setText((String)newValue);
				}
			} finally {
				uiUpdateInProgress = false;
			}
		}
		if (evt.getSource().equals(currentLinkSort)) {
			if (uiUpdateInProgress)
				return;
			uiUpdateInProgress = true;
			try {
				if (LinkSort.PROPERTY_NAME.equals(propertyName)) {
					nameLinkSort.setText((String)newValue);
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
