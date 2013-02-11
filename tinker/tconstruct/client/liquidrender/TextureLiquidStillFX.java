package tinker.tconstruct.client.liquidrender;

import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import cpw.mods.fml.client.FMLTextureFX;

public class TextureLiquidStillFX extends FMLTextureFX
{
	private final int redMin, redMax, greenMin, greenMax, blueMin, blueMax;
	private final String texture;

	protected float red[];
	protected float green[];
	protected float blue[];
	protected float alpha[];

	public TextureLiquidStillFX(int redMin, int redMax, int greenMin, int greenMax, int blueMin, int blueMax, int spriteIndex, String texture)
	{
		super(spriteIndex);

		this.redMin = redMin;
		this.redMax = redMax;
		this.greenMin = greenMin;
		this.greenMax = greenMax;
		this.blueMin = blueMin;
		this.blueMax = blueMax;
		this.texture = texture;
		setup();
	}

	@Override
	public void setup ()
	{
		super.setup();

		red = new float[tileSizeSquare];
		green = new float[tileSizeSquare];
		blue = new float[tileSizeSquare];
		alpha = new float[tileSizeSquare];
	}

	@Override
	public void bindImage (RenderEngine renderengine)
	{
		ForgeHooksClient.bindTexture(texture, 0);
	}

	@Override
	public void onTick ()
	{
		int var2;
        float var3;
        int r;
        int g;
        int b;
        int var8;
        int var9;

        for (int var1 = 0; var1 < tileSizeBase; ++var1)
        {
            for (var2 = 0; var2 < tileSizeBase; ++var2)
            {
                var3 = 0.0F;
                int var4 = (int)(MathHelper.sin((float)var2 * (float)Math.PI * 2.0F / 16.0F) * 1.2F);
                r = (int)(MathHelper.sin((float)var1 * (float)Math.PI * 2.0F / 16.0F) * 1.2F);

                for (g = var1 - 1; g <= var1 + 1; ++g)
                {
                    for (b = var2 - 1; b <= var2 + 1; ++b)
                    {
                        var8 = g + var4 & tileSizeMask;
                        var9 = b + r & tileSizeMask;
                        var3 += this.red[var8 + var9 * tileSizeBase];
                    }
                }

                this.green[var1 + var2 * tileSizeBase] = var3 / 10.0F + (this.blue[(var1 + 0 & tileSizeMask) + (var2 + 0 & tileSizeMask) * tileSizeBase] + this.blue[(var1 + 1 & tileSizeMask) + (var2 + 0 & tileSizeMask) * tileSizeBase] + this.blue[(var1 + 1 & tileSizeMask) + (var2 + 1 & tileSizeMask) * tileSizeBase] + this.blue[(var1 + 0 & tileSizeMask) + (var2 + 1 & tileSizeMask) * tileSizeBase]) / 4.0F * 0.8F;
                this.blue[var1 + var2 * tileSizeBase] += this.alpha[var1 + var2 * tileSizeBase] * 0.01F;

                if (this.blue[var1 + var2 * tileSizeBase] < 0.0F)
                {
                    this.blue[var1 + var2 * tileSizeBase] = 0.0F;
                }

                this.alpha[var1 + var2 * tileSizeBase] -= 0.06F;

                if (Math.random() < 0.005D)
                {
                    this.alpha[var1 + var2 * tileSizeBase] = 1.5F;
                }
            }
        }

        float[] var11 = this.green;
        this.green = this.red;
        this.red = var11;

        for (var2 = 0; var2 < tileSizeSquare; ++var2)
        {
            var3 = this.red[var2] * 2.0F;

            if (var3 > 1.0F)
            {
                var3 = 1.0F;
            }

            if (var3 < 0.0F)
            {
                var3 = 0.0F;
            }

            /*r = (int)(var3 * 100.0F + 155.0F);
            g = (int)(var3 * var3 * 255.0F);
            b = (int)(var3 * var3 * var3 * var3 * 128.0F);*/
            r = (int) (redMin + var3 * (redMax - redMin));
			g = (int) (greenMin + var3 * (greenMax - greenMin));
			b = (int) (blueMin + var3 * (blueMax - blueMin));

            if (this.anaglyphEnabled)
            {
                var8 = (r * 30 + g * 59 + b * 11) / 100;
                var9 = (r * 30 + g * 70) / 100;
                int var10 = (r * 30 + b * 70) / 100;
                r = var8;
                g = var9;
                b = var10;
            }

            this.imageData[var2 * 4 + 0] = (byte)r;
            this.imageData[var2 * 4 + 1] = (byte)g;
            this.imageData[var2 * 4 + 2] = (byte)b;
            this.imageData[var2 * 4 + 3] = -1;
        }
        
		/*for (int i = 0; i < tileSizeBase; ++i)
		{
			for (int j = 0; j < tileSizeBase; ++j)
			{
				float var3 = 0.0F;

				for (int k = i - 1; k <= i + 1; ++k)
				{
					int r = k & tileSizeMask;
					int g = j & tileSizeMask;
					var3 += this.red[r + g * tileSizeBase];
				}

				this.green[i + j * tileSizeBase] = var3 / 3.3F + this.blue[i + j * tileSizeBase] * 0.8F;
			}
		}

		for (int i = 0; i < tileSizeBase; ++i)
		{
			for (int j = 0; j < tileSizeBase; ++j)
			{
				this.blue[i + j * tileSizeBase] += this.alpha[i + j * tileSizeBase] * 0.05F;

				if (this.blue[i + j * tileSizeBase] < 0.0F)
				{
					this.blue[i + j * tileSizeBase] = 0.0F;
				}

				this.alpha[i + j * tileSizeBase] -= 0.1F;

				if (Math.random() < 0.05D)
				{
					this.alpha[i + j * tileSizeBase] = 0.5F;
				}
			}
		}

		float af[] = green;
		green = red;
		red = af;
		for (int i1 = 0; i1 < tileSizeSquare; i1++)
		{
			float f1 = red[i1];
			if (f1 > 1.0F)
			{
				f1 = 1.0F;
			}
			if (f1 < 0.0F)
			{
				f1 = 0.0F;
			}
			float f2 = f1 * f1;
			int r = (int) (redMin + f2 * (redMax - redMin));
			int g = (int) (greenMin + f2 * (greenMax - greenMin));
			int b = (int) (blueMin + f2 * (blueMax - blueMin));
			if (anaglyphEnabled)
			{
				int i3 = (r * 30 + g * 59 + b * 11) / 100;
				int j3 = (r * 30 + g * 70) / 100;
				int k3 = (r * 30 + b * 70) / 100;
				r = i3;
				g = j3;
				b = k3;
			}

			imageData[i1 * 4 + 0] = (byte) r;
			imageData[i1 * 4 + 1] = (byte) g;
			imageData[i1 * 4 + 2] = (byte) b;
			imageData[i1 * 4 + 3] = (byte) 255;
		}*/

	}
}
