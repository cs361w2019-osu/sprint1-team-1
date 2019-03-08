package cs361.battleships.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTest {

    @Test
    public void testInvalidPlacement() {
        Board board = new Board();
        assertFalse(board.placeShip(new Ship("MINESWEEPER"), 11, 'C', true));
        assertFalse(board.placeShip(new Ship("MINESWEEPER"), 1, 'J', false));
    }


    @Test
    public void testShipInBoard() {
        Board board = new Board();
        Ship placedShip = new Ship("MINESWEEPER");
        board.placeShip(placedShip, 1, 'A', true);
        assertTrue(board.getShips().size() == 1);
    }

    @Test
    public void testSquaresEqual() {
        Square square1 = new Square(2, 'B');
        Square square2 = new Square(2, 'B');
        Square square3 = new Square(1, 'A');
        assertTrue(square1.isEqual(square2));
        assertTrue(square2.isEqual(square1));
        assertFalse(square1.isEqual(square3));
    }

    @Test
    public void testSquareSetters() {
        Square square = new Square();
        char col = 'A';
        int row = 1;

        square.setColumn(col);
        square.setRow(row);

        assertTrue(square.getColumn() == col);
        assertTrue(square.getRow() == row);
    }

    @Test
    public void testShipOccupiesSpace() {
        Board board = new Board();
        Square occupiedSquare = new Square(1, 'A');
        Ship placedShip = new Ship("MINESWEEPER");
        board.placeShip(placedShip, occupiedSquare.getRow(), occupiedSquare.getColumn(), true);
        assertTrue(occupiedSquare.isEqual(board.getShips().get(0).getOccupiedSquares().get(0)));
    }

    @Test
    public void testShipHasLength() {
        Ship minesweeper = new Ship("MINESWEEPER");
        Ship destroyer = new Ship("DESTROYER");
        Ship battleship = new Ship("BATTLESHIP");
        assertTrue(minesweeper.getLength() == 2);
        assertTrue(destroyer.getLength() == 3);
        assertTrue(battleship.getLength() == 4);
    }

    @Test
    public void testShipOccupiesMultipleSpaces() {
        Board board = new Board();
        Ship minesweeper = new Ship("MINESWEEPER");
        Ship destroyer = new Ship("DESTROYER");
        Ship battleship = new Ship("BATTLESHIP");

        board.placeShip(minesweeper, 1, 'A', true);
        board.placeShip(destroyer, 1, 'B', true);
        board.placeShip(battleship, 1, 'C', true);

        assertTrue(board.getShips().get(0).getOccupiedSquares().get(1).isEqual(new Square(2, 'A')));
        assertTrue(board.getShips().get(1).getOccupiedSquares().get(2).isEqual(new Square(3, 'B')));
        assertTrue(board.getShips().get(2).getOccupiedSquares().get(3).isEqual(new Square(4, 'C')));
    }

    @Test
    public void testShipDirection() {
        Board board = new Board();
        Ship minesweeper = new Ship("MINESWEEPER");
        Ship destroyer = new Ship("DESTROYER");

        board.placeShip(minesweeper, 1, 'A', true);
        board.placeShip(destroyer, 2, 'B', false);

        assertTrue(board.getShips().get(0).getOccupiedSquares().get(1).isEqual(new Square(2, 'A')));
        assertTrue(board.getShips().get(1).getOccupiedSquares().get(2).isEqual(new Square(2, 'D')));
    }

    @Test
    public void testShipsOverlap() {
        Board board = new Board();
        Ship minesweeper = new Ship("MINESWEEPER");
        Ship destroyer = new Ship("DESTROYER");
        Ship battleship = new Ship("BATTLESHIP");
        Ship submarine = new Ship("SUBMARINE");

        assertTrue(board.placeShip(minesweeper, 1, 'B', true));
        assertFalse(board.placeShip(destroyer, 1, 'B', false));
        assertFalse(board.placeShip(battleship, 1, 'A', false));
        assertTrue(board.placeShip(submarine, 1, 'B', true));
    }

    @Test
    public void testHitShip() {
        Board board = new Board();
        Ship minesweeper = new Ship("MINESWEEPER");
        board.placeShip(minesweeper, 1, 'B', false);

        Result result = board.attack(1, 'C');
        assertTrue(result.getResult() == AttackStatus.HIT);
    }

    @Test
    public void testHitShipArmr() {
        Board board = new Board();
        Ship destroyer = new Ship("DESTROYER");
        board.placeShip(destroyer, 1, 'B', false);

        Result result = board.attack(1, 'C');
        assertTrue(result.getResult() == AttackStatus.HITARMR);
    }

    @Test
    public void testSubmergedSub() {
        Board board = new Board();
        Ship submarine = new Ship("SUBMARINE");
        board.placeShip(submarine, 2, 'B', false);

        Result result;
        result = board.attack(2, 'B');

        System.out.println(result.getResult());
        assertTrue(result.getResult() == AttackStatus.MISS_SUB);

        board.getShips().get(0).setSubmerged(false);
        result = board.attack(2, 'B');

        System.out.println(result.getResult());
        assertTrue(result.getResult() == AttackStatus.HIT);
    }

    @Test
    public void testHealthSquares() {
        Board board = new Board();
        Ship minesweeper = new Ship("MINESWEEPER");
        board.placeShip(minesweeper, 2, 'B', false);

        Ship ship = board.getShips().get(0);

        assertTrue(ship.stillAlive());
        assertTrue(ship.isAlive());

        board.attack(2, 'B');
        board.attack(2,'C');

        ship = board.getShips().get(0);

        assertFalse(ship.stillAlive());
    }

    @Test
    public void testHealthSquareConstructor() {
        int health = 2;
        HealthSquare hs = new HealthSquare(health, false);

        assertTrue(hs.getHealth() == health);

        int row = 1;
        char col = 'A';
        hs = new HealthSquare(row, col, health, false);
        assertTrue(hs.getHealth() == health);
        assertTrue(hs.getColumn() == col);
        assertTrue(hs.getRow() == row);

        hs.setCaptain(true);
        assertTrue(hs.isisCaptain());
    }

    @Test
    public void testShipMisc() {
        Ship ship = new Ship();

        assertTrue(ship.isAlive());
        assertFalse(ship.isShipVertical());

        ship.sinkShip();

        assertFalse(ship.isAlive());
    }


    @Test
    public void testMissShip() {
        Board board = new Board();
        Ship destroyer = new Ship("MINESWEEPER");
        board.placeShip(destroyer, 1, 'B', false);

        Result result1 = board.attack(1, 'A');
        Result result2 = board.attack(1, 'D');
        assertTrue(result1.getResult() == AttackStatus.MISS && result2.getResult() == AttackStatus.MISS);
    }

    @Test
    public void testCaptSinkShip() {
        Board board = new Board();
        Ship minesweeper = new Ship("MINESWEEPER");
        Ship destroyer = new Ship("DESTROYER");
        board.placeShip(minesweeper, 1, 'B', false);
        board.placeShip(destroyer, 3, 'A', false);

        Result result;
        result = board.attack(1, 'B');

        assertTrue(result.getResult() == AttackStatus.SUNK);

        Ship submarine = new Ship("SUBMARINE");
        board.placeShip(submarine, 5, 'A', false); // captain square should be at 5 'D'
        board.getShips().get(2).setSubmerged(false); // unsubmerge sub so it can be hit

        result = board.attack(5, 'D');
        System.out.println(result.getResult());
        assertTrue(result.getResult() == AttackStatus.HITARMR);

        result = board.attack(5, 'D');
        assertTrue(result.getResult() == AttackStatus.SUNK);
    }


    @Test
    public void testSinkShip() {
        Board board = new Board();
        Ship minesweeper = new Ship("MINESWEEPER");
        Ship destroyer = new Ship("DESTROYER");
        board.placeShip(minesweeper, 1, 'B', false);
        board.placeShip(destroyer, 3, 'A', false);

        Result result;
        result = board.attack(3, 'A');
        result = board.attack(3, 'B');
        result = board.attack(3, 'C');
        result = board.attack(3, 'B');


        assertTrue(result.getResult() == AttackStatus.SUNK);
    }
    @Test
    public void testSinkCaptShipProt() {
        Board board = new Board();
        //Ship minesweeper = new Ship("MINESWEEPER");
        Ship destroyer = new Ship("DESTROYER");
        //board.placeShip(minesweeper, 1, 'B', false);
        board.placeShip(destroyer, 3, 'A', false);

        Result result;
        result = board.attack(3, 'A');
        result = board.attack(3, 'B');
        result = board.attack(3, 'C');

        assertTrue(result.getResult() == AttackStatus.HIT);
    }

    @Test
    public void testSonar() {
        Board board = new Board();
        Ship destroyer = new Ship("DESTROYER");
        Ship minesweeper = new Ship("MINESWEEPER");

        board.placeShip(minesweeper, 1, 'B', false);
        board.placeShip(destroyer, 3, 'A', false);
        board.placeSonar(3, 'B');

        Sonar sonar = board.getSonars().get(0);
        Ship ship1 = board.getShips().get(0);
        Ship ship2 = board.getShips().get(1);
        /*
        for (Square square : destroyer.getOccupiedSquares()) {
            assertTrue(sonar.getFoundShips().indexOf(square) != -1);
        }

        assertTrue(sonar.getFoundShips().indexOf(minesweeper.getOccupiedSquares().get(0)) != -1);

        */
        for(int i = 0; i < sonar.getFoundShips().size(); i++) {
            Square hit = sonar.getFoundShips().get(i);
            assertTrue(ship1.getOccupiedSquares().indexOf(hit) != -1
                    || ship2.getOccupiedSquares().indexOf(hit) != -1);
        }
    }

    @Test
    public void testSurrender() {
        Board board = new Board();
        Ship destroyer = new Ship("MINESWEEPER");
        board.placeShip(destroyer, 1, 'B', false);

        board.attack(1, 'C');
        Result result = board.attack(1, 'B');
        assertTrue(result.getResult() == AttackStatus.SURRENDER);
    }

    @Test
    public void testSunkShipAttackLogic() {
        Board board = new Board();
        Ship destroyer = new Ship("MINESWEEPER");
        board.placeShip(destroyer, 1, 'B', false);

        board.attack(1, 'C');
        board.attack(1, 'B');
        int num = board.getAttacks().size();
        System.out.println( num );
        assertTrue(num == 3);
    }

}
