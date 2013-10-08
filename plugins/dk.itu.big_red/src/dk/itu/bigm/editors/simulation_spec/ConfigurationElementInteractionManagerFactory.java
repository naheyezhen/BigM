package dk.itu.bigm.editors.simulation_spec;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import dk.itu.bigm.application.plugin.BigMPlugin;
import dk.itu.bigm.editors.assistants.IFactory;
import dk.itu.bigm.interaction_managers.IInteractionManager;

/**
 * The <strong>ConfigurationElementInteractionManagerFactory</strong> creates
 * {@link IInteractionManager}s from {@link IConfigurationElement}s.
 * @author alec
 * @see BigMPlugin#instantiate(IConfigurationElement)
 */
class ConfigurationElementInteractionManagerFactory
	implements IFactory<IInteractionManager> {
	private IConfigurationElement ice = null;
	
	public IConfigurationElement getCE() {
		return ice;
	}
	
	public ConfigurationElementInteractionManagerFactory(IConfigurationElement ice) {
		this.ice = ice;
	}
	
	@Override
	public String getName() {
		return getCE().getAttribute("name");
	}
	
	@Override
	public IInteractionManager newInstance() {
		try {
			return (IInteractionManager)
					getCE().createExecutableExtension("class");
		} catch (CoreException e) {
			return null;
		}
	}
}