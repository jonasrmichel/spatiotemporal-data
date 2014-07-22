package edu.utexas.ece.mpc.stdata.factories;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.edges.ContextualRelation;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;

public class ContextualRelationFatory extends
		EdgeFrameFactory<ContextualRelation> implements
		IContextualRelationFactory {

	public ContextualRelationFatory(TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry) {
		super(baseGraph, framedGraph, ruleRegistry);
		// TODO Auto-generated constructor stub
	}

}
