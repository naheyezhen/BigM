package org.bigraph.extensions.param;

import org.bigraph.model.Control;
import org.bigraph.model.ModelObject;
import org.bigraph.model.ModelObject.ChangeExtendedData;
import org.bigraph.model.ModelObject.ExtendedDataNormaliser;
import org.bigraph.model.ModelObject.ExtendedDataValidator;
import org.bigraph.model.Node;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.assistants.BigmProperty;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.descriptors.IChangeDescriptor;
import org.bigraph.model.names.policies.INamePolicy;

import static org.bigraph.model.assistants.ExtendedDataUtilities.setProperty;
import static org.bigraph.model.assistants.ExtendedDataUtilities.getProperty;

public abstract class ParameterUtilities {
	private ParameterUtilities() {}
	
	@BigmProperty(fired = String.class, retrieved = String.class)
	public static final String PARAMETER =
			"eD!+dk.itu.bigm.model.Node.parameter";

	@BigmProperty(fired = INamePolicy.class, retrieved = INamePolicy.class)
	public static final String PARAMETER_POLICY =
			"eD!+dk.itu.bigm.model.Control.parameter-policy";
	
	private static final ExtendedDataNormaliser parameterNormaliser =
			new ExtendedDataNormaliser() {
		@Override
		public Object normalise(ChangeExtendedData c, Object rawValue) {
			return getParameterPolicy(((Node)c.getCreator()).getControl()).
					normalise((String)rawValue);
		}
	};
	
	private static final ExtendedDataValidator parameterValidator =
			new ExtendedDataValidator() {
		@Override
		public void validate(ChangeExtendedData c, PropertyScratchpad context)
				throws ChangeRejectedException {
			if (!(c.getCreator() instanceof Node))
				throw new ChangeRejectedException(c,
						c.getCreator() + " is not a Node");
			Node n = (Node)c.getCreator();
				
			Control control = n.getControl();
			INamePolicy policy = getParameterPolicy(control);
			if (policy == null)
				throw new ChangeRejectedException(c,
						"The control " + control.getName() +
						" does not define a parameter");
			
			if (!(c.newValue instanceof String))
				throw new ChangeRejectedException(c,
						"Parameter values must be strings");
			
			if (policy.normalise((String)c.newValue) == null)
				throw new ChangeRejectedException(c,
						"\"" + c.newValue + "\" is not a valid value for " +
						"the parameter of " + control.getName());
		}
	};

	public static INamePolicy getParameterPolicy(Control c) {
		return getParameterPolicy(null, c);
	}

	public static INamePolicy getParameterPolicy(
			PropertyScratchpad context, Control c) {
		return getProperty(context, c, PARAMETER_POLICY, INamePolicy.class);
	}

	public static void setParameterPolicy(Control c, INamePolicy n) {
		c.setExtendedData(PARAMETER_POLICY, n);
	}

	public static IChange changeParameterPolicy(Control c, INamePolicy n) {
		return c.changeExtendedData(PARAMETER_POLICY, n);
	}

	public static String getParameter(Node n) {
		return getParameter(null, n);
	}

	public static String getParameter(
			PropertyScratchpad context, Node n) {
		INamePolicy p = getParameterPolicy(context, n.getControl());
		String s = getProperty(context, n, PARAMETER, String.class),
				t = null;
		if (p != null) {
			t = p.normalise(s);
			if (t == null)
				t = p.get(0);
		}
		if (s != null ? !s.equals(t) : s != t)
			setParameter(context, n, t);
		return t;
	}

	public static void setParameter(Node n, String s) {
		setParameter(null, n, s);
	}
	
	public static void setParameter(
			PropertyScratchpad context, Node n, String s) {
		setProperty(context, n, PARAMETER, s);
	}

	public static IChange changeParameter(Node n, String s) {
		return n.changeExtendedData(
				PARAMETER, s, parameterValidator, parameterNormaliser);
	}
	
	public static IChangeDescriptor changeParameterDescriptor(
			Node.Identifier n, String oldP, String newP) {
		return new ModelObject.ChangeExtendedDataDescriptor(n, PARAMETER,
				oldP, newP, parameterValidator, parameterNormaliser);
	}
}
