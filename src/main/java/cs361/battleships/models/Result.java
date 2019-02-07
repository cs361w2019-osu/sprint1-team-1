package cs361.battleships.models;

public class Result {
	private AttackStatus status;
	private Ship ship;
	private Square loc;

	public AttackStatus getResult() {
		return this.status;
	}

	public void setResult(AttackStatus result) {
		this.status = result;
	}

	public Ship getShip() {
		return this.ship;
	}

	public void setShip(Ship ship) {
		this.ship = ship;
	}

	public Square getLocation() {
		return this.loc;
	}

	public void setLocation(Square square) {
		this.loc = square;
	}
}