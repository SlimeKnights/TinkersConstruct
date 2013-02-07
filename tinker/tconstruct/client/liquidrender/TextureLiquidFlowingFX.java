package tinker.tconstruct.client.liquidrender;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderEngine;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLTextureFX;

public class TextureLiquidFlowingFX extends FMLTextureFX
{
	private final int redMin, redMax, greenMin, greenMax, blueMin, blueMax;
	private final String texture;

	public TextureLiquidFlowingFX(int redMin, int redMax, int greenMin, int greenMax, int blueMin, int blueMax, int spriteIndex, String texture)
	{
		super(spriteIndex);
		this.redMin = redMin;
		this.redMax = redMax;
		this.greenMin = greenMin;
		this.greenMax = greenMax;
		this.blueMin = blueMin;
		this.blueMax = blueMax;
		this.texture = texture;

		tileSize = 2;
	}

	@Override
	protected void setup ()
	{
		super.setup();
		red = new float[tileSizeSquare];
		blue = new float[tileSizeSquare];
		green = new float[tileSizeSquare];
		alpha = new float[tileSizeSquare];
		animFrame = 0;
	}

	@Override
	public void bindImage (RenderEngine renderengine)
	{
		GL11.glBindTexture(3553, renderengine.getTexture(texture));
	}

	@Override
	public void onTick ()
	{
		animFrame++;
		for (int i = 0; i < tileSizeBase; i++)
		{
			for (int k = 0; k < tileSizeBase; k++)
			{
				float f = 0.0F;
				for (int j1 = k - 2; j1 <= k; j1++)
				{
					int k1 = i & tileSizeMask;
					int i2 = j1 & tileSizeMask;
					f += red[k1 + i2 * tileSizeBase];
				}

				blue[i + k * tileSizeBase] = f / 3.2F + green[i + k * tileSizeBase] * 0.8F;
			}
		}

		for (int j = 0; j < tileSizeBase; j++)
		{
			for (int l = 0; l < tileSizeBase; l++)
			{
				green[j + l * tileSizeBase] += alpha[j + l * tileSizeBase] * 0.05F;
				if (green[j + l * tileSizeBase] < 0.0F)
				{
					green[j + l * tileSizeBase] = 0.0F;
				}
				alpha[j + l * tileSizeBase] -= 0.3F;
				if (Math.random() < 0.20000000000000001D)
				{
					alpha[j + l * tileSizeBase] = 0.5F;
				}
			}
		}

		float af[] = blue;
		blue = red;
		red = af;
		for (int i1 = 0; i1 < tileSizeSquare; i1++)
		{
			float f1 = red[i1 - animFrame * tileSizeBase & tileSizeSquareMask];
			if (f1 > 1.0F)
			{
				f1 = 1.0F;
			}
			if (f1 < 0.0F)
			{
				f1 = 0.0F;
			}
			float f2 = f1 * f1;
			/*int r = (int) (10F + f2 * 22F);
			int g = (int) (50F + f2 * 64F);
			int b = 255;*/

			int r = (int) (redMin + f2 * (redMax - redMin));
			int g = (int) (greenMin + f2 * (greenMax - greenMin));
			int b = (int) (blueMin + f2 * (blueMax - blueMin));
			if (anaglyphEnabled)
			{
				int redTemp = (r * 30 + g * 59 + b * 11) / 100;
				int greenTemp = (r * 30 + g * 70) / 100;
				int blueTemp = (r * 30 + b * 70) / 100;
				r = redTemp;
				g = greenTemp;
				b = blueTemp;
			}
			imageData[i1 * 4 + 0] = (byte) r;
			imageData[i1 * 4 + 1] = (byte) g;
			imageData[i1 * 4 + 2] = (byte) b;
			imageData[i1 * 4 + 3] = /*(byte)l2*/(byte) 255;

			//imageData[i1 * 4 + 0] = (byte) l1;
			//imageData[i1 * 4 + 1] = (byte) l1;
			//imageData[i1 * 4 + 2] = (byte) l1;
			//imageData[i1 * 4 + 3] = /* (byte)l2 */(byte) 255;
		}

	}

	protected float red[];
	protected float blue[];
	protected float green[];
	protected float alpha[];
	private int animFrame;
}
