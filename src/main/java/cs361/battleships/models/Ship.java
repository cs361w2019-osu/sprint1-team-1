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
		} else if (kind.equals("BATTLESHIP") || kind.equals("SUBMARINE")) {
			length = 4; // This is only keeping track of the length of the main body might change later
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
		if (!kind.equals("SUBMARINE")) {
			healthSquares.set(length - 2,
					new HealthSquare(healthSquares.get(length - 2),
					kind.equals("MINESWEEPER") ? 1 : 2, true));
		} else {
		    healthSquares.set(length,
					new HealthSquare(healthSquares.get(length), 2, true));
		}

		if (kind.equals("SUBMARINE")) {
			for (int i = 0; i < length + 1; i++) {
				healthSquares.get(i).setIsSubmerged(true);
			}
		}
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

	public AttackStatus takeDamageFrom(Result attack){
		AttackStatus resp = AttackStatus.MISS;
		for(HealthSquare hs : healthSquares){
			if (attack.getLocation().isEqual(hs) && hs.getIsSubbmerged()) {
				resp = AttackStatus.MISS_SUB;
			} else if (attack.getLocation().isEqual(hs) && hs.getHealth() == 2) {
				hs.setHealth(1);
				resp = AttackStatus.HITARMR;
			} else if (attack.getLocation().isEqual(hs) && hs.getHealth() == 1 && hs.isisCaptain()) {
				hs.setHealth(0);
				alive = false;
				resp = AttackStatus.SUNK;
			} else if (attack.getLocation().isEqual(hs) && hs.getHealth() == 1) {
				hs.setHealth(0);
				resp = AttackStatus.HIT;
			}
		}



		return resp;
	}

	public void setSubmerged(boolean submerged) {
		if (!kind.equals("SUBMARINE")) {
			System.out.println("Only Submarines can submerge");
			return;
		}
		for (int i = 0; i < length + 1; i++) {
			healthSquares.get(i).setIsSubmerged(submerged);
		}
	}

}
