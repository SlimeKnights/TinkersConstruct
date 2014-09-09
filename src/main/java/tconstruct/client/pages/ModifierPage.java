package tconstruct.client.pages;

import mantle.client.pages.BookPage;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import org.w3c.dom.*;

import java.util.LinkedList;
import java.util.List;

public class ModifierPage extends BookPage
{
    String type;
    ItemStack[] icons;
    ItemStack[][] iconsMulti;
    ItemStack[] toolMulti;

    long lastUpdate;
    int counter;

    @Override
    public void readPageFromXML (Element element)
    {
        NodeList nodes = element.getElementsByTagName("tooltype");
        if (nodes != null)
            type = nodes.item(0).getTextContent();

        nodes = element.getElementsByTagName("recipe");
        if (nodes != null) {
            String recipe = nodes.item(0).getTextContent();
            icons = MantleClientRegistry.getRecipeIcons(recipe);

            if(type.equals("travelmulti"))
            {
                List<ItemStack[]> stacks = new LinkedList<ItemStack[]>();
                List<String> tools = new LinkedList<String>();
                String[] suffixes = new String[] { "goggles", "vest", "wings", "boots", "glove", "belt" };
                for(String suffix : suffixes)
                {
                    ItemStack[] icons2 = MantleClientRegistry.getRecipeIcons(nodes.item(0).getTextContent() + suffix);
                    if(icons2 != null) {
                        stacks.add(icons2);
                        tools.add(suffix);
                    }
                }

                iconsMulti = new ItemStack[stacks.size()][];
                toolMulti = new ItemStack[stacks.size()];
                for(int i = 0; i < stacks.size(); i++) {
                    iconsMulti[i] = stacks.get(i);
                    toolMulti[i] = MantleClientRegistry.getManualIcon("travel" + tools.get(i));
                }

                icons = iconsMulti[0];

                lastUpdate = System.currentTimeMillis();
                counter = 0;
            }
        }
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight, boolean isTranslatable)
    {
        String tStation = new String("Tool Station");
        if(icons.length > 4)
            tStation = "Tinker Table";
        if(icons.length > 3)
            tStation = "Tool Forge";
        if (isTranslatable)
            tStation = StatCollector.translateToLocal(tStation);
        manual.fonts.drawString("\u00a7n" + tStation, localWidth + 60, localHeight + 4, 0);
        GL11.glScalef(2f, 2f, 2f);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        ItemStack toolstack = MantleClientRegistry.getManualIcon("ironpick");
        if (type.equals("weapon"))
            toolstack = MantleClientRegistry.getManualIcon("ironlongsword");
        if(type.equals("travelgoggles"))
            toolstack = MantleClientRegistry.getManualIcon("travelgoggles");
        if(type.equals("travelvest"))
            toolstack = MantleClientRegistry.getManualIcon("travelvest");
        if(type.equals("travelwings"))
            toolstack = MantleClientRegistry.getManualIcon("travelwings");
        if(type.equals("travelboots"))
            toolstack = MantleClientRegistry.getManualIcon("travelboots");
        if(type.equals("travelbelt"))
            toolstack = MantleClientRegistry.getManualIcon("travelbelt");
        if(type.equals("travelglove"))
            toolstack = MantleClientRegistry.getManualIcon("travelglove");
        if(type.equals("travelmulti"))
            toolstack = toolMulti[counter];

        // update displayed item
        if(iconsMulti != null && iconsMulti.length > 0 && type.equals("travelmulti") && System.currentTimeMillis() - lastUpdate > 1000)
        {
            lastUpdate = System.currentTimeMillis();
            counter++;
            if(counter >= iconsMulti.length)
                counter = 0;
            icons = iconsMulti[counter];
            toolstack = toolMulti[counter];
        }

        manual.renderitem.zLevel = 100;
        if(icons.length < 4) {
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, toolstack, (localWidth + 54) / 2, (localHeight + 54) / 2);
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 130) / 2, (localHeight + 54) / 2);
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[1], (localWidth + 18) / 2, (localHeight + 36) / 2);
            if (icons[2] != null)
                manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[2], (localWidth + 18) / 2, (localHeight + 74) / 2);
        }
        else
        {
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, toolstack, (localWidth + 74) / 2, (localHeight + 54) / 2);
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[0], (localWidth + 140) / 2, (localHeight + 54) / 2);
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[1], (localWidth - 2) / 2, (localHeight + 36) / 2);
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[2], (localWidth - 2) / 2, (localHeight + 74) / 2);
            manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[3], (localWidth + 36) / 2, (localHeight + 36) / 2);
            if(icons[4] != null)
                manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[4], (localWidth + 36) / 2, (localHeight + 74) / 2);
        }
        manual.renderitem.zLevel = 0;

        GL11.glScalef(0.5F, 0.5F, 0.5F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/bookmodify.png");

    @Override
    public void renderBackgroundLayer (int localWidth, int localHeight)
    {
        manual.getMC().getTextureManager().bindTexture(background);
        if(icons.length > 3)
            manual.drawTexturedModalRect(localWidth - 7, localHeight + 32, 0, 80, 182, 78);
        else
            manual.drawTexturedModalRect(localWidth + 12, localHeight + 32, 0, 0, 154, 78);
    }
}
