package tconstruct.client.entity;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MiniGardyRender extends RenderLiving
{
    public MiniGardyRender()
    {
        super(new ModelMiniGardy(), 0.5F);
    }

    @Override
    protected void renderEquippedItems (EntityLivingBase par1EntityLiving, float par2)
    {
        float f1 = 1.0F;
        GL11.glColor3f(f1, f1, f1);
        super.renderEquippedItems(par1EntityLiving, par2);
        ItemStack heldItem = par1EntityLiving.getHeldItem();
        ItemStack helmetItem = par1EntityLiving.getCurrentItemOrArmor(4);
        float f2;

        ModelMiniGardy model = (ModelMiniGardy) this.mainModel;
        if (helmetItem != null)
        {
            GL11.glPushMatrix();
            model.head.postRender(0.0625F);

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(helmetItem, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, helmetItem, BLOCK_3D));

            if (helmetItem.getItem() instanceof ItemBlock)
            {
                if (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[helmetItem.itemID].getRenderType()))
                {
                    f2 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(f2, -f2, -f2);
                }

                this.renderManager.itemRenderer.renderItem(par1EntityLiving, helmetItem, 0);
            }
            else if (helmetItem.getItem().itemID == Item.skull.itemID)
            {
                f2 = 1.0625F;
                GL11.glScalef(f2, -f2, -f2);
                String s = "";

                if (helmetItem.hasTagCompound() && helmetItem.getTagCompound().hasKey("SkullOwner"))
                {
                    s = helmetItem.getTagCompound().getString("SkullOwner");
                }

                TileEntitySkullRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, helmetItem.getItemDamage(), s);
            }

            GL11.glPopMatrix();
        }

        if (heldItem != null)
        {
            GL11.glPushMatrix();

            //if (this.mainModel.isChild)
            {
                f2 = 0.5F;
                GL11.glTranslatef(0.0F, 0.75F, 0.0F);
                GL11.glRotatef(-10.0F, 0F, 0.0F, 1.0F);
                GL11.glScalef(f2, f2, f2);
            }

            model.torso.postRender(0.0625F);
            model.rightArm.postRender(0.0625F);
            model.rightHand.postRender(0.0625F);
            GL11.glTranslatef(-0.125F, 1F, 0F);

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(heldItem, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, heldItem, BLOCK_3D));

            if (heldItem.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[heldItem.itemID].getRenderType())))
            {
                f2 = 0.5F;
                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
                f2 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-f2, -f2, f2);
            }
            else if (heldItem.itemID == Item.bow.itemID)
            {
                f2 = 0.625F;
                GL11.glTranslatef(-0.175F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            //if (Item.itemsList[heldItem.itemID].isFull3D())
            {
                f2 = 0.625F;

                if (Item.itemsList[heldItem.itemID].shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                this.translateItem();
                GL11.glScalef(f2, -f2, f2);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            /*else
            {
                f2 = 0.375F;
                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
                GL11.glScalef(f2, f2, f2);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }*/

            float f6;
            float f11;
            int j;
            float f12;

            if (heldItem.getItem().requiresMultipleRenderPasses())
            {
                for (j = 0; j < heldItem.getItem().getRenderPasses(heldItem.getItemDamage()); j++)
                {
                    int k = heldItem.getItem().getColorFromItemStack(heldItem, j);
                    f12 = (float) (k >> 16 & 255) / 255.0F;
                    f11 = (float) (k >> 8 & 255) / 255.0F;
                    f6 = (float) (k & 255) / 255.0F;
                    GL11.glColor4f(f12, f11, f6, 1.0F);
                    this.renderManager.itemRenderer.renderItem(par1EntityLiving, heldItem, j);
                }
            }
            else
            {
                j = heldItem.getItem().getColorFromItemStack(heldItem, 0);
                f6 = (float) (j >> 16 & 255) / 255.0F;
                f12 = (float) (j >> 8 & 255) / 255.0F;
                f11 = (float) (j & 255) / 255.0F;
                GL11.glColor4f(f6, f12, f11, 1.0F);
                this.renderManager.itemRenderer.renderItem(par1EntityLiving, heldItem, 0);
            }

            GL11.glPopMatrix();
        }
    }

    protected void translateItem ()
    {
        GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
    }

    @Override
    protected ResourceLocation func_110775_a (Entity par1Entity)
    {
        return texture;
    }

    static final ResourceLocation texture = new ResourceLocation("assets/tinker/textures/mob/googirl.png");
}
