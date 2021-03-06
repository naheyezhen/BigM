package dk.itu.bigm.editors.rule;

import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.RetargetAction;

import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.editors.bigraph.BigraphEditorActionBarContributor;

public class ActionBarContributor extends
		dk.itu.bigm.editors.assistants.ActionBarContributor {
	@Override
	protected void buildActions() {
		super.buildActions();
		
		addRetargetAction(new ZoomInRetargetAction());
		addRetargetAction(new ZoomOutRetargetAction());
		
		addRetargetAction(new RetargetAction(
				GEFActionConstants.TOGGLE_GRID_VISIBILITY,
				"Snap to grid", IAction.AS_CHECK_BOX) {
			{
				setImageDescriptor(BigMPlugin.getImageDescriptor(
						"resources/icons/actions/snap-to-grid.png"));
			}
		});
		addRetargetAction(new RetargetAction(
				GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY,
				"Snap to nearby objects", IAction.AS_CHECK_BOX) {
			{
				setImageDescriptor(BigMPlugin.getImageDescriptor(
						"resources/icons/actions/snap-to-object.png"));
			}
		});
		addRetargetAction(new RetargetAction(
				BigraphEditorActionBarContributor.ACTION_GUIDE,
				"Toggle guide display", IAction.AS_CHECK_BOX) {
			{
				setImageDescriptor(BigMPlugin.getImageDescriptor(
						"resources/icons/actions/guide-lines.png"));
			}
		});
	}

	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(getAction(GEFActionConstants.ZOOM_IN));
		toolBarManager.add(getAction(GEFActionConstants.ZOOM_OUT));
		toolBarManager.add(new ZoomComboContributionItem(getPage()));
		
		toolBarManager.add(new Separator());
		
		toolBarManager.add(
				getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
		toolBarManager.add(
				getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));
		toolBarManager.add(
				getAction(BigraphEditorActionBarContributor.ACTION_GUIDE));
	}

}
