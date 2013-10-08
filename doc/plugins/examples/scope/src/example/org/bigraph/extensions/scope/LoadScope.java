package example.org.bigraph.extensions.scope;

import org.bigraph.model.ModelObject;
import org.bigraph.model.PortSpec;
import org.bigraph.model.changes.IChangeExecutor;
import org.bigraph.model.loaders.IXMLLoader;
import org.bigraph.model.loaders.IXMLLoader.Undecorator;
import org.bigraph.model.process.IParticipantHost;
import org.w3c.dom.Element;

public class LoadScope implements Undecorator {
	private IXMLLoader loader;
	
	@Override
	public void setHost(IParticipantHost host) {
		if (host instanceof IXMLLoader)
			this.loader = (IXMLLoader)host;
	}

	@Override
	public void undecorate(ModelObject object, Element el) {
		if (object instanceof PortSpec) {
			String scope = el.getAttributeNS(SaveScope.XMLNS, "scope");
			if ("true".equals(scope))
				loader.addChange(
						Scope.changeScoped((PortSpec)object, true));
		}
	}

	@Override
	public void finish(IChangeExecutor ex) {
		/* do nothing */
	}
}
