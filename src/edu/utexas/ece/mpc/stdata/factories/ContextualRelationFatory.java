package edu.utexas.ece.mpc.stdata.factories;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.frames.FramedGraph;

import edu.utexas.ece.mpc.stdata.edges.ContextualRelation;
import edu.utexas.ece.mpc.stdata.rules.IRuleRegistry;

public class ContextualRelationFatory extends
		EdgeFrameFactory<ContextualRelation> implements
		IContextualRelationFactory {

	public ContextualRelationFatory() {
		super(ContextualRelation.class);
	}

	public ContextualRelationFatory(TransactionalGraph baseGraph,
			FramedGraph framedGraph, IRuleRegistry ruleRegistry) {
		super(ContextualRelation.class, baseGraph, framedGraph, ruleRegistry);
		// TODO Auto-generated constructor stub
	}

}
