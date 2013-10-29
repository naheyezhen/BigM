package org.bigraph.model.savers;

import org.bigraph.model.Control;
import org.bigraph.model.ModelObject;
import org.bigraph.model.PortSpec;
import org.bigraph.model.Signature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.bigraph.model.loaders.BigmNamespaceConstants.SIGNATURE;
import static org.bigraph.model.loaders.BigmNamespaceConstants.FORMATIONRULES;

public class SignatureXMLSaver extends XMLSaver {
	public SignatureXMLSaver() {
		this(null);
	}
	
	public SignatureXMLSaver(ISaver parent) {
		super(parent);
		setDefaultNamespace(SIGNATURE);
	}
	
	@Override
	public Signature getModel() {
		return (Signature)super.getModel();
	}
	
	@Override
	public SignatureXMLSaver setModel(ModelObject model) {
		if (model == null || model instanceof Signature)
			super.setModel(model);
		return this;
	}
	
	@Override
	public void exportObject() throws SaveFailedException {
		setDocument(createDocument(SIGNATURE, "signature:signature"));
		processModel(getDocumentElement());
		finish();
	}

	@Override
	public Element processModel(Element e) throws SaveFailedException {
		Signature s = getModel();
		
		if(s.getControls().size() >= 6){
			for(int i = 0; i < 6; i++){
				if(s.getControls().get(0).getName().equals("Greater") || 
						s.getControls().get(0).getName().equals("Less") || 
						s.getControls().get(0).getName().equals("GreaterOrEqual") || 
						s.getControls().get(0).getName().equals("LessOrEqual") || 
						s.getControls().get(0).getName().equals("Equal") || 
						s.getControls().get(0).getName().equals("NotEqual")){				
					s.getControls().remove(0);
				}
			}			
			
		}
		for (Signature t : s.getSignatures())
			appendChildIfNotNull(e, processOrReference(
					newElement(SIGNATURE, "signature:signature"),
					t, new SignatureXMLSaver(this)));
		
		appendChildIfNotNull(e,
				processOrReference(newElement(FORMATIONRULES, "formationrules:formationrules"),
					s.getFormRules(), new FormRulesXMLSaver(this)));
		
		processRelationElement(getDocumentElement(), "greater", "Greater");
		processRelationElement(getDocumentElement(), "less", "Less");
		processRelationElement(getDocumentElement(), "greaterOrEqual", "GreaterOrEqual");
		processRelationElement(getDocumentElement(), "lessOrEqual", "LessOrEqual");
		processRelationElement(getDocumentElement(), "equal", "Equal");
		processRelationElement(getDocumentElement(), "notEqual", "NotEqual");
		
		for (Control c : s.getControls())
			appendChildIfNotNull(e,
				processControl(newElement(SIGNATURE, "signature:control"), c));
		
		return executeDecorators(s, e);
	}
	
	private Element processControl(Element e, Control c) {
		applyAttributes(e,
				"name", c.getName(),
				"kind", c.getKind().toString(),
				"placesort", c.getPlaceSort() == null ? "none" : c.getPlaceSort());
		
		for (PortSpec p : c.getPorts())
			e.appendChild(processPort(
				newElement(SIGNATURE, "signature:port"), p));
		
		return executeDecorators(c, e);
	}
	
	private Element processPort(Element e, PortSpec p) {
		applyAttributes(e,
				"name", p.getName(),
				"portsort", p.getPortSort() == null ? "none" : p.getPortSort());
		return executeDecorators(p, e);
	}
	
	private void processRelationElement(Element e, String label, String name){
		String BIGM = "http://www.itu.dk/research/pls/xmlns/2010/bigm";
		Document doc = e.getOwnerDocument();	
		Element control = newElement(SIGNATURE, "signature:control");	
		control.setAttributeNS(BIGM, "bigm:label", label);		
		applyAttributes(control,
				"name", name,
				"kind", "atomic",
				"placesort", "UserDef:Decision");

		Element gl = newElement(SIGNATURE, "signature:port");
		applyAttributes(gl,
				"name", "left",
				"portsort", "LeftValue:none");
		control.appendChild(gl);	
		
		Element pAL =
				doc.createElementNS(BIGM, "bigm:port-appearance");
		pAL.setAttributeNS(BIGM, "bigm:segment",
				"" + "0");
		pAL.setAttributeNS(BIGM, "bigm:distance",
				"" + "0.75");
			gl.appendChild(pAL);	
		
		Element gr = newElement(SIGNATURE, "signature:port");
		applyAttributes(gr,
				"name", "right",
				"portsort", "RightValue:none");
		control.appendChild(gr);
		
		Element pAR =
				doc.createElementNS(BIGM, "bigm:port-appearance");
		pAR.setAttributeNS(BIGM, "bigm:segment",
				"" + "0");
		pAR.setAttributeNS(BIGM, "bigm:distance",
				"" + "0.25");
			gr.appendChild(pAR);	
		
		Element asC = doc.createElementNS(BIGM, "bigm:shape");
		asC.setAttributeNS(BIGM, "bigm:shape", "oval");
		control.appendChild(asC);
		
		Element aEC = doc.createElementNS(BIGM, "bigm:appearance");
		aEC.setAttributeNS(BIGM, "bigm:outlineColor", "#ff0000");
		control.appendChild(aEC);
		
		e.appendChild(control);	
	}

}
