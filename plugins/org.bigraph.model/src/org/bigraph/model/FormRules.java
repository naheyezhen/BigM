package org.bigraph.model;

import java.util.ArrayList;
import java.util.List;

import org.bigraph.model.FormationRule.ChangeRemoveFormationRule;
import org.bigraph.model.ModelObject.Identifier.Resolver;
import org.bigraph.model.ModelObject;
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
import org.bigraph.model.interfaces.IFormRules;
import org.bigraph.model.names.HashMapNamespace;
import org.bigraph.model.names.Namespace;
import org.bigraph.model.names.policies.StringNamePolicy;

public class FormRules extends ModelObject
		implements IFormRules, IChangeExecutor,
				ModelObject.Identifier.Resolver {
	private FormRules parent;
	private List<FormRules> formruless = new ArrayList<FormRules>();
	
	/**
	 * The property name fired when a formation rule is added or removed.
	 */
	@BigmProperty(fired = FormationRule.class, retrieved = List.class)
	public static final String PROPERTY_FORMATIONRULE = "FormRulesFomationRule";
	
	@BigmProperty(fired = FormRules.class, retrieved = List.class)
	public static final String PROPERTY_CHILD = "FormRulesChild";
	
	@BigmProperty(fired = FormRules.class, retrieved = FormRules.class)
	public static final String PROPERTY_PARENT = "FormRulesParent";
	
	/**
	 * The property name fired when the sortset changes.
	 */
	@BigmProperty(fired = SortSet.class, retrieved = SortSet.class)
	public static final String PROPERTY_SORTSET = "FormRulesSortSet";
	
	private SortSet sortSet;
	
	protected void setSortSet(SortSet sortSet) {
		SortSet oldSortSet = this.sortSet;
		this.sortSet = sortSet;
		firePropertyChange(PROPERTY_SORTSET, oldSortSet, sortSet);
	}
	
	public SortSet getSortSet() {
		return sortSet;
	}
	
	public SortSet getSortSet(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_SORTSET, SortSet.class);
	}
	
	abstract class FormRulesChange extends ModelObjectChange {
		@Override
		public FormRules getCreator() {
			return FormRules.this;
		}
	}
	
	public final class ChangeAddFormationRule extends FormRulesChange {
		public final FormationRule formationRule;
		public final String name;
		public final String sort1;
		public final String sort2;
		public final String constraint;
		public final String type;
		
		public ChangeAddFormationRule(FormationRule formationRule, String name, String sort1, String sort2, String constraint, String type) {
			this.formationRule = formationRule;
			this.type = type;
			this.name = name;
			this.sort1 = sort1;
			this.sort2 = sort2;
			this.constraint = constraint;
		}
		
		public ChangeAddFormationRule(FormationRule formationRule, String name) {
			this.formationRule = formationRule;
			this.name = name;
			this.sort1 = "";
			this.sort2 = "";
			this.constraint = "";
			this.type = "";
		}


		@Override
		public ChangeRemoveFormationRule inverse() {
			return formationRule.new ChangeRemoveFormationRule();
		}
		
		@Override
		public String toString() {
			return "Change(add formation rule " + formationRule + " to formationrules " +
					getCreator() + " with type " + type + ")";
		}
		
		@Override
		public boolean isReady() {
			return (formationRule != null);
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			context.<FormationRule>getModifiableList(
					getCreator(), PROPERTY_FORMATIONRULE, getFormationRules()).
				add(formationRule);
			context.setProperty(formationRule,
					FormationRule.PROPERTY_FORMRULES, getCreator());
			
			getCreator().getNamespace().put(context, type, formationRule);
			context.setProperty(formationRule, FormationRule.PROPERTY_NAME, type);
		}
	}
	
	private Namespace<FormationRule> ns = new HashMapNamespace<FormationRule>(
			new StringNamePolicy() {
		@Override
		public String get(int value) {
			return "FormationRule" + (value + 1);
		}
	});
	
	public Namespace<FormationRule> getNamespace() {
		return ns;
	}
	
	private ArrayList<FormationRule> formationRules = new ArrayList<FormationRule>();
	
	@Override
	public FormRules clone() {
		FormRules s = (FormRules)super.clone();
		
		for (FormationRule fr : getFormationRules())
			s.addFormationRule(-1, fr.clone(s));
		
		for (FormRules t : getFormRuless())
			s.addFormRules(-1, t.clone());
		
		return s;
	}
	
	protected void addFormationRule(int position, FormationRule fr) {
		if (position == -1) {
			formationRules.add(fr);
		} else formationRules.add(position, fr);
		fr.setFormRules(this);
		firePropertyChange(PROPERTY_FORMATIONRULE, null, fr);
	}
	
	protected void removeFormationRule(FormationRule m) {
		formationRules.remove(m);
		m.setFormRules(null);
		firePropertyChange(PROPERTY_FORMATIONRULE, m, null);
	}
	
	public FormationRule getFormationRule(String name) {
		for (FormationRule fr : getFormationRules())
			if (fr.getName().equals(name))
				return fr;
		FormationRule fr = null;
		for (FormRules s : getFormRuless())
			if ((fr = s.getFormationRule(name)) != null)
				return fr;
		return null;
	}
	
	@Override
	public List<? extends FormationRule> getFormationRules() {
		return formationRules;
	}

	@SuppressWarnings("unchecked")
	public List<? extends FormationRule> getFormationRules(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_FORMATIONRULE, List.class);
	}

	public static final String CONTENT_TYPE = "dk.itu.bigm.formation_rules";
	
	@Override
	public void tryValidateChange(IChange b) throws ChangeRejectedException {
		ExecutorManager.getInstance().tryValidateChange(b);
	}
	
	@Override
	public void tryApplyChange(IChange b) throws ChangeRejectedException {
		ExecutorManager.getInstance().tryApplyChange(b);
	}

	static {
		ExecutorManager.getInstance().addHandler(new FormRulesHandler());
	}
	
	@Override
	public void dispose() {
		if (formationRules != null) {
			for (FormationRule fr : formationRules)
				fr.dispose();
			formationRules.clear();
			formationRules = null;
		}
		
		if (formruless != null) {
			for (FormRules f : formruless)
				f.dispose();
			formruless.clear();
			formruless = null;
		}

		super.dispose();
	}
	
	@Override
	protected Object getProperty(String name) {
		if (PROPERTY_FORMATIONRULE.equals(name)) {
			return getFormationRules();
		} else if (PROPERTY_PARENT.equals(name)) {
			return getParent();
		} else if (PROPERTY_CHILD.equals(name)) {
			return getFormRuless();
		} else return super.getProperty(name);
	}
	
	public FormRules getParent() {
		return parent;
	}
	
	public FormRules getParent(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_PARENT, FormRules.class);
	}
	
	protected void setParent(FormRules newValue) {
		FormRules oldValue = parent;
		parent = newValue;
		firePropertyChange(PROPERTY_PARENT, oldValue, newValue);
	}
	
	public List<? extends FormRules> getFormRuless() {
		return formruless;
	}
	
	@SuppressWarnings("unchecked")
	public List<? extends FormRules> getFormRuless(
			PropertyScratchpad context) {
		return getProperty(context, PROPERTY_CHILD, List.class);
	}
	
	protected void addFormRules(int position, FormRules f) {
		if (position == -1) {
			formruless.add(f);
		} else formruless.add(position, f);
		f.setParent(this);
		firePropertyChange(PROPERTY_CHILD, null, f);
	}
	
	protected void removeFormRules(FormRules f) {
		formruless.remove(f);
		f.setParent(null);
		firePropertyChange(PROPERTY_CHILD, f, null);
	}
	
	public IChange changeAddFormationRule(FormationRule fr, String name, String sort1, String sort2, String constraint, String type) {
		return new ChangeAddFormationRule(fr, name, sort1, sort2, constraint, type);
	}
	
	public IChange changeAddFormationRule(FormationRule fr, String name) {
		return new ChangeAddFormationRule(fr, name);
	}
	
	@Override
	public Object lookup(
			PropertyScratchpad context, ModelObject.Identifier identifier) {
		if (identifier instanceof FormRules.Identifier) {
			return this;
		} else if (identifier instanceof FormationRule.Identifier) {
			return getFormationRule(((FormationRule.Identifier)identifier).getName());
		}
		return null;
	}
	
	public static final class Identifier implements ModelObject.Identifier {
		@Override
		public FormRules lookup(PropertyScratchpad context, Resolver r) {
			return require(r.lookup(context, this), FormRules.class);
		}
	}
	
	abstract static class FormRulesChangeDescriptor
			extends ModelObjectChangeDescriptor {
		static {
			DescriptorExecutorManager.getInstance().addHandler(
					new FormRulesDescriptorHandler());
		}
		
		@Override
		public IChange createChange(PropertyScratchpad context, Resolver r)
				throws ChangeCreationException {
			return new BoundDescriptor(r, this);
		}
	}
	
	public static final class ChangeAddFormationRuleDescriptor
			extends FormRulesChangeDescriptor {
		private final Identifier target;
		private final int position;
		private final FormationRule.Identifier formationRule;
		
		public ChangeAddFormationRuleDescriptor(
				Identifier target, int position, FormationRule.Identifier formationRule) {
			this.target = target;
			this.position = position;
			this.formationRule = formationRule;
		}
		
		public Identifier getTarget() {
			return target;
		}
		
		public int getPosition() {
			return position;
		}
		
		public FormationRule.Identifier getFormationRule() {
			return formationRule;
		}
		
		@Override
		public IChange createChange(PropertyScratchpad context, Resolver r)
				throws ChangeCreationException {
			FormRules f = getTarget().lookup(context, r);
			if (f == null)
				throw new ChangeCreationException(this,
						"" + getTarget() + ": lookup failed");
			return f.changeAddFormationRule(new FormationRule(), getFormationRule().getName(), null, null, null, null);
		}
		
		@Override
		public IChangeDescriptor inverse() {
			throw new UnsupportedOperationException(
					"FIXME: ChangeAddFormationRuleDescriptor.inverse() " +
					"not implementable");
		}
	}
	
	public static final class ChangeAddFormRulesDescriptor
			extends FormRulesChangeDescriptor {
		private final Identifier target;
		private final int position;
		private final FormRules f;
		
		public ChangeAddFormRulesDescriptor(
				Identifier target, int position, FormRules f) {
			this.target = target;
			this.position = position;
			this.f = f;
		}
		
		public Identifier getTarget() {
			return target;
		}
		
		public int getPosition() {
			return position;
		}
		
		public FormRules getFormRules() {
			return f;
		}
		
		@Override
		public IChangeDescriptor inverse() {
			return new ChangeRemoveFormRulesDescriptor(
					getTarget(), getPosition(), getFormRules());
		}
		
		@Override
		public void simulate(PropertyScratchpad context, Resolver r) {
			FormRules self = getTarget().lookup(context, r);
			context.<FormRules>getModifiableList(
					self, PROPERTY_CHILD, self.getFormRuless()).add(
							getFormRules());
			context.setProperty(getFormRules(), PROPERTY_PARENT, self);
		}
	}
	
	public static final class ChangeRemoveFormRulesDescriptor
			extends FormRulesChangeDescriptor {
		private final Identifier target;
		private final int position;
		private final FormRules f;

		public ChangeRemoveFormRulesDescriptor(
				Identifier target, int position, FormRules f) {
			this.target = target;
			this.position = position;
			this.f = f;
		}

		public Identifier getTarget() {
			return target;
		}

		public int getPosition() {
			return position;
		}

		public FormRules getFormRules() {
			return f;
		}

		@Override
		public IChangeDescriptor inverse() {
			return new ChangeAddFormRulesDescriptor(
					getTarget(), getPosition(), getFormRules());
		}
		
		@Override
		public void simulate(PropertyScratchpad context, Resolver r) {
			FormRules self = getTarget().lookup(context, r);
			
			context.<FormRules>getModifiableList(
					self, PROPERTY_CHILD, self.getFormRuless()).remove(
							getFormRules());
			context.setProperty(getFormRules(), PROPERTY_PARENT, null);
		}
	}
	
	public static final class ChangeSetSortSetDescriptor extends FormRulesChangeDescriptor {
		private final Identifier target;
		private final SortSet oldSortSet;
		private final SortSet newSortSet;

		public ChangeSetSortSetDescriptor(Identifier target,
				SortSet oldSortSet, SortSet newSortSet) {
			this.target = target;
			this.oldSortSet = oldSortSet;
			this.newSortSet = newSortSet;
		}

		public Identifier getTarget() {
			return target;
		}

		public SortSet getOldSortSet() {
			return oldSortSet;
		}

		public SortSet getNewSortSet() {
			return newSortSet;
		}

		@Override
		public IChangeDescriptor inverse() {
			return new ChangeSetSortSetDescriptor(getTarget(),
					getNewSortSet(), getOldSortSet());
		}

		@Override
		public void simulate(PropertyScratchpad context, Resolver r) {
			FormRules self = getTarget().lookup(context, r);
			context.setProperty(self, PROPERTY_FORMATIONRULE, getNewSortSet());
		}
	}
	
}