package my.halferty.isometricterrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import my.halferty.isometricterrain.Square.Type;

public class Terrain {

	// Constants
	private float SEED = 50.0f;
	private float OFFSET = 32.0f;
	private float WATER_LEVEL = 40.0f;

	// Vars
	private int size;
	private List<List<Square>> squares = new ArrayList<List<Square>>();
	private Random r = new Random();

	public Terrain(int size)
	{
		this.size = size;

		// Initialize the lists
		for (int i = 0; i < size; i++) {
			List<Square> column = new ArrayList<Square>();
			for (int j = 0; j < size; j++) {
				column.add(new Square(Type.LAND, 0));
			}
			squares.add(column);
		}
		GenerateTerrain();
		BoxBlur(1);
		WaterLevel();
		BoxBlur(1);
		WaterLevel();
	}

	// Generate terrain using the Diamond-Square algorithm.
	private void GenerateTerrain() {
		setAlt(0, 0, SEED);
		setAlt(0, size-1, SEED);
		setAlt(size-1, 0, SEED);
		setAlt(size-1, size-1, SEED);
		Iterate(0, 0, size - 1, OFFSET);
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

	private void WaterLevel() {
		for (int i = 0; i < size - 1; i++) {
			for (int j = 0; j < size - 1; j++) {
				if (getAlt(i, j) < WATER_LEVEL) {
					setType(i, j, Type.WATER);
					setAlt(i, j, WATER_LEVEL);
				}
			}
		}
		for (int i = 0; i < size - 1; i++) {
			for (int j = 0; j < size - 1; j++) {
				
				if (getType(i, j) == Type.WATER) {
					if (i < size - 1) {
						setAlt(i + 1, j, WATER_LEVEL);
						if (j < size - 1) {
							setAlt(i + 1, j + 1, WATER_LEVEL);
						}
					}
					if (j < size - 1) {
						setAlt(i, j + 1, WATER_LEVEL);
					}
					setAlt(i, j, WATER_LEVEL);
				}
			}
		}
	}

	// Simple 3x3 box blur
	private void BoxBlur(int iterations) {
		for (int a = 0; a < iterations; a++) {
			for (int i = 1; i < size - 1; i++) {
				for (int j = 1; j < size - 1; j++) {
					float[][] box = new float[3][3];
					for (int k = -1; k < 2; k++) {
						for (int l = -1; l < 2; l++) {
							if (((i + k) > 0) && ((j + l) > 0) && ((i + k) < (size - 1)) && ((j + l) < (size - 1))) {
								box[k + 1][l + 1] = getAlt(i + k, j + l);
							}
						}
					}
					if (i == 1) {
						box[0][0] = box[1][0];
						box[0][1] = box[1][1];
						box[0][2] = box[1][2];
					}
					if (j == 1) {
						box[0][0] = box[0][1];
						box[1][0] = box[1][1];
						box[2][0] = box[2][1];
					}
					if (i == size - 2) {
						box[2][0] = box[1][0];
						box[2][1] = box[1][1];
						box[2][2] = box[1][2];
					}
					if (j == size - 2) {
						box[0][2] = box[0][1];
						box[1][2] = box[1][1];
						box[2][2] = box[2][1];
					}
					float total = 0.0f;
					for (int k = 0; k < 3; k++) {
						for (int l = 0; l < 3; l++) {
							total += box[k][l];
						}
					}
					setAlt(i, j, (total / 9.0f));
				}
			}
		}
	}

	// Accessors
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
		return alts[alts.length-1] / (alts[0] + 1);
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
