package hopshackle.algorithms;

import hopshackle.simulation.*;

import java.util.*;

public class PDDLOperator implements StateAction {

	private String name;
	private List<String> parameters;
	private List<PDDLCondition> preconditions;
	private List<PDDLCondition> effects;
	private String[] parameterValues;
	private boolean debug = false;

	public PDDLOperator(String actionName, List<String> parameters,	List<PDDLCondition> preconditions, List<PDDLCondition> effects) {
		name = actionName;
		this.parameters = HopshackleUtilities.cloneList(parameters);
		this.preconditions = HopshackleUtilities.cloneList(preconditions);
		this.effects = HopshackleUtilities.cloneList(effects);
		parameterValues = new String[parameters.size()];
		for (int i = 0; i < parameterValues.length; i++) 
			parameterValues[i] = "";
	}

	public PDDLOperator(PDDLOperator parent) {
		name = parent.name;
		preconditions = new ArrayList<PDDLCondition>();
		for (PDDLCondition pre : parent.preconditions)
			preconditions.add(new PDDLCondition(pre));
		effects = new ArrayList<PDDLCondition>();
		for (PDDLCondition effect : parent.effects)
			effects.add(new PDDLCondition(effect));
		parameters = HopshackleUtilities.cloneList(parent.parameters);
		parameterValues = new String[parameters.size()];
		for (int i = 0; i < parameters.size(); i ++)
			parameterValues[i] = parent.parameterValues[i];
	}

	public List<String> getParameters() {
		return HopshackleUtilities.cloneList(parameters);
	}

	public void fixParameter(String parameter, String value) {
		int index = parameters.indexOf(parameter);
		parameterValues[index] = value; 
		// now need to update pre-conditions and effects
		for (PDDLCondition pre : preconditions)
			pre.fixParameter(parameter, value);
		for (PDDLCondition effect : effects)
			effect.fixParameter(parameter, value);
	}

	public List<PDDLCondition> getPreconditions() {
		return HopshackleUtilities.cloneList(preconditions);
	}

	public List<PDDLOperator> getPossibleInstantiationsFrom(List<String> possibleValues, List<PDDLCondition> initialState) {
		List<PDDLOperator> retValue = new ArrayList<PDDLOperator>();
		//firstly create possibilities
		retValue.add(new PDDLOperator(this));
		List<PDDLOperator> copyToLoopOver = HopshackleUtilities.cloneList(retValue);
		for (String p : parameters) {
			if (debug)
				System.out.println("Starting parameter " + p);
			for (PDDLOperator parent : copyToLoopOver) {
				for (String v : possibleValues) {
					PDDLOperator nextPossibility = new PDDLOperator(parent);
					nextPossibility.fixParameter(p, v);
					retValue.add(nextPossibility);
				}
				retValue.remove(parent);
			}
			// Now check for validity
			List<PDDLOperator> clonedList = HopshackleUtilities.cloneList(retValue);
			for (PDDLOperator option : clonedList) {
				if (!option.isValidWith(initialState, true)) 
					retValue.remove(option);
			}

			if (debug)
				System.out.println("Total options at this stage " + retValue.size());
			copyToLoopOver = HopshackleUtilities.cloneList(retValue);
		}
		return retValue;
	}

	public List<PDDLOperator> getPossibleInstantiationsTo(List<String> possibleValues, List<PDDLCondition> goalState) {
		List<PDDLOperator> retValue = new ArrayList<PDDLOperator>();
		//firstly create possibilities
		retValue.add(new PDDLOperator(this));
		List<PDDLOperator> copyToLoopOver = HopshackleUtilities.cloneList(retValue);
		for (String p : parameters) {
			for (PDDLOperator parent : copyToLoopOver) {
				for (String v : possibleValues) {
					PDDLOperator nextPossibility = new PDDLOperator(parent);
					nextPossibility.fixParameter(p, v);
					retValue.add(nextPossibility);
				}
				retValue.remove(parent);
			}
			// Now check for validity
			List<PDDLOperator> clonedList = HopshackleUtilities.cloneList(retValue);
			for (PDDLOperator option : clonedList) {
				List<PDDLCondition> endState = option.knownFeaturesOfStateAfterAction(true);
				if (!checkRelevance(endState, goalState)) {
					retValue.remove(option);	// not compatible with desired goal	
				} else {
					List<PDDLCondition> startingState = HopshackleUtilities.cloneList(option.preconditions);
					startingState.addAll(goalState);
					if (!isValidWith(startingState, false))
						retValue.remove(option);
				}
			}

			copyToLoopOver = HopshackleUtilities.cloneList(retValue);
		}
		return retValue;
	}

