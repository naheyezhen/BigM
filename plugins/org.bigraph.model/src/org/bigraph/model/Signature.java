package org.bigraph.model;

import java.util.ArrayList;
import java.util.List;
import org.bigraph.model.Control.ChangeRemoveControl;
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
import org.bigraph.model.interfaces.ISignature;
import org.bigraph.model.names.HashMapNamespace;
import org.bigraph.model.names.Namespace;
import org.bigraph.model.names.policies.StringNamePolicy;

/**
 * The Signature is a central storage point for {@link Control}s and their
 * properties (both in terms of the bigraph model and their visual
 * representations). Every {@link Bigraph} has an associated Signature, which
 * they consult whenever they need to create a {@link Node}.
 * @author alec
 * @see ISignature
 */
public class Signature extends ModelObject
		implements ISignature, IChangeExecutor,
				ModelObject.Identifier.Resolver {
	private Signature parent;
	private List<Signature> signatures = new ArrayList<Signature>();
	
	/**
	 * The property name fired when a control is added or removed.
	 */
	@BigmProperty(fired = Control.class, retrieved = List.class)
	public static final String PROPERTY_CONTROL = "SignatureControl";
	
	@BigmProperty(fired = Signature.class, retrieved = List.class)
	public static final String PROPERTY_CHILD = "SignatureChild";
	
	@BigmProperty(fired = Signature.class, retrieved = Signature.class)
	public static final String PROPERTY_PARENT = "SignatureParent";
	
	/**
	 * The property name fired when the formRules changes.
	 */
	@BigmProperty(fired = FormRules.class, retrieved = FormRules.class)
	public static final String PROPERTY_FORMRULES = "SignatureFormRules";
	
	private FormRules formRules;
	
	protected void setFormRules(FormRules formRules) {
		FormRules oldFormRules = this.formRules;
		this.formRules = formRules;
		firePropertyChange(PROPERTY_FORMRULES, oldFormRules, formRules);
	}
	
	public FormRules getFormRules() {
		return formRules;
	}
	
	public FormRules getFormRules(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_FORMRULES, FormRules.class);
	}
	
	abstract class SignatureChange extends ModelObjectChange {
		@Override
		public Signature getCreator() {
			return Signature.this;
		}
	}
	
	public final class ChangeAddControl extends SignatureChange {
		public final Control control;
		public final String name;
		
		public ChangeAddControl(Control control, String name) {
			this.control = control;
			this.name = name;
		}

		@Override
		public ChangeRemoveControl inverse() {
			return control.new ChangeRemoveControl();
		}
		
		@Override
		public String toString() {
			return "Change(add control " + control + " to signature " +
					getCreator() + " with name " + name + ")";
		}
		
		@Override
		public boolean isReady() {
			return (control != null);
		}
		
		@Override
		public void simulate(PropertyScratchpad context) {
			context.<Control>getModifiableList(
					getCreator(), PROPERTY_CONTROL, getControls()).
				add(control);
			context.setProperty(control,
					Control.PROPERTY_SIGNATURE, getCreator());
			
			getCreator().getNamespace().put(context, name, control);
			context.setProperty(control, Control.PROPERTY_NAME, name);
		}
	}
	
	private Namespace<Control> ns = new HashMapNamespace<Control>(
			new StringNamePolicy() {
		@Override
		public String get(int value) {
			return "Control" + (value + 1);
		}
	});
	
	public Namespace<Control> getNamespace() {
		return ns;
	}
	
	private ArrayList<Control> controls = new ArrayList<Control>();
	
	@Override
	public Signature clone() {
		Signature s = (Signature)super.clone();
		
		for (Control c : getControls())
			s.addControl(-1, c.clone(s));
		
		for (Signature t : getSignatures())
			s.addSignature(-1, t.clone());
		
		return s;
	}
	
	protected void addControl(int position, Control c) {
		if (position == -1) {
			controls.add(c);
		} else controls.add(position, c);
		c.setSignature(this);
		firePropertyChange(PROPERTY_CONTROL, null, c);
	}
	
	protected void removeControl(Control m) {
		controls.remove(m);
		m.setSignature(null);
		firePropertyChange(PROPERTY_CONTROL, m, null);
	}
	
	public Control getControl(String name) {
		for (Control c : getControls())
			if (c.getName().equals(name))
				return c;
		Control c = null;
		for (Signature s : getSignatures())
			if ((c = s.getControl(name)) != null)
				return c;
		return null;
	}
	
	@Override
	public List<? extends Control> getControls() {
		return controls;
	}

	@SuppressWarnings("unchecked")
	public List<? extends Control> getControls(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_CONTROL, List.class);
	}

	public static final String CONTENT_TYPE = "dk.itu.bigm.signature";
	
	@Override
	public void tryValidateChange(IChange b) throws ChangeRejectedException {
		ExecutorManager.getInstance().tryValidateChange(b);
	}
	
	@Override
	public void tryApplyChange(IChange b) throws ChangeRejectedException {
		ExecutorManager.getInstance().tryApplyChange(b);
	}

	static {
		ExecutorManager.getInstance().addHandler(new SignatureHandler());
	}
	
	@Override
	public void dispose() {
		if (controls != null) {
			for (Control c : controls)
				c.dispose();
			controls.clear();
			controls = null;
		}
		
		if (signatures != null) {
			for (Signature s : signatures)
				s.dispose();
			signatures.clear();
			signatures = null;
		}

		super.dispose();
	}
	
	/**
	 * {@inheritDoc}
	 * <p><strong>Special notes for {@link Signature}:</strong>
	 * <ul>
	 * <li>Passing {@link #PROPERTY_CONTROL} will return a
	 * {@link List}&lt;{@link Control}&gt;, <strong>not</strong> a {@link
	 * Control}.
	 * <li>Passing {@link #PROPERTY_CHILD} will return a
	 * {@link List}&lt;{@link Signature}&gt;, <strong>not</strong> a {@link
	 * Signature}.
	 * </ul>
	 */
	@Override
	protected Object getProperty(String name) {
		if (PROPERTY_CONTROL.equals(name)) {
			return getControls();
		} else if (PROPERTY_PARENT.equals(name)) {
			return getParent();
		} else if (PROPERTY_CHILD.equals(name)) {
			return getSignatures();
		} else return super.getProperty(name);
	}
	
	public Signature getParent() {
		return parent;
	}
	
	public Signature getParent(PropertyScratchpad context) {
		return getProperty(context, PROPERTY_PARENT, Signature.class);
	}
	
	protected void setParent(Signature newValue) {
		Signature oldValue = parent;
		parent = newValue;
		firePropertyChange(PROPERTY_PARENT, oldValue, newValue);
	}
	
	public List<? extends Signature> getSignatures() {
		return signatures;
	}
	
	@SuppressWarnings("unchecked")
	public List<? extends Signature> getSignatures(
			PropertyScratchpad context) {
		return getProperty(context, PROPERTY_CHILD, List.class);
	}
	
	protected void addSignature(int position, Signature s) {
		if (position == -1) {
			signatures.add(s);
		} else signatures.add(position, s);
		s.setParent(this);
		firePropertyChange(PROPERTY_CHILD, null, s);
	}
	
	protected void removeSignature(Signature s) {
		signatures.remove(s);
		s.setParent(null);
		firePropertyChange(PROPERTY_CHILD, s, null);
	}
	
	public IChange changeAddControl(Control control, String name) {
		return new ChangeAddControl(control, name);
	}
	
	@Override
	public Object lookup(
			PropertyScratchpad context, ModelObject.Identifier identifier) {
		if (identifier instanceof Signature.Identifier) {
			return this;
		} else if (identifier instanceof Control.Identifier) {
			return getControl(((Control.Identifier)identifier).getName());
		} else if (identifier instanceof PortSpec.Identifier) {
			PortSpec.Identifier id = (PortSpec.Identifier)identifier;
			Control c = getControl(id.getControl().getName());
			if (c != null)
				return c.getNamespace().get(context, id.getName());
		}
		return null;
	}
	
	public static final class Identifier implements ModelObject.Identifier {
		@Override
		public Signature lookup(PropertyScratchpad context, Resolver r) {
			return require(r.lookup(context, this), Signature.class);
		}
	}
	
	abstract static class SignatureChangeDescriptor
			extends ModelObjectChangeDescriptor {
		static {
			DescriptorExecutorManager.getInstance().addHandler(
					new SignatureDescriptorHandler());
		}
		
		@Override
		public IChange createChange(PropertyScratchpad context, Resolver r)
				throws ChangeCreationException {
			return new BoundDescriptor(r, this);
		}
	}
	
	public static final class ChangeAddControlDescriptor
			extends SignatureChangeDescriptor {
		private final Identifier target;
		private final int position;
		private final Control.Identifier control;
		
		public ChangeAddControlDescriptor(
				Identifier target, int position, Control.Identifier control) {
			this.target = target;
			this.position = position;
			this.control = control;
		}
		
		public Identifier getTarget() {
			return target;
		}
		
		public int getPosition() {
			return position;
		}
		
		public Control.Identifier getControl() {
			return control;
		}
		
		@Override
		public IChange createChange(PropertyScratchpad context, Resolver r)
				throws ChangeCreationException {
			Signature s = getTarget().lookup(context, r);
			if (s == null)
				throw new ChangeCreationException(this,
						"" + getTarget() + ": lookup failed");
			return s.changeAddControl(new Control(), getControl().getName());
		}
		
		@Override
		public IChangeDescriptor inverse() {
			throw new UnsupportedOperationException(
					"FIXME: ChangeAddControlDescriptor.inverse() " +
					"not implementable");
		}
	}
	
	public static final class ChangeAddSignatureDescriptor
			extends SignatureChangeDescriptor {
		private final Identifier target;
		private final int position;
		private final Signature signature;
		
		public ChangeAddSignatureDescriptor(
				Identifier target, int position, Signature signature) {
			this.target = target;
			this.position = position;
			this.signature = signature;
		}
		
		public Identifier getTarget() {
			return target;
		}
		
		public int getPosition() {
			return position;
		}
		
		public Signature getSignature() {
			return signature;
		}
		
		@Override
		public IChangeDescriptor inverse() {
			return new ChangeRemoveSignatureDescriptor(
					getTarget(), getPosition(), getSignature());
		}
		
		@Override
		public void simulate(PropertyScratchpad context, Resolver r) {
			Signature self = getTarget().lookup(context, r);
			context.<Signature>getModifiableList(
					self, PROPERTY_CHILD, self.getSignatures()).add(
							getSignature());
			context.setProperty(getSignature(), PROPERTY_PARENT, self);
		}
	}
	
	public static final class ChangeRemoveSignatureDescriptor
			extends SignatureChangeDescriptor {
		private final Identifier target;
		private final int position;
		private final Signature signature;

		public ChangeRemoveSignatureDescriptor(
				Identifier target, int position, Signature signature) {
			this.target = target;
			this.position = position;
			this.signature = signature;
		}

		public Identifier getTarget() {
			return target;
		}

		public int getPosition() {
			return position;
		}

		public Signature getSignature() {
			return signature;
		}

		@Override
		public IChangeDescriptor inverse() {
			return new ChangeAddSignatureDescriptor(
					getTarget(), getPosition(), getSignature());
		}
		
		@Override
		public void simulate(PropertyScratchpad context, Resolver r) {
			Signature self = getTarget().lookup(context, r);
			
			context.<Signature>getModifiableList(
					self, PROPERTY_CHILD, self.getSignatures()).remove(
							getSignature());
			context.setProperty(getSignature(), PROPERTY_PARENT, null);
		}
	}
	
	public static final class ChangeSetFormRulesDescriptor 
			extends SignatureChangeDescriptor {
		private final Identifier target;
		private final FormRules oldFormRules;
		private final FormRules newFormRules;

		public ChangeSetFormRulesDescriptor(Identifier target,
				FormRules oldFormRules, FormRules newFormRules) {
			this.target = target;
			this.oldFormRules = oldFormRules;
			this.newFormRules = newFormRules;
		}

		public Identifier getTarget() {
			return target;
		}

		public FormRules getOldFormRules() {
			return oldFormRules;
		}

		public FormRules getNewFormRules() {
			return newFormRules;
		}

		@Override
		public IChangeDescriptor inverse() {
			return new ChangeSetFormRulesDescriptor(getTarget(),
					getNewFormRules(), getOldFormRules());
		}

		@Override
		public void simulate(PropertyScratchpad context, Resolver r) {
			Signature self = getTarget().lookup(context, r);
			context.setProperty(self, PROPERTY_FORMRULES, getNewFormRules());
		}
	}
}