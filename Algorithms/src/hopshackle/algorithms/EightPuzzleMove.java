package hopshackle.algorithms;

import hopshackle.simulation.*;

public enum EightPuzzleMove implements StateAction {

	UP,
	LEFT,
	DOWN,
	RIGHT;

	public int[] getNewBlankPosition(EightPuzzleState startState) {
		boolean invalidMove = false;
		int[] blank = startState.getCurrentBlankPosition();
		switch(this) {
		case UP:
			if (blank[0] == 0) invalidMove = true;
			blank[0]--; 
			break;
		case LEFT:
			if (blank[1] == 0) invalidMove = true;
			blank[1]--;
			break;
		case DOWN:
			if (blank[0] == 2) invalidMove = true;
			blank[0]++;
			break;
		case RIGHT:
			if (blank[1] == 2) invalidMove = true;
			blank[1]++;
			break;
		}
		if (invalidMove) {
			blank[0] = -1;
			blank[1] = -1;
		}
		return blank;
	}

}
