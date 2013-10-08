package org.bigraph.model;

import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.assistants.BigmProperty;
import org.bigraph.model.changes.Change;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.interfaces.ISort;
import org.bigraph.model.names.Namespace;

public class PlaceSort extends NamedModelObject implements ISort {
	/**
	 * The property name when this PlaceSort's containing {@link SortSet}
	 * changes.
	 */
	@BigmProperty(fired = SortSet.class, retrieved = SortSet.class)
	public static final String PROPERTY_SORTSET = "PlaceSortSortSet";
	
	abstract class SortChange extends ModelObjectChange {
		@Override
		public PlaceSort getCreator() {
			return PlaceSort.this;
		}
	}
	
	@Override
	protected Namespace<PlaceSort>
			getGoverningNamespace(PropertyScratchpad context) {
		return getSortSet(context).getNamespacePlaceSort();
	}
	
	public final class ChangeRemovePlaceSort extends SortChange {
		private String oldName;
		private SortSet oldSortSet;
		
		@Override
		public void beforeApply() {
			oldName = getCreator().getName();
			oldSortSet = getSortSet();
		}
		
		@Override
		public boolean canInvert() {
			return (oldSortSet != null);
		}
		
		@Override
		public Change inverse() {
			return oldSortSet.new ChangeAddPlaceSort(getCreator(), oldName);
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			SortSet ss = getCreator().getSortSet(context);
			
			context.<PlaceSort>getModifiableList(
					ss, SortSet.PROPERTY_PLACESORT, ss.getPlaceSorts()).
				remove(getCreator());
			context.setProperty(getCreator(),
					PlaceSort.PROPERTY_SORTSET, null);
			
			ss.getNamespacePlaceSort().remove(getCreator().getName(context));
			context.setProperty(getCreator(), PROPERTY_NAME, null);
		}
	}

	private SortSet sortSet = null;
	
	protected PlaceSort clone(SortSet m) {
		PlaceSort s = (PlaceSort)super.clone();
		
		m.getNamespacePlaceSort().put(s.getName(), s);
		
		return s;
	}
	
	public SortSet getSortSet() {
		return sortSet;
	}
	
	public SortSet getSortSet(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_SORTSET, SortSet.class);
	}
	
	void setSortSet(SortSet sortSet) {
		SortSet oldSortSet = this.sortSet;
		this.sortSet = sortSet;
		firePropertyChange(PROPERTY_SORTSET, oldSortSet, sortSet);
	}
	
	@Override
	protected Object getProperty(String name) {
		if (PROPERTY_SORTSET.equals(name)) {
			return getSortSet();
		} return super.getProperty(name);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	public IChange changeRemovePlaceSort() {
		return new ChangeRemovePlaceSort();
	}
	
	public static final class Identifier extends NamedModelObject.Identifier {
		public Identifier(String name) {
			super(name);
		}
		
		@Override
		public PlaceSort lookup(PropertyScratchpad context, Resolver r) {
			return require(r.lookup(context, this), PlaceSort.class);
		}
		
		@Override
		public Identifier getRenamed(String name) {
			return new Identifier(name);
		}
		
		@Override
		public String toString() {
			return "placesort " + getName();
		}
	}

	@Override
	public org.bigraph.model.NamedModelObject.Identifier getIdentifier() {
		return getIdentifier(null);
	}

	@Override
	public org.bigraph.model.NamedModelObject.Identifier getIdentifier(
			PropertyScratchpad context) {
		return new Identifier(getName(context));
	}
}