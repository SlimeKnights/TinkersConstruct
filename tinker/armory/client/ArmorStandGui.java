package tinker.armory.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import tinker.armory.content.EntityEquipment;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ArmorStandGui extends GuiContainer
{
    public ArmorStandGui(EntityPlayer player, EntityEquipment equipment)
	{
    	super(equipment.getContainer(player));
        this.allowUserInput = true;
	}
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString("Armor Stand", 8, 8, 4210752);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
	{
		int var4 = this.mc.renderEngine.getTexture("/infitextures/gui/armorstand.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(var4);
        int var5 = this.guiLeft;
        int var6 = this.guiTop;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
	}
}
