package my.halferty.isometricterrain;

import java.awt.Button;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class IsometricRenderWindow extends JFrame {

	public IsometricRenderWindow(int width, int height, Terrain terrain) {
		
		setTitle("Isometric Terrain Demo");
		setSize(width, height);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final IsometricView viewCanvas = new IsometricView(width, height, terrain);
		viewCanvas.setRenderer(IsometricView.RenderStyle.FILLED);
		viewCanvas.setShader(IsometricView.ShaderStyle.SLOPE);
		setLayout(new FlowLayout());
		
		final String[] renderOptions = {"Filled", "Wireframe", "Both"};
		JComboBox renderSelect = new JComboBox(renderOptions);
		renderSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewCanvas.rendererChanged(renderOptions[((JComboBox)e.getSource()).getSelectedIndex()]);
			}
		});
		
		final String[] shaderOptions = {"Slope", "Height", "Flat"};
		JComboBox shaderSelect = new JComboBox(shaderOptions);
		shaderSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewCanvas.shaderChanged(shaderOptions[((JComboBox)e.getSource()).getSelectedIndex()]);
			}
		});
		
		JPanel controlsPanel = new JPanel();
		controlsPanel.add(renderSelect);
		controlsPanel.add(shaderSelect);
		controlsPanel.add(new Button("hi"));
		
		add(controlsPanel);
		add(viewCanvas);
		
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
	}
}
