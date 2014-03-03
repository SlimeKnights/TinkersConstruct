package common.darkknight.jewelrycraft.renders;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import common.darkknight.jewelrycraft.model.ModelJewlersCraftingBench;
import common.darkknight.jewelrycraft.tileentity.TileEntityJewelrsCraftingTable;

public class TileEntityJewelrsCraftingTableRender extends TileEntitySpecialRenderer
{
    ModelJewlersCraftingBench modelTable = new ModelJewlersCraftingBench();
    String      texture     = "textures/tileentities/JewelrsCraftingBench.png";

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        ResourceLocation blockTexture = new ResourceLocation("jewelrycraft", texture);
        Minecraft.getMinecraft().renderEngine.bindTexture(blockTexture);
        int block = te.getBlockMetadata();        
        TileEntityJewelrsCraftingTable jt = (TileEntityJewelrsCraftingTable)te;

        GL11.glPushMatrix();
        if (block == 0)
            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        else if (block == 1){
            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(90F, 0.0F, 1.0F, 0.0F);
        }
        else if (block == 2)
            GL11.glRotatef(180F, 1.0F, 0.0F, 0.0F);
        else if (block == 3)
            GL11.glRotatef(180F, 1.0F, 0.0F, 1.0F);

        modelTable.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        if (jt != null)
        {
            if (jt.hasJewelry && jt.jewelry.getIconIndex().getIconName() != "")
            {
                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_LIGHTING);
                EntityItem entityitem = new EntityItem(te.worldObj, 0.0D, 0.0D, 0.0D, jt.jewelry);
                entityitem.getEntityItem().stackSize = 1;
                entityitem.hoverStart = 0.0F;

                GL11.glRotatef(180F, 1F, 0F, 0F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                GL11.glTranslatef(0.0F, -1.6F, 0.6F);
                GL11.glRotatef(jt.angle, 0F, 1F, 0F);
                if(RenderManager.instance.options.fancyGraphics)
                    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                else
                {
                    GL11.glRotatef(180F, 0F, 1F, 0F);
                    RenderManager.instance.options.fancyGraphics = true;
                    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                    RenderManager.instance.options.fancyGraphics = false;
                }

                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glPopMatrix();
            }
            if (jt.hasEndItem && jt.endItem.getIconIndex().getIconName() != "")
            {
                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_LIGHTING);
                EntityItem entityitem = new EntityItem(te.worldObj, 0.0D, 0.0D, 0.0D, jt.endItem);
                entityitem.getEntityItem().stackSize = 1;
                entityitem.hoverStart = 0.0F;

                GL11.glRotatef(180F, 1F, 0F, 0F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                GL11.glTranslatef(0.0F, -1.6F, 0.6F);
                GL11.glRotatef(jt.angle, 0F, 1F, 0F);
                if(RenderManager.instance.options.fancyGraphics)
                    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                else
                {
                    GL11.glRotatef(180F, 0F, 1F, 0F);
                    RenderManager.instance.options.fancyGraphics = true;
                    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                    RenderManager.instance.options.fancyGraphics = false;
                }

                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glPopMatrix();
            }
            if (jt.hasModifier && jt.modifier.getIconIndex().getIconName() != "")
            {
                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_LIGHTING);
                EntityItem entityitem = new EntityItem(te.worldObj, 0.0D, 0.0D, 0.0D, jt.modifier);
                entityitem.getEntityItem().stackSize = 1;
                entityitem.hoverStart = 0.0F;

                GL11.glRotatef(180F, 1F, 0F, 0F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                GL11.glTranslatef(0.55F, -1.5F, -0.45F);
                GL11.glRotatef(jt.angle, 0F, 1F, 0F);
                if(RenderManager.instance.options.fancyGraphics)
                    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                else
                {
                    GL11.glRotatef(180F, 0F, 1F, 0F);
                    RenderManager.instance.options.fancyGraphics = true;
                    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                    RenderManager.instance.options.fancyGraphics = false;
                }
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glPopMatrix();
            }
            if (jt.hasJewel && jt.jewel.getIconIndex().getIconName() != "")
            {
                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_LIGHTING);
                EntityItem entityitem = new EntityItem(te.worldObj, 0.0D, 0.0D, 0.0D, jt.jewel);
                entityitem.getEntityItem().stackSize = 1;
                entityitem.hoverStart = 0.0F;
                
                GL11.glRotatef(180F, 1F, 0F, 0F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                GL11.glTranslatef(-0.55F, -1.5F, -0.45F);
                GL11.glRotatef(jt.angle, 0F, 1F, 0F);
                if(RenderManager.instance.options.fancyGraphics)
                    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                else
                {
                    GL11.glRotatef(180F, 0F, 1F, 0F);
                    RenderManager.instance.options.fancyGraphics = true;
                    RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                    RenderManager.instance.options.fancyGraphics = false;
                }
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glPopMatrix();
            }
        }

        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    public void adjustLightFixture(World world, int i, int j, int k, Block block)
    {
        Tessellator tess = Tessellator.instance;
        float brightness = block.getBlockBrightness(world, i, j, k);
        int skyLight = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
        int modulousModifier = skyLight % 65536;
        int divModifier = skyLight / 65536;
        tess.setColorOpaque_F(brightness, brightness, brightness);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) modulousModifier, divModifier);
    }

}
