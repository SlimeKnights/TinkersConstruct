package tinker.tconstruct.client;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import tinker.tconstruct.tools.ToolCore;
import cpw.mods.fml.client.TextureFXManager;

public class SuperCustomToolRenderer implements IItemRenderer
{
	RenderEngine renderEngine;
	RenderManager renderManager;
	Random random;

	public SuperCustomToolRenderer()
	{
		renderEngine = Minecraft.getMinecraft().renderEngine;
		renderManager = renderManager.instance;
		random = new Random();
	}
	
	

	@Override
	public boolean handleRenderType (ItemStack item, ItemRenderType type)
	{
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper (ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		if (type == ItemRenderType.EQUIPPED || (type == ItemRenderType.ENTITY && helper == ItemRendererHelper.ENTITY_BOBBING))
			return true;
		else
			return false;
	}

	@Override
	public void renderItem (ItemRenderType type, ItemStack item, Object... data)
	{
		if (type == ItemRenderType.INVENTORY)
			renderInventoryItem(item, (RenderBlocks) data[0]);
		if (type == ItemRenderType.EQUIPPED)
			renderEquippedItem(item, (RenderBlocks) data[0], (EntityLiving) data[1]);
		if (type == ItemRenderType.ENTITY)
			renderEntityItem(item, (RenderBlocks) data[0], (EntityItem) data[1]);
	}

	/* Inventory */
	void renderInventoryItem (ItemStack stack, RenderBlocks renderer)
	{
		ToolCore tool = (ToolCore) stack.getItem();
		//int itemID = stack.itemID;
		int itemDamage = stack.getItemDamage();
		float var16;

		GL11.glDisable(GL11.GL_LIGHTING);

		for (int renderPass = 0; renderPass < tool.getRenderPasses(itemDamage); renderPass++)
		{
			String texturePath = tool.getIconLocation(stack, renderPass);
			renderEngine.bindTexture(renderEngine.getTexture(texturePath));

			GL11.glScalef(0.0625F, 0.0625F, 1F);
			this.renderTexturedQuad();
			GL11.glScalef(16F, 16F, 1F);
		}
		
		for (int renderPass = 0; renderPass < 3; renderPass++)
		{
			String texturePath = tool.getEffectLocation(stack, renderPass);
			renderEngine.bindTexture(renderEngine.getTexture(texturePath));

			GL11.glScalef(0.0625F, 0.0625F, 1F);
			this.renderTexturedQuad();
			GL11.glScalef(16F, 16F, 1F);
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	double zLevel = 50;

	/**
	 * Adds a textured quad to the tesselator at the specified position with the specified texture coords, width and
	 * height.  Args: x, y, u, v, width, height
	 */
	public void renderTexturedQuad()
    {
		int x = 0, y = 0, u = 0, v = 0, height = 256, width = 256;
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(x + 0), (double)(y + height), (double)this.zLevel, (double)((float)(u + 0) * var7), (double)((float)(v + height) * var8));
        var9.addVertexWithUV((double)(x + width), (double)(y + height), (double)this.zLevel, (double)((float)(u + width) * var7), (double)((float)(v + height) * var8));
        var9.addVertexWithUV((double)(x + width), (double)(y + 0), (double)this.zLevel, (double)((float)(u + width) * var7), (double)((float)(v + 0) * var8));
        var9.addVertexWithUV((double)(x + 0), (double)(y + 0), (double)this.zLevel, (double)((float)(u + 0) * var7), (double)((float)(v + 0) * var8));
        var9.draw();
    }

	boolean shouldSpreadItems ()
	{
		return true;
	}

	/* Equipped */
	void renderEquippedItem (ItemStack stack, RenderBlocks renderer, EntityLiving living)
	{
        for (int x = 0; x < stack.getItem().getRenderPasses(stack.getItemDamage()); x++)
        {
            this.doRenderEquippedItem(stack, living, x, true);
        }
        
        for (int x = 0; x < 3; x++)
        {
            this.doRenderEquippedItem(stack, living, x, false);
        }
	}
	
	void doRenderEquippedItem (ItemStack stack, EntityLiving living, int renderPass, boolean baseSprite)
	{
		GL11.glPushMatrix();

		Tessellator var5 = Tessellator.instance;
		String texturePath = null;
		if (baseSprite)
			texturePath = ((ToolCore)stack.getItem()).getIconLocation(stack, renderPass);
		else
			texturePath = ((ToolCore)stack.getItem()).getEffectLocation(stack, renderPass);
		renderEngine.bindTexture(renderEngine.getTexture(texturePath));
		float var7 = 0;
		float var8 = 1;
		float var9 = 0;
		float var10 = 1;
		float var11 = 0.0F;
		float var12 = 0.3F;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslatef(-var11, -var12, 0.0F);
		float var13 = 1.5F;
		GL11.glScalef(var13, var13, var13);
		GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
		GL11.glTranslatef(-1.1875f, 0.25F, 0.45F); //Edited to position properly
		renderItemIn2D(var5, var8, var9, var7, var10, 0.0625F);

		if (stack != null && stack.hasEffect() && !baseSprite && renderPass == 2)
		{
			GL11.glDepthFunc(GL11.GL_EQUAL);
			GL11.glDisable(GL11.GL_LIGHTING);
			renderEngine.bindTexture(renderEngine.getTexture("%blur%/misc/glint.png"));
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			float var14 = 0.76F;
			GL11.glColor4f(0.5F * var14, 0.25F * var14, 0.8F * var14, 1.0F);
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPushMatrix();
			float var15 = 0.125F;
			GL11.glScalef(var15, var15, var15);
			float var16 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
			GL11.glTranslatef(var16, 0.0F, 0.0F);
			GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
			renderItemIn2D(var5, 0.0F, 0.0F, 1.0F, 1.0F, 0.0625F);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glScalef(var15, var15, var15);
			var16 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
			GL11.glTranslatef(-var16, 0.0F, 0.0F);
			GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
			renderItemIn2D(var5, 0.0F, 0.0F, 1.0F, 1.0F, 0.0625F);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);

		GL11.glPopMatrix();
	}

	public static void renderItemIn2D (Tessellator par0Tessellator, float par1, float par2, float par3, float par4, float par5)
	{
		float var6 = 1.0F;
		//GL11.glScalef(0.0625F, 0.0625F, 1F);
		par0Tessellator.startDrawingQuads();
		par0Tessellator.setNormal(0.0F, 0.0F, 1.0F);
		par0Tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, (double) par1, (double) par4);
		par0Tessellator.addVertexWithUV((double) var6, 0.0D, 0.0D, (double) par3, (double) par4);
		par0Tessellator.addVertexWithUV((double) var6, 1.0D, 0.0D, (double) par3, (double) par2);
		par0Tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, (double) par1, (double) par2);
		par0Tessellator.draw();
		par0Tessellator.startDrawingQuads();
		par0Tessellator.setNormal(0.0F, 0.0F, -1.0F);
		par0Tessellator.addVertexWithUV(0.0D, 1.0D, (double) (0.0F - par5), (double) par1, (double) par2);
		par0Tessellator.addVertexWithUV((double) var6, 1.0D, (double) (0.0F - par5), (double) par3, (double) par2);
		par0Tessellator.addVertexWithUV((double) var6, 0.0D, (double) (0.0F - par5), (double) par3, (double) par4);
		par0Tessellator.addVertexWithUV(0.0D, 0.0D, (double) (0.0F - par5), (double) par1, (double) par4);
		par0Tessellator.draw();
		par0Tessellator.startDrawingQuads();
		par0Tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		int var7;
		float var8;
		float var9;
		float var10;

		int tileSize = TextureFXManager.instance().getTextureDimensions(GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)).width;

