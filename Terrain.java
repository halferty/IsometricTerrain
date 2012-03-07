package my.halferty.isometricterrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import my.halferty.isometricterrain.Square.Type;

public class Terrain {

	// Constants
	private static final float DEFAULT_SEED = 50.0f;
	private static final float DEFAULT_OFFSET = 32.0f;
	private static final float DEFAULT_WATER_LEVEL = 40.0f;

	// Vars
	private int size;
	private float seed, offset, waterLevel;
	private List<List<Square>> squares = new ArrayList<List<Square>>();
	private Random r = new Random();

	public Terrain(int size)
	{
		this.size = size;
		this.seed = DEFAULT_SEED;
		this.offset = DEFAULT_OFFSET;
		this.waterLevel = DEFAULT_WATER_LEVEL;

		InitLists();
		GenerateTerrain();
	}
	
	public Terrain(int size, float seed, float offset, float waterLevel) {
		this.size = size;
		this.seed = seed;
		this.offset = offset;
		this.waterLevel = waterLevel;

		InitLists();
		GenerateTerrain();
	}
	
	private void InitLists() {
		for (int i = 0; i < size; i++) {
			List<Square> column = new ArrayList<Square>();
			for (int j = 0; j < size; j++) {
				column.add(new Square(Type.LAND, 0));
			}
			squares.add(column);
		}
	}

	// Generate terrain using the Diamond-Square algorithm.
	private void GenerateTerrain() {
		setAlt(0, 0, seed);
		setAlt(0, size-1, seed);
		setAlt(size-1, 0, seed);
		setAlt(size-1, size-1, seed);
		Iterate(0, 0, size - 1, offset);
	}

	private void Iterate(int x, int y, int size, float offset) {
		if (size < 2) {
			return;
		} else {
			// Square step
			float avg = (getAlt(x, y) +
					getAlt(x + size, y) +
					getAlt(x, y + size) +
					getAlt(x + size, y + size)) / 4.0f;
			RandomOffset((x + size / 2), (y + size / 2), avg, offset);

			// Diamond step
			RandomOffset(x, (y + size / 2), avg, offset);
			RandomOffset((x + size), (y + size / 2), avg, offset);
			RandomOffset((x + size / 2), y, avg, offset);
			RandomOffset((x + size / 2), (y + size), avg, offset);

			// Do next iteration
			Iterate(x, y, (size / 2), (offset / 2.0f));
			Iterate(x, y + size / 2, (size / 2), (offset / 2.0f));
			Iterate((x + size / 2), y, (size / 2), (offset / 2.0f));
			Iterate((x + size / 2), (y + size / 2), (size / 2), (offset / 2.0f));
		}
	}

	private void RandomOffset(int x, int y, float avg, float offset) {
		setAlt(x, y, (avg + (r.nextFloat() * 2 * offset) - offset));
	}

	
	// Levels out all points below the water level, and all squares that are
	// entirely below the water level are set to Square.Type.WATER
	public void levelWater() {
		for (int i = 0; i < size - 1; i++) {
			for (int j = 0; j < size - 1; j++) {
				if (getAlt(i, j) < waterLevel) {
					setType(i, j, Type.WATER);
					setAlt(i, j, waterLevel);
				}
			}
		}
		for (int i = 0; i < size - 1; i++) {
			for (int j = 0; j < size - 1; j++) {
				
				if (getType(i, j) == Type.WATER) {
					if (i < size - 1) {
						setAlt(i + 1, j, waterLevel);
						if (j < size - 1) {
							setAlt(i + 1, j + 1, waterLevel);
						}
					}
					if (j < size - 1) {
						setAlt(i, j + 1, waterLevel);
					}
					setAlt(i, j, waterLevel);
				}
			}
		}
	}

	// Simple 3x3 box blur
	public void applyBoxBlur(int iterations) {
		for (int a = 0; a < iterations; a++) {
			// Blur in the  direction
			for (int y = 0; y < size - 1; y++) {
				for (int x = 0; x < size - 1; x++) {
					if (x == 0) {
						setAlt(x, y, (2 * getAlt(x, y) + getAlt(x + 1, y)) / 3.0f);
					} else if (x == size - 1) {
						setAlt(x, y, (2 * getAlt(x, y) + getAlt(x - 1, y)) / 3.0f);
					} else {
						setAlt(x, y, (getAlt(x - 1, y) + getAlt(x, y) + getAlt(x + 1, y)) / 3.0f);
					}
				}
			}
			// Blur in the y direction
			for (int x = 0; x < size - 1; x++) {
				for (int y = 0; y < size - 1; y++) {
					if (y == 0) {
						setAlt(x, y, (2 * getAlt(x, y) + getAlt(x, y + 1)) / 3.0f);
					} else if (y == size - 1) {
						setAlt(x, y, (2 * getAlt(x, y) + getAlt(x, y - 1)) / 3.0f);
					} else {
						setAlt(x, y, (getAlt(x, y - 1) + getAlt(x, y) + getAlt(x, y + 1)) / 3.0f);
					}
				}
			}
		}
	}

	public int getSize() {
		return size;
	}

	public float getAlt(int x, int y) {
		return squares.get(x).get(y).getAlt();
	}

	public void setAlt(int x, int y, float a) {
		squares.get(x).get(y).setAlt(a);
	}

	public Type getType(int x, int y) {
		return squares.get(x).get(y).getType();
	}

	public void setType(int x, int y, Type t) {
		squares.get(x).get(y).setType(t);
	}	

	// Get the slope of a square
	public float getSlope(int x, int y) {
		float[] alts = {getAlt(x, y), getAlt(x, y + 1), getAlt(x + 1, y + 1), getAlt(x + 1, y)};
		Arrays.sort(alts);
		return Math.abs(alts[alts.length-1] - (alts[0] + 1));
	}

	// For debugging
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (List<Square> li: squares) {
			for (Square s: li) {
				sb.append(s.toString());
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
