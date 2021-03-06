package it.uniud.bigmit;

import it.uniud.bigmit.utils.RcpUtils;


import dk.itu.bigm.utilities.resources.Project;
import dk.itu.bigm.utilities.resources.ResourceTreeSelectionDialog;
import dk.itu.bigm.utilities.resources.ResourceTreeSelectionDialog.Mode;

import org.bigraph.model.Signature;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewBrsWizardPage extends WizardPage{
	private IStructuredSelection selection = null;
	private IPath folderPath = null, signaturePath = null;
	
	private Text folderText = null, signatureText = null, nameText = null;
	
	public NewBrsWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName);
		this.selection = selection;
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(3, false));
		
		ModifyListener sharedModifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		};
		
		new Label(root, 0).setText("&Parent folder:");
		
		folderText = new Text(root, SWT.BORDER);
		folderText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		folderText.addModifyListener(sharedModifyListener);
		
		Button folderButton = new Button(root, SWT.CENTER);
		folderButton.setText("&Browse...");
		folderButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				IContainer f = null;
				for (Object i : selection.toArray()) {
					if (i instanceof IFolder)
						f = (IFolder)i;
					else if (i instanceof IResource)
						f = ((IResource)i).getParent();
					else if (i instanceof IAdaptable)
						f = (IFolder)((IAdaptable)i).getAdapter(IFolder.class);
					
					if (f != null)
						break;
				}

				if (f == null)
					f = RcpUtils.getWorkspaceRoot();
				
				ResourceTreeSelectionDialog d =
					new ResourceTreeSelectionDialog(getShell(),
						Project.getWorkspaceRoot(), Mode.CONTAINER);
				if (folderPath != null)
					d.setInitialSelection(RcpUtils.findContainerByPath(null, folderPath));
				d.open();
				IResource result = d.getFirstResult();
				if (result instanceof IContainer)
					setFolderPath(result.getFullPath().makeRelative());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		GridData folderButtonLayoutData = new GridData();
		folderButtonLayoutData.widthHint = 100;
		folderButton.setLayoutData(folderButtonLayoutData);
		
		new Label(root, SWT.NONE).setText("&Signature:");
		
		signatureText = new Text(root, SWT.BORDER);
		signatureText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		signatureText.addModifyListener(sharedModifyListener);
		
		Button signatureButton = new Button(root, SWT.NONE);
		signatureButton.setText("B&rowse...");
		signatureButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ResourceTreeSelectionDialog d =
					new ResourceTreeSelectionDialog(getShell(),
						Project.getWorkspaceRoot(),
						Mode.FILE, Signature.CONTENT_TYPE);
				if (signaturePath != null)
					d.setInitialSelection(Project.findFileByPath(null, signaturePath));
				d.setMessage("Select a signature file.");
				d.open();
				
				IResource result = d.getFirstResult();
				if (result instanceof IFile)
					setSignaturePath(result.getFullPath().makeRelative());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		GridData signatureButtonLayoutData = new GridData();
		signatureButtonLayoutData.widthHint = 100;
		signatureButton.setLayoutData(signatureButtonLayoutData);
		
		new Label(root, SWT.NONE);
		new Label(root, SWT.HORIZONTAL | SWT.SEPARATOR);
		new Label(root, SWT.NONE);
		
		new Label(root, SWT.NONE).setText("&Name:");
		
		nameText = new Text(root, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		nameText.addModifyListener(sharedModifyListener);
		
		setControl(root);
	}
	
	public void setFolderPath(IPath p) {
		folderText.setText(p.toString());
		validate();
	}
	
	public IPath getFolderPath() {
		return folderPath;
	}
	
	public void setSignaturePath(IPath p) {
		signatureText.setText(p.toString());
		validate();
	}
	
	public IPath getSignaturePath() {
		return signaturePath;
	}
	
	public String getFileName() {
		return nameText.getText().concat(".brs");
	}
	
	private boolean validate() {
		setPageComplete(false);
		
		String fT = folderText.getText(), sT = signatureText.getText(),
		nT = nameText.getText();
		folderPath = new Path(fT);
		signaturePath = new Path(sT);
		
		if (fT.length() == 0 || folderPath.segmentCount() == 0) {
			setErrorMessage("Folder name is empty.");
			return false;
		}
		
		IContainer folder = RcpUtils.findContainerByPath(null, folderPath);
		if (folder == null) {
			setErrorMessage("Folder '" + fT + "' does not exist.");
			return false;
		} else if (folder instanceof IWorkspaceRoot) {
			setErrorMessage("'" + fT + "' must be a project or folder.");
			return false;
		}
		
		if (sT.length() == 0 || signaturePath.segmentCount() == 0) {
			setErrorMessage("Signature name is empty.");
			return false;
		}

		IResource signature = RcpUtils.findResourceByPath(null, signaturePath);
		if (signature == null) {
			setErrorMessage("Signature '" + sT + "' does not exist.");
			return false;
		} else if (!(signature instanceof IFile)) {
			setErrorMessage("'" + sT + "' must be a signature.");
			return false;
		} else {
			IContentType t;
			try {
				t = ((IFile)signature).getContentDescription().getContentType();
			} catch (CoreException e) {
				t = null;
			}
			if (t == null || !t.getId().equals(Signature.CONTENT_TYPE)) {
				setErrorMessage("'" + sT + "' must be a signature.");
				return false;
			}
		}
		
		if (nT.length() == 0) {
			setErrorMessage("Name is empty.");
			return false;
		}
		
		String proposedFileName = nT + ".brs";
		
		IPath p = folder.getFullPath().makeRelative();
		if (!p.isValidSegment(proposedFileName)) {
			setErrorMessage("'" + nT + "' contains invalid characters.");
			return false;
		} else {
			p.append(proposedFileName);
			if (RcpUtils.findFileByPath(null, p) != null) {
				setErrorMessage("'" + p.toString() + "' already exists.");
				return false;
			}
		}
		
		setPageComplete(true);
		setErrorMessage(null);
		return true;
	}
}
