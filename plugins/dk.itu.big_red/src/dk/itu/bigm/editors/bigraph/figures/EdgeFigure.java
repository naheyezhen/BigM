package dk.itu.bigm.editors.bigraph.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

public class EdgeFigure extends AbstractFigure {
	private Label labelName = new Label(); 
	
	public EdgeFigure() {
		super();
		
		labelName.setText("?");
		labelName.setForegroundColor(ColorConstants.black);
		add(labelName, 0);
	}
	
	private void repositionLabel() {
		Rectangle r = getConstraint();
		if (r == null)
			return;
		Dimension s = labelName.getPreferredSize();
		setConstraint(labelName, new Rectangle(
				(r.width / 2) - (s.width / 2),
				(r.height / 2) - (s.height / 2) + 10, -1, -1));
	}
	
	@Override
	public void setConstraint(Rectangle r) {
		super.setConstraint(r);
		repositionLabel();
	}
	
	public void setName(String name) {
		labelName.setText(name);
		repositionLabel();
	}
	
	private boolean single = false;
	
	public void setSingle(boolean single) {
		this.single = single;
	}
	
	@Override
	public void setToolTip(String content) {
		super.setToolTip(content);
	}
	
	@Override
	public void setBackgroundColor(Color bg) {
		super.setBackgroundColor(bg);
		super.setForegroundColor(bg);
	}
	
	@Override
	protected void fillShape(Graphics graphics) {
		Rectangle a = start(graphics);
		try {
			// 中间的圈圈
			graphics.setAlpha(255);
			//graphics.fillOval(a);
			//graphics.fillRectangle(a);
			
			// 中间圈圈的边框
			graphics.setAlpha(64);
			graphics.setLineWidth(2);
			graphics.setLineStyle(SWT.LINE_SOLID);
			//graphics.drawOval(a);

			//graphics.drawRectangle(a);
			
			if (single) {
				graphics.setAlpha(255);
				graphics.drawLine(
						a.getLeft().translate(0, 3),
						a.getRight().translate(0, -3));
			}
		} finally {
			stop(graphics);
		}
	}
}
