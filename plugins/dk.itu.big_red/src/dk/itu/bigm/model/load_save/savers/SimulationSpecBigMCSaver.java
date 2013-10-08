package dk.itu.bigm.model.load_save.savers;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.bigraph.model.Bigraph;
import org.bigraph.model.Container;
import org.bigraph.model.Control;
import org.bigraph.model.Edge;
import org.bigraph.model.InnerName;
import org.bigraph.model.Layoutable;
import org.bigraph.model.ModelObject;
import org.bigraph.model.Node;
import org.bigraph.model.OuterName;
import org.bigraph.model.ReactionRule;
import org.bigraph.model.Signature;
import org.bigraph.model.SimulationSpec;
import org.bigraph.model.Site;
import org.bigraph.model.interfaces.IChild;
import org.bigraph.model.interfaces.IInnerName;
import org.bigraph.model.interfaces.ILink;
import org.bigraph.model.interfaces.INode;
import org.bigraph.model.interfaces.IOuterName;
import org.bigraph.model.interfaces.IPort;
import org.bigraph.model.interfaces.IRoot;
import org.bigraph.model.interfaces.ISite;
import org.bigraph.model.names.policies.INamePolicy;
import org.bigraph.model.savers.SaveFailedException;
import org.bigraph.model.savers.Saver;

import dk.itu.bigm.model.ExtendedDataUtilities;

import org.bigraph.extensions.param.ParameterUtilities;

public class SimulationSpecBigMCSaver extends Saver {
	private OutputStreamWriter osw = null;
	
	private boolean namedRules = true;
	
	{
		addOption(new SaverOption("Export named rules") {
			@Override
			public Object get() {
				return namedRules;
			}
			
			@Override
			public void set(Object value) {
				namedRules = (Boolean)value;
			}
		});
	}
	
	private static Pattern p = Pattern.compile("[^a-zA-Z0-9_]");
	
	private static String normaliseName(String name) {
		return p.matcher(name.trim()).replaceAll("_");
	}
	
	@Override
	public SimulationSpec getModel() {
		return (SimulationSpec)super.getModel();
	}
	
	@Override
	public SimulationSpecBigMCSaver setModel(ModelObject model) {
		if (model instanceof SimulationSpec)
			super.setModel(model);
		return this;
	}
	
	private void write(String str) throws SaveFailedException {
		try {
			osw.write(str);
		} catch (IOException e) {
			throw new SaveFailedException(e);
		}
	}
	
	@Override
	public void exportObject() throws SaveFailedException {
		osw = new OutputStreamWriter(getOutputStream());
		processSimulationSpec(getModel());
		try {
			osw.close();
		} catch (IOException e) {
			throw new SaveFailedException(e);
		}
	}

	private final ArrayList<String> controlNames = new ArrayList<String>();
	
	private void writeControl(Control c, String param)
			throws SaveFailedException {
		String name = normaliseName(c.getName());
		if (param != null)
			name += "_P__" + normaliseName(param);
		
		if (!controlNames.contains(name)) {
			switch (c.getKind()) {
			case ACTIVE:
			case ATOMIC:
			default:
				write("%active ");
				break;
			case PASSIVE:
				write("%passive ");
				break;
			}
			
			write(name + " : ");
			write(c.getPorts().size() + ";\n");
			
			controlNames.add(name);
		}
	}
	
	private void recHandleParams(Layoutable l) throws SaveFailedException {
		if (l instanceof Node) {
			Node n = (Node)l;
			String param = ParameterUtilities.getParameter(n);
			if (param != null)
				writeControl(n.getControl(), param);
		}
		
		if (l instanceof Container)
			for (Layoutable i : ((Container)l).getChildren())
				recHandleParams(i);
	}
	
	private boolean recProcessSignature(List<String> names, Signature s)
			throws SaveFailedException {
		boolean parameterised = false;
		for (Control c : s.getControls()) {
			if (names.contains(c.getName()))
				continue;
			INamePolicy policy = ParameterUtilities.getParameterPolicy(c);
			if (policy == null) {
				writeControl(c, null);
			} else parameterised = true;
		}
		for (Signature t : s.getSignatures()) {
			if (recProcessSignature(names, t))
				parameterised = true;
		}
		return parameterised;
	}
	
	private void processSignature(SimulationSpec ss) throws SaveFailedException {
		write("# Controls\n");
		if (recProcessSignature(
				new ArrayList<String>(), ss.getSignature())) {
			recHandleParams(ss.getModel());
			for (ReactionRule r : ss.getRules()) {
				recHandleParams(r.getRedex());
				recHandleParams(r.getReactum());
			}
		}
		write("\n");
	}
	
	private boolean validateSameName(ArrayList<String> names, String name){
		for(String n : names){
			if(n.equals(name))
				return true;
		}
		return false;
	}
	
