package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.io.File;
import java.util.*;

public class PDDLProblemDomainDWRReduced {

	private List<PDDLOperator> actions;
	private List<String> possibleValues;
	private boolean debug = false;

	private PDDLProblemDomainDWRReduced(List<PDDLOperator> allActions) {
		actions = allActions;
	}

	public List<PDDLOperator> getActions() {
		return actions;
	}

	public List<PDDLOperator> getPossibleActionsFrom(List<PDDLCondition> initialState) {
		List<PDDLOperator> retValue = new ArrayList<PDDLOperator>();
		for (PDDLOperator a : actions) {
			if (debug)
				System.out.println("Starting action " + a);
			retValue.addAll(a.getPossibleInstantiationsFrom(possibleValues, initialState));
			if (debug)
				System.out.println("Total possible actions " + retValue.size());
		}
		return retValue;
	}
	

	public List<PDDLOperator> getPossibleActionsFrom(PropositionLayer propLayer) {
		return getPossibleActionsFrom(propLayer.getPropositions());
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

	public static PDDLProblemDomainDWRReduced instantiateProblemDomain(File file) {
		List<String> rawData = HopshackleUtilities.createListFromFile(file);
		String actionName = "";
		List<String> parameters = new ArrayList<String>();
		List<PDDLCondition> preconditions = new ArrayList<PDDLCondition>();
		List<PDDLCondition> effects = new ArrayList<PDDLCondition>();
		List<PDDLOperator> actions = new ArrayList<PDDLOperator>();
		List<PDDLCondition> conditions = new ArrayList<PDDLCondition>();

		LineType currentLine = LineType.INVALID;
		boolean continuingLine = false;

		for (String line : rawData) {
			if (line.contains(";"))	// comment indicator for rest of line
				line = line.substring(0, line.indexOf(";"));
			line = line.trim();
			if (line == "")
				continue;
			String lineType = "";
			if (line.contains(":")) {
				continuingLine = false;
				currentLine = LineType.INVALID;	// any colon indicates a new keyword. Lack of a colon indicates continuation from previous line.
				StringTokenizer st = new StringTokenizer(line, ":");
				do {
					lineType = st.nextToken();
				} while (st.hasMoreTokens() && !isValid(lineType));
			} else {
				// this is a continuation line
				lineType = line;
				continuingLine = true;
			}

			currentLine = getLineType(currentLine, lineType);

			if (currentLine == LineType.ACTION) {
				if (continuingLine)
					throw new AssertionError("Line continuations not currently supported for :action");
				if (!actionName.equals(""))
					actions.add(new PDDLOperator(actionName, parameters, preconditions, effects));
				actionName = lineType.substring(7);	// name follows 'action '
				parameters = new ArrayList<String>();
				preconditions = new ArrayList<PDDLCondition>();
				effects = new ArrayList<PDDLCondition>();
			}

			if (currentLine == LineType.PARAMETER) {
				if (continuingLine)
					throw new AssertionError("Line continuations not currently supported for :parameters");
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

			if (currentLine == LineType.PRECONDITION || currentLine == LineType.EFFECT) {
				int charsToIgnore = 0;
				if (!continuingLine) {	// i.e. first line of effect or precondition
					charsToIgnore = 13;
					if (lineType.startsWith("effect")) {
						charsToIgnore = 7;
					}
					conditions = new ArrayList<PDDLCondition>();
				}
				StringTokenizer restOfLine = new StringTokenizer(lineType.substring(charsToIgnore), "(");
				List<String> args = new ArrayList<String>();
				boolean negated = false;
				boolean negateNextFunction = false;
				if (!restOfLine.hasMoreTokens())
					continue;
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

				if (currentLine == LineType.EFFECT) {
					effects = conditions;
				} else {
					preconditions = conditions;
				}
			}
		}

		if (!actionName.equals(""))
			actions.add(new PDDLOperator(actionName, parameters, preconditions, effects));

		return new PDDLProblemDomainDWRReduced(actions);
	}

	private static LineType getLineType(LineType oldLine, String lineType) {
		if (lineType.startsWith("action"))
			return LineType.ACTION;
		if (lineType.startsWith("parameters"))
			return LineType.PARAMETER;
		if (lineType.startsWith("precondition"))
			return LineType.PRECONDITION;
		if (lineType.startsWith("effect"))
			return LineType.EFFECT;
		return oldLine;	// i.e. no change
	}

	private static boolean isValid(String lineType) {
		if (lineType.startsWith("action")) return true;
		if (lineType.startsWith("parameters")) return true;
		if (lineType.startsWith("precondition")) return true;
		if (lineType.startsWith("effect")) return true;
		return false;
	}
	@Override
	public String toString() {
		StringBuffer temp = new StringBuffer();
		for (PDDLOperator a : actions)
			temp.append(a.toString() + "\n");
		return temp.toString();
	}

}

enum LineType {
	INVALID,
	ACTION,
	PARAMETER,
	EFFECT,
	PRECONDITION;
}
