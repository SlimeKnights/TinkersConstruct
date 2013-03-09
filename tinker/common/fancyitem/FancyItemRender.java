package tinker.common.fancyitem;

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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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
    public void doRenderItem(EntityItem par1EntityItem, double par2, double par4, double par6, float par8, float par9)
    {
        this.random.setSeed(187L);
        ItemStack var10 = par1EntityItem.getEntityItem();

        if (var10.getItem() != null)
        {
            GL11.glPushMatrix();
            float var11 = shouldBob() ? MathHelper.sin(((float)par1EntityItem.age + par9) / 10.0F + par1EntityItem.hoverStart) * 0.1F + 0.1F : 0F;
            float var12 = (((float)par1EntityItem.age + par9) / 20.0F + par1EntityItem.hoverStart) * (180F / (float)Math.PI);
            byte var13 = getMiniBlockCountForItemStack(var10);

            GL11.glTranslatef((float)par2, (float)par4 + var11, (float)par6);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            int var16;
            float var19;
            float var20;
            float var24;

            if (ForgeHooksClient.renderEntityItem(par1EntityItem, var10, var11, var12, random, renderManager.renderEngine, renderBlocks))
            {
                ;
            }
            else if (var10.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.blocksList[var10.itemID].getRenderType()))
            {
                GL11.glRotatef(var12, 0.0F, 1.0F, 0.0F);

                if (field_82407_g)
                {
                    GL11.glScalef(1.25F, 1.25F, 1.25F);
                    GL11.glTranslatef(0.0F, 0.05F, 0.0F);
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                this.loadTexture(Block.blocksList[var10.itemID].getTextureFile());
                float var22 = 0.25F;
                var16 = Block.blocksList[var10.itemID].getRenderType();

                if (var16 == 1 || var16 == 19 || var16 == 12 || var16 == 2)
                {
                    var22 = 0.5F;
                }

                GL11.glScalef(var22, var22, var22);

                for (int var23 = 0; var23 < var13; ++var23)
                {
                    GL11.glPushMatrix();

                    if (var23 > 0)
                    {
                        var24 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var22;
                        var19 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var22;
                        var20 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.2F / var22;
                        GL11.glTranslatef(var24, var19, var20);
                    }

                    var24 = 1.0F;
                    this.itemRenderBlocks.renderBlockAsItem(Block.blocksList[var10.itemID], var10.getItemDamage(), var24);
                    GL11.glPopMatrix();
                }
            }
            else
            {
                int var15;
                float var17;

                if (var10.getItem().requiresMultipleRenderPasses())
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


                    for (var15 = 0; var15 <= var10.getItem().getRenderPasses(var10.getItemDamage()); ++var15)
                    {
                        this.loadTexture(Item.itemsList[var10.itemID].getTextureFile());
                        this.random.setSeed(187L);
                        var16 = var10.getItem().getIconIndex(var10, var15);
                        var17 = 1.0F;

                        if (this.field_77024_a)
                        {
                            int var18 = Item.itemsList[var10.itemID].getColorFromItemStack(var10, var15);
                            var19 = (float)(var18 >> 16 & 255) / 255.0F;
                            var20 = (float)(var18 >> 8 & 255) / 255.0F;
                            float var21 = (float)(var18 & 255) / 255.0F;
                            GL11.glColor4f(var19 * var17, var20 * var17, var21 * var17, 1.0F);
                            this.func_77020_a(par1EntityItem, var16, var13, par9, var19 * var17, var20 * var17, var21 * var17);
                        }
                        else
                        {
                            this.func_77020_a(par1EntityItem, var16, var13, par9, 1.0F, 1.0F, 1.0F);
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

                    var15 = var10.getIconIndex();

                    this.loadTexture(var10.getItem().getTextureFile());

                    if (this.field_77024_a)
                    {
                        var16 = Item.itemsList[var10.itemID].getColorFromItemStack(var10, 0);
                        var17 = (float)(var16 >> 16 & 255) / 255.0F;
                        var24 = (float)(var16 >> 8 & 255) / 255.0F;
                        var19 = (float)(var16 & 255) / 255.0F;
                        var20 = 1.0F;
                        this.func_77020_a(par1EntityItem, var15, var13, par9, var17 * var20, var24 * var20, var19 * var20);
                    }
                    else
                    {
                        this.func_77020_a(par1EntityItem, var15, var13, par9, 1.0F, 1.0F, 1.0F);
                    }
                }
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        }
    }

    private void func_77020_a(EntityItem par1EntityItem, int par2, int par3, float par4, float par5, float par6, float par7)
    {
        Tessellator var8 = Tessellator.instance;
        float var9 = (float)(par2 % 16 * 16 + 0) / 256.0F;
        float var10 = (float)(par2 % 16 * 16 + 16) / 256.0F;
        float var11 = (float)(par2 / 16 * 16 + 0) / 256.0F;
        float var12 = (float)(par2 / 16 * 16 + 16) / 256.0F;
        float var13 = 1.0F;
        float var14 = 0.5F;
        float var15 = 0.25F;
        float var17;

        //if (true)
        //{
            GL11.glPushMatrix();

            if (field_82407_g)
            {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                GL11.glRotatef((((float)par1EntityItem.age + par4) / 20.0F + par1EntityItem.hoverStart) * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            float var16 = 0.0625F;
            var17 = 0.021875F;
            ItemStack var18 = par1EntityItem.getEntityItem();
            int var19 = var18.stackSize;
            byte var24 = getMiniItemCountForItemStack(var18);


            GL11.glTranslatef(-var14, -var15, -((var16 + var17) * (float)var24 / 2.0F));

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

                this.loadTexture(Item.itemsList[var18.itemID].getTextureFile());

                GL11.glColor4f(par5, par6, par7, 1.0F);
                ItemRenderer.renderItemIn2D(var8, var10, var11, var9, var12, var16);

                if (var18 != null && var18.hasEffect())
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
                    float var23 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                    GL11.glTranslatef(var23, 0.0F, 0.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    ItemRenderer.renderItemIn2D(var8, 0.0F, 0.0F, 1.0F, 1.0F, var16);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glScalef(var22, var22, var22);
                    var23 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
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
        //}
        /*else
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

                if (!field_82407_g)
                {
                    GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                }

                GL11.glColor4f(par5, par6, par7, 1.0F);
                var8.startDrawingQuads();
                var8.setNormal(0.0F, 1.0F, 0.0F);
                var8.addVertexWithUV((double)(0.0F - var14), (double)(0.0F - var15), 0.0D, (double)var9, (double)var12);
                var8.addVertexWithUV((double)(var13 - var14), (double)(0.0F - var15), 0.0D, (double)var10, (double)var12);
                var8.addVertexWithUV((double)(var13 - var14), (double)(1.0F - var15), 0.0D, (double)var10, (double)var11);
                var8.addVertexWithUV((double)(0.0F - var14), (double)(1.0F - var15), 0.0D, (double)var9, (double)var11);
                var8.draw();
                GL11.glPopMatrix();
            }
        }*/
    }

    /**
     * Renders the item's icon or block into the UI at the specified position.
     */
    public void renderItemIntoGUI(FontRenderer par1FontRenderer, RenderEngine par2RenderEngine, ItemStack par3ItemStack, int par4, int par5)
    {
        int var6 = par3ItemStack.itemID;
        int var7 = par3ItemStack.getItemDamage();
        int var8 = par3ItemStack.getIconIndex();
        int var10;
        float var12;
        float var13;
        float var16;

        if (par3ItemStack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.blocksList[par3ItemStack.itemID].getRenderType()))
        {
            Block var15 = Block.blocksList[var6];
            par2RenderEngine.bindTexture(par2RenderEngine.getTexture(var15.getTextureFile()));
            GL11.glPushMatrix();
            GL11.glTranslatef((float)(par4 - 2), (float)(par5 + 3), -3.0F + this.zLevel);
            GL11.glScalef(10.0F, 10.0F, 10.0F);
            GL11.glTranslatef(1.0F, 0.5F, 1.0F);
            GL11.glScalef(1.0F, 1.0F, -1.0F);
            GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            var10 = Item.itemsList[var6].getColorFromItemStack(par3ItemStack, 0);
            var16 = (float)(var10 >> 16 & 255) / 255.0F;
            var12 = (float)(var10 >> 8 & 255) / 255.0F;
            var13 = (float)(var10 & 255) / 255.0F;

            if (this.field_77024_a)
            {
                GL11.glColor4f(var16, var12, var13, 1.0F);
            }

            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            this.itemRenderBlocks.useInventoryTint = this.field_77024_a;
            this.itemRenderBlocks.renderBlockAsItem(var15, var7, 1.0F);
            this.itemRenderBlocks.useInventoryTint = true;
            GL11.glPopMatrix();
        }
        else
        {
            int var9;

            if (Item.itemsList[var6].requiresMultipleRenderPasses())
            {
                GL11.glDisable(GL11.GL_LIGHTING);
                par2RenderEngine.bindTexture(par2RenderEngine.getTexture(Item.itemsList[var6].getTextureFile()));

                for (var9 = 0; var9 < Item.itemsList[var6].getRenderPasses(var7); ++var9)
                {
                    var10 = Item.itemsList[var6].getIconIndex(par3ItemStack, var9);
                    int var11 = Item.itemsList[var6].getColorFromItemStack(par3ItemStack, var9);
                    var12 = (float)(var11 >> 16 & 255) / 255.0F;
                    var13 = (float)(var11 >> 8 & 255) / 255.0F;
                    float var14 = (float)(var11 & 255) / 255.0F;

                    if (this.field_77024_a)
                    {
                        GL11.glColor4f(var12, var13, var14, 1.0F);
                    }

                    this.renderTexturedQuad(par4, par5, var10 % 16 * 16, var10 / 16 * 16, 16, 16);
                }

                GL11.glEnable(GL11.GL_LIGHTING);
            }
            else if (var8 >= 0)
            {
                GL11.glDisable(GL11.GL_LIGHTING);

                par2RenderEngine.bindTexture(par2RenderEngine.getTexture(par3ItemStack.getItem().getTextureFile()));

                var9 = Item.itemsList[var6].getColorFromItemStack(par3ItemStack, 0);
                float var17 = (float)(var9 >> 16 & 255) / 255.0F;
                var16 = (float)(var9 >> 8 & 255) / 255.0F;
                var12 = (float)(var9 & 255) / 255.0F;

                if (this.field_77024_a)
                {
                    GL11.glColor4f(var17, var16, var12, 1.0F);
                }

                this.renderTexturedQuad(par4, par5, var8 % 16 * 16, var8 / 16 * 16, 16, 16);
                GL11.glEnable(GL11.GL_LIGHTING);
            }
        }

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    /**
     * Render the item's icon or block into the GUI, including the glint effect.
     */
    public void renderItemAndEffectIntoGUI(FontRenderer par1FontRenderer, RenderEngine par2RenderEngine, ItemStack par3ItemStack, int par4, int par5)
    {
        if (par3ItemStack != null)
        {
            if (!ForgeHooksClient.renderInventoryItem(renderBlocks, par2RenderEngine, par3ItemStack, field_77024_a, zLevel, (float)par4, (float)par5))
            {
                this.renderItemIntoGUI(par1FontRenderer, par2RenderEngine, par3ItemStack, par4, par5);
            }

            if (par3ItemStack != null && par3ItemStack.hasEffect())
            {
                GL11.glDepthFunc(GL11.GL_GREATER);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDepthMask(false);
                par2RenderEngine.bindTexture(par2RenderEngine.getTexture("%blur%/misc/glint.png"));
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

    private void func_77018_a(int par1, int par2, int par3, int par4, int par5)
    {
        for (int var6 = 0; var6 < 2; ++var6)
        {
            if (var6 == 0)
            {
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            }

            if (var6 == 1)
            {
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            }

            float var7 = 0.00390625F;
            float var8 = 0.00390625F;
            float var9 = (float)(Minecraft.getSystemTime() % (long)(3000 + var6 * 1873)) / (3000.0F + (float)(var6 * 1873)) * 256.0F;
            float var10 = 0.0F;
            Tessellator var11 = Tessellator.instance;
            float var12 = 4.0F;

            if (var6 == 1)
            {
                var12 = -1.0F;
            }

            var11.startDrawingQuads();
            var11.addVertexWithUV((double)(par2 + 0), (double)(par3 + par5), (double)this.zLevel, (double)((var9 + (float)par5 * var12) * var7), (double)((var10 + (float)par5) * var8));
            var11.addVertexWithUV((double)(par2 + par4), (double)(par3 + par5), (double)this.zLevel, (double)((var9 + (float)par4 + (float)par5 * var12) * var7), (double)((var10 + (float)par5) * var8));
            var11.addVertexWithUV((double)(par2 + par4), (double)(par3 + 0), (double)this.zLevel, (double)((var9 + (float)par4) * var7), (double)((var10 + 0.0F) * var8));
            var11.addVertexWithUV((double)(par2 + 0), (double)(par3 + 0), (double)this.zLevel, (double)((var9 + 0.0F) * var7), (double)((var10 + 0.0F) * var8));
            var11.draw();
        }
    }

    /**
     * Renders the item's overlay information. Examples being stack count or damage on top of the item's image at the
     * specified position.
     */
    public void renderItemOverlayIntoGUI(FontRenderer par1FontRenderer, RenderEngine par2RenderEngine, ItemStack par3ItemStack, int par4, int par5)
    {
        if (par3ItemStack != null)
        {
            if (par3ItemStack.stackSize > 1)
            {
                String var6 = "" + par3ItemStack.stackSize;
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                par1FontRenderer.drawStringWithShadow(var6, par4 + 19 - 2 - par1FontRenderer.getStringWidth(var6), par5 + 6 + 3, 16777215);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }

            if (par3ItemStack.isItemDamaged())
            {
                int var11 = (int)Math.round(13.0D - (double)par3ItemStack.getItemDamageForDisplay() * 13.0D / (double)par3ItemStack.getMaxDamage());
                int var7 = (int)Math.round(255.0D - (double)par3ItemStack.getItemDamageForDisplay() * 255.0D / (double)par3ItemStack.getMaxDamage());
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                Tessellator var8 = Tessellator.instance;
                int var9 = 255 - var7 << 16 | var7 << 8;
                int var10 = (255 - var7) / 4 << 16 | 16128;
                this.renderQuad(var8, par4 + 2, par5 + 13, 13, 2, 0);
                this.renderQuad(var8, par4 + 2, par5 + 13, 12, 1, var10);
                this.renderQuad(var8, par4 + 2, par5 + 13, var11, 1, var9);
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
    private void renderQuad(Tessellator par1Tessellator, int par2, int par3, int par4, int par5, int par6)
    {
        par1Tessellator.startDrawingQuads();
        par1Tessellator.setColorOpaque_I(par6);
        par1Tessellator.addVertex((double)(par2 + 0), (double)(par3 + 0), 0.0D);
        par1Tessellator.addVertex((double)(par2 + 0), (double)(par3 + par5), 0.0D);
        par1Tessellator.addVertex((double)(par2 + par4), (double)(par3 + par5), 0.0D);
        par1Tessellator.addVertex((double)(par2 + par4), (double)(par3 + 0), 0.0D);
        par1Tessellator.draw();
    }

    /**
     * Adds a textured quad to the tesselator at the specified position with the specified texture coords, width and
     * height.  Args: x, y, u, v, width, height
     */
    public void renderTexturedQuad(int par1, int par2, int par3, int par4, int par5, int par6)
    {
        float var7 = 0.00390625F;
        float var8 = 0.00390625F;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)this.zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + par6) * var8));
        var9.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + par5) * var7), (double)((float)(par4 + 0) * var8));
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.zLevel, (double)((float)(par3 + 0) * var7), (double)((float)(par4 + 0) * var8));
        var9.draw();
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRenderItem((EntityItem)par1Entity, par2, par4, par6, par8, par9);
    }

    /* ==== Forge start ===== */
    /**
     * Items should spread out when rendered in 3d?
     * @return
     */
    public boolean shouldSpreadItems()
    {
        return true;
    }

    /**
     * Items should have a bob effect
     * @return
     */
    public boolean shouldBob()
    {
        return true;
    }

    public byte getMiniBlockCountForItemStack(ItemStack stack)
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

    /**
     * Allows for a subclass to override how many rendered items appear in a
     * "mini item 3d stack"
     * @param stack
     * @return
     */
    public byte getMiniItemCountForItemStack(ItemStack stack)
    {
        byte var24;
        int var19 = stack.stackSize;
        if (var19 < 2)
        {
            var24 = 1;
        }
        else if (var19 < 16)
        {
            var24 = 2;
        }
        else if (var19 < 32)
        {
            var24 = 3;
        }
        else
        {
            var24 = 4;
        }
        return var24;
    }
}
