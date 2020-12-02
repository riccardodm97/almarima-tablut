package it.unibo.almarima.tablut.local.control;

import java.util.Date;
import java.util.logging.*;

//import org.apache.commons.cli.*;

import it.unibo.almarima.tablut.external.Action;
import it.unibo.almarima.tablut.external.State;
import it.unibo.almarima.tablut.external.StateTablut;
import it.unibo.almarima.tablut.external.State.Turn;
import it.unibo.almarima.tablut.local.config.ServerConfig;
import it.unibo.almarima.tablut.local.exceptions.AgentStoppedException;
import it.unibo.almarima.tablut.local.exceptions.GameFinishedException;
import it.unibo.almarima.tablut.local.game.Game;
import it.unibo.almarima.tablut.local.game.GameAshtonTablut;
import it.unibo.almarima.tablut.local.gui.Gui;
import it.unibo.almarima.tablut.local.logging.TablutLogger;

public class OfflineServer implements Runnable, OfflineAgent {

	private Shared whiteShared;
	private Shared blackShared;

	/**
	 * Timeout for waiting for a client to connect
	 */
	public static int connectionTimeout = 300;

	/**
	 * State of the game
	 */
	private State state;
	/**
	 * Whether the gui must be enabled or not
	 */
	private boolean enableGui;

	/**
	 * Action chosen by a player
	 */
	private Action move;
	/**
	 * Errors allowed
	 */
	private int errors;
	/**
	 * Repeated positions allowed
	 */
	private int repeated;

	/**
	 * Counter for the errors of the black player
	 */
	private int blackErrors;
	/**
	 * Counter for the errors of the white player
	 */
	private int whiteErrors;

	private int cacheSize;

	private Game game;
	private Gui theGui;
	/**
	 * Integer that represents the game type
	 */
	private int gameC;

	public OfflineServer(Shared whiteShared, Shared blackShared, int timeout, int cacheSize, int numErrors, int repeated,
			boolean gui) {
		this.whiteShared = whiteShared;
		this.blackShared = blackShared;
		this.enableGui = gui;
		this.errors = numErrors;
		this.cacheSize = cacheSize;
	}

	public OfflineServer(Shared whiteShared, Shared blackShared, ServerConfig config) {
		this(whiteShared, blackShared, config.getTimeout(), config.getCacheSize(), config.getMaxErrors(), config.getRepeated(), config.getGui());
	}

	public void initializeGUI(State state) {
		this.theGui = new Gui(this.gameC);
		this.theGui.update(state);
	}

	/**
	 * Not used
	 */
	public void run() { }

