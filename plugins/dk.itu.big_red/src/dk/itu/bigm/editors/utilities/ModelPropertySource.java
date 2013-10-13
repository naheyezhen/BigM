package dk.itu.bigm.editors.utilities;

import java.util.ArrayList;
import java.util.List;

import org.bigraph.extensions.param.ParameterUtilities;
import org.bigraph.model.InnerName;
import org.bigraph.model.Layoutable;
import org.bigraph.model.Link;
import org.bigraph.model.LinkSort;
import org.bigraph.model.ModelObject;
import org.bigraph.model.NamedModelObject;
import org.bigraph.model.Node;
import org.bigraph.model.Port;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.descriptors.BoundDescriptor;
import org.bigraph.model.names.policies.INamePolicy;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import dk.itu.bigm.model.Colour;
import dk.itu.bigm.model.ColourUtilities;
import dk.itu.bigm.model.ExtendedDataUtilities;
import dk.itu.bigm.utilities.CommonFuncUtilities;
import dk.itu.bigm.utilities.ui.NullTextPropertyDescriptor;

import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetBool;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetClass;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetCmp;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetDTD;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetDer;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetInUse;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetInd;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetList;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetMath;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetPri;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetPro;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetString;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetUML;
import static dk.itu.bigm.utilities.ui.LinkSortPrefs.keySetUserDef;

public class ModelPropertySource implements IRedPropertySource {
	private Layoutable object;
	private List<String> innerSort = new ArrayList<String>();
	private String[] sortKeys;
	private String[] sortRoles = new String[] { "<none>", "start", "end" };

	public ModelPropertySource(Layoutable object) {
		this.object = object;
	}

