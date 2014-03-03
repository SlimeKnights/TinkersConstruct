package common.darkknight.jewelrycraft.renders;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;

import org.lwjgl.opengl.GL11;

import common.darkknight.jewelrycraft.model.ModelDisplayer;
import common.darkknight.jewelrycraft.tileentity.TileEntityDisplayer;

public class TileEntityDisplayerRender extends TileEntitySpecialRenderer
{
    ModelDisplayer displayer = new ModelDisplayer();
    String texture = "textures/tileentities/Displayer.png";

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        TileEntityDisplayer disp = (TileEntityDisplayer)te;
        int block = disp.getBlockMetadata();

        ResourceLocation blockTexture = new ResourceLocation("jewelrycraft", texture);
        Minecraft.getMinecraft().renderEngine.bindTexture(blockTexture);

        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        displayer.render((Entity) null, disp.ringTranslation1, disp.ringTranslation2, disp.ringTranslation3, 0.0F, 0.0F, 0.0625F);
        if(disp != null && disp.hasObject && disp.object != null && disp.object != new ItemStack(0, 0, 0))
        {
            int ind = -3;
            GL11.glPushMatrix();
            renderLabel(EnumChatFormatting.YELLOW + disp.object.getDisplayName(), 0F, (-0.171F)*ind, 0, block, disp);
            GL11.glPopMatrix();
            ind++;
            GL11.glPushMatrix();
            renderLabel(Integer.toString(disp.quantity), 0F, (-0.171F)*ind, 0, block, disp);
            GL11.glPopMatrix();
            ind++;
            if(disp.object.itemID != Item.map.itemID && disp.object != null && disp.object != new ItemStack(0, 0, 0) && disp.object.getTooltip(null, true) != null)
            {
                for(int i = 1; i < disp.object.getTooltip(new FakePlayer(te.worldObj, "Player"), true).size(); i++)
                {
                    if(disp.object.getTooltip(new FakePlayer(te.worldObj, "Player"), true).get(i).toString() != "")
                    {
                        GL11.glPushMatrix();
                        renderLabel(disp.object.getTooltip(new FakePlayer(te.worldObj, "Player"), true).get(i).toString(), 0F, (-0.171F)*ind, 0, block, disp);
                        GL11.glPopMatrix();
                        ind++;
                    }
                }
            }
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            EntityItem entityitem = new EntityItem(te.worldObj, 0.0D, 0.0D, 0.0D, disp.object);
            entityitem.hoverStart = 0.0F;
            disp.object.stackSize = 1;
            GL11.glRotatef(180F, 1F, 0F, 0F);
            GL11.glTranslatef(0.0F, -0.6F + disp.ringTranslation1/5, 0F);
            GL11.glRotatef(disp.rotAngle, 0F, 1F, 0F);
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

    protected void renderLabel(String par2Str, double x, double y, double z, int metadata, TileEntity te)
    {
        FontRenderer fontrenderer = RenderManager.instance.getFontRenderer();
        if(te.worldObj.getClosestPlayer((double)te.xCoord, (double)te.yCoord, (double)te.zCoord, 3.5D) != null)
        {
            float var14 = 0.01266667F * 1.5F;
            float var17 = 0.015F;
            GL11.glRotatef(180F, 0F, 0F, 1F);
            if(metadata == 0) GL11.glRotatef(0F, 0F, 1F, 0F);
            else if(metadata == 1) GL11.glRotatef(270F, 0F, 1F, 0F);
            else if(metadata == 2) GL11.glRotatef(180F, 0F, 1F, 0F);
            else if(metadata == 3) GL11.glRotatef(90F, 0F, 1F, 0F);
            GL11.glTranslatef((float)x, (float)y, (float)z + 0.45F);
            GL11.glScalef(-0.015F, -var14, 0.015F);
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            Tessellator tessellator = Tessellator.instance;
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            int j = fontrenderer.getStringWidth(par2Str) / 2;
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_F(0.2F, 0.2F, 0.2F, 0.8F);
            tessellator.addVertex((double)(-33.333 - 0), -1D, 0.1D);
            tessellator.addVertex((double)(-33.333 - 0), 8D, 0.1D);
            tessellator.addVertex((double)(33.333 + 0), 8D, 0.1D);
            tessellator.addVertex((double)(33.333 + 0), -1D, 0.1D);
            tessellator.draw();
            if ((fontrenderer.getStringWidth(par2Str)/2) > 30) var17 = 0.9F / fontrenderer.getStringWidth(par2Str); 
            else var17 = var14;
            GL11.glScalef(var17*70F, 1F, 0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            fontrenderer.drawStringWithShadow(par2Str, -j, 0, Color.GRAY.getRGB());
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        }
    }
}
