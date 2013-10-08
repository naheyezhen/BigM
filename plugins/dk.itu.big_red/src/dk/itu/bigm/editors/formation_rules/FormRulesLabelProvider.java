package dk.itu.bigm.editors.formation_rules;

import org.bigraph.model.FormRules;
import org.bigraph.model.FormationRule;
import org.bigraph.model.assistants.FileData;
import org.bigraph.model.resources.IFileWrapper;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

class FormRulesLabelProvider
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
		if (element instanceof FormationRule) {
			return (FormationRule.PROPERTY_NAME.equals(property));
		} else return false;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof FormRules) {
			FormRules s = (FormRules)element;
			IFileWrapper f = FileData.getFile(s);
			return (f != null ? f.getPath() : "(embedded)");
		} else if (element instanceof FormationRule) {
			FormationRule c = (FormationRule)element;
			String name = c.getName();
			return name;
		} else return null;
	}
}
