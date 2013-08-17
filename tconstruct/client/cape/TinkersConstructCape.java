package tconstruct.client.cape;

import java.util.ArrayList;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import tconstruct.client.TProxyClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventBus;
import net.minecraftforge.event.ForgeSubscribe;
import org.lwjgl.opengl.GL11;

public class TinkersConstructCape extends RenderPlayer {
	public TinkersConstructCape() {
		MinecraftForge.EVENT_BUS.register(this);
		setRenderManager(RenderManager.instance);
	}

	@ForgeSubscribe
	public void receiveRenderEvent(RenderPlayerEvent.Pre aEvent) {
		if (TinkersConstruct_Utility.getFullInvisibility(aEvent.entityPlayer)) {
			aEvent.setCanceled(true);
			return;
		}
	}

	@ForgeSubscribe
	public void receiveRenderSpecialsEvent(RenderPlayerEvent.Specials.Pre aEvent) {
		AbstractClientPlayer aPlayer = (AbstractClientPlayer) aEvent.entityPlayer;
		float aPartialTicks = aEvent.partialTicks;

		if (TinkersConstruct_Utility.getFullInvisibility(aPlayer)) {
			aEvent.setCanceled(true);
			return;
		}
		if (aPlayer.isInvisible())
			return;
		if (TinkersConstruct_Utility.getPotion(aPlayer,
				Integer.valueOf(Potion.invisibility.id).intValue()))
			return;
		try {
			ResourceLocation tResource = null;

			TProxyClient.CapeList.add(aPlayer.username.toLowerCase());
			if (TProxyClient.CapeList.contains(aPlayer.username.toLowerCase()))
				tResource = new ResourceLocation("tinker",
						"textures/Cape.png");
			if (!aPlayer.getHideCape()) {
				func_110776_a(tResource);
				GL11.glPushMatrix();
				GL11.glTranslatef(0.0F, 0.0F, 0.125F);
				double d0 = aPlayer.field_71091_bM
						+ (aPlayer.field_71094_bP - aPlayer.field_71091_bM)
						* aPartialTicks
						- (aPlayer.prevPosX + (aPlayer.posX - aPlayer.prevPosX)
								* aPartialTicks);
				double d1 = aPlayer.field_71096_bN
						+ (aPlayer.field_71095_bQ - aPlayer.field_71096_bN)
						* aPartialTicks
						- (aPlayer.prevPosY + (aPlayer.posY - aPlayer.prevPosY)
								* aPartialTicks);
				double d2 = aPlayer.field_71097_bO
						+ (aPlayer.field_71085_bR - aPlayer.field_71097_bO)
						* aPartialTicks
						- (aPlayer.prevPosZ + (aPlayer.posZ - aPlayer.prevPosZ)
								* aPartialTicks);
				float f6 = aPlayer.prevRenderYawOffset
						+ (aPlayer.renderYawOffset - aPlayer.prevRenderYawOffset)
						* aPartialTicks;
				double d3 = MathHelper.sin(f6 * 3.141593F / 180.0F);
				double d4 = -MathHelper.cos(f6 * 3.141593F / 180.0F);
				float f7 = (float) d1 * 10.0F;
				float f8 = (float) (d0 * d3 + d2 * d4) * 100.0F;
				float f9 = (float) (d0 * d4 - d2 * d3) * 100.0F;
				if (f7 < -6.0F)
					f7 = -6.0F;
				if (f7 > 32.0F)
					f7 = 32.0F;
				if (f8 < 0.0F)
					f8 = 0.0F;
				float f10 = aPlayer.prevCameraYaw
						+ (aPlayer.cameraYaw - aPlayer.prevCameraYaw)
						* aPartialTicks;
				f7 += MathHelper
						.sin((aPlayer.prevDistanceWalkedModified + (aPlayer.distanceWalkedModified - aPlayer.prevDistanceWalkedModified)
								* aPartialTicks) * 6.0F)
						* 32.0F * f10;
				if (aPlayer.isSneaking()) {
					f7 += 25.0F;
				}
				GL11.glRotatef(6.0F + f8 / 2.0F + f7, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(f9 / 2.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-f9 / 2.0F, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				((ModelBiped) this.mainModel).renderCloak(0.0625F);
				GL11.glPopMatrix();
			}
		} catch (Throwable e) {
			
		}
	}
}