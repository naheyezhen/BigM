package org.bigraph.model.loaders;

import org.bigraph.model.FormRules;
import org.bigraph.model.FormationRule;
import org.bigraph.model.SortSet;
import org.bigraph.model.assistants.FileData;
import org.bigraph.model.changes.descriptors.BoundDescriptor;
import org.bigraph.model.resources.IFileWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.bigraph.model.loaders.BigmNamespaceConstants.FORMATIONRULES;
import static org.bigraph.model.loaders.BigmNamespaceConstants.SORTSET;

public class FormRulesXMLLoader extends XMLLoader {
	public FormRulesXMLLoader() {
	}
	
	public FormRulesXMLLoader(Loader parent) {
		super(parent);
	}
	
	private FormRules frs;
	
	@Override
	public FormRules importObject() throws LoadFailedException {
		try {
			Document d = validate(parse(getInputStream()),
					Schemas.getFormRulesSchema());
			FormRules s = makeObject(d.getDocumentElement());
			FileData.setFile(s, getFile());
			return s;
		} catch (LoadFailedException e) {
			throw e;
		} catch (Exception e) {
			throw new LoadFailedException(e);
		}
	}

	private void makeFormationRule(Element e) throws LoadFailedException {
		FormationRule model = new FormationRule();
		addChange(frs.changeAddFormationRule(
				model, getAttributeNS(e, FORMATIONRULES, "name"), 
					getAttributeNS(e, FORMATIONRULES, "sort1"),
					getAttributeNS(e, FORMATIONRULES, "sort2"),
					getAttributeNS(e, FORMATIONRULES, "constraint"),
					getAttributeNS(e, FORMATIONRULES, "type")));
		
		if(getAttributeNS(e, FORMATIONRULES, "type") != null){
			addChange(model.changeFormRuleType(getAttributeNS(e, FORMATIONRULES, "type")));			
		}
		if(getAttributeNS(e, FORMATIONRULES, "sort1") != null){
			addChange(model.changeFormRuleSort1(getAttributeNS(e, FORMATIONRULES, "sort1")));			
		}
		if(getAttributeNS(e, FORMATIONRULES, "sort2") != null){
			addChange(model.changeFormRuleSort2(getAttributeNS(e, FORMATIONRULES, "sort2")));			
		}
		if(getAttributeNS(e, FORMATIONRULES, "constraint") != null){
			addChange(model.changeFormRuleCons(getAttributeNS(e, FORMATIONRULES, "constraint")));			
		}
		
		executeUndecorators(model, e);
	}
	
	private void makeFormRules(Element e) throws LoadFailedException {
		FormRulesXMLLoader f = new FormRulesXMLLoader(this);
		FormRules t = f.makeObject(e);
		if (t != null)
			addChange(new BoundDescriptor(frs,
					new FormRules.ChangeAddFormRulesDescriptor(
							new FormRules.Identifier(), -1, t)));
	}
	
	@Override
	public FormRules makeObject(Element e) throws LoadFailedException {
		cycleCheck();
		String replacement = getAttributeNS(e, FORMATIONRULES, "src");
		if (replacement != null) {
			return loadRelative(replacement, FormRules.class,
					new FormRulesXMLLoader(this));
		} else frs = new FormRules();
		
		for (Element j : getNamedChildElements(e, FORMATIONRULES, "formationrules"))
			makeFormRules(j);
		
		for (Element j : getNamedChildElements(e, FORMATIONRULES, "formationrule"))
			makeFormationRule(j);
		
		SortSet sortSet = loadSub(
				selectFirst(
					getNamedChildElement(e, SORTSET, "sortset"),
					getNamedChildElement(e, FORMATIONRULES, "sortset")),
				FORMATIONRULES, SortSet.class, new SortSetXMLLoader(this));
		if (sortSet != null)
			addChange(new BoundDescriptor(frs,
					new FormRules.ChangeSetSortSetDescriptor(
							new FormRules.Identifier(),
							null, sortSet)));
		
		executeUndecorators(frs, e);
		executeChanges(frs);
		return frs;
	}
	
	@Override
	public FormRulesXMLLoader setFile(IFileWrapper f) {
		return (FormRulesXMLLoader)super.setFile(f);
	}
}
