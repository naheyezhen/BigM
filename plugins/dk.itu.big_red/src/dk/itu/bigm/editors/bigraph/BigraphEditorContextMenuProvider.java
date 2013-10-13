package dk.itu.bigm.editors.bigraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bigraph.model.Bigraph;
import org.bigraph.model.Control;
import org.bigraph.model.Edge;
import org.bigraph.model.Layoutable;
import org.bigraph.model.Link;
import org.bigraph.model.NamedModelObject;
import org.bigraph.model.Node;
import org.bigraph.model.Site;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.descriptors.BoundDescriptor;
import org.bigraph.model.changes.descriptors.ChangeCreationException;
import org.bigraph.model.utilities.FilteringIterable;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import dk.itu.bigm.editors.bigraph.actions.BigraphRelayoutAction;
import dk.itu.bigm.editors.bigraph.commands.ChangeCommand;
import dk.itu.bigm.editors.bigraph.parts.BigraphPart;
import dk.itu.bigm.editors.bigraph.parts.LinkConnectionPart;
import dk.itu.bigm.editors.bigraph.parts.LinkPart;
import dk.itu.bigm.editors.bigraph.parts.NodePart;
import dk.itu.bigm.editors.utilities.ModelPropertySource;
import dk.itu.bigm.model.ControlUtilities;
import dk.itu.bigm.model.ExtendedDataUtilities;
import dk.itu.bigm.model.LayoutUtilities;
import dk.itu.bigm.model.LinkStyleUtilities;
import dk.itu.bigm.model.LinkStyleUtilities.Style;

public class BigraphEditorContextMenuProvider extends ContextMenuProvider {
	public BigraphEditorContextMenuProvider(EditPartViewer viewer,
			ActionRegistry registry) {
		super(viewer);
		setActionRegistry(registry);
	}

	private ActionRegistry actionRegistry;

