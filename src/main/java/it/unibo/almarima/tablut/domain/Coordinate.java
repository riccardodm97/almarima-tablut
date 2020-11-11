package it.unibo.almarima.tablut.domain;

import java.security.InvalidParameterException;

/**
 * this class represents a couple of 2D coordinates inside the grid
 * 
 */
public class Coordinate {

	private int x;
	private int y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Coordinate(String position) {
		if (position.length() == 2) {
			this.x = Character.toLowerCase(position.charAt(0)) - 97;
			this.y = Integer.parseInt(position.charAt(1) + "") - 1;
		} else {
			throw new InvalidParameterException("the FROM and the TO string must have length=2");
		}
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public String toString() {
		return String.valueOf((char)(this.getY() + 65)) + (this.getY() + 1);
	}

}