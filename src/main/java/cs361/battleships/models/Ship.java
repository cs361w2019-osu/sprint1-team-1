package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Ship {

	@JsonProperty private List<Square> occupiedSquares = new ArrayList<>();
	@JsonProperty private List<Square> healthSquares = new ArrayList<>();

	private boolean alive;

	private String kind;
	private int length;
	private boolean vertical;

	public Ship() {
		this.alive = true;
		this.vertical = false;
	}
	
	public Ship(String kind) {
		this.kind = kind;
		if (kind.equals("MINESWEEPER")) {
			length = 2;
		} else if (kind.equals("DESTROYER")) {
			length = 3;
		} else if (kind.equals("BATTLESHIP")) {
			length = 4;
		}

		this.alive = true;
		this.vertical = false;
	}

	public List<Square> getOccupiedSquares() {
		return occupiedSquares;
	}

	public void setLocation(List<Square> newOccupiedSquares) {
		if (! occupiedSquares.isEmpty()) {
			occupiedSquares.clear();
		}
		for(Square s : newOccupiedSquares) {
			occupiedSquares.add(s);
			healthSquares.add(s);
		}
	}

	public int getLength() {
		return length;
	}

	public String getKind() {
		return kind;
	}


	public List<Square> getHealthSquares() {
		return healthSquares;
	}


	public boolean isAlive() {
		return this.alive;
	}

	public void sinkShip(){
		this.alive = false;
	}

	public boolean isVertical() {
		return this.vertical;
	}
}
