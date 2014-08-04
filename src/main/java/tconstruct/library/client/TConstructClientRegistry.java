package tconstruct.library.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mantle.lib.client.MantleClientRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.tools.ToolCore;

public class TConstructClientRegistry
{
    public static ArrayList<ToolGuiElement> toolButtons = new ArrayList<ToolGuiElement>(20);
    public static ArrayList<ToolGuiElement> tierTwoButtons = new ArrayList<ToolGuiElement>();
    public static Map<String, ItemStack> manualIcons = new HashMap<String, ItemStack>();
    public static ItemStack defaultStack = new ItemStack(Items.iron_ingot);

    public static void addMaterialRenderMapping (int materialID, String domain, String renderName, boolean useDefaultFolder)
    {
        for (ToolCore tool : TConstructRegistry.getToolMapping())
        {
            String[] toolIcons = new String[tool.getPartAmount() + 1];
            for (int i = 0; i < tool.getPartAmount() + 1; i++)
            {
                String icon = domain + ":";
                if (useDefaultFolder)
                    icon += tool.getDefaultFolder() + "/";
                icon += renderName + tool.getIconSuffix(i);
                toolIcons[i] = icon;
            }
            tool.registerPartPaths(materialID, toolIcons);
        }
    }

    public static void addAlternateMaterialRenderMapping (ToolCore tool, int materialID, String domain, String renderName, boolean useDefaultFolder)
    {
        String[] toolIcons = new String[tool.getPartAmount() + 1];
        for (int i = 0; i < tool.getPartAmount() + 1; i++)
        {
            String icon = domain + ":";
            if (useDefaultFolder)
                icon += tool.getDefaultFolder() + "/";
            icon += renderName + tool.getIconSuffix(i);
            toolIcons[i] = icon;
        }
        tool.registerAlternatePartPaths(materialID, toolIcons);
    }

    public static void addEffectRenderMapping (ToolCore tool, int materialID, String domain, String renderName, boolean useDefaultFolder)
    {
        String icon = domain + ":";
        if (useDefaultFolder)
            icon += tool.getDefaultFolder() + "/";
        icon += renderName + tool.getEffectSuffix();
        tool.registerEffectPath(materialID, icon);
    }

    public static void addEffectRenderMapping (int materialID, String domain, String renderName, boolean useDefaultFolder)
    {
        for (ToolCore tool : TConstructRegistry.getToolMapping())
        {
            String icon = domain + ":";
            if (useDefaultFolder)
                icon += tool.getDefaultFolder() + "/";
            icon += renderName + tool.getEffectSuffix();
            tool.registerEffectPath(materialID, icon);
        }
    }

    public static void addSingleEffectRenderMapping (ToolCore tool, int materialID, String domain, String renderName, boolean useDefaultFolder)
    {
        String icon = domain + ":";
        if (useDefaultFolder)
            icon += tool.getDefaultFolder() + "/";
        icon += renderName + tool.getEffectSuffix();
        tool.registerEffectPath(materialID, icon);
    }

    public static void registerManualModifier (String name, ItemStack output, ItemStack topinput)
    {
        registerManualModifier(name, output, topinput, null);
    }

    public static void registerManualModifier (String name, ItemStack output, ItemStack topinput, ItemStack bottominput)
    {
        ItemStack[] recipe = new ItemStack[3];
        recipe[0] = ModifyBuilder.instance.modifyItem(output, new ItemStack[] {topinput, bottominput});//ToolBuilder.instance.buildTool(output, topinput, bottominput, "");
        recipe[1] = topinput;
        recipe[2] = bottominput;
        MantleClientRegistry.recipeIcons.put(name, recipe);
    }

    public static void registerManualSmeltery (String name, ItemStack output, ItemStack liquid, ItemStack cast)
    {
        ItemStack[] recipe = new ItemStack[3];
        recipe[0] = output;
        recipe[1] = liquid;
        recipe[2] = cast;
        MantleClientRegistry.recipeIcons.put(name, recipe);
    }

    //Gui
    public static void addToolButton (ToolGuiElement element)
    {
        toolButtons.add(element);
    }

    public static void addToolButton (int slotType, int xButton, int yButton, int[] xIcons, int[] yIcons, String title, String body, String domain, String texture)
    {
        toolButtons.add(new ToolGuiElement(slotType, xButton, yButton, xIcons, yIcons, title, body, domain, texture));
    }

    public static void addTierTwoButton (ToolGuiElement element)
    {
        tierTwoButtons.add(element);
    }

    public static void addTierTwoButton (int slotType, int xButton, int yButton, int[] xIcons, int[] yIcons, String title, String body, String domain, String texture)
    {
        tierTwoButtons.add(new ToolGuiElement(slotType, xButton, yButton, xIcons, yIcons, title, body, domain, texture));
    }

    public static ArrayList<ToolGuiElement> getToolButtons ()
    {
        return toolButtons;
    }
}
