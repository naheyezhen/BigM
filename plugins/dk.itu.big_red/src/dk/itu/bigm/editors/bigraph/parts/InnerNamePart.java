package dk.itu.bigm.editors.bigraph.parts;

import org.bigraph.model.InnerName;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import dk.itu.bigm.editors.bigraph.figures.NameFigure;
import dk.itu.bigm.editors.bigraph.figures.assistants.FixedPointAnchor.Orientation;
import dk.itu.bigm.editors.bigraph.policies.EdgeCreationPolicy;
import dk.itu.bigm.editors.bigraph.policies.LayoutableDeletePolicy;
import dk.itu.bigm.editors.bigraph.policies.LayoutableLayoutPolicy;

/**
 * NameParts represent {@link InnerName}s, the model objects which define
 * (along with outer names) a bigraph's interface.
 * @see InnerName
 * @author alec
 */
public class InnerNamePart extends PointPart {
	@Override
	protected IFigure createFigure() {
		return new NameFigure();
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new LayoutableLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new LayoutableDeletePolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new EdgeCreationPolicy());
	}
	
	@Override
	protected void refreshVisuals(){
		super.refreshVisuals();
		((NameFigure)getFigure()).setName(getModel().getName());
	}

	@Override
	public Orientation getAnchorOrientation() {
		return Orientation.NORTH;
	}
	
	@Override
	String getTypeName() {
		return "Inner name";
	}
}
