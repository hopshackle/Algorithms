package hopshackle.algorithms;

import java.util.*;

import hopshackle.simulation.*;

public class EightPuzzleState implements State {

	private int[][] boardPosition = new int[3][3];
	private int[] blankPosition = new int[2];

	public EightPuzzleState(int[][] startingPositions) {
		boardPosition = cloneBoardPosition(startingPositions);
		updateBlankPosition();
	}

	public EightPuzzleState(String string) {
		int[][] board = new int[3][3];
		String[] components = string.split(" ");
		int count = 0;
		for (String s : components) {
			int value = Integer.valueOf(s);
			board[count/3][count - 3*(count/3)] = value;
			count++;
		}
		boardPosition = cloneBoardPosition(board);
		updateBlankPosition();
	}

	public int[] getCurrentBlankPosition() {
		int[] retValue = new int[2];
		retValue[0] = blankPosition[0];
		retValue[1] = blankPosition[1];
		return retValue;
	}

	@Override
	public EightPuzzleState applyAction(StateAction action) {
		EightPuzzleMove move = (EightPuzzleMove)action;
		int[] newBlank = move.getNewBlankPosition(this);
		if (newBlank[0] == -1)
			return null;	// invalid move
		int numberAtNewBlank = boardPosition[newBlank[0]][newBlank[1]];
		int[][] newPosition = cloneBoardPosition(boardPosition);
		newPosition[newBlank[0]][newBlank[1]] = 0;
		newPosition[blankPosition[0]][blankPosition[1]] = numberAtNewBlank;
		return new EightPuzzleState(newPosition);
	}

	@Override
	public List<EightPuzzleMove> getValidActions() {
		List<EightPuzzleMove> actions = new ArrayList<EightPuzzleMove>();
		for (EightPuzzleMove move : EightPuzzleMove.values()) {
			if (move.getNewBlankPosition(this)[0] == -1)
				continue;
			actions.add(move);
		}
		return actions;
	}

	private void updateBlankPosition() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (boardPosition[i][j] == 0) {
					blankPosition[0] = i;
					blankPosition[1] = j;
				}
			}
		}
	}
	
	private static int[][] cloneBoardPosition(int[][] startingPositions) {
		int[][] retValue = new int[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				retValue[i][j] = startingPositions[i][j];
			}
		}
		return retValue;
	}
	
	@Override
	public String toString() {
		StringBuffer retValue = new StringBuffer();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				retValue.append(boardPosition[i][j] + " ");
			}
		}
		retValue.deleteCharAt(retValue.length()-1);
		return retValue.toString();
	}
	
	@Override
	public int hashCode() {
		int retValue = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				retValue += boardPosition[i][j] * (i+1) * (3*j + 5);
			}
		}
		return retValue;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof EightPuzzleState))
			return false;
		EightPuzzleState other = (EightPuzzleState) o;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (boardPosition[i][j] != other.boardPosition[i][j])
					return false;
			}
		}
		return true;
	}

}
