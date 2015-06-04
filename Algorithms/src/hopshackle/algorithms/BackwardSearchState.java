package hopshackle.algorithms;

import java.util.*;

import hopshackle.simulation.*;

public class BackwardSearchState implements State {

	private PDDLProblemDomain domain;
	private List<PDDLCondition> knownPredicates;
	
	public BackwardSearchState(PDDLProblemDomain problemDomain, List<PDDLCondition> predicates, boolean complete) {
		domain = problemDomain;
		knownPredicates = new ArrayList<PDDLCondition>();
		for (PDDLCondition p : predicates)
			knownPredicates.add(new PDDLCondition(p));
	}
	
	@Override
	public List<? extends StateAction> getValidActions() {
		return domain.getPossibleActionsTo(knownPredicates);
	}

	@Override
	public State applyAction(StateAction action) {
		if (action instanceof PDDLOperator) {
			PDDLOperator operator = (PDDLOperator) action;
			List<PDDLCondition> updatedStateKnowledge = operator.getRegressionSet(knownPredicates);
			return new BackwardSearchState(domain, updatedStateKnowledge, false);
		} else {
			throw new AssertionError("Invalid StateAction type in BackwardSearchState: " + action.toString());
		}
	}
}
