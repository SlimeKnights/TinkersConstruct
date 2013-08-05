package mods.tinker.tconstruct.client.gui;

import mods.tinker.tconstruct.entity.Gardeslime;
import mods.tinker.tconstruct.inventory.MiniGardyContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class MiniGardyGui extends GuiContainer
{
    public Gardeslime gardy;

    public MiniGardyGui(InventoryPlayer inventoryplayer, Gardeslime gardy)
    {
        super(new MiniGardyContainer(inventoryplayer, gardy));
        this.gardy = gardy;
    }

    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        fontRenderer.drawString(gardy.getEntityName(), 8, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
    }

    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture("/mods/tinker/textures/gui/googirl.png");
        int cornerX = (width - xSize) / 2;
        int cornerY = (height - ySize) / 2;
        drawTexturedModalRect(cornerX, cornerY, 0, 0, xSize, ySize);
    }
}
