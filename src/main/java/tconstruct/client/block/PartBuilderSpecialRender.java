package tconstruct.client.block;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import tconstruct.blocks.logic.PartBuilderWorldLogic;
import tconstruct.entity.FancyEntityItem;
import tconstruct.library.ItemBlocklike;

/* Special renderer, only used for drawing tools */

@SideOnly(Side.CLIENT)
public class PartBuilderSpecialRender extends TileEntitySpecialRenderer
{
    @Override
    public void renderTileEntityAt (TileEntity logic, double var2, double var4, double var6, float var8)
    {
        //TConstruct.logger.info("Render!!!");
        this.render((PartBuilderWorldLogic) logic, var2, var4, var6, var8);
    }

    public void render (PartBuilderWorldLogic logic, double posX, double posY, double posZ, float var8)
    {
        GL11.glPushMatrix();
        float var10 = (float) (posX - 0.5F);
        float var11 = (float) (posY - 0.5F);
        float var12 = (float) (posZ - 0.5F);
        GL11.glTranslatef(var10, var11, var12);
        this.renderItems(logic);
        GL11.glPopMatrix();
    }

    private void renderItems (PartBuilderWorldLogic logic)
    {
        ItemStack stack = logic.getStackInSlot(0);
        if (stack != null)
            renderItem(logic, stack, 0);
        
        stack = logic.getStackInSlot(1);
        if (stack != null)
            renderItem(logic, stack, 1/16f);

        stack = logic.getStackInSlot(2);
        if (stack != null)
            renderItem(logic, stack, 1/16f);
    }

    void renderItem (PartBuilderWorldLogic logic, ItemStack stack, float yOffset)
    {
        FancyEntityItem entityitem = new FancyEntityItem(logic.worldObj, 0.0D, 0.0D, 0.0D, stack);
        entityitem.getEntityItem().stackSize = 1;
        entityitem.hoverStart = 0.0F;
        GL11.glPushMatrix();
        GL11.glTranslatef(1F, 1.48F+yOffset, 0.55F);
        GL11.glRotatef(90F, 1, 0F, 0F);
        GL11.glScalef(2F, 2F, 2F);
        if (stack.getItem() instanceof ItemBlock || stack.getItem() instanceof ItemBlocklike)
        {
            GL11.glRotatef(90F, -1, 0F, 0F);
            GL11.glTranslatef(0F, -0.1F, 0.2275F);
        }

        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;

        GL11.glPopMatrix();
    }
}
