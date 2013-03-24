package mods.tinker.tconstruct.library;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TConstructClientRegistry
{
	public static Map<String, ItemStack> manualIcons = new HashMap<String, ItemStack>();
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
					icon += tool.getDefaultFolder()+"/";
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
				icon += tool.getDefaultFolder()+"/";
			icon += renderName + tool.getEffectSuffix();
			tool.registerEffectPath(materialID, icon);
		}
	}
	
	public static void registerManualIcon(String name, ItemStack stack)
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
}
