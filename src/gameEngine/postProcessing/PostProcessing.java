package gameEngine.postProcessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import gameEngine.bloom.BrightFilter;
import gameEngine.bloom.CombineFilter;
import gameEngine.gaussianBlur.HorizontalBlur;
import gameEngine.gaussianBlur.VerticalBlur;
import gameEngine.graphics.Loader;
import gameEngine.model.RawModel;

public class PostProcessing {

	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };
	private static RawModel quad;
	private static ContrastChanger contrastChanger;
	private static BrightFilter brightFilter;
	private static HorizontalBlur hBlur;
	private static VerticalBlur vBlur;
	private static HorizontalBlur h1Blur;
	private static VerticalBlur v1Blur;
	private static HorizontalBlur h2Blur;
	private static VerticalBlur v2Blur;
	private static CombineFilter combineFilter;

	public static void init(Loader loader) {
		quad = loader.loadToVAO(POSITIONS, 2);
		contrastChanger = new ContrastChanger();

		brightFilter = new BrightFilter(Display.getWidth() / 4, Display.getHeight() / 4);
		hBlur = new HorizontalBlur(Display.getWidth() / 4, Display.getHeight() / 2);
		vBlur = new VerticalBlur(Display.getWidth() / 4, Display.getHeight() / 2);
		h1Blur = new HorizontalBlur(Display.getWidth() / 8, Display.getHeight() / 4);
		v1Blur = new VerticalBlur(Display.getWidth() / 8, Display.getHeight() / 4);
		h2Blur = new HorizontalBlur(Display.getWidth() / 8, Display.getHeight() / 8);
		v2Blur = new VerticalBlur(Display.getWidth() / 8, Display.getHeight() / 8);
		combineFilter = new CombineFilter();
	}

	public static void doPostProcessing(int colourTexture) {
		start();
		brightFilter.render(colourTexture);
		hBlur.render(brightFilter.getOutputTexture());
		vBlur.render(hBlur.getOutputTexture());
		h1Blur.render(brightFilter.getOutputTexture());
		v1Blur.render(h1Blur.getOutputTexture());
		h2Blur.render(brightFilter.getOutputTexture());
		v2Blur.render(h2Blur.getOutputTexture());
		combineFilter.render(colourTexture, vBlur.getOutputTexture());
		end();
	}

	public static void cleanUp() {
		brightFilter.cleanUp();
		contrastChanger.cleanUp();
		hBlur.cleanUp();
		vBlur.cleanUp();
		combineFilter.cleanUp();
	}

	private static void start() {
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	private static void end() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

}
