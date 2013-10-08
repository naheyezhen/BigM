package org.bigraph.model;

import java.util.List;

import org.bigraph.model.ModelObject.Identifier.Resolver;
import org.bigraph.model.SortSet.ChangeAddPlaceSortDescriptor;
import org.bigraph.model.SortSet.ChangeAddSortSetDescriptor;
import org.bigraph.model.SortSet.ChangeRemoveSortSetDescriptor;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.descriptors.ChangeCreationException;
import org.bigraph.model.changes.descriptors.IChangeDescriptor;
import org.bigraph.model.changes.descriptors.experimental.IDescriptorStepExecutor;
import org.bigraph.model.changes.descriptors.experimental.IDescriptorStepValidator;

final class SortSetDescriptorHandler implements IDescriptorStepExecutor,
		IDescriptorStepValidator {
	@Override
	public boolean tryValidateChange(Process context, IChangeDescriptor change)
			throws ChangeCreationException {
		final PropertyScratchpad scratch = context.getScratch();
		final Resolver resolver = context.getResolver();
		if (change instanceof ChangeAddPlaceSortDescriptor) {
			ChangeAddPlaceSortDescriptor cd = (ChangeAddPlaceSortDescriptor)change;
			SortSet s = cd.getTarget().lookup(scratch, resolver);
			
			if (s == null)
				throw new ChangeCreationException(cd,
						"" + cd.getTarget() + ": lookup failed");
			
			NamedModelObjectDescriptorHandler.checkName(scratch, cd,
					cd.getSort(), s.getNamespacePlaceSort(),
					cd.getSort().getName()); 
		} else if (change instanceof ChangeAddSortSetDescriptor) {
			ChangeAddSortSetDescriptor cd =
					(ChangeAddSortSetDescriptor)change;
			SortSet s = cd.getTarget().lookup(scratch, resolver);
			
			if (s == null)
				throw new ChangeCreationException(cd,
						"" + cd.getTarget() + ": lookup failed");
			
			SortSet ch = cd.getSortSet();
			if (ch == null)
				throw new ChangeCreationException(cd,
						"Can't insert a null sortset");
			
			int position = cd.getPosition();
			if (position < -1 ||
					position > s.getSortSets(scratch).size())
				throw new ChangeCreationException(cd,
						"" + position + " is not a valid position");
		} else if (change instanceof ChangeRemoveSortSetDescriptor) {
			ChangeRemoveSortSetDescriptor cd =
					(ChangeRemoveSortSetDescriptor)change;
			SortSet s = cd.getTarget().lookup(scratch, resolver);
			
			if (s == null)
				throw new ChangeCreationException(cd,
						"" + cd.getTarget() + ": lookup failed");
			
			List<? extends SortSet> sorts = s.getSortSets(scratch);
			int position = cd.getPosition();
			if (position == -1)
				position = sorts.size() - 1;
			if (position < 0 || position >= sorts.size())
				throw new ChangeCreationException(cd,
						"" + position + " is not a valid position");
			
			if (!sorts.get(position).equals(cd.getSortSet()))
				throw new ChangeCreationException(cd,
						"" + cd.getSortSet() +
						" is not at position " + position);
		} else return false;
		return true;
	}

	@Override
	public boolean executeChange(Resolver resolver, IChangeDescriptor change) {
		if (change instanceof ChangeAddPlaceSortDescriptor) {
			ChangeAddPlaceSortDescriptor cd = (ChangeAddPlaceSortDescriptor)change;
			SortSet s = cd.getTarget().lookup(null, resolver);
			PlaceSort ps = new PlaceSort();
			
			ps.setName(s.getNamespacePlaceSort().put(cd.getSort().getName(), ps));
			s.addPlaceSort(cd.getPosition(), ps);
		} else if (change instanceof ChangeAddSortSetDescriptor) {
			ChangeAddSortSetDescriptor cd =
					(ChangeAddSortSetDescriptor)change;
			cd.getTarget().lookup(null, resolver).addSortSet(
					cd.getPosition(), cd.getSortSet());
		} else if (change instanceof ChangeRemoveSortSetDescriptor) {
			ChangeRemoveSortSetDescriptor cd =
					(ChangeRemoveSortSetDescriptor)change;
			cd.getTarget().lookup(null, resolver).removeSortSet(
					cd.getSortSet());
		} else return false;
		return true;
	}
}
