package my.halferty.isometricterrain;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;


public class IsometricView extends Canvas implements MouseListener, MouseMotionListener, MouseWheelListener {

	// Constants
	private final int SQUARE_RENDER_SIZE = 4;
	private static final int MAX_ZOOM_SPEED = 2;

	// Variables
	private Terrain terrain;
	private Matrix4 iso;
	private float[] view = new float[3];
	private Point offscreen_draw_location = new Point(0, 0);
	private Point mouse_drag_offset = new Point(0, 0);
	private int zoom_level = 20;
	private Image buffer_image;
	private Graphics buffer_graphics;
	private ArrayList<Polygon> polys = new ArrayList<Polygon>();
	private int[] fps_samples = new int[100];
	
	// Render settings
	public enum RenderStyle { BOTH, WIREFRAME, FILLED }
	public enum ShaderStyle { FLAT, SLOPE, HEIGHT }
	
	private RenderStyle renderStyle = RenderStyle.BOTH;
	private ShaderStyle shaderStyle = ShaderStyle.SLOPE;
	
	// Constructor
	public IsometricView(int width, int height, Terrain terrain) {
		this.terrain = terrain;
		setLocation(20, 20);
		this.setSize(width, height);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		CreateIsometricView();
		offscreen_draw_location.setLocation(width * -1, height * -1);
	}

	private void CreateIsometricView() {
		iso = createIsometricMatrix(zoom_level, getWidth(), getHeight());
	}

	public void paint(Graphics g) {
		buffer_image = createImage(getWidth(), getHeight());
		buffer_graphics = buffer_image.getGraphics();
		int polygons_drawn = 0;
		
		for (int y = 0; y < terrain.getSize()-1; y++)
		{
			for (int x = 0; x < terrain.getSize()-1; x++)
			{
				float[] alts = {terrain.getAlt(x, y), terrain.getAlt(x, y+1), terrain.getAlt(x+1, y+1), terrain.getAlt(x+1, y)};

				float[][] model = {
						{ x - 0.5f, alts[0] * -0.2f, y - 0.5f, },	// Top corner
						{ x - 0.5f, alts[1] * -0.2f, y + 0.5f, },	// Left
						{ x + 0.5f, alts[2] * -0.2f, y + 0.5f, },	// Bottom
						{ x + 0.5f, alts[3] * -0.2f, y - 0.5f, }};	// Right
				Polygon p = new Polygon();
				for (int k = 0; k < 4; k++)
				{
					iso.transform(model[k], view);
					p.addPoint((int) view[0] + offscreen_draw_location.x, (int) view[1] + offscreen_draw_location.y);
					
					// Only draw on-screen polys.
					if (
							(view[0] > 1-offscreen_draw_location.x - SQUARE_RENDER_SIZE * 2) &&
							(view[0] < 1-offscreen_draw_location.x + getWidth() + SQUARE_RENDER_SIZE * 2) &&
							(view[1] > 1-offscreen_draw_location.y - SQUARE_RENDER_SIZE * 2) &&
							(view[1] < 1-offscreen_draw_location.y + getHeight() + SQUARE_RENDER_SIZE * 2)
							) {

						if (renderStyle == RenderStyle.FILLED || renderStyle == RenderStyle.BOTH) {
							polygons_drawn++;
							
							if (terrain.getType(x, y) == Square.Type.LAND) {
								int greenShade;
								if (shaderStyle == ShaderStyle.SLOPE) {
									// Base the shade of green on the slope.
									greenShade = (int) (Math.abs(terrain.getSlope(x, y) *128));
									if (greenShade > 255) greenShade = 255;
								} else if (shaderStyle == ShaderStyle.HEIGHT) {
									// Base the shade of green on the height.
									greenShade = (int) (Math.abs(terrain.getAlt(x, y)));
									if (greenShade > 255) greenShade = 255;
								} else {
									greenShade = 255;
								}
								buffer_graphics.setColor(new Color(0, greenShade, 0));
								
							} else if (terrain.getType(x, y) == Square.Type.WATER) {
								buffer_graphics.setColor(new Color(0, 0, 255));
							}
							buffer_graphics.fillPolygon(p);
						}
						if (renderStyle == RenderStyle.WIREFRAME || renderStyle == RenderStyle.BOTH) {
							polygons_drawn++;
							buffer_graphics.setColor(new Color(0, 0, 0));
							buffer_graphics.drawPolygon(p);
						}
					}
				}
			}
		}
		
		buffer_graphics.setColor(new Color(0, 0, 0));
		buffer_graphics.clearRect(98, 47, 190, 20);
		buffer_graphics.drawString("drawing " + polygons_drawn + " polys at ", 100, 60);
		g.drawImage(buffer_image, 0, 0, this);
	}

	public void update(Graphics g) {
		paint(g);
	}

	private static Matrix4 createIsometricMatrix(int scale, int x, int y)
	{
		Matrix4 iso = new Matrix4();
		iso.m00 = iso.m33 = 1.0f * scale;
		iso.m10 = iso.m12 = 0.5f * scale;
		iso.m11 = 2.0f * scale;
		iso.m02 = -1.0f * scale;
		iso.m21 = -0.05f * scale;
		iso.m03 = x;
		iso.m13 = y;
		return iso;
	}

	private static class Matrix4
	{
		public float m00, m01, m02, m03;
		public float m10, m11, m12, m13;
		public float m20, m21, m22, m23;
		public float m30, m31, m32, m33;

		public void transform(float[] in, float[] out)
		{
			float x = in[0];
			float y = in[1];
			float z = in[2];

			out[0] = m00 * x + m01 * y + m02 * z + m03;
			out[1] = m10 * x + m11 * y + m12 * z + m13;
			out[2] = m20 * x + m21 * y + m22 * z + m23;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouse_drag_offset.x = e.getPoint().x - offscreen_draw_location.x;
		mouse_drag_offset.y = e.getPoint().y - offscreen_draw_location.y;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		offscreen_draw_location.x = arg0.getPoint().x - mouse_drag_offset.x;
		offscreen_draw_location.y = arg0.getPoint().y - mouse_drag_offset.y;
		paint(this.getGraphics());
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		int scrollwheelClicks = arg0.getWheelRotation();
		if (scrollwheelClicks > MAX_ZOOM_SPEED) {
			scrollwheelClicks = MAX_ZOOM_SPEED;
		}
		if (scrollwheelClicks < -MAX_ZOOM_SPEED) {
			scrollwheelClicks = -MAX_ZOOM_SPEED;
		}
		zoom_level -= scrollwheelClicks;
		if (zoom_level < 10) zoom_level = 10;
		if (zoom_level > 30) zoom_level = 30;
		CreateIsometricView();
		paint(this.getGraphics());
	}

	public void rendererChanged(String option) {
		if (option == "Filled") {
			renderStyle = RenderStyle.FILLED;
		} else if (option == "Both") {
			renderStyle = RenderStyle.BOTH;
		} else if (option == "Wireframe") {
			renderStyle = RenderStyle.WIREFRAME;
		}
		paint(this.getGraphics());
	}

	public void shaderChanged(String option) {
		if (option == "Height") {
			shaderStyle = ShaderStyle.HEIGHT;
		} else if (option == "Flat") {
			shaderStyle = ShaderStyle.FLAT;
		} else if (option == "Slope") {
			shaderStyle = ShaderStyle.SLOPE;
		}
		paint(this.getGraphics());
	}
	
	public void setRenderer(RenderStyle style) {
		renderStyle = style;
	}
	
	public void setShader(ShaderStyle style) {
		shaderStyle = style;
	}
}
