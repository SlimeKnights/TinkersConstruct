package mods.tinker.tconstruct.client.pages;

import mods.tinker.tconstruct.client.TProxyClient;
import mods.tinker.tconstruct.client.gui.GuiManual;
import mods.tinker.tconstruct.library.client.TConstructClientRegistry;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CraftingPage extends BookPage
{
    String text;
    String size;
    ItemStack[] icons;
    @Override
    public void readPageFromXML (Element element)
    {
        NodeList nodes = element.getElementsByTagName("text");
        if (nodes != null)
            text = nodes.item(0).getTextContent();

        nodes = element.getElementsByTagName("name");
        if (nodes != null)
            icons = TConstructClientRegistry.getRecipeIcons(nodes.item(0).getTextContent());

        nodes = element.getElementsByTagName("size");
        if (nodes != null)
            size = nodes.item(0).getTextContent();
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight)
    {
        if (size.equals("two"))
            drawCraftingPage(text, icons, 2, localWidth, localHeight + 12);
        if (size.equals("three"))
            drawCraftingPage(text, icons, 3, localWidth + (side != 1 ? 6 : 0), localHeight + 12);
    }
    
    public void drawCraftingPage (String info, ItemStack[] icons, int recipeSize, int localWidth, int localHeight)
    {
        if (info != null)
            manual.fonts.drawString("\u00a7n" + info, localWidth + 50, localHeight + 4, 0);

        GL11.glScalef(2f, 2f, 2f);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        manual.renderitem.zLevel = 100;

        if (recipeSize == 2)
        {
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 126) / 2, (localHeight + 68) / 2);
            if (icons[0].stackSize > 1)
                manual.renderitem.renderItemOverlayIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 126) / 2, (localHeight + 68) / 2, String.valueOf(icons[0].stackSize));
            for (int i = 0; i < icons.length - 1; i++)
            {
                if (icons[i + 1] != null)
                    manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[i + 1], (localWidth + 14 + 36 * (i % 2)) / 2, (localHeight + 36 * (i / 2) + 52) / 2);
            }
        }

        if (recipeSize == 3)
        {
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 138) / 2, (localHeight + 70) / 2);
            if (icons[0].stackSize > 1)
                manual.renderitem.renderItemOverlayIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 126) / 2, (localHeight + 68) / 2, String.valueOf(icons[0].stackSize));
            for (int i = 0; i < icons.length - 1; i++)
            {
                if (icons[i + 1] != null)
                    manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[i + 1], (localWidth - 2 + 36 * (i % 3)) / 2, (localHeight + 36 * (i / 3) + 34) / 2);
            }
        }

        manual.renderitem.zLevel = 0;
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public void renderBackgroundLayer(int localwidth, int localheight) 
    {
        if (size.equals("two"))
            drawBackground(2, localwidth, localheight + 12);

        if (size.equals("three"))
            drawBackground(3, localwidth + (side != 1 ? 6 : 0), localheight + 12);
    }

    public void drawBackground (int size, int localWidth, int localHeight)
    {
        manual.getMC().renderEngine.bindTexture("/mods/tinker/textures/gui/bookcrafting.png");
        if (size == 2)
            manual.drawTexturedModalRect(localWidth + 8, localHeight + 46, 0, 116, 154, 78);
        if (size == 3)
            manual.drawTexturedModalRect(localWidth - 8, localHeight + 28, 0, 0, 183, 114);
    }

}
