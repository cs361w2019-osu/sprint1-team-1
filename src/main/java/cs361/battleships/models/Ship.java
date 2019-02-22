package cs361.battleships.models;

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
		System.out.println("setting up ship");
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


	public boolean isStillAlive(){
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
		AttackStatus res = AttackStatus.MISS;
		for(HealthSquare hs : healthSquares){
			if(attack.getLocation().isEqual(hs) && hs.getHealth() == 2){
				hs.setHealth(1);
				res = AttackStatus.HITARMR;
			}
			else if(attack.getLocation().isEqual(hs) && hs.getHealth() == 1 && hs.isCaptain()){
				hs.setHealth(0);
				alive = false;
				res = AttackStatus.SUNK;
			}
			else if(attack.getLocation().isEqual(hs) && hs.getHealth() == 1){
				hs.setHealth(0);
				res = AttackStatus.HIT;
			}
		}

		if(!isStillAlive())
			return AttackStatus.SUNK;

		return res;
	}


}
