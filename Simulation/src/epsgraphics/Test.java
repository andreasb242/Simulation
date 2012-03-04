package epsgraphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.FileOutputStream;
import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException {
		FileOutputStream out = new FileOutputStream("test.eps");
		Graphics2D g = new EpsGraphics("Test.eps", out, 0, 0, 200, 200, ColorMode.COLOR_RGB);
		g.setColor(Color.black);

		// Line thickness 2.
		g.setStroke(new BasicStroke(2.0f));

		// Draw a line.
		g.drawLine(10, 10, 50, 10);

		// Fill a rectangle in blue
		g.setColor(Color.blue);
		g.fillRect(10, 0, 20, 20);

		// Get the EPS output.
		String output = g.toString();

		out.close();
		
		System.out.println(output);
	}
}
