package my.halferty.isometricterrain;

public class Square {
	
	// Types
	public enum Type {
		LAND, WATER
	}
	
	// Variables
	private Type type;
	private float altitude;
	
	// Constructors
	public Square() {
		this.type = Type.WATER;
		this.altitude = 0.0f;
	}
	
	public Square(Type t, int a) {
		this.type = t;
		this.altitude = a;
	}
	
	// Getters and setters
	public Type getType() {
		return type;
	}
	
	public void setType(Type t) {
		this.type = t;
	}
	
	public float getAlt() {
		return altitude;
	}
	
	public void setAlt(float altitude) {
		this.altitude = altitude;
	}
	
	// For debugging
	@Override
	public String toString() {
		if (type == Type.LAND) {
			return String.valueOf((int)getAlt());
		} else if (type == Type.WATER) {
			return "w";
		} else {
			return "?";
		}
	}
}
