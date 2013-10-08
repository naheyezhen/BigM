package dk.itu.bigm.editors.sortset;

import org.bigraph.model.LinkSort;
import org.bigraph.model.PlaceSort;
import org.bigraph.model.SortSet;
import org.bigraph.model.assistants.FileData;
import org.bigraph.model.resources.IFileWrapper;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

class SortSetSortsLabelProvider
		extends BaseLabelProvider implements ILabelProvider{	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		if (element instanceof PlaceSort) {
			return (PlaceSort.PROPERTY_NAME.equals(property));
		} else if (element instanceof LinkSort) {
			return (LinkSort.PROPERTY_NAME.equals(property));
		} else return false;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof SortSet) {
			SortSet s = (SortSet)element;
			IFileWrapper f = FileData.getFile(s);
			return (f != null ? f.getPath() : "(embedded)");
		} else if (element instanceof PlaceSort) {
			PlaceSort c = (PlaceSort)element;
			String name = c.getName();
			return name;
		} else if (element instanceof LinkSort) {
			LinkSort c = (LinkSort)element;
			String name = c.getName();
			return name;
		} else return null;
	}
}
