package dk.itu.bigm.editors.bigraph.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import dk.itu.bigm.editors.bigraph.CombinedCommandFactory;

public class LinkConnectionDeletePolicy extends ConnectionEditPolicy {
    @Override
	protected Command getDeleteCommand(GroupRequest request) {
    	return CombinedCommandFactory.createDeleteCommand(request);
    }
}