		float tx = 1.0f / (32 * tileSize);
		float tz = 1.0f / tileSize;

		for (var7 = 0; var7 < tileSize; ++var7)
		{
			var8 = (float) var7 / tileSize;
			var9 = par1 + (par3 - par1) * var8 - tx;
			var10 = var6 * var8;
			par0Tessellator.addVertexWithUV((double) var10, 0.0D, (double) (0.0F - par5), (double) var9, (double) par4);
			par0Tessellator.addVertexWithUV((double) var10, 0.0D, 0.0D, (double) var9, (double) par4);
			par0Tessellator.addVertexWithUV((double) var10, 1.0D, 0.0D, (double) var9, (double) par2);
			par0Tessellator.addVertexWithUV((double) var10, 1.0D, (double) (0.0F - par5), (double) var9, (double) par2);
		}

		par0Tessellator.draw();
		par0Tessellator.startDrawingQuads();
		par0Tessellator.setNormal(1.0F, 0.0F, 0.0F);

		for (var7 = 0; var7 < tileSize; ++var7)
		{
			var8 = (float) var7 / tileSize;
			var9 = par1 + (par3 - par1) * var8 - tx;
			var10 = var6 * var8 + tz;
			par0Tessellator.addVertexWithUV((double) var10, 1.0D, (double) (0.0F - par5), (double) var9, (double) par2);
			par0Tessellator.addVertexWithUV((double) var10, 1.0D, 0.0D, (double) var9, (double) par2);
			par0Tessellator.addVertexWithUV((double) var10, 0.0D, 0.0D, (double) var9, (double) par4);
			par0Tessellator.addVertexWithUV((double) var10, 0.0D, (double) (0.0F - par5), (double) var9, (double) par4);
		}

