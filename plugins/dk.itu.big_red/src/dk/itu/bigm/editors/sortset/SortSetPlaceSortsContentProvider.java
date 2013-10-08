package dk.itu.bigm.editors.sortset;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import org.bigraph.model.PlaceSort;
import org.bigraph.model.SortSet;
import org.eclipse.jface.viewers.AbstractTreeViewer;

import dk.itu.bigm.utilities.ui.jface.ModelObjectTreeContentProvider;

class SortSetPlaceSortsContentProvider extends ModelObjectTreeContentProvider {
	public SortSetPlaceSortsContentProvider(AbstractTreeViewer atv) {
		super(atv);
	}
	
	private void recursivelyListen(SortSet ss) {
		ss.addPropertyChangeListener(this);
		for (PlaceSort s : ss.getPlaceSorts())
			s.addPropertyChangeListener(this);
		for (SortSet t : ss.getSortSets())
			recursivelyListen(t);
	}
	
	private void recursivelyStopListening(SortSet ss) {
		for (SortSet t : ss.getSortSets())
			recursivelyStopListening(t);
		for (PlaceSort s : ss.getPlaceSorts())
			s.removePropertyChangeListener(this);
		ss.removePropertyChangeListener(this);
	}
	
	@Override
	protected void unregister(Object oldInput) {
		if (oldInput instanceof SortSet)
			recursivelyStopListening((SortSet)oldInput);
	}
	
	@Override
	protected void register(Object newInput) {
		if (newInput instanceof SortSet)
			recursivelyListen((SortSet)newInput);
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof SortSet) {
			SortSet s = (SortSet)parentElement;
			ArrayList<Object> r = new ArrayList<Object>();
			r.addAll(s.getSortSets());
			r.addAll(s.getPlaceSorts());
			return r.toArray();
		} else return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof SortSet) {
			return ((SortSet)element).getParent();
		} else if (element instanceof PlaceSort) {
			return ((PlaceSort)element).getSortSet();
		} else return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof SortSet) {
			SortSet s = (SortSet)element;
			return (s.getPlaceSorts().size() > 0 ||
					s.getSortSets().size() > 0);
		} else return false;
	}
	
	private void updateViewer(Object object, String... properties) {
		getViewer().update(object, properties);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Object
			source = evt.getSource(),
			oldValue = evt.getOldValue(),
			newValue = evt.getNewValue();
		String pn = evt.getPropertyName();
		if (source instanceof SortSet) {
			if (SortSet.PROPERTY_PLACESORT.equals(pn)) {
				if (oldValue == null) {
					PlaceSort s = (PlaceSort)newValue;
					getViewer().add(s.getSortSet(), s);
					s.addPropertyChangeListener(this);
				} else if (newValue == null) {
					PlaceSort s = (PlaceSort)oldValue;
					s.removePropertyChangeListener(this);
					getViewer().remove(s);
				}
			} else if (SortSet.PROPERTY_CHILD.equals(pn)) {
				if (oldValue == null) {
					SortSet s = (SortSet)newValue;
					getViewer().add(s.getParent(), s);
					s.addPropertyChangeListener(this);
				} else if (newValue == null) {
					SortSet s = (SortSet)oldValue;
					s.removePropertyChangeListener(this);
					getViewer().remove(s);
				}
			}
		}
		updateViewer(source, pn);
	}
}
