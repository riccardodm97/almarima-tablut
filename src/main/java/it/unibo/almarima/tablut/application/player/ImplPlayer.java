package it.unibo.almarima.tablut.application.player;

import it.unibo.almarima.tablut.application.domain.BoardState;
import it.unibo.almarima.tablut.application.domain.Move;
import it.unibo.almarima.tablut.application.heuristics.*;
import it.unibo.almarima.tablut.application.player.MiniMaxTree.TimeLimitException;
import it.unibo.almarima.tablut.external.State;


public class ImplPlayer extends TablutPlayer{

	long timeLimit = (this.getTimeout()-5)*1000;
	
	Heuristic h;

    public ImplPlayer(int timeout, State.Turn role ) {

		super(timeout, role);
		h= new KingHeuristic();
    }
    
    /*return best move computed from player*/
    public  Move computeMove(){
        // keep track of time started and time limit
    	long startTime = System.currentTimeMillis();
    	long endTime = startTime + timeLimit;
    			
    	// start minimaxPruning algorithm with depth of 2
    	int iterDepth = 2;
    	Move chosenMove = boardState.getRandomMove();
		try {
			// if move is chosen without time limit reached, set it to chosenMove
			chosenMove = new MiniMaxTree(iterDepth, (BoardState) boardState.clone(), endTime, h).getBestMove();
        } catch (TimeLimitException e) {
            //System.out.println("Reached time limit while trying iterDepth " + iterDepth--);
		}
		
		// keep running minimax-pruning with higher depth as long as there is time left
    	while ((System.currentTimeMillis() - startTime) < timeLimit) {
			iterDepth++;
			try {
				// if move is chosen without time limit reached, set it to chosenMove
	    		Move newMove = new MiniMaxTree(iterDepth, (BoardState) boardState.clone(), endTime, h).getBestMove();
				chosenMove = newMove;
			} catch (TimeLimitException e) {
                //	System.out.println("Reached time limit while trying iterDepth " + iterDepth--);
			}

    	}

        return chosenMove;

    }

            
    
    

    

    
   

	


    

    
}