		par0Tessellator.draw();
		par0Tessellator.startDrawingQuads();
		par0Tessellator.setNormal(0.0F, 1.0F, 0.0F);

		for (var7 = 0; var7 < tileSize; ++var7)
		{
			var8 = (float) var7 / tileSize;
			var9 = par4 + (par2 - par4) * var8 - tx;
			var10 = var6 * var8 + tz;
			par0Tessellator.addVertexWithUV(0.0D, (double) var10, 0.0D, (double) par1, (double) var9);
			par0Tessellator.addVertexWithUV((double) var6, (double) var10, 0.0D, (double) par3, (double) var9);
			par0Tessellator.addVertexWithUV((double) var6, (double) var10, (double) (0.0F - par5), (double) par3, (double) var9);
			par0Tessellator.addVertexWithUV(0.0D, (double) var10, (double) (0.0F - par5), (double) par1, (double) var9);
		}

		par0Tessellator.draw();
		par0Tessellator.startDrawingQuads();
		par0Tessellator.setNormal(0.0F, -1.0F, 0.0F);

		for (var7 = 0; var7 < tileSize; ++var7)
		{
			var8 = (float) var7 / tileSize;
			var9 = par4 + (par2 - par4) * var8 - tx;
			var10 = var6 * var8;
			par0Tessellator.addVertexWithUV((double) var6, (double) var10, 0.0D, (double) par3, (double) var9);
			par0Tessellator.addVertexWithUV(0.0D, (double) var10, 0.0D, (double) par1, (double) var9);
			par0Tessellator.addVertexWithUV(0.0D, (double) var10, (double) (0.0F - par5), (double) par1, (double) var9);
			par0Tessellator.addVertexWithUV((double) var6, (double) var10, (double) (0.0F - par5), (double) par3, (double) var9);
		}