	protected ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	protected void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;
	}

	protected Iterable<? extends EditPart> getSelection() {
		return new FilteringIterable<EditPart>(EditPart.class, getViewer()
				.getSelectedEditParts());
	}

	protected EditPart getFirstSelection() {
		Iterator<? extends EditPart> it = getSelection().iterator();
		if (it.hasNext()) {
			return it.next();
		} else
			return null;
	}

	private void populateMenu(final PaletteViewer pv,
			final PaletteContainer pc, final IMenuManager menu) {
		for (Object j : pc.getChildren()) {
			if (j instanceof ToolEntry) {
				final ToolEntry k = (ToolEntry) j;
				Action toolAction = new Action(k.getLabel()) {
					@Override
					public void run() {
						pv.setActiveTool(k);
					}
				};
				menu.add(toolAction);
			} else if (j instanceof PaletteContainer) {
				PaletteContainer k = (PaletteContainer) j;
				MenuManager sub = new MenuManager(k.getLabel());
				populateMenu(pv, k, sub);
				menu.add(sub);
			} else if (j instanceof PaletteSeparator) {
				menu.add(new Separator());
			}
		}
	}

	private static final String GROUP_VARYING = "BigraphEditorContextMenuProvider+Varying";

	private final void addLinkOptions(final Link l, final IMenuManager menu) {
		MenuManager styleMenu = new MenuManager("Style");
		Style currentStyle = LinkStyleUtilities.getStyle(l);
		for (final Style i : Style.values()) {
			Action a = new Action(i.getDisplayName(), Action.AS_RADIO_BUTTON) {
				@Override
				public void run() {
					if (!isChecked())
						return;
					getViewer()
							.getEditDomain()
							.getCommandStack()
							.execute(
									new ChangeCommand(LinkStyleUtilities
											.changeStyle(l, i), l.getBigraph()));
				}
			};
			a.setChecked(i.equals(currentStyle));
			styleMenu.add(a);
		}
		menu.appendToGroup(GROUP_VARYING, styleMenu);

		if (l instanceof Edge) {
			Action a = new Action("Autolayout", Action.AS_CHECK_BOX) {
				@Override
				public void run() {
					Rectangle r;
					if (isChecked()) {
						r = null;
					} else
						r = LayoutUtilities.getLayout(l);
					getViewer()
							.getEditDomain()
							.getCommandStack()
							.execute(
									new ChangeCommand(LayoutUtilities
											.changeLayout(l, r), l.getBigraph()));
				}
			};
			a.setChecked(LayoutUtilities.getLayoutRaw(l) == null);
			menu.appendToGroup(GROUP_VARYING, a);
		}
	}

	private final void addClassNodeOptions(final NodePart n, final IMenuManager menu) {
		String nodeNameString = n.getModel().getName();
		Action a = new Action("isInstance", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				// 处理为实例或类				
				if(!n.getModel().getName().startsWith("_class_")){// 原来是实例的话
					try {
						// 这里相当于手工往commandstack里面添加一个change
						// getViewer().getEditDomain().getCommandStack() 获取commandstack
						getViewer().getEditDomain().getCommandStack().execute(
							// 创建一个command
							// changeCommand(Ichange,target)
							new ChangeCommand(
								// 创建一个修改名字的ichange
								// name-->identifier
								new NamedModelObject.ChangeNameDescriptor(n.getModel().getIdentifier(),"_class_" + System.currentTimeMillis())
									// 这个地方很纠结PropertyScratchpad 这个东西应该是一个上下文的玩意儿
									// 后面的resolver应该是要在上下文查找某个东西 具体不清楚。。。
									.createChange(new PropertyScratchpad(),n.getBigraph())
								, n.getBigraph()));
					} catch (ChangeCreationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{// 由类变成实例
					try {
						getViewer().getEditDomain().getCommandStack().execute(
							new ChangeCommand(
								new NamedModelObject.ChangeNameDescriptor(
									n.getModel().getIdentifier(),n.getBigraph().getFirstUnusedName((Layoutable)n.getModel()))
										.createChange(new PropertyScratchpad(),n.getBigraph()),n.getBigraph()));
					} catch (ChangeCreationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		// 如果是以空格开头 就说明是类，而不是实例
		a.setChecked(!nodeNameString.startsWith("_class_"));
		menu.appendToGroup(GROUP_VARYING, a);
	}
	
	private final void addAnonymousObjOptions(final NodePart n, final IMenuManager menu) {
		String nodeNameString = n.getModel().getName();
		Action a = new Action("isAnonymousObj", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				// 由于外层已经将class这种情况处理了（变灰不能点），所以这边就不考虑了~
				// 处理为实例或类				
				if(!n.getModel().getName().startsWith("_anonymousObj_")){// 普通实例 变成 匿名对象
					try {
						// 这里相当于手工往commandstack里面添加一个change
						// getViewer().getEditDomain().getCommandStack() 获取commandstack
						getViewer().getEditDomain().getCommandStack().execute(
							// 创建一个command
							// changeCommand(Ichange,target)
							new ChangeCommand(
								// 创建一个修改名字的ichange
								// name-->identifier
								new NamedModelObject.ChangeNameDescriptor(n.getModel().getIdentifier(),"_anonymousObj_" + System.currentTimeMillis())
									// 这个地方很纠结PropertyScratchpad 这个东西应该是一个上下文的玩意儿
									// 后面的resolver应该是要在上下文查找某个东西 具体不清楚。。。
									.createChange(new PropertyScratchpad(),n.getBigraph())
								, n.getBigraph()));
					} catch (ChangeCreationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{// 由类变成实例
					try {
						getViewer().getEditDomain().getCommandStack().execute(
							new ChangeCommand(
								new NamedModelObject.ChangeNameDescriptor(
									n.getModel().getIdentifier(),n.getBigraph().getFirstUnusedName((Layoutable)n.getModel()))
										.createChange(new PropertyScratchpad(),n.getBigraph()),n.getBigraph()));
					} catch (ChangeCreationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		// 不是以class开头 才可能是匿名对象
		a.setEnabled(!nodeNameString.startsWith("_class_"));
		a.setChecked(nodeNameString.startsWith("_anonymousObj_"));
		menu.appendToGroup(GROUP_VARYING, a);
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);

		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, getActionRegistry()
				.getAction(ActionFactory.UNDO.getId()));

		menu.appendToGroup(GEFActionConstants.GROUP_UNDO, getActionRegistry()
				.getAction(ActionFactory.REDO.getId()));

		menu.appendToGroup(GEFActionConstants.GROUP_COPY, getActionRegistry()
				.getAction(ActionFactory.CUT.getId()));

		menu.appendToGroup(GEFActionConstants.GROUP_COPY, getActionRegistry()
				.getAction(ActionFactory.COPY.getId()));

		menu.appendToGroup(GEFActionConstants.GROUP_COPY, getActionRegistry()
				.getAction(ActionFactory.PASTE.getId()));

		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, getActionRegistry()
				.getAction(ActionFactory.DELETE.getId()));

		menu.appendToGroup(GEFActionConstants.GROUP_EDIT, getActionRegistry()
				.getAction(ActionFactory.PROPERTIES.getId()));

		menu.add(new GroupMarker(GROUP_VARYING));

		EditPart selection = getFirstSelection();
		if (selection instanceof BigraphPart) {
			menu.appendToGroup(GROUP_VARYING,
					getActionRegistry().getAction(BigraphRelayoutAction.ID));
			System.out.println("bigraph part");
		} else if (selection instanceof LinkPart) {
			addLinkOptions(((LinkPart) selection).getModel(), menu);
		} else if (selection instanceof LinkConnectionPart) {
			addLinkOptions(((LinkConnectionPart) selection).getModel()
					.getLink(), menu);
		} else if (selection instanceof NodePart) {// 右键选中的是node
			System.out.println("node part");
			addClassNodeOptions((NodePart) selection, menu);
			addAnonymousObjOptions((NodePart) selection, menu);

		}

		menu.appendToGroup(GEFActionConstants.GROUP_REST, new Separator());

		MenuManager palette = new MenuManager("Palette");
		menu.appendToGroup(GEFActionConstants.GROUP_REST, palette);
		PaletteViewer pv = getViewer().getEditDomain().getPaletteViewer();
		populateMenu(getViewer().getEditDomain().getPaletteViewer(),
				pv.getPaletteRoot(), palette);
	}
}
