package org.bigraph.model;

import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.IStepExecutor;
import org.bigraph.model.changes.IStepValidator;

public class InnerNameHandler implements IStepExecutor, IStepValidator {
	@Override
	public boolean executeChange(IChange b) {
		if (b instanceof InnerName.ChangeInnerSort) {
			InnerName.ChangeInnerSort c = (InnerName.ChangeInnerSort)b;
			c.getCreator().setInnerSort(c.innerSort);
		} else return false;
		return true;
	}
	
	@Override
	public boolean tryValidateChange(Process process, IChange b)
			throws ChangeRejectedException {
		if (b instanceof InnerName.ChangeInnerSort) {
			
		} else return false;
		return true;
	}
}
