package hopshackle.algorithms;

import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

import org.junit.*;

public class PDDLProblemDomainTest {

	PDDLProblemDomain prob;
	List<String> possibleValues;
	
	@Before
	public void setUp() throws Exception {
		File file = new File("C:\\Users\\James\\Downloads\\random-domain.txt");
		prob = PDDLProblemDomain.instantiateProblemDomain(file);
		
		possibleValues = new ArrayList<String>();
		possibleValues.add("A");
		possibleValues.add("B");
		possibleValues.add("C");
		
		prob.setPossibleValues(possibleValues);
	}

	@Test
	public void baseExample() {
		List<PDDLCondition> initialState = new ArrayList<PDDLCondition>();
		PDDLCondition startingCondition1 = new PDDLCondition("S", 2, false);
		startingCondition1.fixParameter(1, "B");
		startingCondition1.fixParameter(2, "B");
		initialState.add(startingCondition1);
		
		List<PDDLOperator> possActions = prob.getPossibleActionsFrom(initialState);
		for (PDDLOperator a : possActions) {
			System.out.println(a);
		}
		System.out.println("Total actions: " + possActions.size());
		assertTrue(possActions.isEmpty());
		
		PDDLCondition startingCondition2 = new PDDLCondition("R", 2, false);
		startingCondition2.fixParameter(1, "B");
		startingCondition2.fixParameter(2, "B");
		initialState.add(startingCondition2);
		
		possActions = prob.getPossibleActionsFrom(initialState);
		for (PDDLOperator a : possActions) {
//			System.out.println(a);
		}
		System.out.println("Total actions: " + possActions.size());
		assertEquals(possActions.size(), 2);
	}

	@Test
	public void fiveConditionInitialState() {
		List<PDDLCondition> initialState = new ArrayList<PDDLCondition>();
		initialState.add(newGroundState("S", "B;B", false));
		initialState.add(newGroundState("S", "C;B", false));
		initialState.add(newGroundState("S", "A;C", false));
		initialState.add(newGroundState("R", "B;B", false));
		initialState.add(newGroundState("R", "C;B", false));
		
		List<PDDLOperator> possActions = prob.getPossibleActionsFrom(initialState);
		for (PDDLOperator a : possActions) {
//			System.out.println(a);
		}
		assertEquals(possActions.size(), 5);
	}
	
	@Test
	public void checkCompatibility() {
		List<PDDLCondition> goalState = new ArrayList<PDDLCondition>();
		goalState.add(newGroundState("S", "B;B", false));
		
		List<PDDLCondition> other = new ArrayList<PDDLCondition>();
		other.add(newGroundState("S", "B;B", true));
		assertFalse(PDDLOperator.checkRelevance(goalState, other));
		
		other = new ArrayList<PDDLCondition>();
		other.add(newGroundState("S", "B;A", true));
		other.add(newGroundState("S", "C;C", false));
		assertFalse(PDDLOperator.checkRelevance(goalState, other));
		
		other.add(newGroundState("S", "B;B", false));
		assertTrue(PDDLOperator.checkRelevance(goalState, other));
	}
	
	@Test
	public void isValidWithTakesAccountOfStrictness() {
		List<PDDLCondition> initialState = new ArrayList<PDDLCondition>();
		initialState.add(newGroundState("S", "B;B", false));
		
		PDDLOperator op1 = prob.getActions().get(0);
		op1.fixParameter("?x1", "B");
		op1.fixParameter("?x2", "B");
		op1.fixParameter("?x3", "B");
		
		assertFalse(op1.isValidWith(initialState, true));
		assertTrue(op1.isValidWith(initialState, false));
		
		initialState.add(newGroundState("R", "B;B", false));
		assertTrue(op1.isValidWith(initialState, true));
		assertTrue(op1.isValidWith(initialState, false));
	}
	
	@Test
	public void actionsLeadingToGoalState() {
		List<PDDLCondition> goal = new ArrayList<PDDLCondition>();

		goal.add(newGroundState("S", "A;B", false));
		goal.add(newGroundState("S", "B;C", false));
		
		PDDLOperator op1 = prob.getActions().get(0);
		List<PDDLOperator> actions = op1.getPossibleInstantiationsTo(possibleValues, goal);
		assertEquals(actions.size(), 11); 
		
		goal = new ArrayList<PDDLCondition>();
		goal.add(newGroundState("S", "A;B", true));
		goal.add(newGroundState("S", "B;C", true));
		actions = op1.getPossibleInstantiationsTo(possibleValues, goal);
		assertEquals(actions.size(), 0);
		
		goal = new ArrayList<PDDLCondition>();
		goal.add(newGroundState("S", "A;B", false));
		goal.add(newGroundState("S", "B;C", true));
		actions = op1.getPossibleInstantiationsTo(possibleValues, goal);
		assertEquals(actions.size(), 5);
	}
	
	@Test
	public void actionsLeadingToGoalStateCoursera() {
		List<PDDLCondition> goal = new ArrayList<PDDLCondition>();

		goal.add(newGroundState("S", "A;A", false));
		List<PDDLOperator> actions = prob.getPossibleActionsTo(goal);
		assertEquals(actions.size(), 5);
	}

	private PDDLCondition newGroundState(String name, String args, boolean negated) {
		PDDLCondition retValue = new PDDLCondition(name, 2, negated);
		String[] argValues = args.split(";");
		for (int i = 0; i < argValues.length; i++)
			retValue.fixParameter(i+1, argValues[i].trim());
		return retValue;
	}

}
