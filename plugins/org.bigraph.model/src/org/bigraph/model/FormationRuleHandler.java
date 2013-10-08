package org.bigraph.model;

import org.bigraph.model.FormationRule.ChangeRemoveFormationRule;
import org.bigraph.model.FormationRule.ChangeSetFormRuleCons;
import org.bigraph.model.FormationRule.ChangeSetFormRuleSort1;
import org.bigraph.model.FormationRule.ChangeSetFormRuleSort2;
import org.bigraph.model.FormationRule.ChangeSetFormRuleType;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.IStepExecutor;
import org.bigraph.model.changes.IStepValidator;
import org.bigraph.model.names.Namespace;

final class FormationRuleHandler implements IStepExecutor, IStepValidator {
	@Override
	public boolean executeChange(IChange b) {
		if (b instanceof ChangeRemoveFormationRule) {
			ChangeRemoveFormationRule c = (ChangeRemoveFormationRule)b;
			Namespace<FormationRule> ns =
					c.getCreator().getFormRules().getNamespace();
			c.getCreator().getFormRules().removeFormationRule(c.getCreator());
			ns.remove(c.getCreator().getName());
		} else if (b instanceof ChangeSetFormRuleType) {
			ChangeSetFormRuleType c = (ChangeSetFormRuleType)b;
			c.getCreator().setType(c.type);
		} else if (b instanceof ChangeSetFormRuleSort1) {
			ChangeSetFormRuleSort1 c = (ChangeSetFormRuleSort1)b;
			c.getCreator().setSort1(c.sort1);
		} else if (b instanceof ChangeSetFormRuleSort2) {
			ChangeSetFormRuleSort2 c = (ChangeSetFormRuleSort2)b;
			c.getCreator().setSort2(c.sort2);
		} else if (b instanceof ChangeSetFormRuleCons) {
			ChangeSetFormRuleCons c = (ChangeSetFormRuleCons)b;
			c.getCreator().setConstraint(c.constraint);
		} else return false;
		return true;
	}
	
	@Override
	public boolean tryValidateChange(Process process, IChange b)
			throws ChangeRejectedException {
		final PropertyScratchpad context = process.getScratch();
		if (b instanceof ChangeRemoveFormationRule) {
			FormationRule fr = ((ChangeRemoveFormationRule)b).getCreator();
			if (fr.getFormRules(context) == null)
				throw new ChangeRejectedException(b,
						"" + fr + " doesn't have a parent");
		} else if (b instanceof ChangeSetFormRuleType) {
			/* do nothing */
		} else if (b instanceof ChangeSetFormRuleSort1) {
			/* do nothing */
		} else if (b instanceof ChangeSetFormRuleSort2) {
			/* do nothing */
		} else if (b instanceof ChangeSetFormRuleCons) {
			/* do nothing */
		} else return false;
		return true;
	}
}
