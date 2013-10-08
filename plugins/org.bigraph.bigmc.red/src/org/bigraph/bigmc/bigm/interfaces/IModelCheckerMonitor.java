package org.bigraph.bigmc.bigm.interfaces;

/**
 * An <strong>IModelCheckerMonitor</strong> allows progress information and
 * cancellation requests to be carried between an {@link IModelChecker} and its
 * host.
 * @author alec
 */
public interface IModelCheckerMonitor {
	void start(String name, int totalWork);
	
	void worked(int units);
	void subtask(String name);
	
	void end();
	
	boolean isCanceled();
	void setCanceled(boolean canceled);
}
