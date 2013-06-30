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

public class ModifierPage extends BookPage
{
    String type;
    ItemStack[] icons;
    @Override
    public void readPageFromXML (Element element)
    {
        NodeList nodes = element.getElementsByTagName("tooltype");
        if (nodes != null)
            type = nodes.item(0).getTextContent();

        nodes = element.getElementsByTagName("recipe");
        if (nodes != null)
            icons = TConstructClientRegistry.getRecipeIcons(nodes.item(0).getTextContent());
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight)
    {
        manual.fonts.drawString("\u00a7nTool Station", localWidth + 60, localHeight + 4, 0);
        GL11.glScalef(2f, 2f, 2f);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        ItemStack toolstack = TConstructClientRegistry.getManualIcon("ironpick");
        if (type.equals("weapon"))
            toolstack = TConstructClientRegistry.getManualIcon("ironlongsword");

        manual.renderitem.zLevel = 100;
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, toolstack, (localWidth + 54) / 2, (localHeight + 54) / 2);
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 130) / 2, (localHeight + 54) / 2);
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[1], (localWidth + 18) / 2, (localHeight + 36) / 2);
        if (icons[2] != null)
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[2], (localWidth + 18) / 2, (localHeight + 74) / 2);
        manual.renderitem.zLevel = 0;

        GL11.glScalef(0.5F, 0.5F, 0.5F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public void renderBackgroundLayer(int localWidth, int localHeight) 
    {
        manual.getMC().renderEngine.bindTexture("/mods/tinker/textures/gui/bookmodify.png");
        manual.drawTexturedModalRect(localWidth + 12, localHeight + 32, 0, 0, 154, 78);
    }
}
