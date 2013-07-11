package mods.tinker.tconstruct.client.entity;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;
import mods.tinker.tconstruct.entity.GolemBase;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelSnowMan;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

public class GolemRender extends RenderLiving
{
    public static final float kon = -57.29578F;
    public static final float scale = 0.7F;

    public GolemRender(float f)
    {
        super(new ModelSnowMan(), f);
    }

    protected void preRenderScale (EntityLiving entityliving, float f)
    {
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GolemBase entitygenericgolem = (GolemBase) entityliving;
        /*if (entitygenericgolem.isSlime())
        {
            int i = 1;
            float f1 = (entitygenericgolem.slimeB + (entitygenericgolem.slimeC - entitygenericgolem.slimeB) * f) / 1.5F;
            float f2 = 0.8F / (f1 + 1.0F);
            float f3 = i;
            GL11.glScalef(f2 * f3, (1.0F / f2) * f3, f2 * f3);
        }*/
    }

    public void renderBeam (Entity entity, double d, double d1, double d2, double d3, double d4, double d5)
    {
        float f = (float) (d3 - d);
        float f1 = (float) (d4 - 1.0D - d1);
        float f2 = (float) (d5 - d2);
        float f3 = MathHelper.sqrt_float(f * f + f2 * f2);
        float f4 = MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d, (float) d1 + 2.0F, (float) d2);
        GL11.glRotatef(((float) (-Math.atan2(f2, f)) * 180F) / 3.141593F - 90F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(((float) (-Math.atan2(f3, f1)) * 180F) / 3.141593F - 90F, 1.0F, 0.0F, 0.0F);
        Tessellator tessellator = Tessellator.instance;
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(2884 /*GL_CULL_FACE*/);
        loadTexture("/mob/enderdragon/beam.png");
        GL11.glShadeModel(7425 /*GL_SMOOTH*/);
        float f5 = 0.0F - (float) entity.ticksExisted * 0.01F;
        float f6 = MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2) / 32F - (float) entity.ticksExisted * 0.01F;
        tessellator.startDrawing(5);
        int i = 8;
        for (int j = 0; j <= i; j++)
        {
            float f7 = MathHelper.sin(((float) (j % i) * 3.141593F * 2.0F) / (float) i) * 0.75F;
            float f8 = MathHelper.cos(((float) (j % i) * 3.141593F * 2.0F) / (float) i) * 0.75F;
            float f9 = ((float) (j % i) * 1.0F) / (float) i;
            tessellator.setColorOpaque_I(0);
            tessellator.addVertexWithUV(f7 * 0.2F, f8 * 0.2F, 0.0D, f9, f6);
            tessellator.setColorOpaque_I(0xffffff);
            tessellator.addVertexWithUV(f7, f8, f4, f9, f5);
        }

