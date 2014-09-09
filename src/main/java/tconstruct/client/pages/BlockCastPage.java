package tconstruct.client.pages;

import mantle.client.pages.BookPage;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import org.w3c.dom.*;

public class BlockCastPage extends BookPage
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
            icons = MantleClientRegistry.getRecipeIcons(nodes.item(0).getTextContent());
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight, boolean isTranslatable)
    {
        if (text != null)
        {
            if (isTranslatable)
                text = StatCollector.translateToLocal(text);
            manual.fonts.drawString("\u00a7n" + text, localWidth + 70, localHeight + 4, 0);
        }
        GL11.glScalef(2f, 2f, 2f);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        manual.renderitem.zLevel = 100;

        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 138) / 2, (localHeight + 110) / 2);
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[1], (localWidth + 70) / 2, (localHeight + 74) / 2);
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[2], (localWidth + 70) / 2, (localHeight + 110) / 2);

        if (icons[0].stackSize > 1)
            manual.renderitem.renderItemOverlayIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 106) / 2, (localHeight + 74) / 2, String.valueOf(icons[0].stackSize));

        manual.renderitem.zLevel = 0;
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        String ingr = new String("Ingredients");
        if (isTranslatable)
            ingr = StatCollector.translateToLocal(ingr);
        manual.fonts.drawString(ingr + ":", localWidth + 120, localHeight + 32, 0);
        manual.fonts.drawString("- " + icons[1].getDisplayName(), localWidth + 120, localHeight + 42, 0);
        if (icons[2] != null)
            manual.fonts.drawString("- " + icons[2].getDisplayName(), localWidth + 120, localHeight + 50, 0);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/booksmeltery.png");

    @Override
    public void renderBackgroundLayer (int localWidth, int localHeight)
    {
        manual.getMC().getTextureManager().bindTexture(background);
        manual.drawTexturedModalRect(localWidth, localHeight + 32, 0, 0, 174, 115);
        manual.drawTexturedModalRect(localWidth + 62, localHeight + 105, 2, 118, 45, 45);
    }
}
