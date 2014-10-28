package tconstruct.tools.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import tconstruct.library.accessory.AccessoryCore;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.HarvestTool;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.util.HarvestLevels;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public final class ToolStationGuiHelper {
    // non-instantiable
    private ToolStationGuiHelper() {}

    private static FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;

    public static void drawToolStats (ItemStack stack, int x, int y)
    {

        if (stack.getItem() instanceof ToolCore)
        {
            ToolCore tool = (ToolCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag(tool.getBaseTagName());
            drawCenteredString(fontRendererObj, "\u00A7n" + tool.getLocalizedToolName(), x + 55, y+8, 0xffffff);

            drawModularToolStats(stack, tool, tags, x, y + 24);
        }
        if(stack.getItem() instanceof ArmorCore)
        {
            ArmorCore armor = (ArmorCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag(armor.getBaseTagName());
            drawCenteredString(fontRendererObj, "\u00A7n" + stack.getDisplayName(), x+55, y+8, 0xffffff); // todo: localize

            drawModularArmorStats(stack, armor, tags, x, y+24);
        }
        if(stack.getItem() instanceof AccessoryCore)
        {
            AccessoryCore accessory = (AccessoryCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag(accessory.getBaseTagName());
            drawCenteredString(fontRendererObj, "\u00A7n" + stack.getDisplayName(), x+55, y+8, 0xffffff); // todo: localize

            drawModularAccessoryStats(stack, accessory, tags, x, y+24);
        }
    }

    public static void drawModularToolStats (ItemStack stack, ToolCore tool, NBTTagCompound tags, int x, int y)
    {
        FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRenderer;
        List categories = Arrays.asList(tool.getTraits());
        final int durability = tags.getInteger("Damage");
        final int maxDur = tags.getInteger("TotalDurability");
        int availableDurability = maxDur - durability;

        // Durability
        int base = y;
        int offset = 0;
        if (maxDur > 0)
        {
            if (maxDur >= 10000)
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation1"), x, base + offset * 11, 0xffffff);
                offset++;
                fontRendererObj.drawString("- " + availableDurability + "/" + maxDur, x, base + offset * 10, 0xffffff);
                offset++;
            }
            else
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation2") + availableDurability + "/" + maxDur, x, base + offset * 10, 0xffffff);
                offset++;
            }
        }

        final float stonebound = tags.getFloat("Shoddy");
        // Attack
        if (categories.contains("weapon"))
        {
            int attack = (int) (tags.getInteger("Attack")) + 1;
            float stoneboundDamage = (float) Math.log(durability / 72f + 1) * -2 * stonebound;
            attack += stoneboundDamage;
            attack *= tool.getDamageModifier();
            if (attack < 1)
                attack = 1;

            String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
            if (attack % 2 == 0)
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation3") + attack / 2 + heart, x, base + offset * 10, 0xffffff);
            else
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation3") + attack / 2f + heart, x, base + offset * 10, 0xffffff);
            offset++;

            if (stoneboundDamage != 0)
            {
                DecimalFormat df = new DecimalFormat("##.##");
                heart = stoneboundDamage == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
                String bloss = stoneboundDamage > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                fontRendererObj.drawString(bloss + df.format(stoneboundDamage / 2f) + heart, x, base + offset * 10, 0xffffff);
                offset++;
            }
            offset++;
        }

        if (categories.contains("bow"))
        {
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            int drawSpeed = tags.getInteger("DrawSpeed");
            float flightSpeed = tags.getFloat("FlightSpeed");
            float trueDraw = drawSpeed / 20f;
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation6") + df.format(trueDraw) + "s", x, base + offset * 10, 0xffffff);
            offset++;
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation7") + df.format(flightSpeed) + "x", x, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }

        if (categories.contains("ammo"))
        {
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            int attack = (int) (tags.getInteger("Attack"));
            float mass = tags.getFloat("Mass");
            float shatter = tags.getFloat("BreakChance");
            float accuracy = tags.getFloat("Accuracy");

            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation10"), x, base + offset * 10, 0xffffff);
            offset++;
            String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
            if (attack % 2 == 0)
                fontRendererObj.drawString("- " + attack / 2 + heart, x, base + offset * 10, 0xffffff);
            else
                fontRendererObj.drawString("- " + attack / 2f + heart, x, base + offset * 10, 0xffffff);
            offset++;
            int minAttack = attack;
            int maxAttack = attack * 2;
            heart = StatCollector.translateToLocal("gui.partcrafter9");
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation11"), x, base + offset * 10, 0xffffff);
            offset++;
            fontRendererObj.drawString(df.format(minAttack / 2f) + "-" + df.format(maxAttack / 2f) + heart, x, base + offset * 10, 0xffffff);
            offset++;
            offset++;

            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation8") + df.format(mass), x, base + offset * 10, 0xffffff);
            offset++;
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation9") + df.format(accuracy - 4) + "%", x, base + offset * 10, 0xffffff);
            offset++;
            /*
             * this.fontRendererObj.drawString("Chance to break: " +
             * df.format(shatter)+"%", xSize + 8, base + offset * 10, 0xffffff);
             * offset++;
             */
            offset++;
        }

        // Mining
        if (categories.contains("dualharvest"))
        {
            float mineSpeed = tags.getInteger("MiningSpeed") / 100f * ((HarvestTool) tool).breakSpeedModifier();
            float mineSpeed2 = tags.getInteger("MiningSpeed2") / 100f * ((HarvestTool) tool).breakSpeedModifier();
            float stoneboundSpeed = (float) Math.log(durability / ((HarvestTool) tool).stoneboundModifier() + 1) * 2 * stonebound;
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            float trueSpeed = mineSpeed + stoneboundSpeed;
            float trueSpeed2 = mineSpeed2 + stoneboundSpeed;

            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation12"), x, base + offset * 10, 0xffffff);
            offset++;
            fontRendererObj.drawString("- " + df.format(trueSpeed) + ", " + df.format(trueSpeed2), x, base + offset * 10, 0xffffff);
            offset++;
            if (stoneboundSpeed != 0)
            {
                String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                fontRendererObj.drawString(bloss + df.format(stoneboundSpeed), x, base + offset * 10, 0xffffff);
                offset++;
            }
            offset++;
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation13"), x, base + offset * 10, 0xffffff);
            offset++;
            fontRendererObj.drawString("- " + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel")) + ", " + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel2")), x, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }
        else if (categories.contains("harvest"))
        {
            float trueSpeed = AbilityHelper.calcToolSpeed(tool, tags);
            float stoneboundSpeed = AbilityHelper.calcToolSpeed(tool, tags);

            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            trueSpeed += stoneboundSpeed;
            if (trueSpeed < 0)
                trueSpeed = 0;
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation14") + df.format(trueSpeed), x, base + offset * 10, 0xffffff);
            offset++;
            if (stoneboundSpeed != 0 && !Float.isNaN(stoneboundSpeed))
            {
                String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                fontRendererObj.drawString(bloss + df.format(stoneboundSpeed), x, base + offset * 10, 0xffffff);
                offset++;
            }
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation15") + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel")), x, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }
        else if (categories.contains("utility"))
        {
            float mineSpeed = tags.getInteger("MiningSpeed");
            float trueSpeed = mineSpeed / (100f);
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation16") + trueSpeed, x, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }

        int modifiers = tags.getInteger("Modifiers");
        if (modifiers > 0)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), x, base + offset * 10, 0xffffff);
            offset++;
        }
        if (tags.hasKey("Tooltip1"))
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation17"), x, base + offset * 10, 0xffffff);
        }

        boolean displayToolTips = true;
        int tipNum = 0;
        while (displayToolTips)
        {
            tipNum++;
            String tooltip = "ModifierTip" + tipNum;
            if (tags.hasKey(tooltip))
            {
                String tipName = tags.getString(tooltip);
                fontRendererObj.drawString("- " + tipName, x, base + (offset + tipNum) * 10, 0xffffff);
            }
            else
                displayToolTips = false;
        }
    }

    private static DecimalFormat df =  new DecimalFormat("##.#");

    // todo: do this properly, quick and dirty fix
    public static void drawModularArmorStats (ItemStack stack, ArmorCore armor, NBTTagCompound tags, int x, int y)
    {
        List categories = Arrays.asList(armor.getTraits());
        int base = y;
        int offset = 0;

        // durability
        final int durability = tags.getInteger("Damage");
        final int maxDur = tags.getInteger("TotalDurability");
        int availableDurability = maxDur - durability;

        // Durability
        if (maxDur > 0)
        {
            if (maxDur >= 10000)
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation1"), x, base + offset * 11, 0xffffff);
                offset++;
                fontRendererObj.drawString("- " + availableDurability + "/" + maxDur, x, base + offset * 10, 0xffffff);
                offset++;
            }
            else
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation2") + availableDurability + "/" + maxDur, x, base + offset * 10, 0xffffff);
                offset++;
            }
        }
        // Damage reduction
        double damageReduction = tags.getDouble("DamageReduction");
        if(damageReduction > 0.000001d)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation19") + df.format(damageReduction), x, base + offset * 10, 0xffffff);
            offset++;
        }

        // Protection
        double protection = armor.getProtection(stack);
        double maxProtection = tags.getDouble("MaxDefense");
        //if(maxProtection > protection)
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation20") + df.format(protection) + "/" + df.format(maxProtection), 294, base + offset * 10, 0xffffff);
        //else
        //  fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation20") + df.format(protection), x, base + offset * 10, 0xffffff);
        offset++;

        offset++;
        int modifiers = tags.getInteger("Modifiers");
        if (modifiers > 0)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), x, base + offset * 10, 0xffffff);
            offset++;
        }
        if (tags.hasKey("Tooltip1"))
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation17"), x, base + offset * 10, 0xffffff);
        }

        boolean displayToolTips = true;
        int tipNum = 0;
        while (displayToolTips)
        {
            tipNum++;
            String tooltip = "ModifierTip" + tipNum;
            if (tags.hasKey(tooltip))
            {
                String tipName = tags.getString(tooltip);
                fontRendererObj.drawString("- " + tipName, x, base + (offset + tipNum) * 10, 0xffffff);
            }
            else
                displayToolTips = false;
        }
    }

    // todo: also quick and dirty fix
    public static void drawModularAccessoryStats (ItemStack stack, AccessoryCore accessory, NBTTagCompound tags, int x, int y)
    {
        List categories = Arrays.asList(accessory.getTraits());
        int base = y;
        int offset = 0;

        if (categories.contains("utility"))
        {
            float mineSpeed = tags.getInteger("MiningSpeed");
            float trueSpeed = mineSpeed / (100f);
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation16") + trueSpeed, x, base + offset * 10, 0xffffff);
            offset++;
        }

        offset++;
        int modifiers = tags.getInteger("Modifiers");
        if (modifiers > 0)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), x, base + offset * 10, 0xffffff);
            offset++;
        }
        if (tags.hasKey("Tooltip1"))
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation17"), x, base + offset * 10, 0xffffff);
        }

        boolean displayToolTips = true;
        int tipNum = 0;
        while (displayToolTips)
        {
            tipNum++;
            String tooltip = "ModifierTip" + tipNum;
            if (tags.hasKey(tooltip))
            {
                String tipName = tags.getString(tooltip);
                fontRendererObj.drawString("- " + tipName, x, base + (offset + tipNum) * 10, 0xffffff);
            }
            else
                displayToolTips = false;
        }
    }

    /**
     * Renders the specified text to the screen, center-aligned.
     * Copied out of GUI
     */
    public static void drawCenteredString(FontRenderer p_73732_1_, String p_73732_2_, int p_73732_3_, int p_73732_4_, int p_73732_5_)
    {
        p_73732_1_.drawStringWithShadow(p_73732_2_, p_73732_3_ - p_73732_1_.getStringWidth(p_73732_2_) / 2, p_73732_4_, p_73732_5_);
    }
}
