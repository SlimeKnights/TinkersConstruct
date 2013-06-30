package mods.tinker.tconstruct.client.pages;

import mods.tinker.tconstruct.client.gui.PartCrafterGui;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.client.TConstructClientRegistry;
import mods.tinker.tconstruct.library.crafting.PatternBuilder;
import mods.tinker.tconstruct.library.tools.ToolMaterial;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MaterialPage extends BookPage
{
    String title;
    ItemStack[] icons;
    String iconText;
    ToolMaterial material;
    @Override
    public void readPageFromXML (Element element)
    {
        NodeList nodes = element.getElementsByTagName("title");
        if (nodes != null)
            title = nodes.item(0).getTextContent();

        icons = new ItemStack[4];

        nodes = element.getElementsByTagName("text");
        if (nodes != null)
            iconText = nodes.item(0).getTextContent();

        nodes = element.getElementsByTagName("icon");
        if (nodes != null)
            icons[0] = TConstructClientRegistry.getManualIcon(nodes.item(0).getTextContent());

        nodes = element.getElementsByTagName("toolmaterial");
        if (nodes != null && nodes.getLength() > 0)
            material = TConstructRegistry.getMaterial(nodes.item(0).getTextContent());
        else
            material = TConstructRegistry.getMaterial(title);

        nodes = element.getElementsByTagName("material").item(0).getChildNodes();

        icons[1] = TConstructClientRegistry.getManualIcon(nodes.item(1).getTextContent());
        icons[2] = PatternBuilder.instance.getShardFromSet(material.name());
        icons[3] = PatternBuilder.instance.getRodFromSet(material.name());
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight)
    {
        manual.fonts.drawString("\u00a7n" + title, localWidth + 70, localHeight + 4, 0);
        manual.fonts.drawSplitString(iconText, localWidth, localHeight + 16, 178, 0);

        manual.fonts.drawString("Material: ", localWidth + 108, localHeight + 40, 0);
        manual.fonts.drawString("Shard: ", localWidth + 108, localHeight + 72, 0);
        manual.fonts.drawString("Rod: ", localWidth + 108, localHeight + 104, 0);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        manual.renderitem.zLevel = 100;
        //renderitem.renderItemAndEffectIntoGUI(fonts, getMC().renderEngine, icons[0], localWidth + 50, localHeight + 0);
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[1], localWidth + 108, localHeight + 50);
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[2], localWidth + 108, localHeight + 82);
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[3], localWidth + 108, localHeight + 114);
        manual.renderitem.zLevel = 0;
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        String icon = icons[1].getTooltip(manual.getMC().thePlayer, false).get((0)).toString();
        int iconOffset = icon.length() > 12 ? 0 : 3;
        manual.fonts.drawSplitString(icon, localWidth + 128, localHeight + 50 + iconOffset, 52, 0);

        if (icons[2] != null)
        {
            icon = icons[2].getTooltip(manual.getMC().thePlayer, false).get((0)).toString();
            iconOffset = icon.length() > 12 ? 0 : 3;
            manual.fonts.drawSplitString(icons[2].getTooltip(manual.getMC().thePlayer, false).get((0)).toString(), localWidth + 128, localHeight + 82 + iconOffset, 52, 0);
        }
        if (icons[3] != null)
        {
            icon = icons[3].getTooltip(manual.getMC().thePlayer, false).get((0)).toString();
            iconOffset = icon.length() > 12 ? 0 : 3;
            manual.fonts.drawSplitString(icons[3].getTooltip(manual.getMC().thePlayer, false).get((0)).toString(), localWidth + 128, localHeight + 114 + iconOffset, 52, 0);
        }

        manual.fonts.drawString("Durability: " + material.durability(), localWidth, localHeight + 40, 0);
        manual.fonts.drawString("Handle Modifier: " + material.handleDurability() + "x", localWidth, localHeight + 50, 0);
        manual.fonts.drawString("Full Tool Durability: " + (int) (material.durability() * material.handleDurability()), localWidth, localHeight + 60, 0);

        manual.fonts.drawString("Mining Speed: " + material.toolSpeed() / 100f, localWidth, localHeight + 80, 0);
        manual.fonts.drawString("Mining Level: " + material.harvestLevel() + " (" + PartCrafterGui.getHarvestLevelName(material.harvestLevel()) + ")", localWidth, localHeight + 90, 0);
        int attack = material.attack();
        String heart = attack == 2 ? " Heart" : " Hearts";
        if (attack % 2 == 0)
            manual.fonts.drawString("Base Attack: " + material.attack() / 2 + heart, localWidth, localHeight + 100, 0);
        else
            manual.fonts.drawString("Base Attack: " + material.attack() / 2f + heart, localWidth, localHeight + 100, 0);

        int offset = 0;
        String ability = material.ability();
        if (!ability.equals(""))
        {
            manual.fonts.drawString("Material ability: " + material.ability(), localWidth, localHeight + 120 + 10 * offset, 0);
            offset++;
            if (ability.equals("Writable"))
                manual.fonts.drawString("+1 Modifiers", localWidth, localHeight + 120 + 10 * offset, 0);
        }

        if (material.reinforced() > 0)
        {
            manual.fonts.drawString("Material ability: Reinforced", localWidth, localHeight + 120 + 10 * offset, 0);
            offset++;
            manual.fonts.drawString("Reinforced level: " + material.reinforced(), localWidth, localHeight + 120 + 10 * offset, 0);
            offset++;
        }

        if (material.shoddy() > 0)
        {
            manual.fonts.drawString("Stonebound level: " + material.shoddy(), localWidth, localHeight + 120 + 10 * offset, 0);
            offset++;
        }
        else if (material.shoddy() < 0)
        {
            manual.fonts.drawString("Splintering level: " + -material.shoddy(), localWidth, localHeight + 120 + 10 * offset, 0);
            offset++;
        }
    }
}
