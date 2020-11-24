import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.almarima.tablut.application.domain.BoardState;
import it.unibo.almarima.tablut.application.domain.Coordinates;
import it.unibo.almarima.tablut.application.domain.Move;
import it.unibo.almarima.tablut.external.State;
import it.unibo.almarima.tablut.external.State.Pawn;
import it.unibo.almarima.tablut.external.StateTablut;


public class BoardStateTest {

    private static State s;
    private static BoardState b;
    
    @BeforeAll
    public static void init(){
        s = new StateTablut();
    }

    @BeforeEach
    public void boardInit(){
        b = new BoardState(s);
    }

    @Test
    public void creationTest() throws UnknownHostException, IOException {
        assertEquals(b.getPawnAt(Coordinates.get(4, 4)), Pawn.KING);
        assertNotEquals(b.getPawnAt(Coordinates.get(0, 0)), Pawn.KING);
    }

    @Test
    public void creationTest2(){
        assertEquals(9, b.getPlayerPieceCoordinates().size());
        assertEquals(16, b.getOpponentPieceCoordinates().size());
        assertEquals(b.getNumberPlayerPieces(BoardState.WHITE), b.getPlayerPieceCoordinates().size());
        assertEquals(b.getNumberPlayerPieces(BoardState.BLACK), b.getOpponentPieceCoordinates().size());

    }

    @Test
    public void creationTest3(){
        assertEquals(Coordinates.get(4,4), b.getKingPosition());
        assertTrue(Coordinates.isCenter(b.getKingPosition()));
        assertTrue(b.getPlayerPieceCoordinates().contains(b.getKingPosition()));
    }

    @Test
    public void cloneTest(){
        BoardState cl = (BoardState) b.clone();
        assertTrue(cl.equals(b));
    }

    @Test
    public void cloneTest2(){
        BoardState cl= (BoardState) b.clone();
        BoardState cl2 = (BoardState) cl.clone();
        assertTrue(cl2.clone().equals(b));
    }


    @Test
    public void processMoveTest() {
        
    }

    public void updateWinner() {

    }

    public void testGetAllLegalMoves() {

    }

    public void testPlayerHasALegalMove() {

    }

    public void testGetLegalMovesForPosition() {

    }

    public void testGetLegalCoordsInDirection() {

    }

    public void testCanCaptureWithCoord() {

    }

    public void testGetPlayerPieceCoordinates() {

    }

    public void testGetOpponentPieceCoordinates() {

    }

    public void testGetPlayerCoordSet() {

    }

    public void testIsLegal() {

    }

    public void testGetPawnAt() {

    }

    public void testFromTurnPlayerToChar() {

    }

    public void testTurnPlayerCanMoveFrom() {

    }

    public void testIsOpponentPieceAt() {

    }

    public void testCoordIsEmpty() {

    }

    public void testGetOpponent() {

    }

    public void testGetNumberPlayerPieces() {

    }

    public void testGetKingPosition() {

    }

    @Test
    public void testCitadelRules() {
        BoardState b1 = new BoardState(new StateTablut());
        BoardState b2 = new BoardState(new StateTablut());
        BoardState b3 = new BoardState(new StateTablut());
        BoardState b4 = new BoardState(new StateTablut());
        BoardState b5 = new BoardState(new StateTablut());
        BoardState b6 = new BoardState(new StateTablut());

        /**
         * Testing whether non black can move from into citadels
         */
        assertTrue(b1.citadelRules(Coordinates.get(4, 5), Coordinates.get(5, 5)));
        b1.processMove(new Move(Coordinates.get(4, 5), Coordinates.get(5, 5), 1));
       
        assertTrue(b1.citadelRules(Coordinates.get(4, 5), Coordinates.get(5, 5)));
        b1.processMove(new Move(Coordinates.get(4, 1), Coordinates.get(3, 1), 0));
        
        assertFalse(b1.citadelRules(Coordinates.get(4, 3), Coordinates.get(4, 1)));
        assertThrows(IllegalArgumentException.class, () -> {
            b1.processMove(new Move(Coordinates.get(4, 3), Coordinates.get(4, 1), 1));
        });


        /**
         * Testing whether black can move outside citadels
         */
        b2.processMove(new Move(Coordinates.get(4, 3), Coordinates.get(3, 3), 1));
        assertTrue(b2.citadelRules(Coordinates.get(4, 1), Coordinates.get(3, 1)));

        /**
         * Testing whether black can move back into citadels
         */
        b3.processMove(new Move(Coordinates.get(4, 3), Coordinates.get(3, 3), 1));
        b3.processMove(new Move(Coordinates.get(3, 0), Coordinates.get(2, 0), 0));
        b3.processMove(new Move(Coordinates.get(3, 3), Coordinates.get(4, 3), 1));
        assertFalse(b3.citadelRules(Coordinates.get(2, 0), Coordinates.get(3, 0)));

        /**
         * Testing whether black can move inside its own citadels
         */
        b4.processMove(new Move(Coordinates.get(4, 3), Coordinates.get(3, 3), 1));
        b4.processMove(new Move(Coordinates.get(3, 0), Coordinates.get(2, 0), 0));
        b4.processMove(new Move(Coordinates.get(3, 3), Coordinates.get(4, 3), 1));
        assertTrue(b4.citadelRules(Coordinates.get(4, 0), Coordinates.get(3, 0)));

        /**
         * Testing whether black can move to other citadels
         */
        b5.processMove(new Move(Coordinates.get(3, 4), Coordinates.get(3, 3), 1));
        b5.processMove(new Move(Coordinates.get(0, 3), Coordinates.get(1, 3), 0));
        b5.processMove(new Move(Coordinates.get(3, 3), Coordinates.get(2, 3), 1));
        b5.processMove(new Move(Coordinates.get(3, 8), Coordinates.get(3, 7), 0));
        b5.processMove(new Move(Coordinates.get(2, 3), Coordinates.get(1, 3), 1));
        assertFalse(b5.citadelRules(Coordinates.get(3, 0), Coordinates.get(3, 8)));
    }

    @Test
    public void testGetRandomMove() {
        BoardState bs = new BoardState(new StateTablut());
        assertTrue(bs.getAllLegalMoves().stream().anyMatch(i -> bs.getRandomMove().equals(i)));
    }




    
}
