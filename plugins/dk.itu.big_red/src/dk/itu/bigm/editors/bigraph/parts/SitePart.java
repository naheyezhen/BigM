package dk.itu.bigm.editors.bigraph.parts;

import java.beans.PropertyChangeEvent;

import org.bigraph.model.Site;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import dk.itu.bigm.editors.bigraph.figures.SiteFigure;
import dk.itu.bigm.editors.bigraph.policies.LayoutableDeletePolicy;
import dk.itu.bigm.model.ExtendedDataUtilities;

public class SitePart extends AbstractPart {
	@Override
	public Site getModel() {
		return (Site)super.getModel();
	}
	
	@Override
	protected IFigure createFigure() {
		return new SiteFigure();
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new LayoutableDeletePolicy());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getSource() == getModel())
			if (evt.getPropertyName().equals(ExtendedDataUtilities.ALIAS))
		    	refreshVisuals();
	}
	
	@Override
	protected void refreshVisuals(){
		super.refreshVisuals();
		
		SiteFigure figure = (SiteFigure)getFigure();
		Site model = getModel();

		if (ExtendedDataUtilities.getAlias(model) == null) {
			figure.setName(model.getName(), false);
		} else figure.setName(ExtendedDataUtilities.getAlias(model), true);
	}
	
	@Override
	public String getToolTip() {
		return "Site " + getModel().getName();
	}
}
