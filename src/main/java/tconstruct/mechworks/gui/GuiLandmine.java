package tconstruct.mechworks.gui;

import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import tconstruct.mechworks.inventory.ContainerLandmine;
import tconstruct.mechworks.landmine.behavior.Behavior;

/**
 * 
 * @author fuj1n
 * 
 */
public class GuiLandmine extends GuiContainer
{

    ContainerLandmine container;

    ResourceLocation background = new ResourceLocation("tinker:textures/gui/landmine.png");

    public GuiLandmine(ContainerLandmine par1Container)
    {
        super(par1Container);
        container = par1Container;
    }

    @Override
    protected void renderToolTip (ItemStack par1ItemStack, int par2, int par3)
    {
        List list = par1ItemStack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

        Behavior b = Behavior.getBehaviorFromStack(par1ItemStack);
        if (b != null)
        {
            b.getInformation(par1ItemStack, list);
        }

        for (int k = 0; k < list.size(); ++k)
        {
            if (k == 0)
            {
                list.set(k, par1ItemStack.getRarity().rarityColor + (String) list.get(k));
            }
            else
            {
                list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));
            }
        }

        FontRenderer font = par1ItemStack.getItem().getFontRenderer(par1ItemStack);
        drawHoveringText(list, par2, par3, (font == null ? fontRendererObj : font));
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int i, int j)
    {
        if (container.te != null)
        {
            fontRendererObj.drawString(container.te.getInventoryName(), 8, 5, 4210752);
        }
        else
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.landmine"), 8, 5, 4210752);
        }
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 3, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(background);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

}
