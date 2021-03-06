package dk.itu.bigm.editors.bigraph;

import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;

import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.editors.AbstractGEFEditor;
import dk.itu.bigm.editors.actions.TogglePropertyAction;
import dk.itu.bigm.editors.assistants.ActionBarContributor;

public class BigraphEditorActionBarContributor extends ActionBarContributor {
	public static final String ACTION_GUIDE = TogglePropertyAction.getId(
			AbstractGEFEditor.PROPERTY_DISPLAY_GUIDES);
	
	@Override
	protected void buildActions() {
		super.buildActions();
		
		IWorkbenchWindow iww = getPage().getWorkbenchWindow();
		
		addAction(ActionFactory.NEW.create(iww));
		addAction(ActionFactory.SAVE.create(iww));
		addAction(ActionFactory.PRINT.create(iww));
		
		addRetargetAction(new DeleteRetargetAction());

		addRetargetAction((RetargetAction)ActionFactory.CUT.create(iww));
		addRetargetAction((RetargetAction)ActionFactory.COPY.create(iww));
		addRetargetAction((RetargetAction)ActionFactory.PASTE.create(iww));
		
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
				ACTION_GUIDE, "Toggle guide display", IAction.AS_CHECK_BOX) {
			{
				setImageDescriptor(BigMPlugin.getImageDescriptor(
						"resources/icons/actions/guide-lines.png"));
			}
		});
	}

	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
		toolBarManager.add(getAction(ActionFactory.REDO.getId()));
		toolBarManager.add(getAction(ActionFactory.DELETE.getId()));
		
		toolBarManager.add(new Separator());
		
		toolBarManager.add(getAction(ActionFactory.CUT.getId()));
		toolBarManager.add(getAction(ActionFactory.COPY.getId()));
		toolBarManager.add(getAction(ActionFactory.PASTE.getId()));
		
		toolBarManager.add(new Separator());
		
		toolBarManager.add(getAction(GEFActionConstants.ZOOM_IN));
		toolBarManager.add(getAction(GEFActionConstants.ZOOM_OUT));
		toolBarManager.add(new ZoomComboContributionItem(getPage()));
		
		toolBarManager.add(new Separator());
		
		toolBarManager.add(
				getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
		toolBarManager.add(
				getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));
		toolBarManager.add(getAction(ACTION_GUIDE));
	}
}
