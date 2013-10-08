package org.bigraph.model.savers;

import org.bigraph.model.LinkSort;
import org.bigraph.model.ModelObject;
import org.bigraph.model.PlaceSort;
import org.bigraph.model.SortSet;
import org.w3c.dom.Element;

import static org.bigraph.model.loaders.BigmNamespaceConstants.SORTSET;

public class SortSetXMLSaver extends XMLSaver {
	public SortSetXMLSaver() {
		this(null);
	}
	
	public SortSetXMLSaver(ISaver parent) {
		super(parent);
		setDefaultNamespace(SORTSET);
	}
	
	@Override
	public SortSet getModel() {
		return (SortSet)super.getModel();
	}
	
	@Override
	public SortSetXMLSaver setModel(ModelObject model) {
		if (model == null || model instanceof SortSet)
			super.setModel(model);
		return this;
	}
	
	@Override
	public void exportObject() throws SaveFailedException {
		setDocument(createDocument(SORTSET, "sortset:sortset"));
		processModel(getDocumentElement());
		finish();
	}

	@Override
	public Element processModel(Element e) throws SaveFailedException {
		SortSet s = getModel();
		
		if(s != null){
			for (SortSet t : s.getSortSets())
				appendChildIfNotNull(e, processOrReference(
						newElement(SORTSET, "sortset:sortset"),
						t, new SortSetXMLSaver()));
		
			for (PlaceSort ps : s.getPlaceSorts())
				appendChildIfNotNull(e,
					processPlaceSort(newElement(SORTSET, "sortset:placesort"), ps));
			
			for (LinkSort ls : s.getLinkSorts())
				appendChildIfNotNull(e,
					processLinkSort(newElement(SORTSET, "sortset:linksort"), ls));
		}
		return executeDecorators(s, e);
	}
	
	private Element processPlaceSort(Element e, PlaceSort sort) {
		applyAttributes(e, "name", sort.getName());			
		return executeDecorators(sort, e);
	}
	
	private Element processLinkSort(Element e, LinkSort sort) {
		applyAttributes(e, "name", sort.getName());			
		return executeDecorators(sort, e);
	}
	
}
