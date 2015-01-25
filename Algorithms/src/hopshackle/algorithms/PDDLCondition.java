package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.util.*;

public class PDDLCondition {

	private boolean NOT;
	private String name;
	private List<String> args;
	private String[] argumentValues;

	public PDDLCondition(String functionName, int args, boolean negated) {
		NOT = negated;
		name = functionName;
		this.args = new ArrayList<String>();
		for (int i = 1 ; i <= args; i++) {
			this.args.add("x" + i);
		}
		argumentValues = new String[this.args.size()];
		for (int i = 0; i < argumentValues.length; i++)
			argumentValues[i] = "";
	}

	public PDDLCondition(String functionName, List<String> args, boolean negated) {
		NOT = negated;
		name = functionName;
		this.args = HopshackleUtilities.cloneList(args);
		argumentValues = new String[args.size()];
		for (int i = 0; i < argumentValues.length; i++)
			argumentValues[i] = "";
	}

	public PDDLCondition(PDDLCondition parent) {
		this.NOT = parent.NOT;
		this.name = parent.name;
		this.args = HopshackleUtilities.cloneList(parent.args);
		argumentValues = new String[args.size()];
		for (int i = 0; i < argumentValues.length; i++)
			argumentValues[i] = parent.argumentValues[i];
	}

	public static PDDLCondition fromStringRepresentation(String input, boolean ground) {
		input = input.replace("(", "");
		input = input.replace(")", "");
		input = input.trim();
		boolean negated = false;
		StringTokenizer st = new StringTokenizer(input, " ");
		String functionName = st.nextToken();
		if (functionName.equals("not")) {
			negated = true;
			functionName = st.nextToken();
		}
		List<String> arguments = new ArrayList<String>();
		if (st.hasMoreTokens()) {
			do {
				arguments.add(st.nextToken());
			} while (st.hasMoreTokens());
		}
		PDDLCondition retValue = new PDDLCondition(functionName, arguments, negated);
		if (ground) {
			for (String arg : arguments) {
				retValue.fixParameter(arg, arg);
			}
		}
		return retValue;
	}

	public void fixParameter(int argumentNo, String argValue) {
		argumentValues[argumentNo - 1] = argValue;
	}

	public void fixParameter(String argName, String argValue) {	
		int index = args.indexOf(argName);
		int lastIndex = args.lastIndexOf(argName);
		if (index > -1) {
			if (lastIndex > -1 && lastIndex > index) {
				for (int i = index; i <= lastIndex; i++)
					fixParameter(i + 1, argValue);
			} else {
				fixParameter(index + 1, argValue);
			}
		}
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuffer output = new StringBuffer(name + " ");
		if (NOT)
			output = new StringBuffer(" not " + name + " ");
		int count = 0;
		for (String a : args) {
			output.append(a + " (" + argumentValues[count] + ") ");
			count++;
		}
		return output.substring(0, output.length()).toString();
	}

	/* 
	 * Returns true if the two Conditions match (at least on all of their instantiated variables)
	 */
	public boolean matches(PDDLCondition matchingCondition) {
		// for the moment an argument can either have any value, or a specific value - but not a subset
		boolean containsWildcards = false;
		if (!matchingCondition.sameTypeAs(this)) 
			return false;
		if (!NOT == matchingCondition.NOT)
			return false;	// they may be consistent; but they do not match
		for (int i = 0; i < args.size(); i++) {
			// In the matching case, two different parameter names do not preclude an actual match. (They may have the same value)
			// Only identical parameter names in all cases definitely indicates a match.
			if (matchingCondition.argumentValues[i].equals("")) {
				containsWildcards = true;
				continue;
			}
			if (argumentValues[i].equals("")) {
				containsWildcards = true;
				continue;
			}
			if (!argumentValues[i].equals(matchingCondition.argumentValues[i]))
				return false;
		}
		// at this point, we know that arguments are consistent
		// we only invoke NOT if we have a perfect match of all arguments, and none are wild
		if (NOT != matchingCondition.NOT && !containsWildcards)
			return false;
		return true;
	}

	private boolean sameTypeAs(PDDLCondition precondition) {
		return precondition.name.equals(name);
	}

	/*
	 * Returns true if the effect is incompatible with the Condition - i.e. they cannot
	 * both be predicates in the same state
	 */
	public boolean negates(PDDLCondition effect) {
		if (!this.sameTypeAs(effect))
			return false;
		// to negate we must be a perfect match on arguments, and have opposite signs of NOT
		if (NOT == effect.NOT)
			return false;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).startsWith("?") && effect.args.get(i).startsWith("?")) {	// these are parameter names, so we check identity on these
				if (!args.get(i).equals(effect.args.get(i)))
					return false;
			} else {	// these are not parameter names - we have to check actual values
				if (argumentValues[i].equals(""))
					return false;
				if (effect.argumentValues[i].equals(""))
					return false;
				if (!argumentValues[i].equals(effect.argumentValues[i]))
					return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int retValue = 0;
		if (NOT) retValue += 3;
		
		retValue += name.hashCode() * 17;
		
		for (String arg : argumentValues) {
			retValue += arg.hashCode();
		}
		
		return retValue;
	}

	public boolean isNegative() {
		return NOT;
	}

	@Override
	public boolean equals(Object other) {
		PDDLCondition comparison = null;
		if (other instanceof PDDLCondition) {
			comparison = (PDDLCondition) other;
		} else {
			return false;
		}

		if (comparison.NOT != NOT)
			return false;

		if (!sameTypeAs(comparison))
			return false;

		for (int i = 0; i < args.size(); i++) {
			// and we can always compare instantiated value for equality
			if (!argumentValues[i].equals(comparison.argumentValues[i]))
				return false;

			if (argumentValues[i].equals("")) {		// only relevant if both are blank - we then compare the variable name
				if (args.get(i).startsWith("?") && comparison.args.get(i).startsWith("?")) {
					// arg names may be compared for equality
					if (!args.get(i).equals(comparison.args.get(i)))
						return false;
				} 
			}
		}
		return true;
	}
}
