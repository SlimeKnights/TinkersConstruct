package mods.tinker.tconstruct.client;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.client.armor.WingModel;
import mods.tinker.tconstruct.common.TContent;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TClientEvents
{
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
