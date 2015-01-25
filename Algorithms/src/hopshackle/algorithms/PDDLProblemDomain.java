package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.io.File;
import java.util.*;

public class PDDLProblemDomain {

	private List<PDDLOperator> actions;
	private List<String> possibleValues;

	private PDDLProblemDomain(List<PDDLOperator> allActions) {
		actions = allActions;
	}

	public List<PDDLOperator> getActions() {
		return actions;
	}
	
	public List<PDDLOperator> getPossibleActionsFrom(List<PDDLCondition> initialState) {
		List<PDDLOperator> retValue = new ArrayList<PDDLOperator>();
		for (PDDLOperator a : actions) {
			retValue.addAll(a.getPossibleInstantiationsFrom(possibleValues, initialState));
		}
		return retValue;
	}
	
	public List<PDDLOperator> getPossibleActionsTo(List<PDDLCondition> goalState) {
		List<PDDLOperator> retValue = new ArrayList<PDDLOperator>();
		for (PDDLOperator a : actions) {
			retValue.addAll(a.getPossibleInstantiationsTo(possibleValues, goalState));
		}
		return retValue;
	}

	public List<String> getPossibleValues() {
		return HopshackleUtilities.cloneList(possibleValues);
	}
	
	public void setPossibleValues(List<String> values) {
		possibleValues = HopshackleUtilities.cloneList(values);
	}
	
	public static PDDLProblemDomain instantiateProblemDomain(File file) {
		List<String> rawData = HopshackleUtilities.createListFromFile(file);
		String actionName = "";
		List<String> parameters = new ArrayList<String>();
		List<PDDLCondition> preconditions = new ArrayList<PDDLCondition>();
		List<PDDLCondition> effects = new ArrayList<PDDLCondition>();
		List<PDDLOperator> actions = new ArrayList<PDDLOperator>();
		for (String line : rawData) {
			StringTokenizer st = new StringTokenizer(line, ":");
			String lineType = "";
			do {
				lineType = st.nextToken();
			} while (st.hasMoreTokens() && !isValid(lineType));

			if (lineType.startsWith("action")) {
				if (!actionName.equals(""))
					actions.add(new PDDLOperator(actionName, parameters, preconditions, effects));
				actionName = lineType.substring(7);	// name follows 'action '
				parameters = new ArrayList<String>();
				preconditions = new ArrayList<PDDLCondition>();
				effects = new ArrayList<PDDLCondition>();
			}

			if (lineType.startsWith("parameters")) {
				StringTokenizer restOfLine = new StringTokenizer(lineType, " ");
				parameters = new ArrayList<String>();
				do {
					String parameterName = restOfLine.nextToken();
					if (parameterName.contains("?")) {
						parameterName = parameterName.replace("(", "");
						parameterName = parameterName.replace(")", "");
						parameters.add(parameterName.trim());
					}
				} while(restOfLine.hasMoreTokens());
			}


			if (lineType.startsWith("precondition") || lineType.startsWith("effect")) {
				int charsToIgnore = 13;
				if (lineType.startsWith("effect")) {
					charsToIgnore = 7;
				}
				StringTokenizer restOfLine = new StringTokenizer(lineType.substring(charsToIgnore), "(");
				List<PDDLCondition> conditions = new ArrayList<PDDLCondition>();
				List<String> args = new ArrayList<String>();
				boolean negated = false;
				boolean negateNextFunction = false;
				do {
					String functionName = "";
					String segment = restOfLine.nextToken().trim();
					segment = segment.replace(")", "");
					if (segment.startsWith("and")) 
						continue;	// we just assume this for the moment
					// so we must have the start of a function
					if (segment.startsWith("not")) {
						negateNextFunction = true;	
						continue;
					}

					StringTokenizer functionTokenizer = new StringTokenizer(segment.trim(), " ");
					do {
						String functionMoiety = functionTokenizer.nextToken();
						if (functionMoiety.startsWith("?")) {
							args.add(functionMoiety.substring(0));
						} else {
							if (!functionName.equals(""))
								conditions.add(new PDDLCondition(functionName, args, negated));
							functionName = functionMoiety;
							args = new ArrayList<String>();
							negated = negateNextFunction;
							negateNextFunction = false;
						}
					} while (functionTokenizer.hasMoreTokens());
					if (!functionName.equals(""))
						conditions.add(new PDDLCondition(functionName, args, negated));

				} while(restOfLine.hasMoreTokens());

				if (lineType.startsWith("effect")) {
					effects = conditions;
				} else {
					preconditions = conditions;
				}
			}
		}

		if (!actionName.equals(""))
			actions.add(new PDDLOperator(actionName, parameters, preconditions, effects));

		return new PDDLProblemDomain(actions);
	}

	private static boolean isValid(String lineType) {
		if (lineType.startsWith("action")) return true;
		if (lineType.startsWith("parameters")) return true;
		if (lineType.startsWith("precondition")) return true;
		if (lineType.startsWith("effect")) return true;
		return false;
	}

}
