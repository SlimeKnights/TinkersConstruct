package mods.tinker.tconstruct.client.pages;

import mods.tinker.tconstruct.client.TProxyClient;
import mods.tinker.tconstruct.client.gui.GuiManual;
import mods.tinker.tconstruct.library.client.BookPage;
import mods.tinker.tconstruct.library.client.TConstructClientRegistry;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TitlePage extends BookPage
{
    String text;
    @Override
    public void readPageFromXML (Element element)
    {
        NodeList nodes = element.getElementsByTagName("text");
        if (nodes != null)
            text = nodes.item(0).getTextContent();
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight)
    {
        manual.fonts.drawSplitString(text, localWidth, localHeight, 178, 0);
    }
}
