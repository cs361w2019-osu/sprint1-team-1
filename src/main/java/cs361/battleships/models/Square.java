package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;

@SuppressWarnings("unused")

@JsonSubTypes({
		@JsonSubTypes.Type(value = HealthSquare.class, name = "healthSquare")
})
public class Square {

	private int row;
	private char column;

	public Square() {
	}

	public Square(int row, char column) {
		this.row = row;
		this.column = column;
	}

	public Square(Square s){
		this.row = s.getRow();
		this.column = s.getColumn();
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
		return this.row == otherSquare.getRow() && this.column == otherSquare.getColumn(); }
}
