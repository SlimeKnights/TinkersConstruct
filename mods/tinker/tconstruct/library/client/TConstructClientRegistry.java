package mods.tinker.tconstruct.library.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.crafting.ToolBuilder;
import mods.tinker.tconstruct.library.tools.ToolCore;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TConstructClientRegistry
{
    public static ArrayList<ToolGuiElement> toolButtons = new ArrayList<ToolGuiElement>(20);
    public static Map<String, ItemStack> manualIcons = new HashMap<String, ItemStack>();
    public static Map<String, ItemStack[]> recipeIcons = new HashMap<String, ItemStack[]>();
    public static ItemStack defaultStack = new ItemStack(Item.ingotIron);

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

    public static void registerManualIcon (String name, ItemStack stack)
    {
        manualIcons.put(name, stack);
    }

    public static ItemStack getManualIcon (String textContent)
    {
        ItemStack stack = manualIcons.get(textContent);
        if (stack != null)
            return stack;
        return defaultStack;
    }

    public static void registerManualSmallRecipe (String name, ItemStack output, ItemStack... stacks)
    {
        ItemStack[] recipe = new ItemStack[5];
        recipe[0] = output;
        System.arraycopy(stacks, 0, recipe, 1, 4);
        recipeIcons.put(name, recipe);
    }

    public static void registerManualLargeRecipe (String name, ItemStack output, ItemStack... stacks)
    {
        ItemStack[] recipe = new ItemStack[10];
        recipe[0] = output;
        System.arraycopy(stacks, 0, recipe, 1, 9);
        recipeIcons.put(name, recipe);
    }

    public static void registerManualFurnaceRecipe (String name, ItemStack output, ItemStack input)
    {
        ItemStack[] recipe = new ItemStack[2];
        recipe[0] = output;
        recipe[1] = input;
        recipeIcons.put(name, recipe);
    }

    public static void registerManualModifier (String name, ItemStack output, ItemStack topinput)
    {
        registerManualModifier(name, output, topinput, null);
    }

    public static void registerManualModifier (String name, ItemStack output, ItemStack topinput, ItemStack bottominput)
    {
        ItemStack[] recipe = new ItemStack[3];
        recipe[0] = ToolBuilder.instance.buildTool(output, topinput, bottominput, "");
        recipe[1] = topinput;
        recipe[2] = bottominput;
        recipeIcons.put(name, recipe);
    }

    public static ItemStack[] getRecipeIcons (String recipeName)
    {
        return recipeIcons.get(recipeName);
    }

    //Gui
    public static void addToolButton (ToolGuiElement element)
    {
        toolButtons.add(element);
    }

    public static void addToolButton (int slotType, int xButton, int yButton, int[] xIcons, int[] yIcons, String title, String body, String texture)
    {
        toolButtons.add(new ToolGuiElement(slotType, xButton, yButton, xIcons, yIcons, title, body, texture));
    }

    public static ArrayList<ToolGuiElement> getToolButtons ()
    {
        return toolButtons;
    }
}
