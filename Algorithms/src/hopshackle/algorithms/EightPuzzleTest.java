package hopshackle.algorithms;

import static org.junit.Assert.*;

import java.util.List;

import hopshackle.simulation.*;

import org.junit.*;

public class EightPuzzleTest {

	private GoalMatcher<EightPuzzleState> goalTest;
	private ValuationFunction<EightPuzzleState> heuristic;
	
	@Before
	public void setUp() throws Exception {
		goalTest = new GoalMatcher<EightPuzzleState>() {
			
			@Override
			public boolean supercedes(GoalMatcher<EightPuzzleState> competitor) {
				return false;
			}
			
			@Override
			public boolean matches(EightPuzzleState state) {
				if (state.toString().equals("0 1 2 3 4 5 6 7 8"))
					return true;
				return false;
			}
		};
		
		heuristic = new ValuationFunction<EightPuzzleState>() {
			
			@Override
			public double getValue(EightPuzzleState item) {
				String[] position = item.toString().split(" ");
				double retValue = 8.0;
				for (int i = 0; i < 9; i++) {
					if (!String.valueOf(i).equals("0") && position[i].equals(String.valueOf(i)))
						retValue -= 1.0;
				}
				return retValue;
			}
		};
	}

	@Test
	public void startsInGoalState() {
		EightPuzzleState startState = new EightPuzzleState("0 1 2 3 4 5 6 7 8");
		List<StateAction> plan = AStarSearch.findPlan(startState, goalTest, heuristic);
		assertTrue(plan.isEmpty());
	}
	
	@Test
	public void oneMoveToGoalState() {
		EightPuzzleState startState = new EightPuzzleState("3 1 2 0 4 5 6 7 8");
		List<StateAction> plan = AStarSearch.findPlan(startState, goalTest, heuristic);
		assertEquals(plan.size(), 1);
		assertTrue(plan.get(0) == EightPuzzleMove.UP);
	}
	
	@Test
	public void fiveMovesToGoalState() {
		EightPuzzleState startState = new EightPuzzleState("3 1 2 0 7 5 4 6 8");
		List<StateAction> plan = AStarSearch.findPlan(startState, goalTest, heuristic);
		assertEquals(plan.size(), 5);
	}
	
	@Test
	public void manyMovesToGoalState() {
		EightPuzzleState startState = new EightPuzzleState("1 6 4 8 7 0 3 2 5");
		List<StateAction> plan = AStarSearch.findPlan(startState, goalTest, heuristic);
		assertEquals(plan.size(), 21);
	}
	
	@Test
	public void evenMoreMovesToGoalState() {
		EightPuzzleState startState = new EightPuzzleState("8 1 7 4 5 6 2 0 3");
		List<StateAction> plan = AStarSearch.findPlan(startState, goalTest, heuristic);
		assertEquals(plan.size(), 25);
	}
	
	@Test
	public void noGoalTestSpecified() {
		EightPuzzleState startState = new EightPuzzleState("0 1 2 3 4 5 6 7 8");
		List<StateAction> plan = AStarSearch.findPlan(startState, null, null);
		assertTrue(plan.isEmpty());
	}

}
