package tconstruct.client.pages;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tconstruct.library.client.TConstructClientRegistry;


public class ToolPage extends BookPage
{
    String title;
    ItemStack[] icons;
    String[] iconText;

    @Override
    public void readPageFromXML (Element element)
    {
        NodeList nodes = element.getElementsByTagName("title");
        if (nodes != null)
            title = nodes.item(0).getTextContent();

        nodes = element.getElementsByTagName("item");
        iconText = new String[nodes.getLength() + 2];
        icons = new ItemStack[nodes.getLength() + 1];

        for (int i = 0; i < nodes.getLength(); i++)
        {
            NodeList children = nodes.item(i).getChildNodes();
            iconText[i + 2] = children.item(1).getTextContent();
            icons[i + 1] = TConstructClientRegistry.getManualIcon(children.item(3).getTextContent());
        }

        nodes = element.getElementsByTagName("text");
        if (nodes != null)
        {
            iconText[0] = nodes.item(0).getTextContent();
            iconText[1] = nodes.item(1).getTextContent();
        }

        nodes = element.getElementsByTagName("icon");
        if (nodes != null)
            icons[0] = TConstructClientRegistry.getManualIcon(nodes.item(0).getTextContent());
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight)
    {
        manual.fonts.drawString("\u00a7n" + title, localWidth + 70, localHeight + 4, 0);
        manual.fonts.drawSplitString(iconText[0], localWidth, localHeight + 16, 178, 0);
        int size = iconText[0].length() / 48;
        manual.fonts.drawSplitString(iconText[1], localWidth, localHeight + 28 + 10 * size, 118, 0);

        manual.fonts.drawString("Crafting Parts: ", localWidth + 124, localHeight + 28 + 10 * size, 0);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        manual.renderitem.zLevel = 100;
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], localWidth + 50, localHeight + 0);
        for (int i = 1; i < icons.length; i++)
        {
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[i], localWidth + 120, localHeight + 20 + 10 * size + 18 * i);
            int partOffset = iconText[i + 1].length() > 11 ? -3 : 0;
            manual.fonts.drawSplitString(iconText[i + 1], localWidth + 140, localHeight + 24 + 10 * size + 18 * i + partOffset, 44, 0);
        }
        manual.renderitem.zLevel = 0;
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }
}
