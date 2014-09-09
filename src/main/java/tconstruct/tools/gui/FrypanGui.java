package tconstruct.tools.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import tconstruct.tools.logic.FrypanLogic;

public class FrypanGui extends GuiContainer
{
    public FrypanLogic logic;

    public FrypanGui(InventoryPlayer inventoryplayer, FrypanLogic frypan, World world, int x, int y, int z)
    {
        super(frypan.getGuiContainer(inventoryplayer, world, x, y, z));
        logic = frypan;
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        fontRendererObj.drawString(StatCollector.translateToLocal("crafters.Frypan"), 60, 6, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/frypan.png");

    @Override
    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int cornerX = (width - xSize) / 2;
        int cornerY = (height - ySize) / 2;
        drawTexturedModalRect(cornerX, cornerY, 0, 0, xSize, ySize);
        if (logic.fuel > 0)
        {
            int fuel = logic.gaugeFuelScaled(12);
            drawTexturedModalRect(cornerX + 27, (cornerY + 40) - fuel, 176, 12 - fuel, 14, fuel + 2);
        }
        int fuelgague = logic.gaugeProgressScaled(24);
        drawTexturedModalRect(cornerX + 140, cornerY + 8, 176, 14, fuelgague, 16);
    }
}
