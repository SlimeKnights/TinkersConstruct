package tconstruct.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import tconstruct.blocks.logic.CraftingStationLogic;
import tconstruct.inventory.CraftingStationContainer;

public class CraftingStationGui extends GuiContainer
{
    public CraftingStationGui(InventoryPlayer inventoryplayer, CraftingStationLogic logic, int x, int y, int z)
    {
        super(new CraftingStationContainer(inventoryplayer, logic, x, y, z));
    }

    /*public void onGuiClosed ()
    {
        TConstruct.logger.info("Gui Closed");
        super.onGuiClosed();
    }*/

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        fontRenderer.drawString("Crafting", 28, 6, 0x404040);
        fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/crafting.png");

    @Override
    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int l = (width - xSize) / 2;
        int i1 = (height - ySize) / 2;
        drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
    }
}
