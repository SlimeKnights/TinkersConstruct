package mods.tinker.tconstruct.client.gui;

import mods.tinker.tconstruct.blocks.logic.DrawbridgeLogic;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class DrawbridgeGui extends GuiContainer
{
    public DrawbridgeLogic logic;

    public DrawbridgeGui(InventoryPlayer inventoryplayer, DrawbridgeLogic frypan, World world, int x, int y, int z)
    {
        super(frypan.getGuiContainer(inventoryplayer, world, x, y, z));
        logic = frypan;
    }

    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        //fontRenderer.drawString(StatCollector.translateToLocal("aggregator.glowstone"), 60, 6, 0x404040);
        fontRenderer.drawString("Drawbridge", 8, 6, 0x404040);
        fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }
    
    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/drawbridge.png");

    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.func_110434_K().func_110577_a(background);
        int cornerX = (width - xSize) / 2;
        int cornerY = (height - ySize) / 2;
        drawTexturedModalRect(cornerX, cornerY, 0, 0, xSize, ySize);
    }
}