		par0Tessellator.draw();
		//GL11.glScalef(16F, 16F, 1F);
	}

	/* Entity */
	void renderEntityItem (ItemStack stack, RenderBlocks renderer, EntityItem entity)
	{
		this.random.setSeed(187L);
		ItemStack var10 = entity.getEntityItem();
		ToolCore tool = (ToolCore) var10.getItem();

		if (var10.getItem() != null)
		{
			GL11.glPushMatrix();
			float var11 = shouldBob() ? MathHelper.sin(((float) entity.age) / 10.0F + entity.hoverStart) * 0.1F + 0.1F : 0F;
			float var12 = (((float) entity.age) / 20.0F + entity.hoverStart) * (180F / (float) Math.PI);
			byte var13 = getMiniBlockCountForItemStack(var10);

			//GL11.glTranslatef((float) par2, (float) par4 + var11, (float) par6);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			int renderIndex;
			float var19;
			float var20;
			float var24;

			int renderPass;
			float var17;

			for (renderPass = 0; renderPass < tool.getRenderPasses(var10.getItemDamage()); ++renderPass)
			{
				this.random.setSeed(187L);
				var17 = 1.0F;
				
				String texturePath = tool.getIconLocation(var10, renderPass);
				renderEngine.bindTexture(renderEngine.getTexture(texturePath));

				int var18 = Item.itemsList[tool.itemID].getColorFromItemStack(var10, renderPass);
				var19 = (float) (var18 >> 16 & 255) / 255.0F;
				var20 = (float) (var18 >> 8 & 255) / 255.0F;
				float var21 = (float) (var18 & 255) / 255.0F;
				GL11.glColor4f(var19 * var17, var20 * var17, var21 * var17, 1.0F);
				this.func_77020_a(entity, 0, var13, 0, var19 * var17, var20 * var17, var21 * var17, renderPass, true);
			}
			
			for (renderPass = 0; renderPass < 3; ++renderPass)
			{
				this.random.setSeed(187L);
				var17 = 1.0F;
				
				String texturePath = tool.getEffectLocation(var10, renderPass);
				renderEngine.bindTexture(renderEngine.getTexture(texturePath));

				int var18 = Item.itemsList[tool.itemID].getColorFromItemStack(var10, renderPass);
				var19 = (float) (var18 >> 16 & 255) / 255.0F;
				var20 = (float) (var18 >> 8 & 255) / 255.0F;
				float var21 = (float) (var18 & 255) / 255.0F;
				GL11.glColor4f(var19 * var17, var20 * var17, var21 * var17, 1.0F);
				this.func_77020_a(entity, 0, var13, 0, var19 * var17, var20 * var17, var21 * var17, renderPass, false);
			}

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}
	}

	boolean shouldBob ()
	{
		return true;
	}

	public byte getMiniBlockCountForItemStack (ItemStack stack)
	{
		byte var13 = 1;
		if (stack.stackSize > 1)
		{
			var13 = 2;
		}

		if (stack.stackSize > 5)
		{
			var13 = 3;
		}

		if (stack.stackSize > 20)
		{
			var13 = 4;
		}

		if (stack.stackSize > 40)
		{
			var13 = 5;
		}
		return var13;
	}

	private void func_77020_a (EntityItem par1EntityItem, int par2, int par3, float par4, float par5, float par6, float par7, int renderPass, boolean baseSprite)
	{
		Tessellator var8 = Tessellator.instance;
		float var9 = 0;
		float var10 = 1f;
		float var11 = 0;
		float var12 = 1f;
		float var13 = 1.0F;
		float var14 = 0.5F;
		float var15 = 0.25F;
		float var17;

		if (this.renderManager.options.fancyGraphics)
		{
			GL11.glPushMatrix();

			GL11.glRotatef((((float) par1EntityItem.age + par4) / 20.0F + par1EntityItem.hoverStart) * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);

			float var16 = 0.0625F;
			var17 = 0.021875F;
			ItemStack stack = par1EntityItem.getEntityItem();
			ToolCore tool = (ToolCore) stack.getItem();
			int var19 = stack.stackSize;
			byte var24 = getMiniBlockCountForItemStack(stack);

			GL11.glTranslatef(-var14, -var15, -((var16 + var17) * (float) var24 / 2.0F));

			for (int var20 = 0; var20 < var24; ++var20)
			{
				// Makes items offset when in 3D, like when in 2D, looks much better. Considered a vanilla bug...
				if (var20 > 0 && shouldSpreadItems())
				{
					float x = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
					float y = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
					float z = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
					GL11.glTranslatef(x, y, var16 + var17);
				}
				else
				{
					GL11.glTranslatef(0f, 0f, var16 + var17);
				}
				
				//String texturePath = tool.getIconLocation(stack, renderPass);

				String texturePath = null;
				if (baseSprite)
					texturePath = ((ToolCore)stack.getItem()).getIconLocation(stack, renderPass);
				else
					texturePath = ((ToolCore)stack.getItem()).getEffectLocation(stack, renderPass);
				renderEngine.bindTexture(renderEngine.getTexture(texturePath));

				GL11.glColor4f(par5, par6, par7, 1.0F);
				ItemRenderer.renderItemIn2D(var8, var10, var11, var9, var12, var16);

				if (stack != null && stack.hasEffect())
				{
					GL11.glDepthFunc(GL11.GL_EQUAL);
					GL11.glDisable(GL11.GL_LIGHTING);
					this.renderManager.renderEngine.bindTexture(this.renderManager.renderEngine.getTexture("%blur%/misc/glint.png"));
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
					float var21 = 0.76F;
					GL11.glColor4f(0.5F * var21, 0.25F * var21, 0.8F * var21, 1.0F);
					GL11.glMatrixMode(GL11.GL_TEXTURE);
					GL11.glPushMatrix();
					float var22 = 0.125F;
					GL11.glScalef(var22, var22, var22);
					float var23 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
					GL11.glTranslatef(var23, 0.0F, 0.0F);
					GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
					ItemRenderer.renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, var16);
					GL11.glPopMatrix();
					GL11.glPushMatrix();
					GL11.glScalef(var22, var22, var22);
					var23 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
					GL11.glTranslatef(-var23, 0.0F, 0.0F);
					GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
					ItemRenderer.renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, 0.0625F);
					GL11.glPopMatrix();
					GL11.glMatrixMode(GL11.GL_MODELVIEW);
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glDepthFunc(GL11.GL_LEQUAL);
				}
			}

			GL11.glPopMatrix();
		}
		else
		{
			for (int var25 = 0; var25 < par3; ++var25)
			{
				GL11.glPushMatrix();

				if (var25 > 0)
				{
					var17 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
					float var27 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
					float var26 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
					GL11.glTranslatef(var17, var27, var26);
				}

				if (!false)
				{
					GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
				}

				GL11.glColor4f(par5, par6, par7, 1.0F);
				var8.startDrawingQuads();
				var8.setNormal(0.0F, 1.0F, 0.0F);
				var8.addVertexWithUV((double) (0.0F - var14), (double) (0.0F - var15), 0.0D, (double) var9, (double) var12);
				var8.addVertexWithUV((double) (var13 - var14), (double) (0.0F - var15), 0.0D, (double) var10, (double) var12);
				var8.addVertexWithUV((double) (var13 - var14), (double) (1.0F - var15), 0.0D, (double) var10, (double) var11);
				var8.addVertexWithUV((double) (0.0F - var14), (double) (1.0F - var15), 0.0D, (double) var9, (double) var11);
				var8.draw();
				GL11.glPopMatrix();
			}
		}
	}
}
