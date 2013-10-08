package org.bigraph.model.savers;

import java.util.ArrayList;
import java.util.List;

import org.bigraph.model.Control;
import org.bigraph.model.FormRules;
import org.bigraph.model.FormationRule;
import org.bigraph.model.LinkSort;
import org.bigraph.model.ModelObject;
import org.bigraph.model.PlaceSort;
import org.bigraph.model.PortSpec;
import org.bigraph.model.Signature;
import org.bigraph.model.SimulationSpec;
import org.bigraph.model.SortSet;
import org.w3c.dom.Element;

import static org.bigraph.model.loaders.BigmNamespaceConstants.SORTING;

public class SortingXMLSaver extends XMLSaver {
	public SortingXMLSaver() {
		this(null);
	}
	
	public SortingXMLSaver(ISaver parent) {
		super(parent);
		setDefaultNamespace(SORTING);
	}
	
	@Override
	public SimulationSpec getModel() {
		return (SimulationSpec)super.getModel();
	}
	
	@Override
	public SortingXMLSaver setModel(ModelObject model) {
		if (model == null || model instanceof SimulationSpec)
			super.setModel(model);
		return this;
	}
	
	@Override
	public void exportObject() throws SaveFailedException {
		setDocument(createDocument(SORTING, "sorting:sorting"));
		processModel(getDocumentElement());
		finish();
	}
	
	@Override
	public Element processModel(Element e) throws SaveFailedException {
		Signature s = getModel().getSignature();
		if(s != null){
			FormRules frs = s.getFormRules();
			SortSet ss = s.getFormRules().getSortSet();
			if(frs != null){
				if(ss != null){
					if(!ss.getPlaceSorts().isEmpty()){
						for (PlaceSort ps : ss.getPlaceSorts()){
							appendChildIfNotNull(e,
									processPlaceSort(newElement(SORTING, "sorting:placesort"), ps, s));
						}
					}
					if(!ss.getLinkSorts().isEmpty()){
						for (LinkSort ls : ss.getLinkSorts()){
							appendChildIfNotNull(e,
									processLinkSort(newElement(SORTING, "sorting:linksort"), ls, s));
						}
					}
				}
				appendChildIfNotNull(e,
						processFormationRules(newElement(SORTING, "sorting:constraints"), frs));
			}
		}
		
		return executeDecorators(s, e);
	}
	
	private Element processPlaceSort(Element e, PlaceSort sort, Signature s) {
		applyAttributes(e, "name", sort.getName());	
		appendChildIfNotNull(e, processControls(newElement(SORTING, "sorting:controls"), sort, s));
		return executeDecorators(sort, e);
	}
	
	private Element processLinkSort(Element e, LinkSort sort, Signature s) {
		applyAttributes(e, "name", sort.getName());	
		appendChildIfNotNull(e, processPorts(newElement(SORTING, "sorting:ports"), sort, s));
		return executeDecorators(sort, e);
	}
	
	private Element processFormationRules(Element e, FormRules frs) {
		for (FormationRule fr : frs.getFormationRules()){
			if(fr.getType().contains("Place Sorting")){
				appendChildIfNotNull(e,
						processPlaceFormRule(newElement(SORTING, "sorting:placeconstraint"), fr));
			}else if(fr.getType().contains("Link Sorting")){
				appendChildIfNotNull(e,
						processLinkFormRule(newElement(SORTING, "sorting:linkconstraint"), fr));
			}
		}
		return executeDecorators(frs, e);
	}
	
	@SuppressWarnings("unchecked")
	private Element processControls(Element e, PlaceSort sort, Signature s) {
		List<Control> cons = (ArrayList<Control>)s.getControls();
		for (Control c : cons){
			if(c.getPlaceSort().contains(sort.getName())){
				appendChildIfNotNull(e, processControl(newElement(SORTING, "sorting:control"), c));
			}
		}
		return executeDecorators(sort, e);
	}
	
	@SuppressWarnings("unchecked")
	private Element processPorts(Element e, LinkSort sort, Signature s) {
		List<Control> cons = (ArrayList<Control>)s.getControls();
		for (Control c : cons){
			List<PortSpec> ports = (ArrayList<PortSpec>)c.getPorts();
			for(int i = 0; i < ports.size(); i++){
				PortSpec p = ports.get(i);
				if(p.getPortSort().contains(sort.getName())){
					appendChildIfNotNull(e, processPort(newElement(SORTING, "sorting:port"), c, i+1));
				}
			}
		}
		return executeDecorators(sort, e);
	}
	
	private Element processControl(Element e, Control c) {
		applyAttributes(e, "name", c.getName());	
		return e;
	}
	
	private Element processPort(Element e, Control c, int index) {
		applyAttributes(e, "control", c.getName());	
		applyAttributes(e, "index", index);
		return e;
	}
	
	private Element processPlaceFormRule(Element e, FormationRule fr) {
		applyAttributes(e, "name", fr.getConstraint());	
		applyAttributes(e, "parent", fr.getSort2());	
		applyAttributes(e, "child", fr.getSort1());	
		return e;
	}
	
	private Element processLinkFormRule(Element e, FormationRule fr) {
		applyAttributes(e, "name", fr.getConstraint());	
		applyAttributes(e, "one", fr.getSort1());	
		applyAttributes(e, "another", fr.getSort2());	
		return e;
	}
}
