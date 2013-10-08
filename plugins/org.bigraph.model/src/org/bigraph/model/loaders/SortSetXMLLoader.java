package org.bigraph.model.loaders;

import org.bigraph.model.LinkSort;
import org.bigraph.model.PlaceSort;
import org.bigraph.model.SortSet;
import org.bigraph.model.assistants.FileData;
import org.bigraph.model.changes.descriptors.BoundDescriptor;
import org.bigraph.model.resources.IFileWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.bigraph.model.loaders.BigmNamespaceConstants.SORTSET;;

public class SortSetXMLLoader extends XMLLoader {
	public SortSetXMLLoader() {
	}
	
	public SortSetXMLLoader(Loader parent) {
		super(parent);
	}
	
	private SortSet sortSet;
	
	@Override
	public SortSet importObject() throws LoadFailedException {
		try {
			Document d = validate(parse(getInputStream()),
					Schemas.getSortSetSchema());
			SortSet ss = makeObject(d.getDocumentElement());
			FileData.setFile(ss, getFile());
			return ss;
		} catch (LoadFailedException e) {
			throw e;
		} catch (Exception e) {
			throw new LoadFailedException(e);
		}
	}

	private void makePlaceSort(Element e) throws LoadFailedException {
		PlaceSort model = new PlaceSort();
		addChange(sortSet.changeAddPlaceSort(
				model, getAttributeNS(e, SORTSET, "name")));
		
		executeUndecorators(model, e);
	}
	
	private void makeLinkSort(Element e) throws LoadFailedException {
		LinkSort model = new LinkSort();
		addChange(sortSet.changeAddLinkSort(
				model, getAttributeNS(e, SORTSET, "name")));
		
		executeUndecorators(model, e);
	}
	
	private void makeSortSet(Element e) throws LoadFailedException {
		SortSetXMLLoader so = new SortSetXMLLoader(this);
		SortSet t = so.makeObject(e);
		if (t != null)
			addChange(new BoundDescriptor(sortSet,
					new SortSet.ChangeAddSortSetDescriptor(
							new SortSet.Identifier(), -1, t)));
	}
	
	@Override
	public SortSet makeObject(Element e) throws LoadFailedException {
		cycleCheck();
		String replacement = getAttributeNS(e, SORTSET, "src");
		if (replacement != null) {
			return loadRelative(replacement, SortSet.class,
					new SortSetXMLLoader(this));
		} else sortSet = new SortSet();
		
		for (Element j : getNamedChildElements(e, SORTSET, "sortset"))
			makeSortSet(j);
		
		for (Element j : getNamedChildElements(e, SORTSET, "placesort"))
			makePlaceSort(j);
		
		for (Element j : getNamedChildElements(e, SORTSET, "linksort"))
			makeLinkSort(j);
		
		executeUndecorators(sortSet, e);
		executeChanges(sortSet);
		return sortSet;
	}
	
	@Override
	public SortSetXMLLoader setFile(IFileWrapper f) {
		return (SortSetXMLLoader)super.setFile(f);
	}
}
