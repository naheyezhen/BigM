package dk.itu.bigm.model;

import org.bigraph.model.InnerName;
import org.bigraph.model.Layoutable;
import org.bigraph.model.Link;
import org.bigraph.model.ModelObject;
import org.bigraph.model.ModelObject.ExtendedDataNormaliser;
import org.bigraph.model.Node;
import org.bigraph.model.Port;
import org.bigraph.model.Site;
import org.bigraph.model.ModelObject.ChangeExtendedData;
import org.bigraph.model.ModelObject.ExtendedDataValidator;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.assistants.BigmProperty;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.descriptors.IChangeDescriptor;
import org.bigraph.model.names.policies.BoundedIntegerNamePolicy;
import org.bigraph.model.names.policies.INamePolicy;

import scala.reflect.generic.Trees.New;

import static org.bigraph.model.assistants.ExtendedDataUtilities.getProperty;
import static org.bigraph.model.assistants.ExtendedDataUtilities.setProperty;

/**
 * The <strong>ExtendedDataUtilities</strong> class is a collection of static
 * methods and fields for manipulating some of the extended data used by Big
 * Red.
 * @author alec
 * @see ColourUtilities
 * @see LayoutUtilities
 */
public abstract class ExtendedDataUtilities {
	private ExtendedDataUtilities() {}
	
	
	private static final ExtendedDataNormaliser commentNormaliser =
			new ExtendedDataNormaliser() {
		@Override
		public Object normalise(ChangeExtendedData c, Object rawValue) {
			if (rawValue instanceof String) {
				String s = ((String)rawValue).trim();
				if (s.length() > 0)
					return s;
			}
			return null;
		}
	};
	
	@BigmProperty(fired = String.class, retrieved = String.class)
	public static final String COMMENT =
			"eD!+dk.itu.bigm.model.ModelObject.comment";
	
	public static String getComment(ModelObject m) {
		return getComment(null, m);
	}
	
	public static String getComment(
			PropertyScratchpad context, ModelObject m) {
		return getProperty(context, m, COMMENT, String.class);
	}
	
	public static void setComment(ModelObject m, String s) {
		m.setExtendedData(COMMENT, s);
	}
	
	public static IChange changeComment(ModelObject m, String s) {
		return m.changeExtendedData(COMMENT, s, null, commentNormaliser);
	}
	
	public static IChangeDescriptor changeCommentDescriptor(
			ModelObject.Identifier l, String oldC, String newC) {
		return new ModelObject.ChangeExtendedDataDescriptor(
				l, COMMENT, oldC, newC, null, commentNormaliser);
	}
	
	private static final ExtendedDataValidator aliasValidator =
			new ExtendedDataValidator() {
		@Override
		public void validate(ChangeExtendedData c, PropertyScratchpad context)
				throws ChangeRejectedException {
			if (c.newValue != null) {
				if (!(c.newValue instanceof String))
					throw new ChangeRejectedException(c,
							"Aliases must be strings");
				INamePolicy np = new BoundedIntegerNamePolicy(0);
				if (np.normalise((String)c.newValue) == null)
					throw new ChangeRejectedException(c,
							"\"" + c.newValue + "\" is not a valid alias" +
							" for " + c.getCreator());
			}
		}
	};
	
	public static final String ALIAS =
			"eD!+dk.itu.bigm.model.Site.alias";
	
	public static String getAlias(Site s) {
		return getAlias(null, s);
	}
	
	public static String getAlias(PropertyScratchpad context, Site s) {
		return getProperty(context, s, ALIAS, String.class);
	}
	
	public static void setAlias(Site s, String a) {
		setAlias(null, s, a);
	}
	
	public static void setAlias(PropertyScratchpad context, Site s, String a) {
		setProperty(context, s, ALIAS, a);
	}
	
	public static IChange changeAlias(Site s, String a) {
		return s.changeExtendedData(ALIAS, a, aliasValidator);
	}
	
	public static IChangeDescriptor changeAliasDescriptor(
			Site.Identifier s, String oldA, String newA) {
		return new Layoutable.ChangeExtendedDataDescriptor(
				s, ALIAS, oldA, newA, aliasValidator, null);
	}
	
	
	public static final String LINKSORT =
			"eD!+dk.itu.bigm.model.linkSort";
	
	public static String getLinkType(Link l) {
		return getLinkType(null, l);
	}
	
	public static String getLinkType(PropertyScratchpad context, Link l) {
		return l.getLinkSort();
	}
	
	public static final String PLACESORT =
			"eD!+dk.itu.bigm.model.placeSort";
	
	public static String getPlaceSort(Node n) {
		return getPlaceSort(null, n);
	}
	
	public static String getPlaceSort(PropertyScratchpad context, Node n) {
		return n.getControl().getPlaceSort();
	}
	
	public static final String PORTSORT =
			"eD!+dk.itu.bigm.model.portSort";
	
	public static String getPortSort(Port n) {
		return getPortSort(null, n);
	}
	
	public static String getPortSort(PropertyScratchpad context, Port n) {
		return n.getSpec().getPortSort();
	}
	
	public static final String INNERSORT =
			"eD!+dk.itu.bigm.model.innerSort";
	
	public static String getInnerSort(InnerName i) {
		return getInnerSort(null, i);
	}
	
	public static String getInnerSort(PropertyScratchpad context, InnerName i) {
		return i.getInnerSort();
	}
	
	public static IChange changeInnerSort(InnerName i, String a) {
		return i.changeInnerSort(a);
	}
	
	public static final String INNERSORTROLE =
			"eD!+dk.itu.bigm.model.innerSortRole";
	
	public static String getInnerSortRole(InnerName i) {
		return getInnerSortRole(null, i);
	}
	
	public static String getInnerSortRole(PropertyScratchpad context, InnerName i) {
		return i.getInnerSort().split(":")[1];
	}
}
