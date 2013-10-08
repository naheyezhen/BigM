package dk.itu.bigm.editors.bigraph.commands;

import java.util.ArrayList;

import org.bigraph.model.Bigraph;
import org.bigraph.model.Edge;
import org.bigraph.model.FormRules;
import org.bigraph.model.FormationRule;
import org.bigraph.model.InnerName;
import org.bigraph.model.Link;
import org.bigraph.model.LinkSort;
import org.bigraph.model.OuterName;
import org.bigraph.model.Point;
import org.bigraph.model.Port;
import org.bigraph.model.Signature;
import org.bigraph.model.changes.ChangeGroup;

import dk.itu.bigm.editors.bigraph.parts.LinkPart;

/**
 * A LinkConnectionCreateCommand is in charge of creating and updating {@link
 * Link}s on a {@link Bigraph} in response to user input. It can either join a
 * {@link Point} to an existing {@link Link}, or join two {@link Point}s
 * together, creating a new {@link Edge} in the process.
 * </ul>
 * @author alec
 */
public class LinkConnectionCreateCommand extends ChangeCommand {
	private ChangeGroup cg = new ChangeGroup();
	private Object first = null, second = null;
	
	public LinkConnectionCreateCommand() {
		setChange(cg);
	}

	public void setFirst(Object e) {
		if (!(e instanceof LinkPart.Connection)) {
			first = e;
		} else first = ((LinkPart.Connection)e).getLink();
	}
	
	public void setSecond(Object e) {
		if (!(e instanceof LinkPart.Connection)) {
			second = e;
		} else second = ((LinkPart.Connection)e).getLink();
	}
	
	@SuppressWarnings("unchecked")
	private boolean validateSorts(String firstSort, String secondSort, Signature signature) {
		boolean result = false;
		
		if(!firstSort.contains("_") && !secondSort.contains("_")){
			String fSort = firstSort.split(":")[0];
			String sSort = secondSort.split(":")[0];
			FormRules frs = signature.getFormRules();
			if (frs != null && frs.getSortSet() != null
					&& frs.getSortSet().getLinkSorts() != null) {
				ArrayList<LinkSort> pss = (ArrayList<LinkSort>) frs
						.getSortSet().getLinkSorts();
				ArrayList<String> psString = new ArrayList<String>();
				for (LinkSort s : pss) {
					psString.add(s.getName());
				}
				boolean sortValid = (psString.indexOf(fSort) >= 0)
						&& (psString.indexOf(sSort) >= 0);
				if (sortValid) {
					for (FormationRule fr : frs.getFormationRules()) {
						if (fr.getType() != null
								&& fr.getType().contains("Link Sorting")) {
							if ((fSort.equals(fr.getSort1()) && sSort.equals(fr
									.getSort2()))
									|| (fSort.equals(fr.getSort2()) && sSort
											.equals(fr.getSort1()))) {
								if ("link".equals(fr.getConstraint())) {
									return true;
								} else {
									return false;
								}
							}
						}
					}
					return true;
				}
			}
			return true;
		}
		else{
			if(firstSort.contains(":") && secondSort.contains(":")){
				if(firstSort.split(":")[0].equals(secondSort.split(":")[0])){
					if((firstSort.split(":")[1].equals("start") && secondSort.split(":")[1].equals("end"))
							|| (firstSort.split(":")[1].equals("end") && secondSort.split(":")[1].equals("start"))
							|| (firstSort.split(":")[1].equals("none") && secondSort.split(":")[1].equals("none"))){
						result = true;
					}
				}
			}else if(!firstSort.contains(":") && secondSort.contains(":")){
				if(firstSort.equals(secondSort.split(":")[0]))
					result = true;
			}else if(firstSort.contains(":") && !secondSort.contains(":")){
				if(secondSort.equals(firstSort.split(":")[0]))
					result = true;
			}else if(!firstSort.contains(":") && !secondSort.contains(":")){
				if(firstSort.equals(secondSort))
					result = true;
			}
		}
		return result;
	}

