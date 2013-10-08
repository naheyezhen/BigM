package it.uniud.bigmit.editparts;

import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.ui.IWorkbenchPart;

public class BigmitRootEditPart extends ScalableRootEditPart {

	private IWorkbenchPart part;
	
	public BigmitRootEditPart( IWorkbenchPart part )
	{
		this.part = part;
	}
	public IWorkbenchPart getWorkbenchPart()
	{
		return part;
	}
	
}
