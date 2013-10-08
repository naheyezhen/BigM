package org.bigraph.model;

import org.bigraph.model.assistants.ExecutorManager;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.assistants.BigmProperty;
import org.bigraph.model.changes.Change;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.interfaces.IFormationRule;
import org.bigraph.model.names.HashMapNamespace;
import org.bigraph.model.names.Namespace;
import org.bigraph.model.names.policies.StringNamePolicy;

public class FormationRule extends NamedModelObject implements IFormationRule{
	private String sort1;
	private String sort2;
	private String constraint;
	private String type;
	
	/**
	 * The property name when this FormationRule's containing {@link FormationRules}
	 * changes.
	 */
	@BigmProperty(fired = FormRules.class, retrieved = FormRules.class)
	public static final String PROPERTY_FORMRULES = "FormRuleFormationRules";
	
	/**
	 * The property name when this FormationRule's containing {@link Type}
	 * changes.
	 */
	@BigmProperty(fired = String.class, retrieved = String.class)
	public static final String PROPERTY_TYPE = "FormRuleType";
	
	/**
	 * The property name when this FormationRule's containing {@link Sort1}
	 * changes.
	 */
	@BigmProperty(fired = String.class, retrieved = String.class)
	public static final String PROPERTY_SORT1 = "FormRuleSort1";
	
	/**
	 * The property name when this FormationRule's containing {@link Sort2}
	 * changes.
	 */
	@BigmProperty(fired = String.class, retrieved = String.class)
	public static final String PROPERTY_SORT2 = "FormRuleSort2";
	
	/**
	 * The property name when this FormationRule's containing {@link Constraint}
	 * changes.
	 */
	@BigmProperty(fired = String.class, retrieved = String.class)
	public static final String PROPERTY_CONSTRAINT = "FormRuleConstraint";
	
	abstract class FormationRuleChange extends ModelObjectChange {
		@Override
		public FormationRule getCreator() {
			return FormationRule.this;
		}
	}
	
	@Override
	protected Namespace<FormationRule>
			getGoverningNamespace(PropertyScratchpad context) {
		return getFormRules(context).getNamespace();
	}
	
	static {
		ExecutorManager.getInstance().addHandler(new FormationRuleHandler());
	}

	public final class ChangeRemoveFormationRule extends FormationRuleChange {
		private String oldName;
		private String oldSort1;
		private String oldSort2;
		private String oldConstraint;
		private String oldType;
		private FormRules oldFormRules;
		
		@Override
		public void beforeApply() {
			oldName = getCreator().getName();
			oldFormRules = getFormRules();
		}
		
		@Override
		public boolean canInvert() {
			return (oldFormRules != null);
		}
		
		@Override
		public Change inverse() {
			return oldFormRules.new ChangeAddFormationRule(getCreator(), oldName, oldSort1, oldSort2, oldConstraint, oldType);
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			FormRules frs = getCreator().getFormRules(context);
			
			context.<FormationRule>getModifiableList(
					frs, FormRules.PROPERTY_FORMATIONRULE, frs.getFormationRules()).
				remove(getCreator());
			context.setProperty(getCreator(),
					FormationRule.PROPERTY_FORMRULES, null);
			
			frs.getNamespace().remove(getCreator().getName(context));
			context.setProperty(getCreator(), PROPERTY_NAME, null);
		}
	}
	
	public final class ChangeSetFormRuleType extends FormationRuleChange {
		public final String type;
		
		public ChangeSetFormRuleType(String type) {
			this.type = type;
		}
		
		private String oldType;
		@Override
		public void beforeApply() {
			oldType = getCreator().getType();
		}
		
		@Override
		public boolean canInvert() {
			return (oldType != null);
		}
		
		@Override
		public boolean isReady() {
			return (type != null);
		}
		
		@Override
		public ChangeSetFormRuleType inverse() {
			return new ChangeSetFormRuleType(oldType);
		}
		
		@Override
		public String toString() {
			return "Change(set type of " + getCreator() + " to " + type + ")";
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			context.setProperty(getCreator(), PROPERTY_TYPE, type);
		}
	}
	
	public final class ChangeSetFormRuleSort1 extends FormationRuleChange {
		public final String sort1;
		
		public ChangeSetFormRuleSort1(String sort1) {
			this.sort1 = sort1;
		}
		
		private String oldSort1;
		@Override
		public void beforeApply() {
			oldSort1 = getCreator().getType();
		}
		
		@Override
		public boolean canInvert() {
			return (oldSort1 != null);
		}
		
		@Override
		public boolean isReady() {
			return (sort1 != null);
		}
		
		@Override
		public ChangeSetFormRuleType inverse() {
			return new ChangeSetFormRuleType(oldSort1);
		}
		
		@Override
		public String toString() {
			return "Change(set sort1 of " + getCreator() + " to " + sort1 + ")";
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			context.setProperty(getCreator(), PROPERTY_SORT1, sort1);
		}
	}
	
