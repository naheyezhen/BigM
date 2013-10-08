package dk.itu.bigm.application.standalone;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.utilities.ui.UI;

/**
 * When BigM is running as a stand-alone Eclipse application, this class
 * is responsible for setting up a workbench so that the BigM plugin can
 * operate.
 * <p>Running the <i>plugin</i> is the responsibility of {@link BigMPlugin}.
 */
public class BigMApplication implements IApplication {
	@Override
	public Object start(IApplicationContext context) {
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new BigMApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}
	
	@Override
	public void stop() {
		final IWorkbench workbench = UI.getWorkbench();
		if (UI.getWorkbench() == null)
			return;
		final Display display = UI.getDisplay();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
