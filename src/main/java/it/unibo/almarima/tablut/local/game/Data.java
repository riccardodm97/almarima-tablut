package it.unibo.almarima.tablut.local.game;

import it.unibo.almarima.tablut.application.domain.Move;
import it.unibo.almarima.tablut.application.domain.Valuation;

/*this class will be ONLY used for testing purposes
  it keeps data that will be logged in a file*/
  
public class Data {

    private Move m;
    private Valuation v;
    private int depth;
    private int heuristics;
    private int prunings;
    private int moves;
	
	public Data(Move m, Valuation v,int iterDepth, int heuristics, int prunings, int moves) {
        this.m=m;
        this.v=v;
        this.depth=iterDepth;
        this.heuristics = heuristics;
        this.prunings = prunings;
        this.moves = moves;
    }

    public Move getMove() {
        return m;
    }

    public void setMove(Move m) {
        this.m = m;
    }

    public Valuation getValuation() {
        return v;
    }

    public void setValuation(Valuation v) {
        this.v = v;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getHeuristics() {
        return this.heuristics;
    }

    public int getPrunings() {
        return this.prunings;
    }

    public int getMoves() {
        return this.moves;
    }
    
    
    
}