        tessellator.draw();
        GL11.glEnable(2884 /*GL_CULL_FACE*/);
        GL11.glShadeModel(7424 /*GL_FLAT*/);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
    }

    public void renderGolem (EntityLiving entityliving, double d, double d1, double d2, float f, float f1)
    {
        super.doRenderLiving(entityliving, d, d1, d2, f, f1);
    }

    public void renderCow (EntityMob entitymob, double d, double d1, double d2, float f, float f1)
    {
        super.doRenderLiving(entitymob, d, d1, d2, f, f1);
    }

    public void doRenderLiving (EntityLiving entityliving, double d, double d1, double d2, float f, float f1)
    {
        /*loadTexture("/terrain.png");
        renderBlocks.renderNorthFace(Block.brick, d, d1 + 5D, d2, 1);
        renderBlocks.renderSouthFace(Block.brick, d, d1 + 5D, d2, 1);
        renderBlocks.renderEastFace(Block.brick, d, d1 + 5D, d2, 1);
        renderBlocks.renderWestFace(Block.brick, d, d1 + 5D, d2, 1);*/
    }

    protected void renderSnowmanPumpkin (GolemBase par1EntitySnowman, float par2)
    {
        super.renderEquippedItems(par1EntitySnowman, par2);
        ItemStack itemstack = new ItemStack(Block.pumpkin, 1);

        if (itemstack != null && itemstack.getItem() instanceof ItemBlock)
        {
            GL11.glPushMatrix();
            //this.snowmanModel.head.postRender(0.0625F);

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack, BLOCK_3D));

            if (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[itemstack.itemID].getRenderType()))
            {
                float f1 = 0.625F;
                GL11.glTranslatef(0.0F, -0.34375F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f1, -f1, f1);
            }

            this.renderManager.itemRenderer.renderItem(par1EntitySnowman, itemstack, 0);
            GL11.glPopMatrix();
        }
    }

    protected void renderEquippedItems (EntityLiving par1EntityLiving, float par2)
    {
        this.renderSnowmanPumpkin((GolemBase) par1EntityLiving, par2);
    }

    public void doRender (Entity entity, double d, double d1, double d2, float f, float f1)
    {
        ItemStack itemstack = new ItemStack(Block.pumpkin, 1);

        if (itemstack != null && itemstack.getItem() instanceof ItemBlock)
        {
            GL11.glPushMatrix();
            //this.snowmanModel.head.postRender(0.0625F);

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack, BLOCK_3D));

            if (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[itemstack.itemID].getRenderType()))
            {
                float swipe = 0.625F;
                GL11.glTranslatef(0.0F, -0.34375F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(swipe, -swipe, swipe);
            }

            //this.renderManager.itemRenderer.renderItem((EntityLiving) entity, itemstack, 0);
            GL11.glPopMatrix();
        }

        boolean flag = false;
        float f2 = 1.0F;
        float f3 = 0.0F;
        float f4 = 0.0F;
        GolemBase golembase = (GolemBase) entity;
        ModelSnowMan modelgenericgolem = (ModelSnowMan) mainModel;
        /*if (golembase == null || golembase.body == null)
        {
            return;
        }*/
        int i = -1;
        int j = 0;
        /*do
        {
            if (j >= 3)
            {
                break;
            }
            if (golembase.body[1][j] == mod_Golems.golemHead.blockID)
            {
                i = j;
                break;
            }
            j++;
        } while (true);*/
        i = 0;
        super.doRenderLiving((EntityLiving) entity, d, d1, d2, f, f1);
        loadTexture("/terrain.png");
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        j = 3;
        World world = entity.worldObj;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d, (float) d1, (float) d2);
        /*if (entitygenericgolem.lifespan <= 0L)
        {
            entitygenericgolem.rotationYaw = entitygenericgolem.yawFreeze;
        }*/
        GL11.glRotatef(entity.rotationYaw, 0.0F, -1F, 0.0F);
        GL11.glScalef(0.7F, 0.7F, 0.7F);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        GL11.glRotatef(i == 2 ? modelgenericgolem.head.rotateAngleY * -57.29578F : 0.0F, 0.0F, 1.0F, 0.0F);
        Block block = Block.brick;
        /*if (golembase.isSlime())
        {
            preRenderScale(golembase, f);
        }*/
        if (block != null)
        {
            /*if (block.blockID == mod_Golems.Golem_Head_ID)
            {
                renderFace(golembase, block, world, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ),
                        golembase.meta[1][j - 1]);
            }*/
            renderBlock(block, world, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ), 0);
        }
        /*else if (block == mod_Golems.golemCore)
        {
            renderCore(golembase, block, world, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ), 0.5F, 0.5F, 0.5F, 0.0D);
        }*/
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 1.5625F, 0.0F);
        GL11.glRotatef(i == 1 ? modelgenericgolem.head.rotateAngleY * -57.29578F : 0.0F, 0.0F, 1.0F, 0.0F);
        block = Block.cobblestone;
        if (block != null)// && block != mod_Golems.golemCore)
        {
            /*if (block.blockID == mod_Golems.Golem_Head_ID)
            {
                renderFace(golembase, block, world, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ),
                        golembase.meta[1][j - 2]);
            }*/
            renderBlock(block, world, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ), 0);
        }
        /*else if (block == mod_Golems.golemCore)
        {
            renderCore(golembase, block, world, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ), 0.5F, 0.5F, 0.5F, 1.0D);
        }*/
        GL11.glPopMatrix();
        /*GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 2.625F, 0.0F);
        GL11.glRotatef(i == 0 ? modelgenericgolem.head.rotateAngleY * -57.29578F : 0.0F, 0.0F, 1.0F, 0.0F);
        block = Block.blocksList[golembase.body[1][j - 3]];
        if (block != null && block != mod_Golems.golemCore)
        {
            if (block.blockID == mod_Golems.Golem_Head_ID)
            {
                renderFace(golembase, block, world, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ),
                        golembase.meta[1][j - 3]);
            }
            renderBlock(block, world, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ), golembase.meta[1][j - 3]);
        }
        else if (block == mod_Golems.golemCore)
        {
            renderCore(golembase, block, world, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ), 0.5F, 0.5F, 0.5F, 2D);
        }
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        GL11.glTranslatef(0.7F, 0.9F, 0.0F);
        if (golembase.isSwinging && golembase.arms[0])
        {
            GL11.glTranslatef(0.0F, 0.5F, 0.0F);
            golembase.swingAge -= golembase.dt;
            GL11.glRotatef(-((float) golembase.swingAge / (float) golembase.swingMax) * 70F, 1.0F, 0.0F, 0.0F);
            if (golembase.swingAge <= 0)
            {
                golembase.isSwinging = false;
            }
            GL11.glTranslatef(0.0F, -0.5F, 0.0F);
        }
        GL11.glScalef(0.4F, 1.3F, 0.4F);
        block = Block.blocksList[golembase.body[0][j - 2]];
        if (block != null)
        {
            renderBlock(block, world, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ), golembase.meta[0][j - 2]);
        }
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.5F, 0.0F);
        GL11.glTranslatef(-0.7F, 0.9F, 0.0F);
        if (golembase.isSwinging && golembase.arms[1] && !golembase.arms[0])
        {
            GL11.glTranslatef(0.0F, 0.5F, 0.0F);
            golembase.swingAge -= golembase.dt;
            GL11.glRotatef(-((float) golembase.swingAge / (float) golembase.swingMax) * 70F, 1.0F, 0.0F, 0.0F);
            if (golembase.swingAge <= 0)
            {
                golembase.isSwinging = false;
            }
            GL11.glTranslatef(0.0F, -0.5F, 0.0F);
        }
        GL11.glScalef(0.4F, 1.3F, 0.4F);
        block = Block.blocksList[golembase.body[2][j - 2]];
        if (block != null)
        {
            renderBlock(block, world, MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ), golembase.meta[2][j - 2]);
        }
        GL11.glPopMatrix();*/
        GL11.glPopMatrix();
        GL11.glEnable(2896 /*GL_LIGHTING*/);
    }

    protected void preRenderCallback (EntityLiving entityliving, float f)
    {
        preRenderScale(entityliving, f);
    }

    public void renderBlock (Block block, World world, int x, int y, int z, int side)
    {
        /*float f = 0.5F;
        float f1 = 1.0F;
        float f2 = 0.8F;
        float f3 = 0.6F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
        float f4 = 1.0F;
        float f5 = 1.0F;
        if (f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f * f5, f * f5, f * f5);
        renderBlocks.renderBottomFace(block, -0.5D, -0.5D, -0.5D, block.getIcon(0, side));
        f5 = 1.0F;
        if (f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f1 * f5, f1 * f5, f1 * f5);
        renderBlocks.renderTopFace(block, -0.5D, -0.5D, -0.5D, block.getIcon(1, side));
        f5 = 1.0F;
        if (f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
        renderBlocks.renderEastFace(block, -0.5D, -0.5D, -0.5D, block.getIcon(2, side));
        f5 = 1.0F;
        if (f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f2 * f5, f2 * f5, f2 * f5);
        renderBlocks.renderWestFace(block, -0.5D, -0.5D, -0.5D, block.getIcon(3, side));
        f5 = 1.0F;
        if (f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
        renderBlocks.renderNorthFace(block, -0.5D, -0.5D, -0.5D, block.getIcon(4, side));
        f5 = 1.0F;
        if (f5 < f4)
        {
            f5 = f4;
        }
        tessellator.setColorOpaque_F(f3 * f5, f3 * f5, f3 * f5);
        renderBlocks.renderSouthFace(block, -0.5D, -0.5D, -0.5D, block.getIcon(5, side));
        tessellator.draw();*/
    }

    /*public void renderFace (GolemBase entitygenericgolem, Block block, World world, int i, int j, int k, int l)
    {
        float f = 0.5F;
        float f1 = 1.0F;
        float f2 = 0.8F;
        float f3 = 0.6F;
        float f4 = entitygenericgolem.getFaceR();
        float f5 = entitygenericgolem.getFaceG();
        float f6 = entitygenericgolem.getFaceB();
        double d = 0.001D;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(block.getMixedBrightnessForBlock(world, i, j, k));
        float f7 = 1.0F;
        float f8 = 1.0F;
        if (f8 < f7)
        {
            f8 = f7;
        }
        tessellator.setColorOpaque_F(f4, f5, f6);
        if (block.getIcon(2, l) == BlockGolemHead.face)
        {
            renderBlocks.renderEastFace(block, -0.5D, -0.5D, -0.5D - d, mod_Golems.faceTex);
        }
        f8 = 1.0F;
        if (f8 < f7)
        {
            f8 = f7;
        }
        tessellator.setColorOpaque_F(f4, f5, f6);
        if (block.getIcon(3, l) == BlockGolemHead.face)
        {
            renderBlocks.renderWestFace(block, -0.5D, -0.5D, -0.5D + d, mod_Golems.faceTex);
        }
        f8 = 1.0F;
        if (f8 < f7)
        {
            f8 = f7;
        }
        tessellator.setColorOpaque_F(f4, f5, f6);
        if (block.getIcon(4, l) == BlockGolemHead.face)
        {
            renderBlocks.renderNorthFace(block, -0.5D - d, -0.5D, -0.5D, mod_Golems.faceTex);
        }
        f8 = 1.0F;
        if (f8 < f7)
        {
            f8 = f7;
        }
        tessellator.setColorOpaque_F(f4, f5, f6);
        if (block.getIcon(5, l) == BlockGolemHead.face)
        {
            renderBlocks.renderSouthFace(block, -0.5D + d, -0.5D, -0.5D, mod_Golems.faceTex);
        }
        tessellator.draw();
    }*/
}
