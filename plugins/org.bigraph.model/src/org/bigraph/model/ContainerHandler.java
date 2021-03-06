package org.bigraph.model;

import java.util.ArrayList;
import java.util.List;

import org.bigraph.model.Control.Kind;
import org.bigraph.model.assistants.PropertyScratchpad;
import org.bigraph.model.changes.ChangeRejectedException;
import org.bigraph.model.changes.IChange;
import org.bigraph.model.changes.IStepExecutor;
import org.bigraph.model.changes.IStepValidator;
import org.bigraph.model.names.Namespace;

final class ContainerHandler implements IStepExecutor, IStepValidator {
	@Override
	public boolean executeChange(IChange b) {
		if (b instanceof Container.ChangeAddChild) {
			Container.ChangeAddChild c = (Container.ChangeAddChild) b;
			Namespace<Layoutable> ns = c.getCreator().getBigraph()
					.getNamespace(c.child);
			c.child.setName(ns.put(c.name, c.child));
			c.getCreator().addChild(c.position, c.child);
		} else
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	private static boolean canContain(Container c, Layoutable l) {
		if (c instanceof Node && l instanceof Node) {
			Node cNode = (Node) c;
			Node lNode = (Node) l;

			FormRules frs = cNode.getControl().getSignature().getFormRules();

			String cname = cNode.getControl().getName();
			String lname = lNode.getControl().getName();
			if (frs != null) {
			// 循环所有的formation rule
			for (FormationRule fr : frs.getFormationRules()) {
				// 判断是place sort
				if (fr.getType().equals("Formation Rule for Place Sorting")) {
					String sort1 = fr.getSort1();
					String sort2 = fr.getSort2();

					// 判断fr是否是描述c&l的sorting
					if (cname.equals(fr.getSort2())
							&& lname.equals(fr.getSort1())) {
						if ("in".equals(fr.getConstraint())) {// l in c
							System.out.println("nahe+++ " + cname + " in "
									+ lname);
							return true;
						} else if ("not in".equals(fr.getConstraint())) {
							System.out.println("nahe+++ " + cname
									+ " not in " + lname);
							return false;
						} else {// 未知连接词
							System.out.println("nahe+++ " + cname + " "
									+ fr.getConstraint() + " " + lname);
							return false;
						}
					}
				}
			}
		} else {// 某些情况下 会出现frs为空的情况 此分支为避免空指针报错
			System.out.println("nahe+++ frs is null");
		}
// 两种方法，对是否为空的判断先后，会影响rule右侧的移动（用name的这种判断方法中，右侧是取不到frs的）
//			for(FormationRule fr :frs.getFormationRules()){
//				if(fr != null){
//				  String sort1 =fr.getSort1();
//				  String sort2 =fr.getSort2();
//				  
//				  if(cname.equals(fr.getSort2()) && lname.equals(fr.getSort1())){
//					   if("in".equals(fr.getConstraint())){
//						   	System.out.println("haha"+cname+" in " + lname);
//						   	return true;
//					   }else if("not in".equals(fr.getConstraint())){
//							System.out.println("haha"+cname+"not  in " + lname);
//						   return false;
//					   }else{
//						   return false;
//					   }
//				  }
//			}
//			}
//			String cSort = cNode.getControl().getPlaceSort();
//			String lSort = lNode.getControl().getPlaceSort();
//			if(!cSort.contains("UserDef:") || !lSort.contains("UserDef:")){
//				return true;
//			}
//			
//			cSort = cSort.split(":")[1];
//			lSort = lSort.split(":")[1];
//				
//		
//			if(frs != null && frs.getSortSet() != null && frs.getSortSet().getPlaceSorts() != null){
//				ArrayList<PlaceSort> pss = (ArrayList<PlaceSort>) frs.getSortSet().getPlaceSorts();
//				ArrayList<String> psString = new ArrayList<String>();
//				for(PlaceSort s : pss){
//					psString.add(s.getName());
//				}
//				boolean sortValid = (psString.indexOf(cSort) >= 0) && (psString.indexOf(lSort) >= 0);
//				if(sortValid){
//					for(FormationRule fr : frs.getFormationRules()){
//						if(cSort.equals(fr.getSort2()) && lSort.equals(fr.getSort1())){
//							if("in".equals(fr.getConstraint())){
//								return true;
//							} else{
//								return false;
//							}
//						}
//					}
//					return true;
//				}
//			}
			return true;
		} else {
			return (c instanceof Bigraph && (l instanceof Edge
					|| l instanceof OuterName || l instanceof InnerName || l instanceof Root))
					|| (c instanceof Node && l instanceof Site)
					|| (c instanceof Root && (l instanceof Node || l instanceof Site));
		}
	}

	@Override
	public boolean tryValidateChange(Process process, IChange b)
			throws ChangeRejectedException {
		final PropertyScratchpad context = process.getScratch();
		if (b instanceof Container.ChangeAddChild) {
			Container.ChangeAddChild c = (Container.ChangeAddChild) b;
			Container container = c.getCreator();
			Bigraph bigraph = container.getBigraph(context);

			if (container instanceof Node
					&& ((Node) container).getControl().getKind() == Kind.ATOMIC)
				throw new ChangeRejectedException(b, ((Node) container)
						.getControl().getName() + " is an atomic control");

			NamedModelObjectHandler.checkName(context, b, c.child,
					bigraph.getNamespace(c.child), c.name);

			if (c.child instanceof Edge) {
				if (!(container instanceof Bigraph))
					throw new ChangeRejectedException(b,
							"Edges must be children of the top-level Bigraph");
			} else {
				if (c.child instanceof Container)
					if (((Container) c.child).getChildren(context).size() != 0)
						throw new ChangeRejectedException(b, c.child
								+ " already has child objects");
				if (!canContain(container, c.child))
					throw new ChangeRejectedException(b, container.getType() + " " + container.getIdentifier()
							+ " can't contain " + c.child.getType() + " " + c.child.getIdentifier());
			}

			Container existingParent = c.child.getParent(context);
			if (existingParent != null)
				throw new ChangeRejectedException(b, c.child
						+ " already has a parent (" + existingParent + ")");

			List<? extends Layoutable> siblings = container
					.getChildren(context);
			if (c.position < -1 || c.position > siblings.size())
				throw new ChangeRejectedException(b, "" + c.position
						+ " is not a valid position for " + c.child);
		} else
			return false;
		return true;
	}
}
