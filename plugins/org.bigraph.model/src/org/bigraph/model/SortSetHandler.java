package org.bigraph.model;

import org.bigraph.model.LinkSort.ChangeRemoveLinkSort;
import org.bigraph.model.PlaceSort.ChangeRemovePlaceSort;
import org.bigraph.model.SortSet.ChangeAddLinkSort;
import org.bigraph.model.SortSet.ChangeAddPlaceSort;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.IStepExecutor;
import org.bigraph.model.changes.IStepValidator;
import org.bigraph.model.names.Namespace;

final class SortSetHandler implements IStepExecutor, IStepValidator {
	@Override
	public boolean executeChange(IChange b) {
		if(b instanceof ChangeAddPlaceSort) {
			ChangeAddPlaceSort s = (ChangeAddPlaceSort)b;
			Namespace<PlaceSort> ns = s.getCreator().getNamespacePlaceSort();
			s.sort.setName(ns.put(s.name, s.sort));
			s.getCreator().addPlaceSort(-1, s.sort);
		} else if(b instanceof ChangeAddLinkSort) {
			ChangeAddLinkSort s = (ChangeAddLinkSort)b;
			Namespace<LinkSort> ns = s.getCreator().getNamespaceLinkSort();
			s.sort.setName(ns.put(s.name, s.sort));
			s.getCreator().addLinkSort(-1, s.sort);
		} else if (b instanceof ChangeRemovePlaceSort) {
			ChangeRemovePlaceSort s = (ChangeRemovePlaceSort)b;
			Namespace<PlaceSort> ns =
					s.getCreator().getSortSet().getNamespacePlaceSort();
			s.getCreator().getSortSet().removePlaceSort(s.getCreator());
			ns.remove(s.getCreator().getName());
		} else if(b instanceof ChangeRemoveLinkSort){
			ChangeRemoveLinkSort s = (ChangeRemoveLinkSort)b;
			Namespace<LinkSort> ns =
					s.getCreator().getSortSet().getNamespaceLinkSort();
			s.getCreator().getSortSet().removeLinkSort(s.getCreator());
			ns.remove(s.getCreator().getName());
		} else return false;
		return true;
	}
	
	@Override
	public boolean tryValidateChange(Process process, IChange b)
			throws ChangeRejectedException {
		final PropertyScratchpad context = process.getScratch();
		if (b instanceof ChangeAddPlaceSort) {
			ChangeAddPlaceSort c = (ChangeAddPlaceSort)b;
			NamedModelObjectHandler.checkName(context, c, c.sort,
					c.getCreator().getNamespacePlaceSort(), c.name);
			if (c.sort.getSortSet(context) != null)
				throw new ChangeRejectedException(b,
						"" + c.sort + " already has a parent");
		} else if (b instanceof ChangeAddLinkSort) {
			ChangeAddLinkSort c = (ChangeAddLinkSort)b;
			NamedModelObjectHandler.checkName(context, c, c.sort,
					c.getCreator().getNamespaceLinkSort(), c.name);
			if (c.sort.getSortSet(context) != null)
				throw new ChangeRejectedException(b,
						"" + c.sort + " already has a parent");
		} else if (b instanceof ChangeRemovePlaceSort) {
			PlaceSort ps = ((ChangeRemovePlaceSort)b).getCreator();
			if (ps.getSortSet(context) == null)
				throw new ChangeRejectedException(b,
						"" + ps + " doesn't have a parent");
		} else if(b instanceof ChangeRemoveLinkSort) {
			LinkSort ls = ((ChangeRemoveLinkSort)b).getCreator();
			if (ls.getSortSet(context) == null)
				throw new ChangeRejectedException(b,
						"" + ls + " doesn't have a parent");
		} else return false;
		return true;
	}
}
