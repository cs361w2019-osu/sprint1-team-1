package cs361.battleships.models;

@SuppressWarnings("unused")
public class Square {

	private int row;
	private char column;
	private AttackStatus status;

	public Square() {
	}

	public Square(int row, char column) {
		this.row = row;
		this.column = column;
	}

	public char getColumn() {
		return column;
	}

	public void setColumn(char column) {
		this.column = column;
	}
	
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public boolean isEqual(Square otherSquare) {
		return this.row == otherSquare.row && this.column == otherSquare.column;
	}

	public AttackStatus getResult() {
		return this.status;
	}

	public void setResult(AttackStatus result) {
		this.status = result;
	}

	public void setLocation(Square square) {
		this.row = square.getRow();
		this.column = square.getColumn();
	}
}
