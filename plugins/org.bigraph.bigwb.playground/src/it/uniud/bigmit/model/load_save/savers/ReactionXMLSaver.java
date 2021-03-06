package it.uniud.bigmit.model.load_save.savers;

import it.uniud.bigmit.model.Reaction;

import org.bigraph.model.Bigraph;
import org.bigraph.model.savers.BigraphXMLSaver;
import org.bigraph.model.savers.SaveFailedException;
import org.eclipse.draw2d.geometry.Rectangle;
import org.w3c.dom.Element;

import org.bigraph.model.savers.XMLSaver;

import dk.itu.bigm.model.BigMNamespaceConstants;

public class ReactionXMLSaver extends XMLSaver{
	
	public static final String BRS =
			"http://www.itu.dk/research/pls/xmlns/2012/brs";
	
	public static final String REACTION =
			"http://www.itu.dk/research/pls/xmlns/2012/reaction";
	

	@Override
	public Element processModel(Element e) throws SaveFailedException {
		Object object = getModel();
	
		if (!(object instanceof Reaction))
			throw new SaveFailedException(object + " isn't a valid BRS");
			Reaction ss = (Reaction)object;
		
			Bigraph redex= ss.getRedex();
			redex.setSignature(ss.getSign());
			Element t1=newElement(REACTION, "reaction:redex");
			
			appendChildIfNotNull(e,
					processOrReference(t1,
							redex, new BigraphXMLSaver(this)));
			
			Element pE = newElement(BigMNamespaceConstants.BIGM, "bigm:appearance");
			Rectangle rect= ss.getChildConstraint(redex);
			rectangleToElement(pE, rect);
			t1.appendChild(pE);
			
			Bigraph reactum= ss.getReactum();
			reactum.setSignature(ss.getSign());
			Element t2=newElement(REACTION, "reaction:reactum");
			
			appendChildIfNotNull(e,
					processOrReference(t2,
							reactum, new BigraphXMLSaver(this)));
			
			Element pE2 = newElement(BigMNamespaceConstants.BIGM, "bigm:appearance");
			Rectangle rect2= ss.getChildConstraint(reactum);
			rectangleToElement(pE2, rect2);
			t2.appendChild(pE2);
			
		
		return null;
	}

	@Override
	public void exportObject() throws SaveFailedException {
		setDocument(createDocument(REACTION, "reaction:reaction"));
		processModel(getDocumentElement());
		finish();
		
	}
	
	public static Element rectangleToElement(Element e, Rectangle r) {
		return XMLSaver.applyAttributes(e,
			"width", r.width(), "height", r.height(), "x", r.x(), "y", r.y());
	}


}