	private void processNames(SimulationSpec s) throws SaveFailedException {
		ArrayList<String> names = new ArrayList<String>();
		String name;
		for(ReactionRule r : s.getRules()){
			for(IInnerName i : r.getRedex().getInnerNames()){
				name = "%innername " + normaliseName(i.getName());
				if(!validateSameName(names, name)){
					names.add(name);
				}
			}
			for(IOuterName o : r.getRedex().getOuterNames()){
				name = "%outername " + normaliseName(o.getName());
				if(!validateSameName(names, name)){
					names.add(name);
				}
			}
			for(IInnerName i : r.getReactum().getInnerNames()){
				name = "%innername " + normaliseName(i.getName());
				if(!validateSameName(names, name)){
					names.add(name);
				}
			}
			for(IOuterName o : r.getReactum().getOuterNames()){
				name = "%outername " + normaliseName(o.getName());
				if(!validateSameName(names, name)){
					names.add(name);
				}
			}
		}
		for(IInnerName i : s.getModel().getInnerNames()){
			name = "%innername " + normaliseName(i.getName());
			if(!validateSameName(names, name)){
				names.add(name);
			}
		}
		for (IOuterName o : s.getModel().getOuterNames()){
			name = "%outername " + normaliseName(o.getName());
			if(!validateSameName(names, name)){
				names.add(name);
			}
		}
		Collections.sort(names);
		
		if (names.size() == 0)
			return;
		write("# Names\n");
		for (String n : names)
			write(n + ";\n");
		write("\n");
	}
	
	private static String getPortString(ILink l) {
		String result = "";
		result += (l != null ? normaliseName(l.getName()) : "idle");
		if(l instanceof InnerName){
			result += ":innername";
		}else if(l instanceof OuterName){
			result += ":outername";
		}else if(l instanceof Edge){
			result += ":edge";
		}
		return result;
	}
	
	private void processChild(IChild i, boolean isModel) throws SaveFailedException {
		if (i instanceof ISite) {
			processSite((ISite)i);
		} else if (i instanceof INode) {
			processNode((INode)i, isModel);
		}
	}
	
	private void processSite(ISite i) throws SaveFailedException {
		String alias = ExtendedDataUtilities.getAlias((Site)i); /* XXX!! */
		write("$" + (alias == null ? i.getName() : alias));
	}
	
	private void processNode(INode i, boolean isModel) throws SaveFailedException {
		String
			instanceName = normaliseName(i.getName()),
			controlName = normaliseName(i.getControl().getName()),
			param = ParameterUtilities.getParameter((Node)i); /* XXX!! */
		if (param != null)
			controlName += "_P__" + normaliseName(param);
		if(isModel)
			write(instanceName + ":" + controlName);
		else
			write(controlName);
		
		Iterator<? extends IPort> it = i.getPorts().iterator();
		if (it.hasNext()) {
			write("[" + getPortString(it.next().getLink()));
			while (it.hasNext())
				write("," + getPortString(it.next().getLink()));
			write("]");
		}
		
		Iterator<? extends IChild> in = i.getIChildren().iterator();
		if (in.hasNext()) {
			write(".");
			IChild firstChild = in.next();
			if (in.hasNext()) {
				write("(");
				processChild(firstChild, isModel);
				while (in.hasNext()) {
					write(" | ");
					processChild(in.next(), isModel);
				}
				write(")");
			} else processChild(firstChild, isModel);
		}
	}
	
	private void processRoot(IRoot i, boolean isModel) throws SaveFailedException {
		Iterator<? extends INode> in = i.getNodes().iterator();
		boolean anyNodes = in.hasNext();
		if (anyNodes) {
			processNode(in.next(), isModel);
			while (in.hasNext()) {
				write(" | ");
				processNode(in.next(), isModel);
			}
		}
		
		Iterator<? extends ISite> is = i.getSites().iterator();
		boolean anySites = is.hasNext();
		if (anySites) {
			if (anyNodes)
				write(" | ");
			processSite(is.next());
			while (is.hasNext()) {
				write(" | ");
				processSite(is.next());
			}
		}
		
		if (!anyNodes && !anySites)
			write("nil");
	}
	
	private void processBigraph(Bigraph b, boolean isModel) throws SaveFailedException {
		Iterator<? extends IRoot> ir = b.getRoots().iterator();
		if (ir.hasNext()) {
			processRoot(ir.next(), isModel);
			while (ir.hasNext()) {
				write(" || ");
				processRoot(ir.next(), isModel);
			}
		} else write("nil");
	}
	
	private static <T, V>
	boolean iteratorsMatched(Iterator<T> i, Iterator<V> j) {
		while (i.hasNext() && j.hasNext()) {
			i.next(); j.next();
		}
		return (i.hasNext() == j.hasNext());
	}
	
	private int i = 0;
	
	private void processRule(ReactionRule r) throws SaveFailedException {
		if (!iteratorsMatched(
				r.getRedex().getRoots().iterator(),
				r.getReactum().getRoots().iterator()))
			throw new SaveFailedException(
					"Same number of roots required in redex and reactum");
		if (namedRules)
			write("%rule r_" + (i++) + " "); /* XXX FIXME */
		processBigraph(r.getRedex(), false);
		write(" -> ");
		processBigraph(r.getReactum(), false);
		write(";\n");
	}
	
	private void processModel(Bigraph b) throws SaveFailedException {
		write("%agent ");
		processBigraph(b, true);
		write(";\n");
	}
	
	private void processSimulationSpec(SimulationSpec s) throws SaveFailedException {
		processSignature(s);
		processNames(s);
		
		List<? extends ReactionRule> rules = s.getRules();
		if (rules.size() != 0) {
			write("# Rules\n");
			for (ReactionRule r : s.getRules())
				processRule(r);
			write("\n");
		}
		
		write("# Model\n");
		processModel(s.getModel());
		
		write("\n# Go!\n%check;\n");
	}
}
