package tconstruct.client.pages;

import mantle.client.pages.BookPage;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.*;
import org.w3c.dom.*;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.tools.ToolMaterial;
import tconstruct.library.util.HarvestLevels;

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
            icons[0] = MantleClientRegistry.getManualIcon(nodes.item(0).getTextContent());

        nodes = element.getElementsByTagName("toolmaterial");
        if (nodes != null && nodes.getLength() > 0)
            material = TConstructRegistry.getMaterial(nodes.item(0).getTextContent());
        else
            material = TConstructRegistry.getMaterial(title);

        nodes = element.getElementsByTagName("material").item(0).getChildNodes();

        icons[1] = MantleClientRegistry.getManualIcon(nodes.item(1).getTextContent());
        icons[2] = PatternBuilder.instance.getShardFromSet(material.name());
        icons[3] = PatternBuilder.instance.getRodFromSet(material.name());
    }

    @Override
    public void renderContentLayer (int localWidth, int localHeight, boolean isTranslatable)
    {
        String mat = new String("Material");
        String shard = new String("Shard");
        String rod = new String("Rod");
        String durability = new String("Durability");
        String handleModifier = new String("Handle Modifier");
        String fullToolDurability = new String("Full Tool Durability");
        String miningSpeed = new String("Mining Speed");
        String miningLevel = new String("Mining Level");
        String baseAttack = new String("Base Attack");
        String heart_ = new String("Heart");
        String hearts = new String("Hearts");
        String materialTrait = new String("Material Trait");
        String extraMod = new String("+1 Modifiers");
        String traitReinforced = new String("Material Trait: Reinforced");
        String reinforcedLevel = new String("Reinforced level");
        String stoneboundLevel = new String("Stonebound level");
        String splinteringLevel = new String("Splintering level");

        if (isTranslatable)
        {
            title = StatCollector.translateToLocal(title);
            iconText = StatCollector.translateToLocal(iconText);
            mat = StatCollector.translateToLocal(mat);
            shard = StatCollector.translateToLocal(shard);
            rod = StatCollector.translateToLocal(rod);
            durability = StatCollector.translateToLocal(durability);
            handleModifier = StatCollector.translateToLocal(handleModifier);
            fullToolDurability = StatCollector.translateToLocal(fullToolDurability);
            miningSpeed = StatCollector.translateToLocal(miningSpeed);
            miningLevel = StatCollector.translateToLocal(miningLevel);
            baseAttack = StatCollector.translateToLocal(baseAttack);
            heart_ = StatCollector.translateToLocal(heart_);
            hearts = StatCollector.translateToLocal(hearts);
            materialTrait = StatCollector.translateToLocal(materialTrait);
            extraMod = StatCollector.translateToLocal(extraMod);
            traitReinforced = StatCollector.translateToLocal(traitReinforced);
            reinforcedLevel = StatCollector.translateToLocal(reinforcedLevel);
            stoneboundLevel = StatCollector.translateToLocal(stoneboundLevel);
            splinteringLevel = StatCollector.translateToLocal(splinteringLevel);

        }
        manual.fonts.drawString("\u00a7n" + title, localWidth + 70, localHeight + 4, 0);
        manual.fonts.drawSplitString(iconText, localWidth, localHeight + 16, 178, 0);

        manual.fonts.drawString(mat + ": ", localWidth + 108, localHeight + 40, 0);
        manual.fonts.drawString(shard + ": ", localWidth + 108, localHeight + 72, 0);
        manual.fonts.drawString(rod + ": ", localWidth + 108, localHeight + 104, 0);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        manual.renderitem.zLevel = 100;
        // renderitem.renderItemAndEffectIntoGUI(fonts, getMC().renderEngine,
        // icons[0], localWidth + 50, localHeight + 0);
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[1], localWidth + 108, localHeight + 50);
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[2], localWidth + 108, localHeight + 82);
        manual.renderitem.renderItemAndEffectIntoGUI(manual.fonts, manual.getMC().renderEngine, icons[3], localWidth + 108, localHeight + 114);
        manual.renderitem.zLevel = 0;
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        String icon = icons[1].getTooltip(manual.getMC().thePlayer, false).get((0)).toString();
        if (isTranslatable)
            icon = StatCollector.translateToLocal(icon);
        int iconOffset = icon.length() > 12 ? 0 : 3;
        manual.fonts.drawSplitString(icon, localWidth + 128, localHeight + 50 + iconOffset, 52, 0);

        if (icons[2] != null)
        {
            icon = icons[2].getTooltip(manual.getMC().thePlayer, false).get((0)).toString();
            if (isTranslatable)
                icon = StatCollector.translateToLocal(icon);
            iconOffset = icon.length() > 12 ? 0 : 3;
            manual.fonts.drawSplitString(icons[2].getTooltip(manual.getMC().thePlayer, false).get((0)).toString(), localWidth + 128, localHeight + 82 + iconOffset, 52, 0);
        }
        if (icons[3] != null)
        {
            icon = icons[3].getTooltip(manual.getMC().thePlayer, false).get((0)).toString();
            if (isTranslatable)
                icon = StatCollector.translateToLocal(icon);
            iconOffset = icon.length() > 12 ? 0 : 3;
            manual.fonts.drawSplitString(icons[3].getTooltip(manual.getMC().thePlayer, false).get((0)).toString(), localWidth + 128, localHeight + 114 + iconOffset, 52, 0);
        }

        manual.fonts.drawString(durability + ": " + material.durability(), localWidth, localHeight + 40, 0);
        manual.fonts.drawString(handleModifier + ": " + material.handleDurability() + "x", localWidth, localHeight + 50, 0);
        manual.fonts.drawString(fullToolDurability + ": " + (int) (material.durability() * material.handleDurability()), localWidth, localHeight + 60, 0);

        manual.fonts.drawString(miningSpeed + ": " + material.toolSpeed() / 100f, localWidth, localHeight + 80, 0);
        manual.fonts.drawString(miningLevel + ": " + material.harvestLevel() + " (" + HarvestLevels.getHarvestLevelName(material.harvestLevel()) + ")", localWidth, localHeight + 90, 0);
        int attack = material.attack();
        String heart = attack == 2 ? " " + heart_ : " " + hearts;
        if (attack % 2 == 0)
            manual.fonts.drawString(baseAttack + ": " + material.attack() / 2 + heart, localWidth, localHeight + 100, 0);
        else
            manual.fonts.drawString(baseAttack + ": " + material.attack() / 2f + heart, localWidth, localHeight + 100, 0);

        int offset = 0;
        String ability = material.ability();
        if (!ability.equals(""))
        {
            manual.fonts.drawString(materialTrait + ": " + ability, localWidth, localHeight + 120 + 10 * offset, 0);
            offset++;
            if (material.name().equals("Paper") || material.name().equals("Thaumium"))
                manual.fonts.drawString(extraMod, localWidth, localHeight + 120 + 10 * offset, 0);
        }

        if (material.reinforced() > 0)
        {
            manual.fonts.drawString(traitReinforced, localWidth, localHeight + 120 + 10 * offset, 0);
            offset++;
            manual.fonts.drawString(reinforcedLevel + ": " + material.reinforced(), localWidth, localHeight + 120 + 10 * offset, 0);
            offset++;
        }

        if (material.shoddy() > 0)
        {
            manual.fonts.drawString(stoneboundLevel + ": " + material.shoddy(), localWidth, localHeight + 120 + 10 * offset, 0);
            offset++;
        }
        else if (material.shoddy() < 0)
        {
            manual.fonts.drawString(splinteringLevel + ": " + -material.shoddy(), localWidth, localHeight + 120 + 10 * offset, 0);
            offset++;
        }
    }
}