	/**
	 * This method starts the proper game. It waits the connections from 2 clients,
	 * check the move and update the state. There is a timeout that interrupts games
	 * that last too much
	 */
	public void execute() throws AgentStoppedException {
		/**
		 * Number of hours that a game can last before the timeout
		 */
		int hourlimit = 10;
		/**
		 * Endgame state reached?
		 */
		boolean endgame = false;

		Logger loggSys = TablutLogger.get(TablutLogger.LogSpace.SYSTEM);
		Logger loggGame = TablutLogger.get(TablutLogger.LogSpace.GAME);

		Date starttime = new Date();

		System.out.println("Waiting for connections...");

		String whiteName = "WP";
		String blackName = "BP";

		synchronized (this.whiteShared) {
			this.whiteShared.reset();
			this.whiteShared.notify();
			System.out.println("S: Notify 1 (W) [Server started]");
		}

		synchronized (this.blackShared) {
			this.blackShared.reset();
			this.blackShared.notify();
			System.out.println("S: Notify 1 (B) [Server started]");
		}

		// NAME READING
		synchronized (this.whiteShared) {
			while (this.whiteShared.getName() == "") {
				try {
					System.out.println("S: Wait 2 (W) [Player name]");
					this.whiteShared.wait();
					System.out.println("S: Wake 2 (W) [Player name]");
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new AgentStoppedException();
				}
			}
			whiteName = this.sanitizeString(this.whiteShared.getName());
			this.whiteShared.setName("");
		}
		System.out.println("White player name:\t" + whiteName);
		loggSys.fine("White player name:\t" + whiteName);

		// NAME READING
		synchronized (this.blackShared) {
			while (this.blackShared.getName() == "") {
				try {
					System.out.println("S: Wait 2 (B) [Player name]");
					this.blackShared.wait();
					System.out.println("S: Wake 2 (B) [Player name]");
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new AgentStoppedException();
				}
			}
			blackName = this.sanitizeString(this.blackShared.getName());
			this.blackShared.setName("");
		}
		System.out.println("Black player name:\t" + blackName);
		loggSys.fine("Black player name:\t" + blackName);

		state = new StateTablut();
		state.setTurn(State.Turn.WHITE);

		this.game = new GameAshtonTablut(state, repeated, this.cacheSize, loggGame, whiteName, blackName);

		if (this.enableGui) {
			this.initializeGUI(state);
		}
		System.out.println("Clients connected..");

		synchronized (this.whiteShared) {
			System.out.println("S: Notify 3 (W) [First move processed]");
			this.whiteShared.setMoveRequired(true);
			this.whiteShared.setState(state);
			this.whiteShared.notify();
		}

		synchronized (this.blackShared) {
			System.out.println("S: Notify 3 (B) [First move processed]");
			this.blackShared.setState(state);
			this.blackShared.notify();
		}

		loggSys.fine("Invio messaggio ai giocatori");
		if (enableGui) {
			theGui.update(state);
		}

		// GAME CYCLE
		int turns = 0;
		while (!endgame) {
			turns++;
			// RECEIVE MOVE

			// System.out.println("State: \n"+state.toString());
			// System.out.println("Waiting for " + state.getTurn() + "...");
				if (this.state.getTurn() == Turn.WHITE) {
					synchronized (this.whiteShared) {
						while (this.whiteShared.getMove() == null) {
							try {
								System.out.println("S: Wait 4 (W) [Player move]");
								this.whiteShared.wait();
								System.out.println("S: Wake 4 (W) [Player move]");
							} catch (InterruptedException e) {
								e.printStackTrace();
								throw new AgentStoppedException();
							}
						}
						move = this.whiteShared.getMove();
						this.whiteShared.setMove(null);
					}
				}

				if (this.state.getTurn() == Turn.BLACK) {
					synchronized (this.blackShared) {
						while (this.blackShared.getMove() == null) {
							try {
								System.out.println("S: Wait 4 (B) [Player move]");
								this.blackShared.wait();
								System.out.println("S: Wake 4 (B) [Player move]");
							} catch (InterruptedException e) {
								e.printStackTrace();
								throw new AgentStoppedException();
							}
						}
						move = this.blackShared.getMove();
						this.blackShared.setMove(null);
					}
				}
			loggSys.fine("Move received.\t" + move.toString());
			move.setTurn(state.getTurn());
			// System.out.println("Suggested move: " + move.toString());

			try {
				// aggiorna tutto e determina anche eventuali fine partita
				state = this.game.checkMove(state, move);
			} catch (Exception e) {
				// exception means error, therefore increase the error counters
				if (state.getTurn().equalsTurn("B")) {
					this.blackErrors++;

					if (this.blackErrors > errors) {
						// System.out.println("TOO MANY ERRORS FOR BLACK PLAYER; PLAYER WHITE WIN!");
						e.printStackTrace();
						loggSys.warning("Chiusura sistema per troppi errori giocatore nero");
						throw new AgentStoppedException();
					} else {
						// System.out.println("Error for black player...");
					}
				}
				if (state.getTurn().equalsTurn("W")) {
					this.whiteErrors++;
					if (this.whiteErrors > errors) {
						// System.out.println("TOO MANY ERRORS FOR WHITE PLAYER; PLAYER BLACK WIN!");
						e.printStackTrace();
						loggSys.warning("Chiusura sistema per troppi errori giocatore bianco");
						throw new AgentStoppedException();
					} else {
						// System.out.println("Error for white player...");
					}
				}
			}

			// GAME TOO LONG, TIMEOUT
			Date ti = new Date();
			long hoursoccurred = (ti.getTime() - starttime.getTime()) / 60 / 60 / 1000;
			if (hoursoccurred > hourlimit) {
				// System.out.println("TIMEOUT! END OF THE GAME...");
				loggSys.warning("Chiusura programma per timeout di " + hourlimit + " ore");
				state.setTurn(Turn.DRAW);
			}

			// SEND STATE TO PLAYERS
			synchronized(this.whiteShared) {
				if (state.getTurn() == Turn.WHITE) {
					this.whiteShared.setMoveRequired(true);
				}
				this.whiteShared.incrementTurnNumber();
				this.whiteShared.setState(state);
				this.whiteShared.notify();
				System.out.println("S: Notify 3 (W) [Move processed]");
			}

			synchronized(this.blackShared) {
				if (state.getTurn() == Turn.BLACK) {
					this.blackShared.setMoveRequired(true);
				}
				this.blackShared.incrementTurnNumber();
				this.blackShared.setState(state);
				this.blackShared.notify();
				System.out.println("S: Notify 3 (B) [Move processed]");
			}
			loggSys.fine("Invio messaggio ai client");
			if (enableGui) {
				theGui.update(state);
			}

			switch (state.getTurn()) {
			case WHITE:
				break;
			case BLACK:
				break;
			case BLACKWIN:
				this.game.endGame(state);
				System.out.println("END OF THE GAME");
				System.out.println("RESULT: PLAYER BLACK WIN");
				endgame = true;
				break;
			case WHITEWIN:
				this.game.endGame(state);
				System.out.println("END OF THE GAME");
				System.out.println("RESULT: PLAYER WHITE WIN");
				endgame = true;
				break;
			case DRAW:
				this.game.endGame(state);
				System.out.println("END OF THE GAME");
				System.out.println("RESULT: DRAW");
				endgame = true;
				break;
			default:
				loggSys.warning("Chiusura sistema");
				throw new GameFinishedException(state.getTurn(), whiteShared.getRandomMoves(), blackShared.getRandomMoves(), turns);
			}

		}

		synchronized (this.whiteShared) {
			this.whiteShared.setGameOver(true);
			this.whiteShared.notify();
			System.out.println("S: Notify 5 (W) [Player won]");
		}

		synchronized (this.blackShared) {
			this.blackShared.setGameOver(true);
			this.blackShared.notify();
			System.out.println("S: Notify 5 (B) [Player won]");
		}

		throw new GameFinishedException(state.getTurn(), whiteShared.getRandomMoves(), blackShared.getRandomMoves(), turns);
	}
	
	private String sanitizeString(String name) {
		String temp = "";
		for (int i = 0; i < name.length() && i < 10; i++) {
			char c = name.charAt(i);
			if (Character.isAlphabetic(c) || Character.isDigit(c))
				temp += c;
		}
		return temp;
	}

}