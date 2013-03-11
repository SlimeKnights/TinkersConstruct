package mods.tinker.tconstruct.client;

import mods.tinker.tconstruct.TConstructRegistry;
import mods.tinker.tconstruct.tools.ToolCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;

public class TConstructClientRegistry
{
	public static TextureMap itemMap = Minecraft.getMinecraft().renderEngine.field_94155_m;
	
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
}
