package org.bigraph.model;

import org.bigraph.model.ModelObject;
import org.bigraph.model.Container.ChangeAddChildDescriptor;
import org.bigraph.model.ModelObject.Identifier.Resolver;
import org.bigraph.model.assistants.ExecutorManager;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.assistants.BigmProperty;
import org.bigraph.model.changes.Change;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.descriptors.ChangeCreationException;
import org.bigraph.model.changes.descriptors.experimental.DescriptorExecutorManager;
import org.bigraph.model.names.Namespace;

/**
 * All of the objects which can actually appear as part of a bigraph are
 * instances of <strong>Layoutable</strong>.
 * @author alec
 * @see ModelObject
 */
public abstract class Layoutable extends NamedModelObject {
	/**
	 * The property name fired when the parent changes.
	 */
	@BigmProperty(fired = Container.class, retrieved = Container.class)
	public static final String PROPERTY_PARENT = "LayoutableParent";
	
	abstract class LayoutableChange extends ModelObjectChange {
		@Override
		public Layoutable getCreator() {
			return Layoutable.this;
		}
	}

	abstract static class LayoutableChangeDescriptor
			extends NamedModelObjectChangeDescriptor {
		static {
			DescriptorExecutorManager.getInstance().addHandler(
					new LayoutableDescriptorHandler());
		}
	}
	
	@Override
	protected Namespace<Layoutable> getGoverningNamespace(
			PropertyScratchpad context) {
		return getBigraph(context).getNamespace(this);
	}
	
	public final class ChangeRemove extends LayoutableChange {
		private String oldName;
		private Container oldParent;
		private int oldPosition;
		@Override
		public void beforeApply() {
			Layoutable l = getCreator();
			oldName = l.getName();
			oldParent = l.getParent();
			oldPosition = l.getParent().getChildren().indexOf(l);
		}
		
		@Override
		public boolean canInvert() {
			return (oldName != null && oldParent != null);
		}
		
		@Override
		public Change inverse() {
			return oldParent.new ChangeAddChild(
					getCreator(), oldName, oldPosition);
		}
		
		@Override
		public String toString() {
			return "Change(remove child " + getCreator() + ")";
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			Layoutable l = getCreator();
			Container c = l.getParent(context);
			
			context.<Layoutable>getModifiableList(
					c, Container.PROPERTY_CHILD, c.getChildren()).
				remove(l);
			context.setProperty(l, Layoutable.PROPERTY_PARENT, null);
			
			c.getBigraph(context).getNamespace(l).
					remove(context, l.getName(context));
			context.setProperty(l, Layoutable.PROPERTY_NAME, null);
		}
	}
	
	static {
		ExecutorManager.getInstance().addHandler(new LayoutableHandler());
	}
	
	private Container parent = null;
	
	/**
	 * Returns the {@link Bigraph} that ultimately contains this object.
	 * @return a Bigraph
	 */
	public Bigraph getBigraph() {
		return getBigraph(null);
	}

	public Bigraph getBigraph(PropertyScratchpad context) {
		if (getParent(context) == null) {
			return null;
		} else return getParent(context).getBigraph(context);
	}
	
	/**
	 * Returns the parent of this object.
	 * @return an {@link Container}
	 */
	public Container getParent() {
		return parent;
	}

	public Container getParent(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_PARENT, Container.class);
	}
	
	/**
	 * Changes the parent of this object.
	 * @param p the new parent {@link Container}
	 */
	void setParent(Container parent) {
		Container oldParent = this.parent;
		this.parent = parent;
		firePropertyChange(PROPERTY_PARENT, oldParent, parent);
	}
	
	protected Layoutable clone(Bigraph b) {
		Layoutable l = (Layoutable)super.clone();
		b.getNamespace(l).put(getName(), l);
		return l;
	}
	
	public IChange changeRemove() {
		return new ChangeRemove();
	}
	
	@Override
	protected Object getProperty(String name) {
		if (PROPERTY_PARENT.equals(name)) {
			return getParent();
		} else return super.getProperty(name);
	}
	
	@Override
	public void dispose() {
		parent = null;
		
		super.dispose();
	}
	
	@Override
	public abstract Identifier getIdentifier();
	@Override
	public abstract Identifier getIdentifier(PropertyScratchpad context);
	
	public static abstract class Identifier
			extends NamedModelObject.Identifier {
		public Identifier(String name) {
			super(name);
		}
		
		@Override
		public abstract Layoutable lookup(
				PropertyScratchpad context, Resolver r);
		
		@Override
		public abstract Identifier getRenamed(String name);
	}
	
	public static final class ChangeRemoveDescriptor
			extends LayoutableChangeDescriptor {
		private final Identifier target;
		private final Container.Identifier parent;
		
		public ChangeRemoveDescriptor(
				Identifier target, Container.Identifier parent) {
			this.target = target;
			this.parent = parent;
		}
		
		public Identifier getTarget() {
			return target;
		}
		
		public Container.Identifier getParent() {
			return parent;
		}
		
		@Override
		public boolean equals(Object obj_) {
			if (safeClassCmp(this, obj_)) {
				ChangeRemoveDescriptor obj = (ChangeRemoveDescriptor)obj_;
				return
						safeEquals(getTarget(), obj.getTarget()) &&
						safeEquals(getParent(), obj.getParent());
			} else return false;
		}
		
		@Override
		public int hashCode() {
			return compositeHashCode(ChangeRemoveDescriptor.class,
					target, parent);
		}
		
		@Override
		public IChange createChange(PropertyScratchpad context, Resolver r)
				throws ChangeCreationException {
			Layoutable l = target.lookup(context, r);
			if (l == null)
				return null;
			return l.changeRemove();
		}
		
		@Override
		public ChangeAddChildDescriptor inverse() {
			return new ChangeAddChildDescriptor(getParent(), getTarget());
		}
		
		@Override
		public String toString() {
			return "ChangeDescriptor(remove child " + target + ")";
		}
	}
}
