package mods.tinker.common.fancyitem;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FancyItemRender extends Render
{
	private RenderBlocks itemRenderBlocks = new RenderBlocks();

	/** The RNG used in RenderItem (for bobbing itemstacks on the ground) */
	private Random random = new Random();
	public boolean field_77024_a = true;

	/** Defines the zLevel of rendering of item on GUI. */
	public float zLevel = 0.0F;
	public static boolean field_82407_g = false;

	public FancyItemRender()
	{
		this.shadowSize = 0.15F;
		this.shadowOpaque = 0.75F;
	}

	/**
	 * Renders the item
	 */
	public void doRenderItem (EntityItem par1EntityItem, double par2, double par4, double par6, float par8, float par9)
	{
		this.random.setSeed(187L);
		ItemStack itemstack = par1EntityItem.getEntityItem();

		if (itemstack.getItem() != null)
		{
			GL11.glPushMatrix();
			float f2 = shouldBob() ? MathHelper.sin(((float) par1EntityItem.age + par9) / 10.0F + par1EntityItem.hoverStart) * 0.1F + 0.1F : 0F;
			float f3 = (((float) par1EntityItem.age + par9) / 20.0F + par1EntityItem.hoverStart) * (180F / (float) Math.PI);
			byte b0 = getMiniBlockCount(itemstack);

			GL11.glTranslatef((float) par2, (float) par4 + f2, (float) par6);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			int i;
			float f4;
			float f5;
			float f6;

			Block block = null;
			if (itemstack.itemID < Block.blocksList.length)
			{
				block = Block.blocksList[itemstack.itemID];
			}

			if (ForgeHooksClient.renderEntityItem(par1EntityItem, itemstack, f2, f3, random, renderManager.renderEngine, renderBlocks))
			{
				;
			}
			else if (itemstack.func_94608_d() == 0 && block != null && RenderBlocks.renderItemIn3d(Block.blocksList[itemstack.itemID].getRenderType()))
			{
				GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);

				if (field_82407_g)
				{
					GL11.glScalef(1.25F, 1.25F, 1.25F);
					GL11.glTranslatef(0.0F, 0.05F, 0.0F);
					GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				this.loadTexture("/terrain.png");
				float f7 = 0.25F;
				int j = block.getRenderType();

				if (j == 1 || j == 19 || j == 12 || j == 2)
				{
					f7 = 0.5F;
				}

				GL11.glScalef(f7, f7, f7);

				for (i = 0; i < b0; ++i)
				{
					GL11.glPushMatrix();

					if (i > 0)
					{
						f5 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f7;
						f4 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f7;
						f6 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / f7;
						GL11.glTranslatef(f5, f4, f6);
					}

					f5 = 1.0F;
					this.itemRenderBlocks.renderBlockAsItem(block, itemstack.getItemDamage(), f5);
					GL11.glPopMatrix();
				}
			}
			else
			{
				float f8;

				if (itemstack.getItem().requiresMultipleRenderPasses())
				{
					if (field_82407_g)
					{
						GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
						GL11.glTranslatef(0.0F, -0.05F, 0.0F);
					}
					else
					{
						GL11.glScalef(0.5F, 0.5F, 0.5F);
					}

					this.loadTexture("/gui/items.png");

					for (int k = 0; k <= itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); ++k)
					{
						this.random.setSeed(187L);
						Icon icon = itemstack.getItem().getIcon(itemstack, k);
						f8 = 1.0F;

						if (this.field_77024_a)
						{
							i = Item.itemsList[itemstack.itemID].getColorFromItemStack(itemstack, k);
							f5 = (float) (i >> 16 & 255) / 255.0F;
							f4 = (float) (i >> 8 & 255) / 255.0F;
							f6 = (float) (i & 255) / 255.0F;
							GL11.glColor4f(f5 * f8, f4 * f8, f6 * f8, 1.0F);
							this.renderDroppedItem(par1EntityItem, icon, b0, par9, f5 * f8, f4 * f8, f6 * f8);
						}
						else
						{
							this.renderDroppedItem(par1EntityItem, icon, b0, par9, 1.0F, 1.0F, 1.0F);
						}
					}
				}
				else
				{
					if (field_82407_g)
					{
						GL11.glScalef(0.5128205F, 0.5128205F, 0.5128205F);
						GL11.glTranslatef(0.0F, -0.05F, 0.0F);
					}
					else
					{
						GL11.glScalef(0.5F, 0.5F, 0.5F);
					}

					Icon icon1 = itemstack.getIconIndex();

					if (itemstack.func_94608_d() == 0)
					{
						this.loadTexture("/terrain.png");
					}
					else
					{
						this.loadTexture("/gui/items.png");
					}

					if (this.field_77024_a)
					{
						int l = Item.itemsList[itemstack.itemID].getColorFromItemStack(itemstack, 0);
						f8 = (float) (l >> 16 & 255) / 255.0F;
						float f9 = (float) (l >> 8 & 255) / 255.0F;
						f5 = (float) (l & 255) / 255.0F;
						f4 = 1.0F;
						this.renderDroppedItem(par1EntityItem, icon1, b0, par9, f8 * f4, f9 * f4, f5 * f4);
					}
					else
					{
						this.renderDroppedItem(par1EntityItem, icon1, b0, par9, 1.0F, 1.0F, 1.0F);
					}
				}
			}

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
		}
	}

	/**
	 * Renders a dropped item
	 */
	private void renderDroppedItem (EntityItem par1EntityItem, Icon par2Icon, int par3, float par4, float par5, float par6, float par7)
	{
		Tessellator tessellator = Tessellator.instance;

		if (par2Icon == null)
		{
			par2Icon = this.renderManager.renderEngine.func_96448_c(par1EntityItem.getEntityItem().func_94608_d());
		}

		float f4 = par2Icon.func_94209_e();
		float f5 = par2Icon.func_94212_f();
		float f6 = par2Icon.func_94206_g();
		float f7 = par2Icon.func_94210_h();
		float f8 = 1.0F;
		float f9 = 0.5F;
		float f10 = 0.25F;
		float f11;

		GL11.glPushMatrix();

		if (field_82407_g)
		{
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		}
		else
		{
			GL11.glRotatef((((float) par1EntityItem.age + par4) / 20.0F + par1EntityItem.hoverStart) * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
		}

		float f12 = 0.0625F;
		f11 = 0.021875F;
		ItemStack itemstack = par1EntityItem.getEntityItem();
		int j = itemstack.stackSize;
		byte b0 = getMiniItemCount(itemstack);

		GL11.glTranslatef(-f9, -f10, -((f12 + f11) * (float) b0 / 2.0F));

		for (int k = 0; k < b0; ++k)
		{
			// Makes items offset when in 3D, like when in 2D, looks much better. Considered a vanilla bug...
			if (k > 0 && shouldSpreadItems())
			{
				float x = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
				float y = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
				float z = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
				GL11.glTranslatef(x, y, f12 + f11);
			}
			else
			{
				GL11.glTranslatef(0f, 0f, f12 + f11);
			}

			if (itemstack.func_94608_d() == 0 && itemstack.itemID < Block.blocksList.length && Block.blocksList[itemstack.itemID] != null)
			{
				this.loadTexture("/terrain.png");
			}
			else
			{
				this.loadTexture("/gui/items.png");
			}

			GL11.glColor4f(par5, par6, par7, 1.0F);
			ItemRenderer.renderItemIn2D(tessellator, f5, f6, f4, f7, par2Icon.func_94213_j(), par2Icon.func_94208_k(), f12);

			if (itemstack != null && itemstack.hasEffect())
			{
				GL11.glDepthFunc(GL11.GL_EQUAL);
				GL11.glDisable(GL11.GL_LIGHTING);
				this.renderManager.renderEngine.func_98187_b("%blur%/misc/glint.png");
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
				float f13 = 0.76F;
				GL11.glColor4f(0.5F * f13, 0.25F * f13, 0.8F * f13, 1.0F);
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glPushMatrix();
				float f14 = 0.125F;
				GL11.glScalef(f14, f14, f14);
				float f15 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
				GL11.glTranslatef(f15, 0.0F, 0.0F);
				GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
				ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f12);
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glScalef(f14, f14, f14);
				f15 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
				GL11.glTranslatef(-f15, 0.0F, 0.0F);
				GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
				ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f12);
				GL11.glPopMatrix();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}
		}

		GL11.glPopMatrix();

	}

	/**
	 * Renders the item's icon or block into the UI at the specified position.
	 */
	public void renderItemIntoGUI (FontRenderer par1FontRenderer, RenderEngine par2RenderEngine, ItemStack par3ItemStack, int par4, int par5)
	{
		int k = par3ItemStack.itemID;
		int l = par3ItemStack.getItemDamage();
		Icon icon = par3ItemStack.getIconIndex();
		float f;
		float f1;
		float f2;

		Block block = (k < Block.blocksList.length ? Block.blocksList[k] : null);

		if (par3ItemStack.func_94608_d() == 0 && block != null && RenderBlocks.renderItemIn3d(Block.blocksList[k].getRenderType()))
		{
			par2RenderEngine.func_98187_b("/terrain.png");
			GL11.glPushMatrix();
			GL11.glTranslatef((float) (par4 - 2), (float) (par5 + 3), -3.0F + this.zLevel);
			GL11.glScalef(10.0F, 10.0F, 10.0F);
			GL11.glTranslatef(1.0F, 0.5F, 1.0F);
			GL11.glScalef(1.0F, 1.0F, -1.0F);
			GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			int i1 = Item.itemsList[k].getColorFromItemStack(par3ItemStack, 0);
			f2 = (float) (i1 >> 16 & 255) / 255.0F;
			f = (float) (i1 >> 8 & 255) / 255.0F;
			f1 = (float) (i1 & 255) / 255.0F;

			if (this.field_77024_a)
			{
				GL11.glColor4f(f2, f, f1, 1.0F);
			}

			GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			this.itemRenderBlocks.useInventoryTint = this.field_77024_a;
			this.itemRenderBlocks.renderBlockAsItem(block, l, 1.0F);
			this.itemRenderBlocks.useInventoryTint = true;
			GL11.glPopMatrix();
		}
		else
		{
			int j1;

			if (Item.itemsList[k].requiresMultipleRenderPasses())
			{
				GL11.glDisable(GL11.GL_LIGHTING);
				par2RenderEngine.func_98187_b("/gui/items.png");

				for (j1 = 0; j1 < Item.itemsList[k].getRenderPasses(l); ++j1)
				{
					Icon icon1 = Item.itemsList[k].getIcon(par3ItemStack, j1);
					int k1 = Item.itemsList[k].getColorFromItemStack(par3ItemStack, j1);
					f = (float) (k1 >> 16 & 255) / 255.0F;
					f1 = (float) (k1 >> 8 & 255) / 255.0F;
					float f3 = (float) (k1 & 255) / 255.0F;

					if (this.field_77024_a)
					{
						GL11.glColor4f(f, f1, f3, 1.0F);
					}

					this.func_94149_a(par4, par5, icon1, 16, 16);
				}

				GL11.glEnable(GL11.GL_LIGHTING);
			}
			else
			{
				GL11.glDisable(GL11.GL_LIGHTING);

				if (par3ItemStack.func_94608_d() == 0)
				{
					par2RenderEngine.func_98187_b("/terrain.png");
				}
				else
				{
					par2RenderEngine.func_98187_b("/gui/items.png");
				}

				if (icon == null)
				{
					icon = par2RenderEngine.func_96448_c(par3ItemStack.func_94608_d());
				}

				j1 = Item.itemsList[k].getColorFromItemStack(par3ItemStack, 0);
				float f4 = (float) (j1 >> 16 & 255) / 255.0F;
				f2 = (float) (j1 >> 8 & 255) / 255.0F;
				f = (float) (j1 & 255) / 255.0F;

				if (this.field_77024_a)
				{
					GL11.glColor4f(f4, f2, f, 1.0F);
				}

				this.func_94149_a(par4, par5, icon, 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
			}
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	/**
	 * Render the item's icon or block into the GUI, including the glint effect.
	 */
	public void renderItemAndEffectIntoGUI (FontRenderer par1FontRenderer, RenderEngine par2RenderEngine, ItemStack par3ItemStack, int par4, int par5)
	{
		if (par3ItemStack != null)
		{
			if (!ForgeHooksClient.renderInventoryItem(renderBlocks, par2RenderEngine, par3ItemStack, field_77024_a, zLevel, (float) par4, (float) par5))
			{
				this.renderItemIntoGUI(par1FontRenderer, par2RenderEngine, par3ItemStack, par4, par5);
			}

			if (par3ItemStack.hasEffect())
			{
				GL11.glDepthFunc(GL11.GL_GREATER);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDepthMask(false);
				par2RenderEngine.func_98187_b("%blur%/misc/glint.png");
				this.zLevel -= 50.0F;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
				GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
				this.func_77018_a(par4 * 431278612 + par5 * 32178161, par4 - 2, par5 - 2, 20, 20);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDepthMask(true);
				this.zLevel += 50.0F;
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}
		}
	}

	private void func_77018_a (int par1, int par2, int par3, int par4, int par5)
	{
		for (int j1 = 0; j1 < 2; ++j1)
		{
			if (j1 == 0)
			{
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			}

			if (j1 == 1)
			{
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			}

			float f = 0.00390625F;
			float f1 = 0.00390625F;
			float f2 = (float) (Minecraft.getSystemTime() % (long) (3000 + j1 * 1873)) / (3000.0F + (float) (j1 * 1873)) * 256.0F;
			float f3 = 0.0F;
			Tessellator tessellator = Tessellator.instance;
			float f4 = 4.0F;

			if (j1 == 1)
			{
				f4 = -1.0F;
			}

			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV((double) (par2 + 0), (double) (par3 + par5), (double) this.zLevel, (double) ((f2 + (float) par5 * f4) * f), (double) ((f3 + (float) par5) * f1));
			tessellator.addVertexWithUV((double) (par2 + par4), (double) (par3 + par5), (double) this.zLevel, (double) ((f2 + (float) par4 + (float) par5 * f4) * f), (double) ((f3 + (float) par5) * f1));
			tessellator.addVertexWithUV((double) (par2 + par4), (double) (par3 + 0), (double) this.zLevel, (double) ((f2 + (float) par4) * f), (double) ((f3 + 0.0F) * f1));
			tessellator.addVertexWithUV((double) (par2 + 0), (double) (par3 + 0), (double) this.zLevel, (double) ((f2 + 0.0F) * f), (double) ((f3 + 0.0F) * f1));
			tessellator.draw();
		}
	}

	/**
	 * Renders the item's overlay information. Examples being stack count or damage on top of the item's image at the
	 * specified position.
	 */
	public void renderItemOverlayIntoGUI (FontRenderer par1FontRenderer, RenderEngine par2RenderEngine, ItemStack par3ItemStack, int par4, int par5)
	{
		this.func_94148_a(par1FontRenderer, par2RenderEngine, par3ItemStack, par4, par5, (String) null);
	}

	public void func_94148_a (FontRenderer par1FontRenderer, RenderEngine par2RenderEngine, ItemStack par3ItemStack, int par4, int par5, String par6Str)
	{
		if (par3ItemStack != null)
		{
			if (par3ItemStack.stackSize > 1 || par6Str != null)
			{
				String s1 = par6Str == null ? String.valueOf(par3ItemStack.stackSize) : par6Str;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				par1FontRenderer.drawStringWithShadow(s1, par4 + 19 - 2 - par1FontRenderer.getStringWidth(s1), par5 + 6 + 3, 16777215);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}

			if (par3ItemStack.isItemDamaged())
			{
				int k = (int) Math.round(13.0D - (double) par3ItemStack.getItemDamageForDisplay() * 13.0D / (double) par3ItemStack.getMaxDamage());
				int l = (int) Math.round(255.0D - (double) par3ItemStack.getItemDamageForDisplay() * 255.0D / (double) par3ItemStack.getMaxDamage());
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				Tessellator tessellator = Tessellator.instance;
				int i1 = 255 - l << 16 | l << 8;
				int j1 = (255 - l) / 4 << 16 | 16128;
				this.renderQuad(tessellator, par4 + 2, par5 + 13, 13, 2, 0);
				this.renderQuad(tessellator, par4 + 2, par5 + 13, 12, 1, j1);
				this.renderQuad(tessellator, par4 + 2, par5 + 13, k, 1, i1);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}

	/**
	 * Adds a quad to the tesselator at the specified position with the set width and height and color.  Args:
	 * tessellator, x, y, width, height, color
	 */
	private void renderQuad (Tessellator par1Tessellator, int par2, int par3, int par4, int par5, int par6)
	{
		par1Tessellator.startDrawingQuads();
		par1Tessellator.setColorOpaque_I(par6);
		par1Tessellator.addVertex((double) (par2 + 0), (double) (par3 + 0), 0.0D);
		par1Tessellator.addVertex((double) (par2 + 0), (double) (par3 + par5), 0.0D);
		par1Tessellator.addVertex((double) (par2 + par4), (double) (par3 + par5), 0.0D);
		par1Tessellator.addVertex((double) (par2 + par4), (double) (par3 + 0), 0.0D);
		par1Tessellator.draw();
	}

	public void func_94149_a (int par1, int par2, Icon par3Icon, int par4, int par5)
	{
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + par5), (double) this.zLevel, (double) par3Icon.func_94209_e(), (double) par3Icon.func_94210_h());
		tessellator.addVertexWithUV((double) (par1 + par4), (double) (par2 + par5), (double) this.zLevel, (double) par3Icon.func_94212_f(), (double) par3Icon.func_94210_h());
		tessellator.addVertexWithUV((double) (par1 + par4), (double) (par2 + 0), (double) this.zLevel, (double) par3Icon.func_94212_f(), (double) par3Icon.func_94206_g());
		tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + 0), (double) this.zLevel, (double) par3Icon.func_94209_e(), (double) par3Icon.func_94206_g());
		tessellator.draw();
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
	 */
	public void doRender (Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
	{
		this.doRenderItem((EntityItem) par1Entity, par2, par4, par6, par8, par9);
	}

	/**
	 * Items should spread out when rendered in 3d?
	 * @return
	 */
	public boolean shouldSpreadItems ()
	{
		return true;
	}

	/**
	 * Items should have a bob effect
	 * @return
	 */
	public boolean shouldBob ()
	{
		return true;
	}

	public byte getMiniBlockCount (ItemStack stack)
	{
		byte ret = 1;
		if (stack.stackSize > 1)
			ret = 2;
		if (stack.stackSize > 5)
			ret = 3;
		if (stack.stackSize > 20)
			ret = 4;
		if (stack.stackSize > 40)
			ret = 5;
		return ret;
	}

	/**
	 * Allows for a subclass to override how many rendered items appear in a
	 * "mini item 3d stack"
	 * @param stack
	 * @return
	 */
	public byte getMiniItemCount (ItemStack stack)
	{
		byte ret = 1;
		if (stack.stackSize > 1)
			ret = 2;
		if (stack.stackSize > 15)
			ret = 3;
		if (stack.stackSize > 31)
			ret = 4;
		return ret;
	}
}
