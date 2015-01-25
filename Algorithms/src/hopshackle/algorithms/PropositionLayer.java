package hopshackle.algorithms;

import hopshackle.simulation.HopshackleUtilities;

import java.util.*;

public class PropositionLayer {
	
	private Set<PDDLCondition> propositions = new HashSet<PDDLCondition>();
	
	public PropositionLayer(List<PDDLCondition> initialState) {
		for (PDDLCondition prop : initialState)
			propositions.add(prop);
	}
	
	public PropositionLayer(PropositionLayer parent) {
		for (PDDLCondition prop : parent.propositions)
			this.propositions.add(prop);
	}
	
	public void applyAction(PDDLOperator action) {
		if (!action.isGround())
			throw new AssertionError("Operator must be in Ground state for proposition layer application");
		
		for (PDDLCondition positiveEffect : action.getPositiveEffects()) {
			propositions.add(positiveEffect);
		}
	}
	
	public String toString() {
		StringBuffer retValue = new StringBuffer();
		List<String> atoms = new ArrayList<String>();
		for (PDDLCondition p : propositions) {
			atoms.add(p.toString());
		}
		Collections.sort(atoms);
		for (String text : atoms) {
			retValue.append(text + "\n");
		}
		return retValue.toString();
	}

	public int getSize() {
		return propositions.size();
	}

	public List<PDDLCondition> getPropositions() {
		List<PDDLCondition> retValue = new ArrayList<PDDLCondition>();
		for (PDDLCondition prop : propositions) {
			retValue.add(prop);
		}
		return retValue;
	}

}
