package tinker.tconstruct.client;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texturefx.TextureCompassFX;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import tinker.tconstruct.logic.CastingTableLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/* Special renderer, only used for drawing tools */

@SideOnly(Side.CLIENT)
public class CastingTableSpecialRenderer extends TileEntitySpecialRenderer
{
	@Override
    public void renderTileEntityAt(TileEntity logic, double var2, double var4, double var6, float var8)
    {
		//System.out.println("Render!!!");
        this.render((CastingTableLogic)logic, var2, var4, var6, var8);
    }

    public void render(CastingTableLogic logic, double posX, double posY, double posZ, float var8)
    {
    	GL11.glPushMatrix();
        float var10 = (float) (posX - 0.5F);
        float var11 = (float) (posY - 0.5F);
        float var12 = (float) (posZ - 0.5F);
        GL11.glTranslatef(var10, var11, var12);
        this.func_82402_b(logic);
        GL11.glPopMatrix();
    }

    private void func_82402_b(CastingTableLogic logic)
    {
    	ItemStack stack = logic.getStackInSlot(0);
    	
        if (stack != null)
        {
            EntityItem entityitem = new EntityItem(logic.worldObj, 0.0D, 0.0D, 0.0D, stack);
            entityitem.func_92014_d().stackSize = 1;
            entityitem.hoverStart = 0.0F;
            GL11.glPushMatrix();
            GL11.glTranslatef(1F, 1.4375F, 0.61F);
            GL11.glRotatef(90F, 1, 0F, 0F);
            GL11.glScalef(1.94F, 1.94F, 1.94F);
            /*GL11.glTranslatef(-0.453125F * (float)Direction.offsetX[logic.hangingDirection], -0.18F, -0.453125F * (float)Direction.offsetZ[logic.hangingDirection]);
            GL11.glRotatef(180.0F + logic.rotationYaw, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef((float)(-90 * logic.getRotation()), 0.0F, 0.0F, 1.0F);

            switch (logic.getRotation())
            {
                case 1:
                    GL11.glTranslatef(-0.16F, -0.16F, 0.0F);
                    break;
                case 2:
                    GL11.glTranslatef(0.0F, -0.32F, 0.0F);
                    break;
                case 3:
                    GL11.glTranslatef(0.16F, -0.16F, 0.0F);
            }*/

            /*if (var3.func_92014_d().getItem() == Item.map)
            {
                this.renderManager.renderEngine.bindTexture(this.renderManager.renderEngine.getTexture("/misc/mapbg.png"));
                Tessellator var4 = Tessellator.instance;
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                GL11.glScalef(0.00390625F, 0.00390625F, 0.00390625F);
                GL11.glTranslatef(-65.0F, -107.0F, -3.0F);
                GL11.glNormal3f(0.0F, 0.0F, -1.0F);
                var4.startDrawingQuads();
                byte var5 = 7;
                var4.addVertexWithUV((double)(0 - var5), (double)(128 + var5), 0.0D, 0.0D, 1.0D);
                var4.addVertexWithUV((double)(128 + var5), (double)(128 + var5), 0.0D, 1.0D, 1.0D);
                var4.addVertexWithUV((double)(128 + var5), (double)(0 - var5), 0.0D, 1.0D, 0.0D);
                var4.addVertexWithUV((double)(0 - var5), (double)(0 - var5), 0.0D, 0.0D, 0.0D);
                var4.draw();
                MapData var6 = Item.map.getMapData(var3.func_92014_d(), logic.worldObj);

                if (var6 != null)
                {
                    this.renderManager.itemRenderer.mapItemRenderer.renderMap((EntityPlayer)null, this.renderManager.renderEngine, var6);
                }
            }
            else*/
            //{
                /*if (var3.func_92014_d().getItem() == Item.compass)
                {
                    double var8 = TextureCompassFX.field_82391_c.field_76868_i;
                    double var9 = TextureCompassFX.field_82391_c.field_76866_j;
                    TextureCompassFX.field_82391_c.field_76868_i = 0.0D;
                    TextureCompassFX.field_82391_c.field_76866_j = 0.0D;
                    TextureCompassFX.func_82390_a(logic.xCoord, logic.zCoord, (double)MathHelper.wrapAngleTo180_float((float)(180)), false, true);
                    TextureCompassFX.field_82391_c.field_76868_i = var8;
                    TextureCompassFX.field_82391_c.field_76866_j = var9;
                    this.renderManager.renderEngine.updateDynamicTexture(TextureCompassFX.field_82391_c, -1);
                }*/

                RenderItem.field_82407_g = true;
                RenderManager.instance.renderEntityWithPosYaw(entityitem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                RenderItem.field_82407_g = false;

                /*if (var3.func_92014_d().getItem() == Item.compass)
                {
                    TextureCompassFX.field_82391_c.onTick();
                    this.renderManager.renderEngine.updateDynamicTexture(TextureCompassFX.field_82391_c, -1);
                }*/
            //}

            GL11.glPopMatrix();
        }
    }
}
