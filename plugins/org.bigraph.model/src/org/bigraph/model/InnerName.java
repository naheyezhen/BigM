package org.bigraph.model;

import org.bigraph.model.assistants.ExecutorManager;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.assistants.BigmProperty;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.interfaces.IInnerName;

/**
 * @author alec
 * @see IInnerName
 */
public class InnerName extends Point implements IInnerName {
	@BigmProperty(fired = String.class, retrieved = String.class)
	public static final String PROPERTY_INNER_SORT = "InnerSort";
	
	abstract class InnerNameChange extends LayoutableChange {
		@Override
		public InnerName getCreator() {
			return InnerName.this;
		}
	}

	static {
		ExecutorManager.getInstance().addHandler(new InnerNameHandler());
	}
	
	public final class ChangeInnerSort extends InnerNameChange {
		public final String innerSort;
		
		public ChangeInnerSort(String innersort) {
			this.innerSort = innersort;
		}
		
		private String oldInnerSort;
		@Override
		public void beforeApply() {
			oldInnerSort = getCreator().getInnerSort();
		}
		
		@Override
		public boolean canInvert() {
			return (oldInnerSort != null);
		}
		
		@Override
		public boolean isReady() {
			return (innerSort != null);
		}
		
		@Override
		public ChangeInnerSort inverse() {
			return new ChangeInnerSort(oldInnerSort);
		}
		
		@Override
		public String toString() {
			return "Change(set inner sort of " + getCreator() + " to " + innerSort + ")";
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			context.setProperty(getCreator(), PROPERTY_INNER_SORT, innerSort);
		}
	}
	
	protected InnerName clone(Bigraph b) {
		InnerName i = (InnerName)super.clone();
		b.getNamespace(i).put(i.getName(), i);
		i.setInnerSort(getInnerSort());
		return i;
	}
	
	private String innerSort = "none:none";
	
	public String getInnerSort() {
		return innerSort;
	}
	
	public String getInnerSort(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_INNER_SORT, String.class);
	}
	
	public void setInnerSort(String innerSort) {
		String oldInnerSort = this.innerSort;
		this.innerSort = innerSort;
		firePropertyChange(PROPERTY_INNER_SORT, oldInnerSort, innerSort);
	}
	
	public IChange changeInnerSort(String innersort) {
		return new ChangeInnerSort(innersort);
	}
	
	public static final class Identifier extends Point.Identifier {
		public Identifier(String name) {
			super(name);
		}
		
		@Override
		public InnerName lookup(PropertyScratchpad context, Resolver r) {
			return require(r.lookup(context, this), InnerName.class);
		}
		
		@Override
		public Identifier getRenamed(String name) {
			return new Identifier(name);
		}
		
		@Override
		public String toString() {
			return "inner name " + getName();
		}
	}
	
	@Override
	public Identifier getIdentifier() {
		return getIdentifier(null);
	}
	
	@Override
	public Identifier getIdentifier(PropertyScratchpad context) {
		return new Identifier(getName(context));
	}
}
