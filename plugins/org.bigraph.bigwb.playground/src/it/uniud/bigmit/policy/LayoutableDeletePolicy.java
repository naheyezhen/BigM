package it.uniud.bigmit.policy;

import it.uniud.bigmit.command.DeleteCommand;
import it.uniud.bigmit.model.BRS;

import java.util.List;

import org.bigraph.model.ModelObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class LayoutableDeletePolicy extends ComponentEditPolicy {
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		@SuppressWarnings("unchecked")
		List<EditPart> parts= deleteRequest.getEditParts();
		DeleteCommand command= new DeleteCommand();
		for(EditPart part: parts){
			
			command.setTarget((BRS)part.getParent().getModel());
			command.setSon((ModelObject)part.getModel());
		}
		command.prepare();
		return command;
	}
}
