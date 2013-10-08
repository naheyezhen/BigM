package org.bigraph.model;

import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.assistants.BigmProperty;
import org.bigraph.model.changes.Change;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.interfaces.ISort;
import org.bigraph.model.names.Namespace;

public class LinkSort extends NamedModelObject implements ISort {
	/**
	 * The property name when this LinkSort's containing {@link SortSet}
	 * changes.
	 */
	@BigmProperty(fired = SortSet.class, retrieved = SortSet.class)
	public static final String PROPERTY_SORTSET = "LinkSortSortSet";
	
	abstract class SortChange extends ModelObjectChange {
		@Override
		public LinkSort getCreator() {
			return LinkSort.this;
		}
	}
	
	@Override
	protected Namespace<LinkSort>
			getGoverningNamespace(PropertyScratchpad context) {
		return getSortSet(context).getNamespaceLinkSort();
	}

	public final class ChangeRemoveLinkSort extends SortChange {
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
			return oldSortSet.new ChangeAddLinkSort(getCreator(), oldName);
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			SortSet ss = getCreator().getSortSet(context);
			
			context.<LinkSort>getModifiableList(
					ss, SortSet.PROPERTY_LINKSORT, ss.getLinkSorts()).
				remove(getCreator());
			context.setProperty(getCreator(),
					LinkSort.PROPERTY_SORTSET, null);
			
			ss.getNamespaceLinkSort().remove(getCreator().getName(context));
			context.setProperty(getCreator(), PROPERTY_NAME, null);
		}
	}
	
	private SortSet sortSet = null;
	
	protected LinkSort clone(SortSet m) {
		LinkSort s = (LinkSort)super.clone();
		
		m.getNamespaceLinkSort().put(s.getName(), s);
		
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
	
	public IChange changeRemoveLinkSort() {
		return new ChangeRemoveLinkSort();
	}
	
	public static final class Identifier extends NamedModelObject.Identifier {
		public Identifier(String name) {
			super(name);
		}
		
		@Override
		public LinkSort lookup(PropertyScratchpad context, Resolver r) {
			return require(r.lookup(context, this), LinkSort.class);
		}
		
		@Override
		public Identifier getRenamed(String name) {
			return new Identifier(name);
		}
		
		@Override
		public String toString() {
			return "linksort " + getName();
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