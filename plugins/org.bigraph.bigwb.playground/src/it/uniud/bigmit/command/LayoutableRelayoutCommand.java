package it.uniud.bigmit.command;

import it.uniud.bigmit.model.BRS;
import it.uniud.bigmit.model.Reaction;

import org.bigraph.model.Bigraph;
import org.bigraph.model.Edge;
import org.bigraph.model.Layoutable;
import org.bigraph.model.ModelObject;
import org.bigraph.model.changes.ChangeGroup;
import org.eclipse.draw2d.geometry.Rectangle;

import dk.itu.bigm.editors.bigraph.commands.ChangeCommand;
import dk.itu.bigm.model.LayoutUtilities;

public class LayoutableRelayoutCommand extends ChangeCommand {
	private ChangeGroup cg = new ChangeGroup();
	
	public LayoutableRelayoutCommand() {
		setChange(cg);
	}
	
	private ModelObject model;
	private ModelObject parent;
	private Rectangle layout;
	
	public void setConstraint(Object rect) {
		if (rect instanceof Rectangle)
			layout = (Rectangle)rect;
	}

	public void setModel(Object model) {
		if (model instanceof Layoutable)
			this.model = (Layoutable)model;
		if (model instanceof ModelObject)
			this.model = (ModelObject)model;
	}
	
	@Override
	public void prepare() {
		cg.clear();
		if (model == null || layout == null)
			return;
		
		
		if((model instanceof Bigraph) || (model instanceof Reaction)){
			
			if(parent instanceof BRS){
				setTarget((BRS)parent);
				cg.add(((BRS)parent).changeLayoutChild(model,layout));
			}else if(parent instanceof Reaction){
				setTarget((Reaction)parent);
				cg.add(((Reaction)parent).changeLayoutChild((Bigraph)model,layout));
			}
		}else{
			setTarget(((Layoutable)model).getBigraph());
			if ((model instanceof Edge || noOverlap()) && boundariesSatisfied())
				cg.add(LayoutUtilities.changeLayout(((Layoutable)model), layout));
		}
	}
	
	public boolean noOverlap() {
		for (Layoutable i : ((Layoutable)model).getParent().getChildren()) {
			if (i instanceof Edge || i == model)
				continue;
			else if (LayoutUtilities.getLayout(i).intersects(layout))
				return false;
		}
		return true;
	}
	
	private boolean boundariesSatisfied() {
		if (!(((Layoutable)model).getParent() instanceof Bigraph))
			return true;
		return true;
	}

	public ModelObject getParent() {
		return parent;
	}

	public void setParent(ModelObject parent) {
		this.parent = parent;
	}
}
