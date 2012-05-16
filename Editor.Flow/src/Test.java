import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.zhaw.simulation.editor.elements.GuiImage;
import ch.zhaw.simulation.editor.elements.global.GlobalImage;
import ch.zhaw.simulation.editor.flow.connector.flowarrow.FlowArrowImage;
import ch.zhaw.simulation.editor.flow.elements.container.ContainerImage;
import ch.zhaw.simulation.editor.flow.elements.density.DensityContainerImage;
import ch.zhaw.simulation.editor.flow.elements.parameter.ParameterImage;
import ch.zhaw.simulation.sysintegration.GuiConfig;
import ch.zhaw.simulation.sysintegration.Sysintegration;

public class Test {
	public static void main(String[] args) throws IOException {
		Sysintegration sys = new Sysintegration();
		GuiConfig config = sys.getGuiConfig();

		BufferedImage img;
//		BufferedImage img = GuiImage.drawToImage(new GlobalImage(22, config));
//		ImageIO.write(img, "PNG", new File("/tmp/global.png"));
//
//		img = GuiImage.drawToImage(new ContainerImage(22, 27, config));
//		ImageIO.write(img, "PNG", new File("/tmp/container.png"));
//		img = GuiImage.drawToImage(new ParameterImage(22, config));
//		ImageIO.write(img, "PNG", new File("/tmp/parameter.png"));
//		img = GuiImage.drawToImage(new FlowArrowImage(22, config));
//		ImageIO.write(img, "PNG", new File("/tmp/arrow.png"));
		img = GuiImage.drawToImage(new DensityContainerImage(22, 27, config));
		ImageIO.write(img, "PNG", new File("/tmp/density.png"));

	}
}