	protected Layoutable getModel() {
		return object;
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	public abstract class ChangeValidator implements ICellEditorValidator {
		public abstract IChange getChange(Object value);

		@Override
		public String isValid(Object value) {
			try {
				getModel().getBigraph().tryValidateChange(getChange(value));
				return null;
			} catch (ChangeRejectedException cre) {
				return cre.getRationale();
			}
		}
	}

	private class NameValidator extends ChangeValidator {
		@Override
		public IChange getChange(Object value) {
			return new BoundDescriptor(getModel().getBigraph(),
					new NamedModelObject.ChangeNameDescriptor(getModel()
							.getIdentifier(), (String) value));
		}
	}

	private List<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();

	protected void addPropertyDescriptor(IPropertyDescriptor d) {
		properties.add(d);
	}

	/**
	 * Creates the {@link IPropertyDescriptor}s for this
	 * {@link ModelPropertySource}.
	 * <p>
	 * Subclasses can override, but they should call the {@code super}
	 * implementation at the earliest opportunity.
	 */
	@SuppressWarnings("unchecked")
	protected void buildPropertyDescriptors() {
		// 这里控制属性页（右侧那货）怎么显示
		addPropertyDescriptor(new PropertyDescriptor("Class", "Class"));
		if (object instanceof Link) {
			addPropertyDescriptor(new PropertyDescriptor(
					ExtendedDataUtilities.LINKSORT, "Link Sort"));
			addPropertyDescriptor(new ColorPropertyDescriptor(
					ColourUtilities.OUTLINE, "Colour"));
		} else if (object instanceof Node) {

			addPropertyDescriptor(new PropertyDescriptor(
					ExtendedDataUtilities.PLACESORT, "Place Sort"));
			addPropertyDescriptor(new ColorPropertyDescriptor(
					ColourUtilities.FILL, "Fill colour"));
			addPropertyDescriptor(new ColorPropertyDescriptor(
					ColourUtilities.OUTLINE, "Outline colour"));
			INamePolicy p = ParameterUtilities
					.getParameterPolicy(((Node) object).getControl());
			if (p != null)
				addPropertyDescriptor(new NullTextPropertyDescriptor(
						ParameterUtilities.PARAMETER, "Parameter"));
		} else if (object instanceof Port) {
			addPropertyDescriptor(new PropertyDescriptor(
					ExtendedDataUtilities.PORTSORT, "Port Sort"));
		} else if (object instanceof InnerName) {
			CommonFuncUtilities.refreshPrefsContent(new Object[] { keySetUML,
					keySetClass, keySetInd, keySetPro, keySetBool,
					keySetString, keySetList, keySetCmp, keySetMath, keySetDTD,
					keySetPri, keySetDer, keySetUserDef }, new String[] {
					"UML_", "OWLClass_", "OWLIndividual_", "OWLProperty_",
					"SWRLBool_", "SWRLString_", "SWRLList_", "SWRLCmp_",
					"SWRLMath_", "SWRLDTD_", "XMLPrimitive_", "SWRLDerived_",
					"UserDef_" }, keySetInUse);

			innerSort.clear();
			// CommonFuncUtilities.copyListKeyToList(keySetInUse, innerSort);
			InnerName in = (InnerName) object;
			ArrayList<LinkSort> linkSorts = (ArrayList<LinkSort>) in
					.getBigraph().getSignature().getFormRules().getSortSet()
					.getLinkSorts();
			for (LinkSort ls : linkSorts) {
				innerSort.add(ls.getName());
			}

			int linkConfigNum = innerSort.size();
			sortKeys = new String[linkConfigNum + 1];
			sortKeys[0] = "<none>";
			CommonFuncUtilities.copyListToArray(innerSort, sortKeys);

			addPropertyDescriptor(new ComboBoxPropertyDescriptor(
					ExtendedDataUtilities.INNERSORT, "InnerName Sort", sortKeys));
			addPropertyDescriptor(new ComboBoxPropertyDescriptor(
					ExtendedDataUtilities.INNERSORTROLE, "Sort Role", sortRoles));
		}

		if (object instanceof ModelObject)
			addPropertyDescriptor(new NullTextPropertyDescriptor(
					ExtendedDataUtilities.COMMENT, "Comment"));
		if (object instanceof Layoutable) {
			if (null != object.getName()) {
				if (object.getName().startsWith("_class_")) {
					// do nothing
				} else if (object.getName().startsWith("_anonymousObj_")) {
					NullTextPropertyDescriptor d = new NullTextPropertyDescriptor(
							"", "Name");
					addPropertyDescriptor(d);
				} else {
					NullTextPropertyDescriptor d = new NullTextPropertyDescriptor(
							Layoutable.PROPERTY_NAME, "Name");
					d.setValidator(new NameValidator());
					addPropertyDescriptor(d);
				}
			}
		}
	}

	@Override
	public final IPropertyDescriptor[] getPropertyDescriptors() {
		properties.clear();
		buildPropertyDescriptors();
		return properties.toArray(new IPropertyDescriptor[0]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals("Class")) {
			return object.getType();
		} else {
			if (ParameterUtilities.PARAMETER.equals(id)) {
				return ParameterUtilities.getParameter((Node) object);
			} else if (ExtendedDataUtilities.COMMENT.equals(id)) {
				return ExtendedDataUtilities.getComment(object);
			} else if (ColourUtilities.FILL.equals(id)) {
				return ColourUtilities.getFill(object).getRGB();
			} else if (ColourUtilities.OUTLINE.equals(id)) {
				return ColourUtilities.getOutline(object).getRGB();
			} else if (Layoutable.PROPERTY_NAME.equals(id)) {
				return object.getName();
			} else if (ExtendedDataUtilities.LINKSORT.equals(id)) {
				return ExtendedDataUtilities.getLinkType((Link) object);
			} else if (ExtendedDataUtilities.PLACESORT.equals(id)) {
				return ExtendedDataUtilities.getPlaceSort((Node) object);
			} else if (ExtendedDataUtilities.PORTSORT.equals(id)) {
				return ExtendedDataUtilities.getPortSort((Port) object);
			} else if (ExtendedDataUtilities.INNERSORT.equals(id)) {
				String innersort = ExtendedDataUtilities
						.getInnerSort((InnerName) object);
				if (innersort == null || innersort.equals("")
						|| innersort.equals("none"))
					return 0;
				else {
					return innerSort.indexOf(innersort.split(":")[0]) + 1;
				}
			} else if (ExtendedDataUtilities.INNERSORTROLE.equals(id)) {
				String innersort = ExtendedDataUtilities
						.getInnerSort((InnerName) object);
				if (innersort == null || innersort.equals("")
						|| innersort.equals("none"))
					return 0;
				else {
					String sortrole = innersort.split(":")[1];
					if (sortrole.equals("start"))
						return 1;
					else if (sortrole.equals("end"))
						return 2;
					else
						return 0;
				}
			} else
				return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	/**
	 * @deprecated Do not call this method.
	 * @throws UnsupportedOperationException
	 *             always and forever
	 */
	@Override
	@Deprecated
	public final void setPropertyValue(Object id, Object value) {
		throw new Error(new UnsupportedOperationException("" + id + ", "
				+ value).fillInStackTrace());
	}

	/**
	 * @deprecated Do not call this method.
	 * @throws UnsupportedOperationException
	 *             always and forever
	 */
	@Override
	@Deprecated
	public final void resetPropertyValue(Object id) {
		throw new Error(
				new UnsupportedOperationException("" + id).fillInStackTrace());
	}

	@Override
	public IChange setPropertyValueChange(Object id, Object newValue) {
		if (Layoutable.PROPERTY_NAME.equals(id)) {
			return new BoundDescriptor(getModel().getBigraph(),
					new NamedModelObject.ChangeNameDescriptor(getModel()
							.getIdentifier(), (String) newValue));
		} else if (ExtendedDataUtilities.COMMENT.equals(id)) {
			return ExtendedDataUtilities.changeComment(getModel(),
					(String) newValue);
		} else if (ExtendedDataUtilities.INNERSORT.equals(id)) {
			String innersort = sortKeys[Integer.parseInt(newValue.toString())];
			if ("<none>".equals(innersort))
				innersort = "none";
			String innerrole = ((InnerName) getModel()).getInnerSort();
			if (innerrole.contains(":"))
				innerrole = innerrole.split(":")[1];
			else
				innerrole = "none";
			return ExtendedDataUtilities.changeInnerSort(
					(InnerName) getModel(), innersort + ":" + innerrole);
		} else if (ExtendedDataUtilities.INNERSORTROLE.equals(id)) {
			String innersort = ((InnerName) getModel()).getInnerSort();
			if (innersort.contains(":"))
				innersort = ((InnerName) getModel()).getInnerSort().split(":")[0];
			else
				innersort = "none";
			String innerrole = sortRoles[Integer.parseInt(newValue.toString())];
			if ("<none>".equals(innerrole))
				innerrole = "none";
			return ExtendedDataUtilities.changeInnerSort(
					(InnerName) getModel(), innersort + ":" + innerrole);
		} else if (ColourUtilities.FILL.equals(id)) {
			return ColourUtilities.changeFill(getModel(), new Colour(
					(RGB) newValue));
		} else if (ColourUtilities.OUTLINE.equals(id)) {
			return ColourUtilities.changeOutline(getModel(), new Colour(
					(RGB) newValue));
		} else if (ParameterUtilities.PARAMETER.equals(id)) {
			return ParameterUtilities.changeParameter((Node) getModel(),
					(String) newValue);
		} else
			return null;
	}

	@Override
	public IChange resetPropertyValueChange(Object id) {
		if (ColourUtilities.FILL.equals(id)) {
			return ColourUtilities.changeFill(getModel(), null);
		} else if (ColourUtilities.OUTLINE.equals(id)) {
			return ColourUtilities.changeOutline(getModel(), null);
		} else
			return null;
	}
}
