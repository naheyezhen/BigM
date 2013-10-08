package dk.itu.bigm.editors.bigraph.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import dk.itu.bigm.editors.bigraph.CombinedCommandFactory;

public class LayoutableDeletePolicy extends ComponentEditPolicy {
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		return CombinedCommandFactory.createDeleteCommand(deleteRequest);
	}
}
