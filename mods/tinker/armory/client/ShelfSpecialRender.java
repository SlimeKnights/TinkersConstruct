package mods.tinker.armory.client;

import mods.tinker.armory.content.ToolrackLogic;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/* Special renderer, only used for drawing tools */

@SideOnly(Side.CLIENT)
public class ShelfSpecialRender extends TileEntitySpecialRenderer
{
	@Override
    public void renderTileEntityAt(TileEntity logic, double var2, double var4, double var6, float var8)
    {
        //this.render((ToolrackLogic)logic, var2, var4, var6, var8);
    }

    /*public void render(ToolrackLogic logic, double posX, double posY, double posZ, float var8)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)posX + 0.25F, (float)posY + 0.5F, (float)posZ + 0.094F); //Center on the block
        int facing = logic.getBlockMetadata() / 4;
        GL11.glRotatef(90.0F * (float)facing, 0.0F, 1.0F, 0.0F); //Rotation angle
        //GL11.glTranslatef(-0.25F, 0.25F, -0.3F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        for (int slot = 0; slot < logic.getSizeInventory(); slot++)
        {
            ItemStack stack = logic.getStackInSlot(slot);
            if (stack != null)
            {
                this.bindTextureByName(stack.getItem().getTextureFile());
                GL11.glPushMatrix();
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                //GL11.glScalef(3F, 3F, 3F);
                //GL11.glRotatef(logic.getAngle(var10), 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(45f, 0.0F, 0.0F, 1.0F);
                int iconIndex;
                int color;

                Item item = stack.getItem();
                int damage = stack.getItemDamage();
                if (item.requiresMultipleRenderPasses())
                {
                    for (int renderPass = 0; renderPass <= item.getRenderPasses(damage); ++renderPass)
                    {
                    	iconIndex = item.getIconIndex(stack, renderPass);
                        color = stack.getItem().getColorFromItemStack(stack, iconIndex);
                        setColor(color);
                        this.drawItem(iconIndex);
                    }
                }
                else
                {
                    iconIndex = stack.getIconIndex();
                    color = Item.itemsList[stack.itemID].getColorFromItemStack(stack, 0);
                    setColor(color);
                    this.drawItem(iconIndex);
                }

                GL11.glPopMatrix();
            }

            GL11.glTranslatef(0.475F, 0.0F, 0.001F);
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }*/

    void drawItem(int iconIndex)
    {
    	//System.out.println("Drawing an item");
        Tessellator tessellator = Tessellator.instance;
        float topLeft = (float)(iconIndex % 16 * 16 + 0) / 256.0F;
        float topRight = (float)(iconIndex % 16 * 16 + 16) / 256.0F;
        float botLeft = (float)(iconIndex / 16 * 16 + 0) / 256.0F;
        float botRight = (float)(iconIndex / 16 * 16 + 16) / 256.0F;
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV(-0.5D, -0.5D, 0.0D, (double)topLeft, (double)botRight);
        tessellator.addVertexWithUV(0.5D, -0.5D, 0.0D, (double)topRight, (double)botRight);
        tessellator.addVertexWithUV(0.5D, 0.5D, 0.0D, (double)topRight, (double)botLeft);
        tessellator.addVertexWithUV(-0.5D, 0.5D, 0.0D, (double)topLeft, (double)botLeft);
        tessellator.addVertexWithUV(-0.5D, 0.5D, 0.0D, (double)topLeft, (double)botLeft);
        tessellator.addVertexWithUV(0.5D, 0.5D, 0.0D, (double)topRight, (double)botLeft);
        tessellator.addVertexWithUV(0.5D, -0.5D, 0.0D, (double)topRight, (double)botRight);
        tessellator.addVertexWithUV(-0.5D, -0.5D, 0.0D, (double)topLeft, (double)botRight);
        tessellator.draw();
        /*tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
        
        float var6 = 1.0F;
        float var7 = 0.0625F;
        int var8;
        float var9;
        float var10;
        float var11;

        int tileSize = TextureFXManager.instance().getTextureDimensions(GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)).width / 16;
        
        float tx = 1.0f / (32 * tileSize);
        float tz = 1.0f /  tileSize;

        for (var8 = 0; var8 < tileSize; ++var8)
        {
            var9 = (float)var8 / tileSize;
            var10 = topLeft + (topRight - topLeft) * var9 - tx;
            var11 = var6 * var9;
            tessellator.addVertexWithUV((double)var11, 0.0D, (double)(0.0F - var7), (double)var10, (double)botRight);
            tessellator.addVertexWithUV((double)var11, 0.0D, 0.0D, (double)var10, (double)botRight);
            tessellator.addVertexWithUV((double)var11, 1.0D, 0.0D, (double)var10, (double)botLeft);
            tessellator.addVertexWithUV((double)var11, 1.0D, (double)(0.0F - var7), (double)var10, (double)botLeft);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);

        for (var8 = 0; var8 < tileSize; ++var8)
        {
            var9 = (float)var8 / tileSize;
            var10 = topLeft + (topRight - topLeft) * var9 - tx;
            var11 = var6 * var9 + tz;
            tessellator.addVertexWithUV((double)var11, 1.0D, (double)(0.0F - var7), (double)var10, (double)botLeft);
            tessellator.addVertexWithUV((double)var11, 1.0D, 0.0D, (double)var10, (double)botLeft);
            tessellator.addVertexWithUV((double)var11, 0.0D, 0.0D, (double)var10, (double)botRight);
            tessellator.addVertexWithUV((double)var11, 0.0D, (double)(0.0F - var7), (double)var10, (double)botRight);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);

        for (var8 = 0; var8 < tileSize; ++var8)
        {
            var9 = (float)var8 / tileSize;
            var10 = botRight + (botLeft - botRight) * var9 - tx;
            var11 = var6 * var9 + tz;
            tessellator.addVertexWithUV(0.0D, (double)var11, 0.0D, (double)topLeft, (double)var10);
            tessellator.addVertexWithUV((double)var6, (double)var11, 0.0D, (double)topRight, (double)var10);
            tessellator.addVertexWithUV((double)var6, (double)var11, (double)(0.0F - var7), (double)topRight, (double)var10);
            tessellator.addVertexWithUV(0.0D, (double)var11, (double)(0.0F - var7), (double)topLeft, (double)var10);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);

        for (var8 = 0; var8 < tileSize; ++var8)
        {
            var9 = (float)var8 / tileSize;
            var10 = botRight + (botLeft - botRight) * var9 - tx;
            var11 = var6 * var9;
            tessellator.addVertexWithUV((double)var6, (double)var11, 0.0D, (double)topRight, (double)var10);
            tessellator.addVertexWithUV(0.0D, (double)var11, 0.0D, (double)topLeft, (double)var10);
            tessellator.addVertexWithUV(0.0D, (double)var11, (double)(0.0F - var7), (double)topLeft, (double)var10);
            tessellator.addVertexWithUV((double)var6, (double)var11, (double)(0.0F - var7), (double)topRight, (double)var10);
        }

        tessellator.draw();*/
    }

    static void setColor(int var0)
    {
        float var1 = (float)(var0 >> 16 & 255) / 255.0F;
        float var2 = (float)(var0 >> 8 & 255) / 255.0F;
        float var3 = (float)(var0 & 255) / 255.0F;
        GL11.glColor4f(var1, var2, var3, 1.0F);
    }
}