	public final class ChangeSetFormRuleSort2 extends FormationRuleChange {
		public final String sort2;
		
		public ChangeSetFormRuleSort2(String sort2) {
			this.sort2 = sort2;
		}
		
		private String oldSort2;
		@Override
		public void beforeApply() {
			oldSort2 = getCreator().getType();
		}
		
		@Override
		public boolean canInvert() {
			return (oldSort2 != null);
		}
		
		@Override
		public boolean isReady() {
			return (sort2 != null);
		}
		
		@Override
		public ChangeSetFormRuleType inverse() {
			return new ChangeSetFormRuleType(oldSort2);
		}
		
		@Override
		public String toString() {
			return "Change(set sort2 of " + getCreator() + " to " + sort2 + ")";
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			context.setProperty(getCreator(), PROPERTY_SORT2, sort2);
		}
	}
	
	public final class ChangeSetFormRuleCons extends FormationRuleChange {
		public final String constraint;
		
		public ChangeSetFormRuleCons(String constraint) {
			this.constraint = constraint;
		}
		
		private String oldConstraint;
		@Override
		public void beforeApply() {
			oldConstraint = getCreator().getType();
		}
		
		@Override
		public boolean canInvert() {
			return (oldConstraint != null);
		}
		
		@Override
		public boolean isReady() {
			return (constraint != null);
		}
		
		@Override
		public ChangeSetFormRuleType inverse() {
			return new ChangeSetFormRuleType(oldConstraint);
		}
		
		@Override
		public String toString() {
			return "Change(set constraint of " + getCreator() + " to " + constraint + ")";
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			context.setProperty(getCreator(), PROPERTY_CONSTRAINT, constraint);
		}
	}
	
	private Namespace<PortSpec> ns = new HashMapNamespace<PortSpec>(
			new StringNamePolicy());
	
	public Namespace<PortSpec> getNamespace() {
		return ns;
	}

	private FormRules formRules = null;
	
	protected FormationRule clone(FormRules m) {
		FormationRule fr = (FormationRule)super.clone();
		
		m.getNamespace().put(fr.getName(), fr);
		
		return fr;
	}
	
	public FormRules getFormRules() {
		return formRules;
	}
	
	public FormRules getFormRules(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_FORMRULES, FormRules.class);
	}
	
	void setFormRules(FormRules formRules) {
		FormRules oldFormRules = this.formRules;
		this.formRules = formRules;
		firePropertyChange(PROPERTY_FORMRULES, oldFormRules, formRules);
	}
	
	@Override
	protected Object getProperty(String name) {
		if (PROPERTY_FORMRULES.equals(name)) {
			return getFormRules();
		}
		return super.getProperty(name);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	public IChange changeRemove() {
		return new ChangeRemoveFormationRule();
	}
	
	public static final class Identifier extends NamedModelObject.Identifier {
		public Identifier(String name) {
			super(name);
		}
		
		@Override
		public FormationRule lookup(PropertyScratchpad context, Resolver r) {
			return require(r.lookup(context, this), FormationRule.class);
		}
		
		@Override
		public Identifier getRenamed(String name) {
			return new Identifier(name);
		}
		
		@Override
		public String toString() {
			return "FormationRule " + getName();
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

	public String getSort1() {
		return sort1;
	}

	public String getSort1(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_SORT1, String.class);
	}

	public void setSort1(String sort1) {
		String oldSort1 = this.sort1;
		this.sort1 = sort1;
		firePropertyChange(PROPERTY_SORT1, oldSort1, sort1);
	}

	public String getSort2() {
		return sort2;
	}

	public String getSort2(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_SORT2, String.class);
	}

	public void setSort2(String sort2) {
		String oldSort2 = this.sort2;
		this.sort2 = sort2;
		firePropertyChange(PROPERTY_SORT2, oldSort2, sort2);
	}
	
	public String getConstraint() {
		return constraint;
	}
	
	public String getConstraint(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_CONSTRAINT, String.class);
	}

	public void setConstraint(String constraint) {
		String oldConstraint = this.constraint;
		this.constraint = constraint;
		firePropertyChange(PROPERTY_CONSTRAINT, oldConstraint, constraint);
	}

	public String getType() {
		return type;
	}
	
	public String getType(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_TYPE, String.class);
	}

	public void setType(String type) {
		String oldType = this.type;
		this.type = type;
		firePropertyChange(PROPERTY_CONSTRAINT, oldType, type);
	}
	
	public IChange changeFormRuleType(String type) {
		return new ChangeSetFormRuleType(type);
	}
	
	public IChange changeFormRuleSort1(String sort1) {
		return new ChangeSetFormRuleSort1(sort1);
	}
	
	public IChange changeFormRuleSort2(String sort2) {
		return new ChangeSetFormRuleSort2(sort2);
	}
	
	public IChange changeFormRuleCons(String cons) {
		return new ChangeSetFormRuleCons(cons);
	}
	
}
