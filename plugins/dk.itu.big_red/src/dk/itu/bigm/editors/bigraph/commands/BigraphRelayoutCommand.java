package dk.itu.bigm.editors.bigraph.commands;

import org.bigraph.model.Bigraph;

import dk.itu.bigm.model.LayoutUtilities;

public class BigraphRelayoutCommand extends ChangeCommand {
	public void setBigraph(Bigraph bigraph) {
		setTarget(bigraph);
		setChange(bigraph != null ? LayoutUtilities.relayout(bigraph) : null);
	}
}
