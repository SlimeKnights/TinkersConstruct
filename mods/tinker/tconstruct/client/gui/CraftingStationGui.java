package mods.tinker.tconstruct.client.gui;

import mods.tinker.tconstruct.blocks.logic.CraftingStationLogic;
import mods.tinker.tconstruct.inventory.CraftingStationContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class CraftingStationGui extends GuiContainer
{
    public CraftingStationGui(InventoryPlayer inventoryplayer, CraftingStationLogic logic, int x, int y, int z)
    {
        super(new CraftingStationContainer(inventoryplayer, logic, x, y, z));
    }

    /*public void onGuiClosed ()
    {
        System.out.println("Gui Closed");
        super.onGuiClosed();
    }*/

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        fontRenderer.drawString("Crafting", 28, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture("/mods/tinker/textures/gui/crafting.png");
        int l = (width - xSize) / 2;
        int i1 = (height - ySize) / 2;
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
    }
}
