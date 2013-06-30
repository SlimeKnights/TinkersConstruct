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

public class SectionPage extends BookPage
{
    String title;
    String body;
    @Override
    public void readPageFromXML (Element element)
    {
        NodeList nodes = element.getElementsByTagName("title");
        if (nodes != null)
            title = nodes.item(0).getTextContent();

        nodes = element.getElementsByTagName("text");
        if (nodes != null)
            body = nodes.item(0).getTextContent();
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight)
    {
        manual.fonts.drawSplitString("\u00a7n" + title, localWidth + 70, localHeight + 4, 178, 0);
        manual.fonts.drawSplitString(body, localWidth, localHeight + 16, 190, 0);
    }
}
