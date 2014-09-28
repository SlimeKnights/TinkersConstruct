package tconstruct.armor.modelblock;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import tconstruct.blocks.logic.DryingRackLogic;
import tconstruct.tools.entity.FancyEntityItem;

/* Special renderer, only used for drawing tools */

@SideOnly(Side.CLIENT)
public class DryingRackSpecialRender extends TileEntitySpecialRenderer
{

    public void render (DryingRackLogic logic, double posX, double posY, double posZ, float var8)
    {
        GL11.glPushMatrix();
        float var10 = (float) (posX - 0.5F);
        float var11 = (float) (posY - 0.5F);
        float var12 = (float) (posZ - 0.5F);
        GL11.glTranslatef(var10, var11, var12);
        this.func_82402_b(logic);
        GL11.glPopMatrix();
    }

    private void func_82402_b (DryingRackLogic logic)
    {
        ItemStack stack = logic.getStackInSlot(0);

        if (stack != null)
            renderItem(logic, stack);
    }

    void renderItem (DryingRackLogic logic, ItemStack stack)
    {
        FancyEntityItem entityitem = new FancyEntityItem(logic.getWorldObj(), 0.0D, 0.0D, 0.0D, stack);
        entityitem.getEntityItem().stackSize = 1;
        entityitem.hoverStart = 0.0F;
        GL11.glPushMatrix();
        int meta = logic.getWorldObj().getBlockMetadata(logic.xCoord, logic.yCoord, logic.zCoord);
        if (meta <= 1)
            GL11.glTranslatef(1F, -0.375F, 0.905F);
        else
        {
            GL11.glTranslatef(1F, 0.375F, 0.905F);

            if (meta / 2 == 2)
            {
                GL11.glRotatef(90F, 0F, 1F, 0F);
                GL11.glTranslatef(-0.0625F, 0F, 0F);
            }
            if (meta == 2)
                GL11.glTranslatef(0F, 0F, 0.375F);
            if (meta == 3)
                GL11.glTranslatef(0F, 0F, -0.375F);
            if (meta == 4)
                GL11.glTranslatef(0F, 0F, 0.2875F);
            if (meta == 5)
                GL11.glTranslatef(0F, 0F, -0.5F);
        }
        GL11.glScalef(2F, 2F, 2F);
        if (stack.getItem() instanceof ItemBlock)
        {
            // GL11.glRotatef(90F, -1, 0F, 0F);
            GL11.glTranslatef(0F, 0.2125F, 0.0375F);
        }

        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;

        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt (TileEntity logic, double var2, double var4, double var6, float var8)
    {
        this.render((DryingRackLogic) logic, var2, var4, var6, var8);
    }
}
