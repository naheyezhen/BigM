package org.bigraph.model;

import java.util.ArrayList;
import java.util.List;

import org.bigraph.model.LinkSort.ChangeRemoveLinkSort;
import org.bigraph.model.ModelObject.Identifier.Resolver;
import org.bigraph.model.ModelObject;
import org.bigraph.model.PlaceSort.ChangeRemovePlaceSort;
import org.bigraph.model.assistants.ExecutorManager;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.assistants.BigmProperty;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.IChangeExecutor;
import org.bigraph.model.changes.descriptors.BoundDescriptor;
import org.bigraph.model.changes.descriptors.ChangeCreationException;
import org.bigraph.model.changes.descriptors.IChangeDescriptor;
import org.bigraph.model.changes.descriptors.experimental.DescriptorExecutorManager;
import org.bigraph.model.interfaces.ISortSet;
import org.bigraph.model.names.HashMapNamespace;
import org.bigraph.model.names.Namespace;
import org.bigraph.model.names.policies.StringNamePolicy;

public class SortSet extends ModelObject
		implements ISortSet, IChangeExecutor,
				ModelObject.Identifier.Resolver {
	private SortSet parent;
	private List<SortSet> sortSets = new ArrayList<SortSet>();
	
	/**
	 * The property name fired when a place sort is added or removed.
	 */
	@BigmProperty(fired = PlaceSort.class, retrieved = List.class)
	public static final String PROPERTY_PLACESORT = "SortSetPlaceSort";
	
	/**
	 * The property name fired when a link sort is added or removed.
	 */
	@BigmProperty(fired = LinkSort.class, retrieved = List.class)
	public static final String PROPERTY_LINKSORT = "SortSetLinkSort";
	
	@BigmProperty(fired = SortSet.class, retrieved = List.class)
	public static final String PROPERTY_CHILD = "SortSetChild";
	
	@BigmProperty(fired = SortSet.class, retrieved = SortSet.class)
	public static final String PROPERTY_PARENT = "SortSetParent";
	
	abstract class SortSetChange extends ModelObjectChange {
		@Override
		public SortSet getCreator() {
			return SortSet.this;
		}
	}
	
	public final class ChangeAddPlaceSort extends SortSetChange {
		public final PlaceSort sort;
		public final String name;
		
		public ChangeAddPlaceSort(PlaceSort sort, String name) {
			this.sort = sort;
			this.name = name;
		}

		@Override
		public ChangeRemovePlaceSort inverse() {
			return sort.new ChangeRemovePlaceSort();
		}
		
		@Override
		public String toString() {
			return "Change(add place sort " + sort + " to sortset " +
					getCreator() + " with name " + name + ")";
		}
		
		@Override
		public boolean isReady() {
			return (sort != null);
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			context.<PlaceSort>getModifiableList(
					getCreator(), PROPERTY_PLACESORT, getPlaceSorts()).
				add(sort);
			context.setProperty(sort,
					PlaceSort.PROPERTY_SORTSET, getCreator());
			
			getCreator().getNamespacePlaceSort().put(context, name, sort);
			context.setProperty(sort, PlaceSort.PROPERTY_NAME, name);
		}
	}
	
	public final class ChangeAddLinkSort extends SortSetChange {
		public final LinkSort sort;
		public final String name;
		
		public ChangeAddLinkSort(LinkSort sort, String name) {
			this.sort = sort;
			this.name = name;
		}

		@Override
		public ChangeRemoveLinkSort inverse() {
			return sort.new ChangeRemoveLinkSort();
		}
		
		@Override
		public String toString() {
			return "Change(add link sort " + sort + " to sortset " +
					getCreator() + " with name " + name + ")";
		}
		
		@Override
		public boolean isReady() {
			return (sort != null);
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			context.<LinkSort>getModifiableList(
					getCreator(), PROPERTY_LINKSORT, getLinkSorts()).
				add(sort);
			context.setProperty(sort,
					LinkSort.PROPERTY_SORTSET, getCreator());
			
			getCreator().getNamespaceLinkSort().put(context, name, sort);
			context.setProperty(sort, LinkSort.PROPERTY_NAME, name);
		}
	}
	
	private Namespace<PlaceSort> ns1 = new HashMapNamespace<PlaceSort>(
			new StringNamePolicy() {
		@Override
		public String get(int value) {
			return "PlaceSort" + (value + 1);
		}
	});
	
	public Namespace<PlaceSort> getNamespacePlaceSort() {
		return ns1;
	}
	
	private Namespace<LinkSort> ns2 = new HashMapNamespace<LinkSort>(
			new StringNamePolicy() {
		@Override
		public String get(int value) {
			return "LinkSort" + (value + 1);
		}
	});
	
	public Namespace<LinkSort> getNamespaceLinkSort() {
		return ns2;
	}
	
	private ArrayList<PlaceSort> placeSorts = new ArrayList<PlaceSort>();
	private ArrayList<LinkSort> linkSorts = new ArrayList<LinkSort>();
	
	@Override
	public SortSet clone() {
		SortSet ss = (SortSet)super.clone();
		
		for (PlaceSort s : getPlaceSorts())
			ss.addPlaceSort(-1, s.clone(ss));
		
		for (LinkSort s : getLinkSorts())
			ss.addLinkSort(-1, s.clone(ss));
		
		for (SortSet t : getSortSets())
			ss.addSortSet(-1, t.clone());
		
		return ss;
	}
	
	protected void addPlaceSort(int position, PlaceSort s) {
		if (position == -1) {
			placeSorts.add(s);
		} else placeSorts.add(position, s);
		s.setSortSet(this);
		firePropertyChange(PROPERTY_PLACESORT, null, s);
	}
	
	protected void removePlaceSort(PlaceSort s) {
		placeSorts.remove(s);
		s.setSortSet(null);
		firePropertyChange(PROPERTY_PLACESORT, s, null);
	}

	protected void addLinkSort(int position, LinkSort s) {
		if (position == -1) {
			linkSorts.add(s);
		} else linkSorts.add(position, s);
		s.setSortSet(this);
		firePropertyChange(PROPERTY_LINKSORT, null, s);
	}

	protected void removeLinkSort(LinkSort s) {
		linkSorts.remove(s);
		s.setSortSet(null);
		firePropertyChange(PROPERTY_LINKSORT, s, null);
	}
	
	public PlaceSort getPlaceSort(String name) {
		for (PlaceSort s : getPlaceSorts())
			if (s.getName().equals(name))
				return s;
		PlaceSort s = null;
		for (SortSet ss : getSortSets())
			if ((s = ss.getPlaceSort(name)) != null)
				return s;
		return null;
	}
	
	public LinkSort getLinkSort(String name) {
		for (LinkSort s : getLinkSorts())
			if (s.getName().equals(name))
				return s;
		LinkSort s = null;
		for (SortSet ss : getSortSets())
			if ((s = ss.getLinkSort(name)) != null)
				return s;
		return null;
	}
	
	@Override
	public List<? extends PlaceSort> getPlaceSorts() {
		return placeSorts;
	}
	
	@Override
	public List<? extends LinkSort> getLinkSorts() {
		return linkSorts;
	}

	@SuppressWarnings("unchecked")
	public List<? extends PlaceSort> getPlaceSorts(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_PLACESORT, List.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<? extends LinkSort> getLinkSorts(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_LINKSORT, List.class);
	}

	public static final String CONTENT_TYPE = "dk.itu.bigm.sortset";
	
	@Override
	public void tryValidateChange(IChange b) throws ChangeRejectedException {
		ExecutorManager.getInstance().tryValidateChange(b);
	}
	
	@Override
	public void tryApplyChange(IChange b) throws ChangeRejectedException {
		ExecutorManager.getInstance().tryApplyChange(b);
	}

	static {
		ExecutorManager.getInstance().addHandler(new SortSetHandler());
	}
	
	@Override
	public void dispose() {
		if (placeSorts != null) {
			for (PlaceSort s : placeSorts)
				s.dispose();
			placeSorts.clear();
			placeSorts = null;
		}
		
		if (linkSorts != null) {
			for (LinkSort s : linkSorts)
				s.dispose();
			linkSorts.clear();
			linkSorts = null;
		}
		
		if (sortSets != null) {
			for (SortSet ss : sortSets)
				ss.dispose();
			sortSets.clear();
			sortSets = null;
		}

		super.dispose();
	}
	
	@Override
	protected Object getProperty(String name) {
		if (PROPERTY_PLACESORT.equals(name)) {
			return getPlaceSorts();
		} else if (PROPERTY_LINKSORT.equals(name)) {
			return getLinkSorts();
		} else if (PROPERTY_PARENT.equals(name)) {
			return getParent();
		} else if (PROPERTY_CHILD.equals(name)) {
			return getSortSets();
		} else return super.getProperty(name);
	}
	
	public SortSet getParent() {
		return parent;
	}
	
	public SortSet getParent(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_PARENT, SortSet.class);
	}
	
	protected void setParent(SortSet newValue) {
		SortSet oldValue = parent;
		parent = newValue;
		firePropertyChange(PROPERTY_PARENT, oldValue, newValue);
	}
	
	public List<? extends SortSet> getSortSets() {
		return sortSets;
	}
	
	@SuppressWarnings("unchecked")
	public List<? extends SortSet> getSortSets(
			PropertyScratchpad context) {
		return getProperty(context, PROPERTY_CHILD, List.class);
	}
	
	protected void addSortSet(int position, SortSet ss) {
		if (position == -1) {
			sortSets.add(ss);
		} else sortSets.add(position, ss);
		ss.setParent(this);
		firePropertyChange(PROPERTY_CHILD, null, ss);
	}
	
	protected void removeSortSet(SortSet ss) {
		sortSets.remove(ss);
		ss.setParent(null);
		firePropertyChange(PROPERTY_CHILD, ss, null);
	}
	
	public IChange changeAddPlaceSort(PlaceSort sort, String name) {
		return new ChangeAddPlaceSort(sort, name);
	}
	
	public IChange changeAddLinkSort(LinkSort sort, String name) {
		return new ChangeAddLinkSort(sort, name);
	}
	
	@Override
	public Object lookup(
			PropertyScratchpad context, ModelObject.Identifier identifier) {
		if (identifier instanceof SortSet.Identifier) {
			return this;
		} else if (identifier instanceof PlaceSort.Identifier) {
			return getPlaceSort(((PlaceSort.Identifier)identifier).getName());
		}else if (identifier instanceof LinkSort.Identifier) {
			return getLinkSort(((LinkSort.Identifier)identifier).getName());
		}	
		return null;
	}
	
	public static final class Identifier implements ModelObject.Identifier {
		@Override
		public SortSet lookup(PropertyScratchpad context, Resolver r) {
			return require(r.lookup(context, this), SortSet.class);
		}
	}
	
	abstract static class SortSetChangeDescriptor
			extends ModelObjectChangeDescriptor {
		static {
			DescriptorExecutorManager.getInstance().addHandler(
					new SortSetDescriptorHandler());
		}
		
		@Override
		public IChange createChange(PropertyScratchpad context, Resolver r)
				throws ChangeCreationException {
			return new BoundDescriptor(r, this);
		}
	}
	
	public static final class ChangeAddPlaceSortDescriptor
			extends SortSetChangeDescriptor {
		private final Identifier target;
		private final int position;
		private final PlaceSort.Identifier sort;
		
		public ChangeAddPlaceSortDescriptor(
				Identifier target, int position, PlaceSort.Identifier sort) {
			this.target = target;
			this.position = position;
			this.sort = sort;
		}
		
		public Identifier getTarget() {
			return target;
		}
		
		public int getPosition() {
			return position;
		}
		
		public PlaceSort.Identifier getSort() {
			return sort;
		}
		
		@Override
		public IChange createChange(PropertyScratchpad context, Resolver r)
				throws ChangeCreationException {
			SortSet ss = getTarget().lookup(context, r);
			if (ss == null)
				throw new ChangeCreationException(this,
						"" + getTarget() + ": lookup failed");
			return ss.changeAddPlaceSort(new PlaceSort(), getSort().getName());
		}
		
		@Override
		public IChangeDescriptor inverse() {
			throw new UnsupportedOperationException(
					"FIXME: ChangeAddSortDescriptor.inverse() " +
					"not implementable");
		}
	}
	
	public static final class ChangeAddLinkSortDescriptor extends
			SortSetChangeDescriptor {
		private final Identifier target;
		private final int position;
		private final LinkSort.Identifier sort;

		public ChangeAddLinkSortDescriptor(Identifier target, int position,
				LinkSort.Identifier sort) {
			this.target = target;
			this.position = position;
			this.sort = sort;
		}

		public Identifier getTarget() {
			return target;
		}

		public int getPosition() {
			return position;
		}

		public LinkSort.Identifier getSort() {
			return sort;
		}

		@Override
		public IChange createChange(PropertyScratchpad context, Resolver r)
				throws ChangeCreationException {
			SortSet ss = getTarget().lookup(context, r);
			if (ss == null)
				throw new ChangeCreationException(this, "" + getTarget()
						+ ": lookup failed");
			return ss.changeAddLinkSort(new LinkSort(), getSort().getName());
		}

		@Override
		public IChangeDescriptor inverse() {
			throw new UnsupportedOperationException(
					"FIXME: ChangeAddSortDescriptor.inverse() "
							+ "not implementable");
		}
	}
	
	public static final class ChangeAddSortSetDescriptor
			extends SortSetChangeDescriptor {
		private final Identifier target;
		private final int position;
		private final SortSet sortSet;
		
		public ChangeAddSortSetDescriptor(
				Identifier target, int position, SortSet sortSet) {
			this.target = target;
			this.position = position;
			this.sortSet = sortSet;
		}
		
		public Identifier getTarget() {
			return target;
		}
		
		public int getPosition() {
			return position;
		}
		
		public SortSet getSortSet() {
			return sortSet;
		}
		
		@Override
		public IChangeDescriptor inverse() {
			return new ChangeRemoveSortSetDescriptor(
					getTarget(), getPosition(), getSortSet());
		}
		
		@Override
		public void simulate(PropertyScratchpad context, Resolver r) {
			SortSet self = getTarget().lookup(context, r);
			context.<SortSet>getModifiableList(
					self, PROPERTY_CHILD, self.getSortSets()).add(
							getSortSet());
			context.setProperty(getSortSet(), PROPERTY_PARENT, self);
		}
	}
	
	public static final class ChangeRemoveSortSetDescriptor
			extends SortSetChangeDescriptor {
		private final Identifier target;
		private final int position;
		private final SortSet sortSet;

		public ChangeRemoveSortSetDescriptor(
				Identifier target, int position, SortSet sortSet) {
			this.target = target;
			this.position = position;
			this.sortSet = sortSet;
		}

		public Identifier getTarget() {
			return target;
		}

		public int getPosition() {
			return position;
		}

		public SortSet getSortSet() {
			return sortSet;
		}

		@Override
		public IChangeDescriptor inverse() {
			return new ChangeAddSortSetDescriptor(
					getTarget(), getPosition(), getSortSet());
		}
		
		@Override
		public void simulate(PropertyScratchpad context, Resolver r) {
			SortSet self = getTarget().lookup(context, r);
			
			context.<SortSet>getModifiableList(
					self, PROPERTY_CHILD, self.getSortSets()).remove(
							getSortSet());
			context.setProperty(getSortSet(), PROPERTY_PARENT, null);
		}
	}
}