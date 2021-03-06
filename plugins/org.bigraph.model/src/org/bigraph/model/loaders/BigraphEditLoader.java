package org.bigraph.model.loaders;

import static org.bigraph.model.loaders.BigmNamespaceConstants.EDIT_BIG;
import static org.bigraph.model.loaders.BigmNamespaceConstants.EDIT_SIG;

import java.util.List;

import org.bigraph.model.Bigraph;
import org.bigraph.model.Container;
import org.bigraph.model.Control;
import org.bigraph.model.Edge;
import org.bigraph.model.InnerName;
import org.bigraph.model.Layoutable;
import org.bigraph.model.Link;
import org.bigraph.model.ModelObject;
import org.bigraph.model.NamedModelObject;
import org.bigraph.model.Node;
import org.bigraph.model.OuterName;
import org.bigraph.model.Point;
import org.bigraph.model.Port;
import org.bigraph.model.Root;
import org.bigraph.model.Site;
import org.bigraph.model.changes.descriptors.IChangeDescriptor;
import org.bigraph.model.loaders.EditXMLLoader.Participant;
import org.bigraph.model.process.IParticipantHost;
import org.w3c.dom.Element;

public class BigraphEditLoader implements Participant {
	private static Root.Identifier getRootIdentifier(Element el) {
		return new Root.Identifier(
				EditXMLLoader.getAttributeNS(el, EDIT_BIG, "name"));
	}
	
	private static Site.Identifier getSiteIdentifier(Element el) {
		return new Site.Identifier(
				EditXMLLoader.getAttributeNS(el, EDIT_BIG, "name"));
	}
	
	private static Edge.Identifier getEdgeIdentifier(Element el) {
		return new Edge.Identifier(
				EditXMLLoader.getAttributeNS(el, EDIT_BIG, "name"));
	}
	
	private static InnerName.Identifier getInnerNameIdentifier(Element el) {
		return new InnerName.Identifier(
				EditXMLLoader.getAttributeNS(el, EDIT_BIG, "name"));
	}
	
	private static OuterName.Identifier getOuterNameIdentifier(Element el) {
		return new OuterName.Identifier(
				EditXMLLoader.getAttributeNS(el, EDIT_BIG, "name"));
	}
	
	private static Bigraph.Identifier getBigraphIdentifier() {
		return new Bigraph.Identifier();
	}
	
	private static Port.Identifier getPortIdentifier(Element el) {
		Node.Identifier id = getNodeIdentifier(
				EditXMLLoader.getNamedChildElement(el, EDIT_BIG, "node-id"));
		return new Port.Identifier(
				EditXMLLoader.getAttributeNS(el, EDIT_BIG, "name"), id);
	}
	
	private static Node.Identifier getNodeIdentifier(Element el) {
		Control.Identifier id = getControlIdentifier(
				EditXMLLoader.getNamedChildElement(el, EDIT_SIG, "control-id"));
		return new Node.Identifier(
				EditXMLLoader.getAttributeNS(el, EDIT_BIG, "name"), id);
	}
	
	private static Control.Identifier getControlIdentifier(Element el) {
		return new Control.Identifier(
				EditXMLLoader.getAttributeNS(el, EDIT_SIG, "name"));
	}
	
	public static ModelObject.Identifier getIdentifier(Element el) {
		if (el == null)
			return null;
		String
			localName = el.getLocalName(),
			namespace = el.getNamespaceURI();
		if (EDIT_BIG.equals(namespace)) {
			if ("bigraph-id".equals(localName)) {
				return getBigraphIdentifier();
			} else if ("root-id".equals(localName)) {
				return getRootIdentifier(el);
			} else if ("edge-id".equals(localName)) {
				return getEdgeIdentifier(el);
			} else if ("innername-id".equals(localName)) {
				return getInnerNameIdentifier(el);
			} else if ("outername-id".equals(localName)) {
				return getOuterNameIdentifier(el);
			} else if ("node-id".equals(localName)) {
				return getNodeIdentifier(el);
			} else if ("port-id".equals(localName)) {
				return getPortIdentifier(el);
			} else if ("site-id".equals(localName)) {
				return getSiteIdentifier(el);
			} else return null;
		} else if (EDIT_SIG.equals(namespace)) {
			if ("control-id".equals(localName)) {
				return getControlIdentifier(el);
			} else return null;
		} else return null;
	}
	
	public static <T extends ModelObject.Identifier> T getIdentifier(
			Element el, Class<T> klass) {
		return ModelObject.require(getIdentifier(el), klass);
	}
	
	@Override
	public IChangeDescriptor getDescriptor(Element descriptor) {
		String
			nsURI = descriptor.getNamespaceURI(),
			localName = descriptor.getLocalName();
		if (EDIT_BIG.equals(nsURI)) {
			List<Element> ids = EditXMLLoader.getChildElements(descriptor);
			if ("add".equals(localName)) {
				Container.Identifier parent = getIdentifier(
						ids.get(0), Container.Identifier.class);
				Layoutable.Identifier child = getIdentifier(
						ids.get(1), Layoutable.Identifier.class);
				return new Container.ChangeAddChildDescriptor(
						parent, child);
			} else if ("remove".equals(localName)) {
				Container.Identifier parent = getIdentifier(
						ids.get(0), Container.Identifier.class);
				Layoutable.Identifier child = getIdentifier(
						ids.get(1), Layoutable.Identifier.class);
				return new Layoutable.ChangeRemoveDescriptor(child, parent);
			} else if ("connect".equals(localName)) {
				Point.Identifier point = getIdentifier(
						ids.get(0), Point.Identifier.class);
				Link.Identifier link = getIdentifier(
						ids.get(1), Link.Identifier.class);
				return new Point.ChangeConnectDescriptor(point, link);
			} else if ("disconnect".equals(localName)) {
				Point.Identifier point = getIdentifier(
						ids.get(0), Point.Identifier.class);
				Link.Identifier link = getIdentifier(
						ids.get(1), Link.Identifier.class);
				return new Point.ChangeDisconnectDescriptor(point, link);
			} else return null;
		} else return null;
	}
	
	@Override
	public IChangeDescriptor getRenameDescriptor(Element id, String name) {
		Layoutable.Identifier
			lid = getIdentifier(id, Layoutable.Identifier.class);
		return (lid != null ?
				new NamedModelObject.ChangeNameDescriptor(lid, name) : null);
	}
	
	@Override
	public void setHost(IParticipantHost host) {
		/* do nothing */
	}
}