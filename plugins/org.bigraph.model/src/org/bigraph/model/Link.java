package org.bigraph.model;

import java.util.ArrayList;
import java.util.List;

import org.bigraph.model.ModelObject.Identifier.Resolver;
import org.bigraph.model.assistants.ExecutorManager;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.assistants.BigmProperty;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.descriptors.BoundDescriptor;
import org.bigraph.model.changes.descriptors.ChangeCreationException;
import org.bigraph.model.changes.descriptors.experimental.DescriptorExecutorManager;
import org.bigraph.model.interfaces.ILink;

/**
 * A Link is the superclass of {@link Edge}s and {@link OuterName}s &mdash;
 * model objects which have multiple connections to {@link Point}s.
 * @author alec
 * @see ILink
 */
public abstract class Link extends Layoutable implements ILink {
	/**
	 * The property name fired when a point is added or removed.
	 */
	@BigmProperty(fired = Point.class, retrieved = List.class)
	public static final String PROPERTY_POINT = "LinkPoint";
	
	@BigmProperty(fired = String.class, retrieved = String.class)
	public static final String PROPERTY_LINK_SORT = "LinkSort";
	
	private String linkSort;
	
	public String getLinkSort() {
		return linkSort;
	}
	
	public String getLinkSort(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_LINK_SORT, String.class);
	}
	
	public void setLinkSort(String linkSort) {
		String oldLinkSort = this.linkSort;
		this.linkSort = linkSort;
		firePropertyChange(PROPERTY_LINK_SORT, oldLinkSort, linkSort);
	}
	
	abstract class LinkChange extends LayoutableChange {
		@Override
		public Link getCreator() {
			return Link.this;
		}
	}

	static {
		ExecutorManager.getInstance().addHandler(new LinkHandler());
	}
	
	public final class ChangeLinkSort extends LinkChange {
		public final String linkSort;
		
		public ChangeLinkSort(String linksort) {
			this.linkSort = linksort;
		}
		
		private String oldLinkSort;
		@Override
		public void beforeApply() {
			oldLinkSort = getCreator().getLinkSort();
		}
		
		@Override
		public boolean canInvert() {
			return (oldLinkSort != null);
		}
		
		@Override
		public boolean isReady() {
			return (linkSort != null);
		}
		
		@Override
		public ChangeLinkSort inverse() {
			return new ChangeLinkSort(oldLinkSort);
		}
		
		@Override
		public String toString() {
			return "Change(set link sort of " + getCreator() + " to " + linkSort + ")";
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			context.setProperty(getCreator(), PROPERTY_LINK_SORT, linkSort);
		}
	}
	
	public IChange changeLinkSort(String linksort) {
		return new ChangeLinkSort(linksort);
	}
	
	abstract static class LinkChangeDescriptor extends LayoutableChangeDescriptor {
		static {
			DescriptorExecutorManager.getInstance().addHandler(
					new LinkDescriptorHandler());
		}
		
		@Override
		public IChange createChange(PropertyScratchpad context, Resolver r)
				throws ChangeCreationException {
			return new BoundDescriptor(r, this);
		}
	}
		
	public static class ChangeLinkSortDescriptor extends LinkChangeDescriptor {
		private final Identifier link;
		private final String linksort;
		
		public ChangeLinkSortDescriptor(
				Identifier link, String linksort) {
			this.link = link;
			this.linksort = linksort;
		}
		
		public Identifier getLink() {
			return link;
		}
		
		public String getLinksort() {
			return linksort;
		}
		
		@Override
		public boolean equals(Object obj_) {
			if (safeClassCmp(this, obj_)) {
				ChangeLinkSortDescriptor obj = (ChangeLinkSortDescriptor)obj_;
				return
						safeEquals(getLinksort(), obj.getLinksort()) &&
						safeEquals(getLink(), obj.getLink());
			} else return false;
		}
		
		@Override
		public int hashCode() {
			return compositeHashCode(
					ChangeLinkSortDescriptor.class, link, linksort);
		}
		
		@Override
		public IChange createChange(PropertyScratchpad context, Resolver r)
				throws ChangeCreationException {
			Link l = link.lookup(context, r);
			if (l == null)
				throw new ChangeCreationException(this,
						"" + link + " didn't resolve to a Link");
			return l.changeLinkSort(linksort);
		}
		
		@Override
		public ChangeLinkSortDescriptor inverse() {
			return new ChangeLinkSortDescriptor(
					getLink().getRenamed(getLinksort()),
					getLink().getName());
		}

		@Override
		public String toString() {
			return "ChangeDescriptor(set link sort of " + link + " to " + linksort + ")";
		}
	}
	
	/**
	 * The {@link Point}s connected to this Link on the bigraph.
	 */
	private ArrayList<Point> points = new ArrayList<Point>();
	
	public Link(){}
	
	public Link(String linkSort) {
		this.linkSort = linkSort;
	}
	
	protected Link clone(Bigraph b) {
		Link l = (Link)super.clone();
		b.getNamespace(l).put(l.getName(), l);
		l.setLinkSort(getLinkSort());
		return l;
	}
	
	/**
	 * Adds the given {@link Point} to this Link's set of points.
	 * @param point a Point
	 */
	protected void addPoint(Point point) {
		if (point == null)
			return;
		points.add(point);
		point.setLink(this);
		firePropertyChange(PROPERTY_POINT, null, point);
	}
	
	/**
	 * Removes the given {@link Point} from this Link's set of points.
	 * @param point a Point
	 */
	protected void removePoint(Point point) {
		if (points.remove(point)) {
			point.setLink(null);
			firePropertyChange(PROPERTY_POINT, point, null);
		}
	}
	
	@Override
	public List<? extends Point> getPoints() {
		return points;
	}
	
	@SuppressWarnings("unchecked")
	public List<? extends Point> getPoints(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_POINT, List.class);
	}
	
	/**
	 * {@inheritDoc}
	 * <p><strong>Special notes for {@link Link}:</strong>
	 * <ul>
	 * <li>Passing {@link #PROPERTY_POINT} will return a {@link List}&lt;{@link
	 * Point}&gt;, <strong>not</strong> a {@link Point}.
	 * </ul>
	 */
	@Override
	protected Object getProperty(String name) {
		if (PROPERTY_POINT.equals(name)) {
			return getPoints();
		} else return super.getProperty(name);
	}
	
	public static abstract class Identifier extends Layoutable.Identifier {
		public Identifier(String name) {
			super(name);
		}
		
		@Override
		public boolean equals(Object obj_) {
			if (obj_ instanceof Identifier) {
				return getName().equals(((Identifier)obj_).getName());
			} else return false;
		}
		
		@Override
		public int hashCode() {
			return compositeHashCode(Identifier.class, getName());
		}
		
		@Override
		public Link lookup(PropertyScratchpad context, Resolver r) {
			return require(r.lookup(context, this), Link.class);
		}
		
		@Override
		public abstract Identifier getRenamed(String name);
	}
	
	@Override
	public abstract Identifier getIdentifier();
	@Override
	public abstract Identifier getIdentifier(PropertyScratchpad context);
	
	@Override
	public void dispose() {
		if (points != null) {
			points.clear();
			points = null;
		}
		
		super.dispose();
	}
}
