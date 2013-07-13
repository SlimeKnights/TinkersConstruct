package mods.tinker.tconstruct.client.pages;

import mods.tinker.tconstruct.library.client.TConstructClientRegistry;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FurnacePage extends BookPage
{
    String text;
    ItemStack[] icons;

    @Override
    public void readPageFromXML (Element element)
    {
        NodeList nodes = element.getElementsByTagName("text");
        if (nodes != null)
            text = nodes.item(0).getTextContent();

        nodes = element.getElementsByTagName("recipe");
        if (nodes != null)
            icons = TConstructClientRegistry.getRecipeIcons(nodes.item(0).getTextContent());
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight)
    {
        if (text != null)
            manual.fonts.drawString("\u00a7n" + text, localWidth + 50, localHeight + 4, 0);

        GL11.glScalef(2f, 2f, 2f);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        manual.renderitem.zLevel = 100;

        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, TConstructClientRegistry.getManualIcon("coal"), (localWidth + 38) / 2, (localHeight + 110) / 2);
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 106) / 2, (localHeight + 74) / 2);
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[1], (localWidth + 38) / 2, (localHeight + 38) / 2);

        if (icons[0].stackSize > 1)
            manual.renderitem.renderItemOverlayIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 106) / 2, (localHeight + 74) / 2, String.valueOf(icons[0].stackSize));

        manual.renderitem.zLevel = 0;
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public void renderBackgroundLayer (int localWidth, int localHeight)
    {
        manual.getMC().renderEngine.bindTexture("/mods/tinker/textures/gui/bookfurnace.png");
        manual.drawTexturedModalRect(localWidth + 32, localHeight + 32, 0, 0, 111, 114);
    }
}
