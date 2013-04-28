package mods.tinker.tconstruct.client.block;

import mods.tinker.tconstruct.blocks.logic.GolemCoreLogic;
import mods.tinker.tconstruct.entity.FancyEntityItem;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/* Special renderer, only used for drawing tools */

@SideOnly(Side.CLIENT)
public class GolemCoreSpecialRender extends TileEntitySpecialRenderer
{
	@Override
	public void renderTileEntityAt (TileEntity logic, double var2, double var4, double var6, float var8)
	{
		this.render((GolemCoreLogic) logic, var2, var4, var6, var8);
	}

	public void render (GolemCoreLogic logic, double posX, double posY, double posZ, float var8)
	{
		GL11.glPushMatrix();
		float var10 = (float) (posX - 0.5F);
		float var11 = (float) (posY - 0.5F);
		float var12 = (float) (posZ - 0.5F);
		GL11.glTranslatef(var10, var11, var12);

		ItemStack stack = logic.getStackInSlot(0);
		if (stack != null)
			renderItem(logic, stack);
		
		GL11.glPopMatrix();
	}
	
	void renderItem(GolemCoreLogic logic, ItemStack stack)
	{
		FancyEntityItem entityitem = new FancyEntityItem(logic.worldObj, 0.0D, 0.0D, 0.0D, stack);
		entityitem.getEntityItem().stackSize = 1;
		entityitem.hoverStart = 0.0F;
		GL11.glPushMatrix();
		GL11.glTranslatef(1F, 0.675F, 1.0F);
		//GL11.glRotatef(90F, 1, 0F, 0F);
		GL11.glScalef(1.4F, 1.4F, 1.4F);
		if (stack.getItem() instanceof ItemBlock)
		{
			GL11.glScalef(1.6F, 1.6F, 1.6F);
			GL11.glTranslatef(0F, 0.045F, 0.0f);
		}

		RenderItem.renderInFrame = true;
		RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
		RenderItem.renderInFrame = false;

		GL11.glPopMatrix();
	}
}
