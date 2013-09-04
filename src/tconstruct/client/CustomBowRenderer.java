package tconstruct.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class CustomBowRenderer implements IItemRenderer
{
    Minecraft mc = Minecraft.getMinecraft();
    private RenderBlocks renderBlocksInstance = new RenderBlocks();

    @Override
    public boolean handleRenderType (ItemStack item, ItemRenderType type)
    {
        return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    }

    @Override
    public boolean shouldUseRenderHelper (ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem (ItemRenderType type, ItemStack item, Object... data)
    {
        EntityLivingBase living = (EntityLivingBase) data[1];
        ItemRenderer renderer = RenderManager.instance.itemRenderer;
        for (int i = 0; i < item.getItem().getRenderPasses(item.getItemDamage()) + 1; i++)
            renderItem(living, item, i, type);
    }

    public void renderItem (EntityLivingBase living, ItemStack stack, int renderPass, ItemRenderType type)
    {
        GL11.glPushMatrix();

        Block block = null;
        if (stack.getItem() instanceof ItemBlock && stack.itemID < Block.blocksList.length)
        {
            block = Block.blocksList[stack.itemID];
        }

        Icon icon = null; //living.getItemIcon(stack, renderPass);
        if (living instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) living;
            if (player.getItemInUse() != null)
                icon = stack.getItem().getIcon(stack, renderPass, player, player.getItemInUse(), player.getItemInUseCount());
            else
                icon = living.getItemIcon(stack, renderPass);
        }
        else
        {
            icon = living.getItemIcon(stack, renderPass);
        }

        if (icon == null)
        {
            GL11.glPopMatrix();
            return;
        }

        /*if (stack.getItemSpriteNumber() == 0)
        {
            this.mc.renderEngine.bindTexture("/terrain.png");
        }
        else
        {
            this.mc.renderEngine.bindTexture("/gui/items.png");
        }*/
        TextureManager texturemanager = this.mc.func_110434_K();
        texturemanager.func_110577_a(texturemanager.func_130087_a(stack.getItemSpriteNumber()));

        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
        {
            GL11.glTranslatef(0.6F, 0.5F, 0.5F);
        }
        else
        {
            GL11.glRotatef(180.0F, 0F, 0F, 1.0F);
            GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.75F);
            GL11.glTranslatef(-0.6F, -0.25F, 1.0F);
            GL11.glScalef(1.75F, 1.75F, 1.75F);
        }

        Tessellator tessellator = Tessellator.instance;
        float f = icon.getMinU();
        float f1 = icon.getMaxU();
        float f2 = icon.getMinV();
        float f3 = icon.getMaxV();
        float f4 = 0.0F;
        float f5 = 0.3F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef(-f4, -f5, 0.0F);
        float f6 = 1.5F;
        GL11.glScalef(f6, f6, f6);
        GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
        ItemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, icon.getOriginX(), icon.getOriginY(), 0.0625F);

        /*if (stack != null && stack.hasEffect() && renderPass == 0)
        {
            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDisable(GL11.GL_LIGHTING);
            this.mc.renderEngine.bindTexture("%blur%/misc/glint.png");
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            float f7 = 0.76F;
            GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            float f8 = 0.125F;
            GL11.glScalef(f8, f8, f8);
            float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
            GL11.glTranslatef(f9, 0.0F, 0.0F);
            GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
            ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(f8, f8, f8);
            f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
            GL11.glTranslatef(-f9, 0.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
            ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        }*/

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        GL11.glPopMatrix();
    }

}