	@Override
	public void prepare() {
		cg.clear();
			
		if (first instanceof Port && second instanceof Port 
				&& validateSorts(((Port)first).getSpec().getPortSort(), ((Port)second).getSpec().getPortSort(), ((Port)second).getBigraph().getSignature()))
		{
			Bigraph b = ((Port)first).getBigraph();
			setTarget(b);
			Edge ed = new Edge(((Port)first).getSpec().getPortSort().split(":")[0]);
			cg.add(b.changeAddChild(ed, b.getFirstUnusedName(ed)));
			cg.add(((Port)first).changeConnect(ed));
			cg.add(((Port)second).changeConnect(ed));
		} 
		else if (first instanceof Port && second instanceof InnerName
				&& validateSorts(((Port)first).getSpec().getPortSort(), ((InnerName)second).getInnerSort(), ((Port)first).getBigraph().getSignature()))
		{
			Bigraph b = ((Port)first).getBigraph();
			setTarget(b);
			Edge ed = new Edge(((Port)first).getSpec().getPortSort().split(":")[0]);
			cg.add(b.changeAddChild(ed, b.getFirstUnusedName(ed)));
			cg.add(((Port)first).changeConnect(ed));
			cg.add(((InnerName)second).changeConnect(ed));
		} 
		else if (first instanceof InnerName && second instanceof Port
				&& validateSorts(((InnerName)first).getInnerSort(), ((Port)second).getSpec().getPortSort(), ((Port)second).getBigraph().getSignature()))
		{
			Bigraph b = ((InnerName)first).getBigraph();
			setTarget(b);
			Edge ed = new Edge(((InnerName)first).getInnerSort().split(":")[0]);
			cg.add(b.changeAddChild(ed, b.getFirstUnusedName(ed)));
			cg.add(((InnerName)first).changeConnect(ed));
			cg.add(((Port)second).changeConnect(ed));
		} 
		else if (first instanceof InnerName && second instanceof InnerName
				&& validateSorts(((InnerName)first).getInnerSort(), ((InnerName)second).getInnerSort(), ((InnerName)second).getBigraph().getSignature()))
		{
			Bigraph b = ((InnerName)first).getBigraph();
			setTarget(b);
			Edge ed = new Edge(((InnerName)first).getInnerSort().split(":")[0]);
			cg.add(b.changeAddChild(ed, b.getFirstUnusedName(ed)));
			cg.add(((InnerName)first).changeConnect(ed));
			cg.add(((InnerName)second).changeConnect(ed));
		} 
		else if (first instanceof Port && second instanceof Edge
				&& validateSorts(((Port)first).getSpec().getPortSort(), ((Edge)second).getLinkSort(), ((Port)first).getBigraph().getSignature()))
		{
			setTarget(((Port)first).getBigraph());
			cg.add(((Port)first).changeConnect((Edge)second));
		} 
		else if (first instanceof Edge && second instanceof Port
				&& validateSorts(((Port)second).getSpec().getPortSort(), ((Edge)first).getLinkSort(), ((Port)second).getBigraph().getSignature()))
		{
			setTarget(((Port)second).getBigraph());
			cg.add(((Port)second).changeConnect((Edge)first));
		} 
		else if (first instanceof Port && second instanceof OuterName
				&& (((OuterName)second).getLinkSort() == null 
				|| ((OuterName)second).getLinkSort().equals("")
				|| ((OuterName)second).getPoints().size() == 0))
		{
			setTarget(((Port)first).getBigraph());
			((OuterName)second).setLinkSort(((Port)first).getSpec().getPortSort());
			cg.add(((Port)first).changeConnect((OuterName)second));
		} 
		else if (first instanceof Port && second instanceof OuterName
				&& validateSorts(((OuterName)second).getLinkSort(), ((Port)first).getSpec().getPortSort(), ((Port)first).getBigraph().getSignature()))
		{
			setTarget(((Port)first).getBigraph());
			cg.add(((Port)first).changeConnect((OuterName)second));
		} 
		else if (first instanceof OuterName && second instanceof Port
				&& validateSorts(((OuterName)first).getLinkSort(), ((Port)second).getSpec().getPortSort(), ((Port)second).getBigraph().getSignature()))
		{
			setTarget(((Port)second).getBigraph());
			cg.add(((Port)second).changeConnect((OuterName)first));
		} 
		else if (first instanceof OuterName && second instanceof Port
				&& (((OuterName)first).getLinkSort() == null 
				|| ((OuterName)first).getLinkSort().equals("")
				|| ((OuterName)first).getPoints().size() == 0))
		{
			setTarget(((Port)second).getBigraph());
			((OuterName)first).setLinkSort(((Port)second).getSpec().getPortSort());
			cg.add(((Port)second).changeConnect((OuterName)first));
		}
		else if (first instanceof InnerName && second instanceof Edge
				&& validateSorts(((InnerName)first).getInnerSort(), ((Edge)second).getLinkSort(), ((Edge)second).getBigraph().getSignature()))
		{
			setTarget(((InnerName)first).getBigraph());
			((InnerName)first).setInnerSort(((Edge)second).getLinkSort() + ":none");
			cg.add(((InnerName)first).changeConnect((Edge)second));
		} 
		else if (first instanceof Edge && second instanceof InnerName
				&& validateSorts(((InnerName)second).getInnerSort(), ((Edge)first).getLinkSort(), ((InnerName)second).getBigraph().getSignature()))
		{
			setTarget(((InnerName)second).getBigraph());
			((InnerName)second).setInnerSort(((Edge)first).getLinkSort() + ":none");
			cg.add(((InnerName)second).changeConnect((Edge)first));
		} 
		else if (first instanceof InnerName && second instanceof OuterName
				&& (((OuterName)second).getLinkSort() == null 
				|| ((OuterName)second).getLinkSort().equals("")
				|| ((OuterName)second).getPoints().size() == 0))
		{
			setTarget(((InnerName)first).getBigraph());
			((OuterName)second).setLinkSort(((InnerName)first).getInnerSort());
			cg.add(((InnerName)first).changeConnect((OuterName)second));
		} 
		else if (first instanceof InnerName && second instanceof OuterName
				&& validateSorts(((OuterName)second).getLinkSort(), ((InnerName)first).getInnerSort(), ((OuterName)second).getBigraph().getSignature()))
		{
			setTarget(((InnerName)first).getBigraph());
			((InnerName)first).setInnerSort(((InnerName)first).getInnerSort().split(":")[0] + ":none");
			cg.add(((InnerName)first).changeConnect((OuterName)second));
		} 
		else if (first instanceof OuterName && second instanceof InnerName
				&& validateSorts(((OuterName)first).getLinkSort(), ((InnerName)second).getInnerSort(), ((InnerName)second).getBigraph().getSignature()))
		{
			setTarget(((InnerName)second).getBigraph());
			((InnerName)second).setInnerSort(((InnerName)second).getInnerSort().split(":")[0] + ":none");
			cg.add(((InnerName)second).changeConnect((OuterName)first));
		} 
		else if (first instanceof OuterName && second instanceof InnerName
				&& (((OuterName)first).getLinkSort() == null 
				|| ((OuterName)first).getLinkSort().equals("")
				|| ((OuterName)first).getPoints().size() == 0))
		{
			setTarget(((InnerName)second).getBigraph());
			((OuterName)first).setLinkSort(((InnerName)second).getInnerSort());
			cg.add(((InnerName)second).changeConnect((OuterName)first));
		}
	}
}
