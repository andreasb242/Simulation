package ch.zhaw.simulation.editor.flow.connector.flowarrow;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.sysintegration.SysintegrationFactory;

public class FlowArrowTest {
	public static void main(String[] args) throws IOException {
		String folder = "/home/andreas/tmp/sim/";
		
		GuiConfig config = SysintegrationFactory.getSysintegration().getGuiConfig();
		FlowArrowImage img = new FlowArrowImage(100, config);

//		ImageIO.write(img.imageB, "PNG", new File(folder + "a_B.png"));
//		ImageIO.write(img.imageR, "PNG", new File(folder + "a_R.png"));
//		ImageIO.write(img.imageT, "PNG", new File(folder + "a_T.png"));
//		ImageIO.write(img.imageSelectedB, "PNG", new File(folder + "a_sB.png"));
//		ImageIO.write(img.imageSelectedR, "PNG", new File(folder + "a_sR.png"));
//		ImageIO.write(img.imageSelectedT, "PNG", new File(folder + "a_sT.png"));
		
		BufferedImage i;
		Graphics2D g;
		
//		i = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
//		g = i.createGraphics();
//		img.drawArrowRight(g, false);
//		g.dispose();
//		ImageIO.write(i, "PNG", new File(folder + "b_R.png"));
//		
//		i = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
//		g = i.createGraphics();
//		img.drawArrowRight(g, true);
//		g.dispose();
//		ImageIO.write(i, "PNG", new File(folder + "b_sR.png"));
		
		
//		i = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
//		g = i.createGraphics();
//		img.drawArrowTop(g, false);
//		g.dispose();
//		ImageIO.write(i, "PNG", new File(folder + "t_R.png"));
//		
//		i = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
//		g = i.createGraphics();
//		img.drawArrowTop(g, true);
//		g.dispose();
//		ImageIO.write(i, "PNG", new File(folder + "t_sR.png"));
		
		

		
		i = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		g = i.createGraphics();
		img.drawArrowBottom(g, false);
		g.dispose();
		ImageIO.write(i, "PNG", new File(folder + "t_B.png"));
		
		i = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		g = i.createGraphics();
		img.drawArrowBottom(g, true);
		g.dispose();
		ImageIO.write(i, "PNG", new File(folder + "t_sB.png"));
		
		
		System.out.println("fertig");
	}
}