	/*
	 * return false if there is a direct contradiction between the two lists of conditions
	 * true otherwise
	 */
	public static boolean checkRelevance(List<PDDLCondition> effects, List<PDDLCondition> target) {
		boolean contribution = false;
		for (PDDLCondition effect : effects) {
			for (PDDLCondition goal : target) {
				if (effect.negates(goal))
					return false;
				if (effect.matches(goal))
					contribution = true;
			}
		}
		return contribution;
	}

	/*
	 * returns true/false depending on whether the operator is valid with the specified list of conditions
	 * If strict = true, then the initialState is assumed to be Complete - i.e. any possible predicate not explicitly
	 * included is false. 
	 * If strict = false, then the initialState is assumed to be incomplete - i.e. no assumptions are made about possible 
	 * predicates that are no explicitly included ( so we just look for contradictions)
	 */
	public boolean isValidWith(List<PDDLCondition> initialState, boolean strict) {
		// for each precondition of the operator, we need to check that this exists in the initial State

		for (PDDLCondition precondition : preconditions) {
			boolean conditionMatched = false;
			boolean conditionNegated = false;
			for (PDDLCondition matchingCondition : initialState) {
				conditionMatched = matchingCondition.matches(precondition);
				conditionNegated = matchingCondition.negates(precondition);
				if (strict && !conditionMatched && !conditionNegated && (matchingCondition.isNegative() || precondition.isNegative()))
					conditionMatched = true;	// since in this case we assume the negative case is true on the other side by omission
				if (conditionMatched)
					break;
				if (conditionNegated)
					return false;	// no point continuing once we have a direct negation
			}
			if (!conditionMatched && strict) 
				return false;
		}
		return true;
	}

	public List<PDDLCondition> knownFeaturesOfStateAfterAction(boolean effectsOnly) {
		// we wish to generate a union of the effects of action, plus the preconditions that have not been superseded
		// we need to be slightly careful as the requirement is:
		// all preconditions - negative effects + positive effects  (in that order, in case a positive effect undoes a 
		// negative effect).

		List<PDDLCondition> retValue = new ArrayList<PDDLCondition>();
		if (!effectsOnly)
			for (PDDLCondition pre : preconditions) {
				retValue.add(new PDDLCondition(pre));
			}

		List<PDDLCondition> toRemove = new ArrayList<PDDLCondition>();
		List<PDDLCondition> negativeEffects = new ArrayList<PDDLCondition>();
		for (PDDLCondition effect : effects) {
			if (effect.isNegative()) {
				for (PDDLCondition pre : retValue) {
					if (pre.negates(effect))
						toRemove.add(pre);
				}
				negativeEffects.add(new PDDLCondition(effect));
			}
		}
		retValue.removeAll(toRemove);
		retValue.addAll(negativeEffects);

		List<PDDLCondition> positiveEffects = new ArrayList<PDDLCondition>();
		for (PDDLCondition effect : effects) {
			if (!effect.isNegative()) {
				boolean alreadyTrue = false;
				for (PDDLCondition pre : retValue) {
					if (pre.equals(effect))
						alreadyTrue = true;
				}
				if (!alreadyTrue)
					positiveEffects.add(new PDDLCondition(effect));
			}
		}
		retValue.addAll(positiveEffects);

		return retValue;
	}

	@Override
	public String toString() {
		StringBuffer output = new StringBuffer(name + " ");
		int count = 0;
		for (String p : parameters) {
			output.append(p + " (" + parameterValues[count] + ") ");
			count++;
		}
		output.append("\n");
		output.append(" preconditions: ");
		for (PDDLCondition pre : preconditions)
			output.append(pre.toString() + " ");
		output.append("\n");
		output.append(" effects: ");
		for (PDDLCondition post : effects)
			output.append(post.toString() + " ");
		output.append("\n");
		return output.toString();
	}

	public List<PDDLCondition> getRegressionSet(List<PDDLCondition> goalState) {
		List<PDDLCondition> retValue = new ArrayList<PDDLCondition>();
		for (PDDLCondition end : goalState) 
			retValue.add(new PDDLCondition(end));
		// we then insert all preconditions that are not already present
		for (PDDLCondition pre : preconditions)
			if (!retValue.contains(pre))
				retValue.add(new PDDLCondition(pre));
		return retValue;
	}

	public boolean isGround() {
		for (String p : parameterValues) 
			if (p.equals(""))
				return false;

		return true;
	}

	public List<PDDLCondition> getPositiveEffects() {
		List<PDDLCondition> retValue = new ArrayList<PDDLCondition>();
		for (PDDLCondition e : effects) {
			if (e.isNegative())
				continue;
			retValue.add(e);
		}
		return retValue;
	}

}
