package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Ship {

	@JsonProperty private List<Square> occupiedSquares = new ArrayList<>();
	@JsonProperty private List<HealthSquare> healthSquares = new ArrayList<>();


	private boolean alive;

	private String kind;
	private int length;
	private boolean shipVertical;


	public Ship() {
		this.alive = true;
		this.shipVertical = false;
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
		this.shipVertical = false;
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
			healthSquares.add(new HealthSquare(s));
		}
		healthSquares.set(length - 2,
				new HealthSquare(healthSquares.get(length - 2),
						kind.equals("MINESWEEPER") ? 1 : 2, true));
	}

	public int getLength() {
		return length;
	}

	public String getKind() {
		return kind;
	}


	public List<HealthSquare> getHealthSquares() {
		return healthSquares;
	}


	public boolean isAlive() {
		return this.alive;
	}

	public void sinkShip(){
		this.alive = false;
	}

	public boolean isShipVertical() {
		return this.shipVertical;
	}


	public boolean stillAlive(){
		if(!alive)
			return false;
		for(HealthSquare hs : healthSquares){
			if(hs.getHealth() != 0)
				return true;
		}
		alive = false;
		return false;
	}

	public boolean move(char direction) {
		boolean moveVertical = false;
		if(direction == 'N' || direction == 'S') {
			moveVertical = true;
		}
		int linearDirection = 0;
		if(direction == 'S' || direction == 'E') {
			linearDirection = 1;
		} else {
			linearDirection = -1;
		}

		if(moveVertical) {
			for(Square s : occupiedSquares) {
				if(s.getRow() + linearDirection < 1 || s.getRow() + linearDirection > 10) {
					System.out.println("can't move to row " + Integer.toString(s.getRow() + linearDirection));
					return false;
				}
			}
		} else {
			for(Square s : occupiedSquares) {
				if(s.getColumn() + linearDirection < 'A' || s.getRow() + linearDirection > 'J') {
					return false;
				}
			}
		}

		if(moveVertical) {
			for(int i = 0; i < occupiedSquares.size(); i++) {
				Square currOcc = occupiedSquares.get(i);
				HealthSquare currHealth = healthSquares.get(i);
				currOcc.setRow(currOcc.getRow() + linearDirection);
				currHealth.setRow(currHealth.getRow() + linearDirection);
				occupiedSquares.set(i, currOcc);
				healthSquares.set(i, currHealth);
			}
		} else {
			for(int i = 0; i < occupiedSquares.size(); i++) {
				Square currOcc = occupiedSquares.get(i);
				HealthSquare currHealth = healthSquares.get(i);
				currOcc.setRow(currOcc.getColumn() + linearDirection);
				currHealth.setRow(currHealth.getColumn() + linearDirection);
				occupiedSquares.set(i, currOcc);
				healthSquares.set(i, currHealth);
			}
		}
		return true;
	}

	public AttackStatus takeDamageFrom(Result attack){
		AttackStatus resp = AttackStatus.MISS;
		for(HealthSquare hs : healthSquares){
			if(attack.getLocation().isEqual(hs) && hs.getHealth() == 2){
				hs.setHealth(1);
				resp = AttackStatus.HITARMR;
			}
			else if(attack.getLocation().isEqual(hs) && hs.getHealth() == 1 && hs.isisCaptain()){
				hs.setHealth(0);
				alive = false;
				resp = AttackStatus.SUNK;
			}
			else if(attack.getLocation().isEqual(hs) && hs.getHealth() == 1){
				hs.setHealth(0);
				resp = AttackStatus.HIT;
			}
		}



		return resp;
	}

	public boolean isEqual(Ship ship) {
		if(ship.kind == this.kind) {
			return true;
		}
		return false;
	}


}
