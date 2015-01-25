package hopshackle.algorithms;

import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

import org.junit.*;

public class PDDLProblemDomainDWRReducedTest {

	PDDLProblemDomainDWRReduced prob;
	List<String> possibleValues;
	List<PDDLCondition> initialState = new ArrayList<PDDLCondition>();

	@Before
	public void setUp() throws Exception {
		File file = new File("C:\\Users\\James\\Downloads\\dwr-domain.txt");
		prob = PDDLProblemDomainDWRReduced.instantiateProblemDomain(file);

		possibleValues = new ArrayList<String>();
		possibleValues.add("r1");
		possibleValues.add("l1");
		possibleValues.add("l2");
		possibleValues.add("k1");
		possibleValues.add("k2");
		possibleValues.add("p1");
		possibleValues.add("q1");
		possibleValues.add("p2");
		possibleValues.add("q2");
		possibleValues.add("ca");
		possibleValues.add("cb");
		possibleValues.add("cc");
		possibleValues.add("cd");
		possibleValues.add("ce");
		possibleValues.add("cf");
		possibleValues.add("pallet");

		prob.setPossibleValues(possibleValues);

		initialState.add(PDDLCondition.fromStringRepresentation("adjacent l1 l2", true));
		initialState.add(PDDLCondition.fromStringRepresentation("adjacent l2 l1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("attached p1 l1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("attached q1 l1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("attached p2 l2", true));
		initialState.add(PDDLCondition.fromStringRepresentation("attached q2 l2", true));
		initialState.add(PDDLCondition.fromStringRepresentation("belong k1 l1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("belong k2 l2", true));
		initialState.add(PDDLCondition.fromStringRepresentation("in ca p1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("in cb p1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("in cc p1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("in cd q1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("in ce q1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("in cf q1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("on ca pallet", true));
		initialState.add(PDDLCondition.fromStringRepresentation("on cb ca", true));
		initialState.add(PDDLCondition.fromStringRepresentation("on cc cb", true));
		initialState.add(PDDLCondition.fromStringRepresentation("on cd pallet", true));
		initialState.add(PDDLCondition.fromStringRepresentation("on ce cd", true));
		initialState.add(PDDLCondition.fromStringRepresentation("on cf ce", true));
		initialState.add(PDDLCondition.fromStringRepresentation("top cc p1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("top cf q1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("top pallet p2", true));
		initialState.add(PDDLCondition.fromStringRepresentation("top pallet q2", true));
		initialState.add(PDDLCondition.fromStringRepresentation("at r1 l1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("unloaded r1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("free l2", true));
		initialState.add(PDDLCondition.fromStringRepresentation("empty k1", true));
		initialState.add(PDDLCondition.fromStringRepresentation("empty k2", true));

	}

	@Test
	public void readDomain() {
		System.out.println(prob);
	}

	@Test
	public void firstActionLayer() {
		List<PDDLOperator> actionLayer = prob.getPossibleActionsFrom(initialState);
		assertEquals(actionLayer.size(), 3);
		// Move robot, or take first cargo on p1
	}

	@Test
	public void firstPropositionLayer() {
		List<PDDLOperator> actionLayer = prob.getPossibleActionsFrom(initialState);
		PropositionLayer firstPL = new PropositionLayer(initialState);
		for (PDDLOperator action : actionLayer) {
			firstPL.applyAction(action);
		}
		assertEquals(firstPL.getSize(), 35);
	}

	@Test
	public void iteration() {
		List<PDDLCondition> goalState = new ArrayList<PDDLCondition>();
		goalState.add(PDDLCondition.fromStringRepresentation("in cb q2", true));
		goalState.add(PDDLCondition.fromStringRepresentation("in cc p2", true));
		goalState.add(PDDLCondition.fromStringRepresentation("in cd q2", true));
		goalState.add(PDDLCondition.fromStringRepresentation("in ce q2", true));		
		goalState.add(PDDLCondition.fromStringRepresentation("in cf q2", true));
		goalState.add(PDDLCondition.fromStringRepresentation("in ca p2", true));
		int iteration = 0;
		PropositionLayer propLayer = new PropositionLayer(initialState);
		List<PDDLOperator> actionLayer = new ArrayList<PDDLOperator>();
		int lastPropLayer = 0;
		int lastActionLayer = 0;
		boolean finished = false;
		do {
			for (PDDLOperator action : actionLayer) {
				propLayer.applyAction(action);
			}
			System.out.println("P" + iteration + " of size " + propLayer.getSize());
			List<PDDLCondition> achieved = new ArrayList<PDDLCondition>();
			for (PDDLCondition g : goalState) {
				if (propLayer.getPropositions().contains(g)) {
					System.out.println("Goal reached");
					achieved.add(g);
				}
			}
			for (PDDLCondition a : achieved)
				goalState.remove(a);
			//			System.out.println(propLayer);
			iteration++;
			actionLayer = prob.getPossibleActionsFrom(propLayer);
			for (PDDLOperator action : actionLayer) {
				//				System.out.println(action);
			}
			System.out.println("A" + iteration + " of size " + actionLayer.size());
			if (lastPropLayer == propLayer.getSize() || lastActionLayer == actionLayer.size())
				finished = true;
			lastPropLayer = propLayer.getSize();
			lastActionLayer = actionLayer.size();
		} while (!finished);
	}
}