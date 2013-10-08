package it.uniud.bigmit.editparts;

import it.uniud.bigmit.figure.SitePlusFigure;

import org.eclipse.draw2d.IFigure;


import dk.itu.bigm.editors.bigraph.parts.SitePart;

public class SitePlusPart extends SitePart{
	
	@Override
	protected IFigure createFigure() {
		return new SitePlusFigure();
	}

	
	
	
}
