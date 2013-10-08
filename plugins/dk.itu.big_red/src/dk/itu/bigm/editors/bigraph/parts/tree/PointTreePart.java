package dk.itu.bigm.editors.bigraph.parts.tree;

import org.bigraph.model.InnerName;
import org.bigraph.model.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.jface.resource.ImageDescriptor;
import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.editors.bigraph.policies.LayoutableDeletePolicy;

public class PointTreePart extends AbstractTreePart {
	@Override
	public Point getModel() {
		return (Point)super.getModel();
	}

	@Override
	protected void createEditPolicies() {
		if (getModel() instanceof InnerName)
			installEditPolicy(EditPolicy.COMPONENT_ROLE,
					new LayoutableDeletePolicy());
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return BigMPlugin.getImageDescriptor(
				"resources/icons/bigraph-palette/inner.png");
	}
}