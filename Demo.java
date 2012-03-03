package my.halferty.isometricterrain;

public class Demo {
	public static void main(String [] args) {
		System.setProperty("sun.java2d.ddscale", "true");
		System.setProperty("sun.java2d.accthreshold", "0");
		System.setProperty("sun.java2d.translaccel", "true");
	    System.setProperty("sun.java2d.d3d", "True");
	    System.setProperty("sun.java2d.ddforcevram", "True");
	    //System.setProperty("sun.java2d.trace", "timestamp,log,count, test");
	    
	    
		Terrain terrain = new Terrain(129);
		IsometricRenderWindow window = new IsometricRenderWindow(1800, 1000, terrain);
	}
}
