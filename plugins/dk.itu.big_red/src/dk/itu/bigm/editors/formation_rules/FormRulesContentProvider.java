package dk.itu.bigm.editors.formation_rules;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import org.bigraph.model.FormRules;
import org.bigraph.model.FormationRule;
import org.eclipse.jface.viewers.AbstractTreeViewer;

import dk.itu.bigm.utilities.ui.jface.ModelObjectTreeContentProvider;

class FormRulesContentProvider extends ModelObjectTreeContentProvider {
	public FormRulesContentProvider(AbstractTreeViewer atv) {
		super(atv);
	}
	
	private void recursivelyListen(FormRules frs) {
		frs.addPropertyChangeListener(this);
		for (FormationRule fr : frs.getFormationRules())
			fr.addPropertyChangeListener(this);
		for (FormRules t : frs.getFormRuless())
			recursivelyListen(t);
	}
	
	private void recursivelyStopListening(FormRules frs) {
		for (FormRules t : frs.getFormRuless())
			recursivelyStopListening(t);
		for (FormationRule fr : frs.getFormationRules())
			fr.removePropertyChangeListener(this);
		frs.removePropertyChangeListener(this);
	}
	
	@Override
	protected void unregister(Object oldInput) {
		if (oldInput instanceof FormRules)
			recursivelyStopListening((FormRules)oldInput);
	}
	
	@Override
	protected void register(Object newInput) {
		if (newInput instanceof FormRules)
			recursivelyListen((FormRules)newInput);
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof FormRules) {
			FormRules f = (FormRules)parentElement;
			ArrayList<Object> r = new ArrayList<Object>();
			r.addAll(f.getFormRuless());
			r.addAll(f.getFormationRules());
			return r.toArray();
		} else return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof FormRules) {
			return ((FormRules)element).getParent();
		} else if (element instanceof FormationRule) {
			return ((FormationRule)element).getFormRules();
		} else return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof FormRules) {
			FormRules f = (FormRules)element;
			return (f.getFormationRules().size() > 0 ||
					f.getFormRuless().size() > 0);
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
		if (source instanceof FormRules) {
			if (FormRules.PROPERTY_FORMATIONRULE.equals(pn)) {
				if (oldValue == null) {
					FormationRule fr = (FormationRule)newValue;
					getViewer().add(fr.getFormRules(), fr);
					fr.addPropertyChangeListener(this);
				} else if (newValue == null) {
					FormationRule s = (FormationRule)oldValue;
					s.removePropertyChangeListener(this);
					getViewer().remove(s);
				}
			} else if (FormRules.PROPERTY_CHILD.equals(pn)) {
				if (oldValue == null) {
					FormRules s = (FormRules)newValue;
					getViewer().add(s.getParent(), s);
					s.addPropertyChangeListener(this);
				} else if (newValue == null) {
					FormRules s = (FormRules)oldValue;
					s.removePropertyChangeListener(this);
					getViewer().remove(s);
				}
			}
		}
		updateViewer(source, pn);
	}
}
