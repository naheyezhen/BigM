package org.bigraph.model;

import org.bigraph.model.Link.ChangeLinkSortDescriptor;
import org.bigraph.model.ModelObject.Identifier.Resolver;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.descriptors.ChangeCreationException;
import org.bigraph.model.changes.descriptors.IChangeDescriptor;
import org.bigraph.model.changes.descriptors.experimental.IDescriptorStepExecutor;
import org.bigraph.model.changes.descriptors.experimental.IDescriptorStepValidator;

final class LinkDescriptorHandler
		implements IDescriptorStepExecutor, IDescriptorStepValidator {
	@Override
	public boolean tryValidateChange(Process context, IChangeDescriptor change)
			throws ChangeCreationException {
		final Resolver resolver = context.getResolver();
		final PropertyScratchpad scratch = context.getScratch();
		if (change instanceof ChangeLinkSortDescriptor) {
			ChangeLinkSortDescriptor co = (ChangeLinkSortDescriptor)change;
			Link l = co.getLink().lookup(scratch, resolver);

			if (l == null)
				throw new ChangeCreationException(co,
						"" + co.getLink() + " didn't resolve to a Link");
		} else return false;
		return true;
	}

	@Override
	public boolean executeChange(Resolver r, IChangeDescriptor change) {
		if (change instanceof ChangeLinkSortDescriptor) {
			ChangeLinkSortDescriptor co = (ChangeLinkSortDescriptor)change;
			co.getLink().lookup(null, r).setLinkSort(co.getLinksort());
		} else return false;
		return true;
	}

}
