package dk.itu.bigm.model.load_save;

import org.bigraph.model.Bigraph;
import org.bigraph.model.Control;
import org.bigraph.model.Layoutable;
import org.bigraph.model.ModelObject;
import org.bigraph.model.Port;
import org.bigraph.model.PortSpec;
import org.bigraph.model.ReactionRule;
import org.bigraph.model.Signature;
import org.bigraph.model.SimulationSpec;
import org.bigraph.model.process.IParticipantHost;
import org.bigraph.model.savers.IXMLSaver;
import org.bigraph.model.savers.XMLSaver;
import org.bigraph.model.savers.Saver.SaverOption;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dk.itu.bigm.model.BigMNamespaceConstants;
import dk.itu.bigm.model.Colour;
import dk.itu.bigm.model.ColourUtilities;
import dk.itu.bigm.model.ControlUtilities;
import dk.itu.bigm.model.ExtendedDataUtilities;
import dk.itu.bigm.model.LayoutUtilities;

public class BigMXMLDecorator implements IXMLSaver.Decorator {
	private boolean generateAppearance = true;
	
	@Override
	public void setHost(IParticipantHost host) {
		if (host instanceof XMLSaver)
			((XMLSaver)host).addOption(new SaverOption(
					"Generate appearance data",
					"Include BigM-specific appearance data " +
					"in the output.") {
				@Override
				public Object get() {
					return generateAppearance;
				}
				
				@Override
				public void set(Object value) {
					if (value instanceof Boolean)
						generateAppearance = (Boolean)value;
				}
			});
	}
	
	public static Element rectangleToElement(Element e, Rectangle r) {
		e.setAttributeNS(null, "width", "" + r.width());
		e.setAttributeNS(null, "height", "" + r.height());
		e.setAttributeNS(null, "x", "" + r.x());
		e.setAttributeNS(null, "y", "" + r.y());
		return e;
	}
	
	@Override
	public void decorate(ModelObject object, Element el) {
		Document doc = el.getOwnerDocument();
		
		if (object instanceof Control) {
			Control c = (Control)object;
			
			if (generateAppearance) {
				Element aE = doc.createElementNS(BigMNamespaceConstants.BIGM, "bigm:shape");
				
				Object shape = ControlUtilities.getShape(c);
				aE.setAttributeNS(BigMNamespaceConstants.BIGM, "bigm:shape",
						(shape instanceof PointList ? "polygon" : "oval"));
				
				if (shape instanceof PointList) {
					PointList pl = (PointList)shape;
					for (int i = 0; i < pl.size(); i++) {
						Point p = pl.getPoint(i);
						Element pE =
								doc.createElementNS(BigMNamespaceConstants.BIGM, "bigm:point");
						pE.setAttributeNS(BigMNamespaceConstants.BIGM, "bigm:x", "" + p.x);
						pE.setAttributeNS(BigMNamespaceConstants.BIGM, "bigm:y", "" + p.y);
						aE.appendChild(pE);
					}
				}
			
				el.setAttributeNS(BigMNamespaceConstants.BIGM, "bigm:label",
						ControlUtilities.getLabel(c));
				el.appendChild(aE);
			}
			/* continue */
		} else if (object instanceof PortSpec) {
			PortSpec p = (PortSpec)object;
			
			if (generateAppearance) {
				Element pA =
					doc.createElementNS(BigMNamespaceConstants.BIGM, "bigm:port-appearance");
				pA.setAttributeNS(BigMNamespaceConstants.BIGM, "bigm:segment",
						"" + ControlUtilities.getSegment(p));
				pA.setAttributeNS(BigMNamespaceConstants.BIGM, "bigm:distance",
						"" + ControlUtilities.getDistance(p));
				el.appendChild(pA);
			}
			return;
		} else if (object instanceof Signature || object instanceof Bigraph ||
				object instanceof Port || object instanceof SimulationSpec ||
				object instanceof ReactionRule)
			return;
		
		Element aE = doc.createElementNS(BigMNamespaceConstants.BIGM, "bigm:appearance");
		
		if (object instanceof Layoutable) {
			Rectangle layout =
					LayoutUtilities.getLayoutRaw((Layoutable)object);
			if (layout != null)
				rectangleToElement(aE, layout);
		}
		
		Colour
			fill = ColourUtilities.getFillRaw(object),
			outline = ColourUtilities.getOutlineRaw(object);
		if (fill != null)
			aE.setAttributeNS(BigMNamespaceConstants.BIGM, "bigm:fillColor",
					fill.toHexString());
		if (outline != null)
			aE.setAttributeNS(BigMNamespaceConstants.BIGM, "bigm:outlineColor",
					outline.toHexString());
		
		String comment = ExtendedDataUtilities.getComment(object);
		if (comment != null)
			aE.setAttributeNS(BigMNamespaceConstants.BIGM, "bigm:comment", comment);
		
		if (generateAppearance && (aE.hasChildNodes() || aE.hasAttributes()))
			el.appendChild(aE);
	}
}
