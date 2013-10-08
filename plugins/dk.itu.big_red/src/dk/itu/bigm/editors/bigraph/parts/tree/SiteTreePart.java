package dk.itu.bigm.editors.bigraph.parts.tree;

import org.bigraph.model.Site;
import org.eclipse.gef.EditPolicy;
import org.eclipse.jface.resource.ImageDescriptor;
import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.editors.bigraph.policies.LayoutableDeletePolicy;

public class SiteTreePart extends AbstractTreePart {
	@Override
	public Site getModel() {
		return (Site)super.getModel();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new LayoutableDeletePolicy());
	}
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return BigMPlugin.getImageDescriptor(
				"resources/icons/bigraph-palette/site.png");
	}
}
