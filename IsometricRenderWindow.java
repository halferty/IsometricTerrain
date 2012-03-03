package my.halferty.isometricterrain;

import javax.swing.JFrame;

public class IsometricRenderWindow extends JFrame {

	public IsometricRenderWindow(int width, int height, Terrain terrain) {
		setTitle("Isometric Terrain Demo");
		setSize(width, height);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(new IsometricView(width, height, terrain));
	}
	
}
