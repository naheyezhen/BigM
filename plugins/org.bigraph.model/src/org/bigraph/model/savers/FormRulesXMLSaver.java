package org.bigraph.model.savers;

import org.bigraph.model.FormRules;
import org.bigraph.model.FormationRule;
import org.bigraph.model.ModelObject;
import org.w3c.dom.Element;

import static org.bigraph.model.loaders.BigmNamespaceConstants.FORMATIONRULES;
import static org.bigraph.model.loaders.BigmNamespaceConstants.SORTSET;

public class FormRulesXMLSaver extends XMLSaver {
	public FormRulesXMLSaver() {
		this(null);
	}
	
	public FormRulesXMLSaver(ISaver parent) {
		super(parent);
		setDefaultNamespace(FORMATIONRULES);
	}
	
	@Override
	public FormRules getModel() {
		return (FormRules)super.getModel();
	}
	
	@Override
	public FormRulesXMLSaver setModel(ModelObject model) {
		if (model == null || model instanceof FormRules)
			super.setModel(model);
		return this;
	}
	
	@Override
	public void exportObject() throws SaveFailedException {
		setDocument(createDocument(FORMATIONRULES, "formationrules:formationrules"));
		processModel(getDocumentElement());
		finish();
	}

	@Override
	public Element processModel(Element e) throws SaveFailedException {
		FormRules s = getModel();
		
		appendChildIfNotNull(e,
				processOrReference(newElement(SORTSET, "sortset:sortset"),
						s.getSortSet(), new SortSetXMLSaver(this)));
		
		for (FormRules t : s.getFormRuless())
			appendChildIfNotNull(e, processOrReference(
					newElement(FORMATIONRULES, "formationrules:formationrules"),
					t, new SignatureXMLSaver(this)));
		
		
		for (FormationRule fr : s.getFormationRules())
			appendChildIfNotNull(e,
				processFormationRule(newElement(FORMATIONRULES, "formationrules:formationrule"), fr));
		return executeDecorators(s, e);
	}
	
	private Element processFormationRule(Element e, FormationRule fr) {
		applyAttributes(e,
				"name", fr.getName(),
				"sort1", fr.getSort1(),
				"sort2", fr.getSort2(),
				"constraint", fr.getConstraint(),
				"type", fr.getType()
				);
		
		return executeDecorators(fr, e);
	}
	
}
