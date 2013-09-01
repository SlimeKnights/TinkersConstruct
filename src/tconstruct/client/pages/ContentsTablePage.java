package tconstruct.client.pages;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tconstruct.library.client.TConstructClientRegistry;

public class ContentsTablePage extends BookPage
{
    String text;
    String[] iconText;
    ItemStack[] icons;

    @Override
    public void readPageFromXML (Element element)
    {
        NodeList nodes = element.getElementsByTagName("text");
        if (nodes != null)
            text = nodes.item(0).getTextContent();

        nodes = element.getElementsByTagName("link");
        iconText = new String[nodes.getLength()];
        icons = new ItemStack[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++)
        {
            NodeList children = nodes.item(i).getChildNodes();
            iconText[i] = children.item(1).getTextContent();
            icons[i] = TConstructClientRegistry.getManualIcon(children.item(3).getTextContent());
        }
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight)
    {
        if (text != null)
            manual.fonts.drawString("\u00a7n" + text, localWidth + 25 + manual.fonts.getStringWidth(text) / 2, localHeight + 4, 0);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        manual.renderitem.zLevel = 100;
        for (int i = 0; i < icons.length; i++)
        {
            manual.renderitem.renderItemIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[i], localWidth + 16, localHeight + 18 * i + 18);
            int yOffset = 18;
            if (iconText[i].length() > 40)
                yOffset = 13;
            manual.fonts.drawString(iconText[i], localWidth + 38, localHeight + 18 * i + yOffset, 0);
        }
        manual.renderitem.zLevel = 0;
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }
}
