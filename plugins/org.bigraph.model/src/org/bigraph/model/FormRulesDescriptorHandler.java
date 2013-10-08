package org.bigraph.model;

import java.util.List;

import org.bigraph.model.FormRules.ChangeAddFormRulesDescriptor;
import org.bigraph.model.FormRules.ChangeAddFormationRuleDescriptor;
import org.bigraph.model.FormRules.ChangeRemoveFormRulesDescriptor;
import org.bigraph.model.FormRules.ChangeSetSortSetDescriptor;
import org.bigraph.model.ModelObject.Identifier.Resolver;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.descriptors.ChangeCreationException;
import org.bigraph.model.changes.descriptors.IChangeDescriptor;
import org.bigraph.model.changes.descriptors.experimental.IDescriptorStepExecutor;
import org.bigraph.model.changes.descriptors.experimental.IDescriptorStepValidator;

final class FormRulesDescriptorHandler implements IDescriptorStepExecutor,
		IDescriptorStepValidator {
	@Override
	public boolean tryValidateChange(Process context, IChangeDescriptor change)
			throws ChangeCreationException {
		final PropertyScratchpad scratch = context.getScratch();
		final Resolver resolver = context.getResolver();
		if (change instanceof ChangeAddFormationRuleDescriptor) {
			ChangeAddFormationRuleDescriptor cd = (ChangeAddFormationRuleDescriptor)change;
			FormRules f = cd.getTarget().lookup(scratch, resolver);
			
			if (f == null)
				throw new ChangeCreationException(cd,
						"" + cd.getTarget() + ": lookup failed");
			
			NamedModelObjectDescriptorHandler.checkName(scratch, cd,
					cd.getFormationRule(), f.getNamespace(),
					cd.getFormationRule().getName()); 
		} else if (change instanceof ChangeAddFormRulesDescriptor) {
			ChangeAddFormRulesDescriptor cd = (ChangeAddFormRulesDescriptor)change;
			FormRules f = cd.getTarget().lookup(scratch, resolver);
			
			if (f == null)
				throw new ChangeCreationException(cd,
						"" + cd.getTarget() + ": lookup failed");
			
			FormRules ch = cd.getFormRules();
			if (ch == null)
				throw new ChangeCreationException(cd,
						"Can't insert a null formation-rules");
			
			int position = cd.getPosition();
			if (position < -1 ||
					position > f.getFormRuless(scratch).size())
				throw new ChangeCreationException(cd,
						"" + position + " is not a valid position");
		} else if (change instanceof ChangeRemoveFormRulesDescriptor) {
			ChangeRemoveFormRulesDescriptor cd =
					(ChangeRemoveFormRulesDescriptor)change;
			FormRules f = cd.getTarget().lookup(scratch, resolver);
			
			if (f == null)
				throw new ChangeCreationException(cd,
						"" + cd.getTarget() + ": lookup failed");
			
			List<? extends FormRules> frs = f.getFormRuless(scratch);
			int position = cd.getPosition();
			if (position == -1)
				position = frs.size() - 1;
			if (position < 0 || position >= frs.size())
				throw new ChangeCreationException(cd,
						"" + position + " is not a valid position");
			
			if (!frs.get(position).equals(cd.getFormRules()))
				throw new ChangeCreationException(cd,
						"" + cd.getFormRules() +
						" is not at position " + position);
		} else if (change instanceof ChangeSetSortSetDescriptor) {
			ChangeSetSortSetDescriptor cd =
					(ChangeSetSortSetDescriptor)change;
			FormRules s = cd.getTarget().lookup(scratch, resolver);
			if (s == null)
				throw new ChangeCreationException(cd,
						"" + cd.getTarget() + ": lookup failed");
		} else return false;
		return true;
	}

	@Override
	public boolean executeChange(Resolver resolver, IChangeDescriptor change) {
		if (change instanceof ChangeAddFormationRuleDescriptor) {
			ChangeAddFormationRuleDescriptor cd = (ChangeAddFormationRuleDescriptor)change;
			FormRules f = cd.getTarget().lookup(null, resolver);
			FormationRule fr = new FormationRule();
			
			fr.setName(f.getNamespace().put(cd.getFormationRule().getName(), fr));
			f.addFormationRule(cd.getPosition(), fr);
		} else if (change instanceof ChangeAddFormRulesDescriptor) {
			ChangeAddFormRulesDescriptor cd = (ChangeAddFormRulesDescriptor)change;
			cd.getTarget().lookup(null, resolver).addFormRules(
					cd.getPosition(), cd.getFormRules());
		} else if (change instanceof ChangeRemoveFormRulesDescriptor) {
			ChangeRemoveFormRulesDescriptor cd = (ChangeRemoveFormRulesDescriptor)change;
			cd.getTarget().lookup(null, resolver).removeFormRules(
					cd.getFormRules());
		} else if (change instanceof ChangeSetSortSetDescriptor) {
			ChangeSetSortSetDescriptor cd =
					(ChangeSetSortSetDescriptor)change;
			cd.getTarget().lookup(null, resolver).setSortSet(
					cd.getNewSortSet());
		} else return false;
		return true;
	}
}
