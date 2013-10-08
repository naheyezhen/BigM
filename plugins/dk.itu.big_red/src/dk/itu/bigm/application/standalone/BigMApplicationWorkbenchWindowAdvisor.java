package dk.itu.bigm.application.standalone;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class BigMApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
    public BigMApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    @Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new BigMApplicationActionBarAdvisor(configurer);
    }
    
    @Override
	public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(800, 600));
        configurer.setTitle("BigM");
    }
}
