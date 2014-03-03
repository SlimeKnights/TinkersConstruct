package common.darkknight.jewelrycraft.renders;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import common.darkknight.jewelrycraft.model.ModelSmelter;
import common.darkknight.jewelrycraft.tileentity.TileEntitySmelter;

public class TileEntitySmelterRender extends TileEntitySpecialRenderer
{
    ModelSmelter modelSmelter = new ModelSmelter();
    String       texture      = "textures/tileentities/Smelter.png";

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);

        ResourceLocation blockTexture = new ResourceLocation("jewelrycraft", texture);
        Minecraft.getMinecraft().renderEngine.bindTexture(blockTexture);
        TileEntitySmelter st = (TileEntitySmelter)te;
        int block = te.getBlockMetadata();

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

        modelSmelter.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        EntityItem entityitem = new EntityItem(te.worldObj, 0.0D, 0.0D, 0.0D, new ItemStack(Block.lavaStill));
        entityitem.getEntityItem().stackSize = 1;
        entityitem.hoverStart = 0.0F;

        GL11.glTranslatef(-0F, 1.25F, -0.3F);
        GL11.glScalef(1.25F, 1.0F, 1.47F);
        GL11.glRotatef(90F, 1F, 0F, 0f);
        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;

        GL11.glTranslatef(0F, 0.46F, 0.0F);
        GL11.glScalef(0.8F, 0.1F, 0F);
        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;

        GL11.glTranslatef(0F, -5.6F, 0.0F);
        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        if (st != null)
        {
            if (st.hasMetal && st.metal.getIconIndex().getIconName() != "")
            {
                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_LIGHTING);
                EntityItem metal = new EntityItem(te.worldObj, 0.0D, 0.0D, 0.0D, st.metal);
                metal.getEntityItem().stackSize = 1;
                metal.hoverStart = 0.0F;

                GL11.glRotatef(-50F, 1F, 0F, 0F);
                GL11.glRotatef(-50F, 0F, 0F, 1F);
                GL11.glRotatef(180F, 1F, 0F, 0F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                GL11.glTranslatef(-0.9F, -0.9F, -1.6F);
                if(RenderManager.instance.options.fancyGraphics) RenderManager.instance.renderEntityWithPosYaw(metal, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                else
                {
                    RenderManager.instance.options.fancyGraphics = true;
                    RenderManager.instance.renderEntityWithPosYaw(metal, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
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
