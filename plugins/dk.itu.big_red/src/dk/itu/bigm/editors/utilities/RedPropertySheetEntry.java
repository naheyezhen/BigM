package dk.itu.bigm.editors.utilities;

import java.util.EventObject;

import org.bigraph.model.changes.ChangeGroup;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.IChangeExecutor;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheetEntry;

import dk.itu.bigm.editors.bigraph.commands.ChangeCommand;
import dk.itu.bigm.editors.bigraph.parts.IBigraphPart;

public class RedPropertySheetEntry extends PropertySheetEntry {
	public RedPropertySheetEntry() {
		this(null);
	}
	
	private final CommandStack commandStack;
	private final CommandStackListener commandStackListener;
	public RedPropertySheetEntry(CommandStack commandStack) {
		this.commandStack = commandStack;
		commandStackListener = (commandStack != null ?
					new CommandStackListener() {
				@Override
				public void commandStackChanged(EventObject event) {
					refreshFromRoot();
				}
			} : null);
		if (commandStackListener != null)
			commandStack.addCommandStackListener(commandStackListener);
	}
	
	@Override
	public void dispose() {
		if (commandStackListener != null)
			commandStack.removeCommandStackListener(commandStackListener);
		super.dispose();
	}
	
	public CommandStack getCommandStack() {
		if (getParent() != null) {
			return getParent().getCommandStack();
		} else {
			if (commandStack == null)
				throw new Error("BUG: root with no command stack");
			return commandStack;
		}
	}
	
	@Override
	protected RedPropertySheetEntry getParent() {
		return (RedPropertySheetEntry)super.getParent();
	}
	
	@Override
	protected RedPropertySheetEntry createChildEntry() {
		return new RedPropertySheetEntry();
	}
	
	@Override
	protected IRedPropertySource getPropertySource(Object object) {
		IPropertySource r = super.getPropertySource(object);
		return (r instanceof IRedPropertySource ?
				(IRedPropertySource)r : null);
	}
	
	@Override
	public void resetPropertyValue() {
		if (getParent() == null)
			return;
		Object[] values = getParent().getValues();
		ChangeGroup cg = new ChangeGroup();
		IChangeExecutor ex = null;
		for (int i = 0; i < values.length; i++) {
			Object o = values[i];
			if (ex == null && o instanceof IBigraphPart)
				ex = ((IBigraphPart)o).getBigraph();
			IRedPropertySource rps = getPropertySource(values[i]);
			if (rps == null)
				continue;
			IChange rch =
					rps.resetPropertyValueChange(getDescriptor().getId());
			if (rch != null)
				cg.add(rch);
		}
		if (cg.size() > 0) {
			getCommandStack().execute(new ChangeCommand(cg, ex));
			refreshFromRoot();
		}
	}
	
	@Override
	protected void valueChanged(PropertySheetEntry child) {
		valueChanged((RedPropertySheetEntry)child, new ChangeGroup());
	}
	
	private void valueChanged(
			RedPropertySheetEntry child, ChangeGroup cg) {
		Object[] values = getValues();
		IChangeExecutor ex = null;
		for (int i = 0; i < values.length; i++) {
			Object o = values[i];
			if (ex == null && o instanceof IBigraphPart)
				ex = ((IBigraphPart)o).getBigraph();
			IRedPropertySource rps = getPropertySource(values[i]);
			if (rps == null)
				continue;
			IChange rch = rps.setPropertyValueChange(
					child.getDescriptor().getId(), child.getEditValue(i));
			if (rch != null)
				cg.add(rch);
		}
		if (getParent() != null) {
			getParent().valueChanged(this, cg);
		} else{
			getCommandStack().execute(new ChangeCommand(cg, ex));
		}
	}
}
