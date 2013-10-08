package org.bigraph.model;

import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.names.Namespace;
import org.bigraph.model.names.policies.INamePolicy;

final class NamedModelObjectHandler {
	static <V> void checkName(
			PropertyScratchpad context, IChange c, V object,
			Namespace<? extends V> ns, String cdt)
			throws ChangeRejectedException {		
//		// TODO 如果有两个相同control 都是类 怎么办。。。 最后检查名字唯一性的时候会通不过的啊~~~
		if (cdt == null || cdt.length() == 0)
			throw new ChangeRejectedException(c, "Names cannot be empty");
		if (ns == null)
			return;
		INamePolicy p = ns.getPolicy();
		String mcdt = (p != null ? p.normalise(cdt) : cdt);
		if (mcdt == null)
			throw new ChangeRejectedException(c,
					"\"" + cdt + "\" is not a valid name for " + object);
		V current = ns.get(context, mcdt);
		if (current != null && current != object)
			throw new ChangeRejectedException(c, "Names must be unique");
	}
}
