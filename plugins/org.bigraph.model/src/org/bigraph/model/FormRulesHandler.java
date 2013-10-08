package org.bigraph.model;

import org.bigraph.model.FormRules.ChangeAddFormationRule;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.IStepExecutor;
import org.bigraph.model.changes.IStepValidator;
import org.bigraph.model.names.Namespace;

final class FormRulesHandler implements IStepExecutor, IStepValidator {
	@Override
	public boolean executeChange(IChange b) {
		if (b instanceof ChangeAddFormationRule) {
			ChangeAddFormationRule c = (ChangeAddFormationRule)b;
			Namespace<FormationRule> ns = c.getCreator().getNamespace();
			c.formationRule.setName(ns.put(c.name, c.formationRule));
			c.getCreator().addFormationRule(-1, c.formationRule);
		} else return false;
		return true;
	}
	
	@Override
	public boolean tryValidateChange(Process process, IChange b)
			throws ChangeRejectedException {
		final PropertyScratchpad context = process.getScratch();
		if (b instanceof ChangeAddFormationRule) {
			ChangeAddFormationRule c = (ChangeAddFormationRule)b;
			NamedModelObjectHandler.checkName(context, c, c.formationRule,
					c.getCreator().getNamespace(), c.name);
			if (c.formationRule.getFormRules(context) != null)
				throw new ChangeRejectedException(b,
						"" + c.formationRule + " already has a parent");
		} else return false;
		return true;
	}
}
