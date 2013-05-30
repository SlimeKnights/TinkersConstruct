package mods.tinker.tconstruct.client;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.client.armor.WingModel;
import mods.tinker.tconstruct.common.TContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TClientEvents
{
	Minecraft mc = Minecraft.getMinecraft();
	EntityPlayer player;
	/* Sounds */
	@ForgeSubscribe
	public void onSound (SoundLoadEvent event)
	{
		try
		{
			event.manager.soundPoolSounds.addSound("sounds/frypan_hit.ogg", TConstruct.class.getResource("/sounds/frypan_hit.ogg"));
			event.manager.soundPoolSounds.addSound("sounds/little_saw.ogg", TConstruct.class.getResource("/sounds/little_saw.ogg"));
			System.out.println("[TConstruct] Successfully loaded sounds.");
		}
		catch (Exception e)
		{
			System.err.println("[TConstruct] Failed to register one or more sounds");
		}
	}

	/* Liquids */
	@ForgeSubscribe
	public void postStitch (TextureStitchEvent.Post event)
	{
		for (int i = 0; i < TContent.liquidIcons.length; i++)
		{
			TContent.liquidIcons[i].setRenderingIcon(TContent.liquidMetalStill.getIcon(0, i));
			LiquidStack canon = TContent.liquidIcons[i].canonical();
			if (canon != null)
				canon.setRenderingIcon(TContent.liquidMetalStill.getIcon(0, i));
		}
	}
	
	/* Health */
	@ForgeSubscribe
	public void renderHealthbar (RenderGameOverlayEvent.Post event)
	{
		if (event.type == RenderGameOverlayEvent.ElementType.HEALTH)
		{
			if (player == null)
				player = mc.thePlayer;
			
			ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
	        int scaledWidth = scaledresolution.getScaledWidth();
	        int scaledHeight = scaledresolution.getScaledHeight();
	        int xBasePos = scaledWidth / 2 - 91;
	        int yBasePos = scaledHeight - 39;
	        this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/newhearts.png");
	        
	        int hp = player.getHealth();
	        for (int iter = 0; iter < hp / 20; iter++)
	        {
	            int renderHearts = (hp - 20*(iter+1)) / 2;
	            if (renderHearts > 10)
	                renderHearts = 10;
	            for (int i = 0; i < renderHearts; i++)
	            {
	                this.drawTexturedModalRect(xBasePos + 8*i, yBasePos, 0 + 18*iter, 0, 8, 8);
	            }
	            if (hp % 2 == 1 && renderHearts < 10)
	            {
	                this.drawTexturedModalRect(xBasePos + 8*renderHearts, yBasePos, 9 + 18*iter, 0, 8, 8);
	            }
	        }

	        this.mc.renderEngine.bindTexture("/gui/icons.png");
		}
	}
	


	public void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + 0) * f), (double)((float)(par4 + par6) * f1));
        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + par5) * f), (double)((float)(par4 + par6) * f1));
        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + par5) * f), (double)((float)(par4 + 0) * f1));
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + 0) * f), (double)((float)(par4 + 0) * f1));
        tessellator.draw();
    }
	double zLevel = 0;

	/* Armor */
	ModelBiped model = new ModelBiped(5f);
	WingModel wings = new WingModel();

	/*static
	{
		model.bipedHead.showModel = false;
	}*/

	private float interpolateRotation (float par1, float par2, float par3)
	{
		float f3;

		for (f3 = par2 - par1; f3 < -180.0F; f3 += 360.0F)
		{
			;
		}

		while (f3 >= 180.0F)
		{
			f3 -= 360.0F;
		}

		return par1 + par3 * f3;
	}

	protected float handleRotationFloat (EntityLiving par1EntityLiving, float par2)
	{
		return (float) par1EntityLiving.ticksExisted + par2;
	}
}
