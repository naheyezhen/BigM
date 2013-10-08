package org.bigraph.model;

import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.IStepExecutor;
import org.bigraph.model.changes.IStepValidator;

public class LinkHandler implements IStepExecutor, IStepValidator {
	@Override
	public boolean executeChange(IChange b) {
		if (b instanceof Link.ChangeLinkSort) {
			Link.ChangeLinkSort c = (Link.ChangeLinkSort)b;
			c.getCreator().setLinkSort(c.linkSort);
		} else return false;
		return true;
	}
	
	@Override
	public boolean tryValidateChange(Process process, IChange b)
			throws ChangeRejectedException {
		if (b instanceof Link.ChangeLinkSort) {
			
		} else return false;
		return true;
	}
}
