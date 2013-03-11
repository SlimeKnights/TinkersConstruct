package mods.tinker.tconstruct;

import java.util.ArrayList;
import java.util.HashMap;

import mods.tinker.tconstruct.client.gui.ToolGuiElement;
import mods.tinker.tconstruct.tools.ToolCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** A registry to store any relevant API work
 * 
 * @author mDiyo
 */

public class TConstructRegistry
{
	public static TConstructRegistry instance = new TConstructRegistry();
	public static ArrayList<ToolCore> tools = new ArrayList<ToolCore>(20);
	public static ArrayList<ToolGuiElement> toolButtons = new ArrayList<ToolGuiElement>(20);
	public static HashMap<Integer, ToolMaterial> toolMaterials = new HashMap<Integer, ToolMaterial>(60);
	@SideOnly(Side.CLIENT)

	//Tools
	public static void addToolMapping (ToolCore tool)
	{
		tools.add(tool);
	}

	public static ArrayList<ToolCore> getToolMapping ()
	{
		return tools;
	}

	//Rendering

	//itemMap.func_94245_a
	

	/*public static void addMaterialRenderMapping(int materialID, String textureLocation)
	{
		for (ToolCore tool : TConstructRegistry.getToolMapping())
		{
			tool.partTextures.put(materialID, textureLocation);
		}
	}
	
	public static void addEffectRenderMapping(int materialID, String textureLocation)
	{
		for (ToolCore tool : TConstructRegistry.getToolMapping())
		{
			tool.effectTextures.put(materialID, textureLocation);
		}
	}*/

	//Materials
	public static void addToolMaterial (int materialID, String materialName, int craftingTier, int harvestLevel, int durability, int miningspeed, int attack, float handleModifier, int reinforced, float shoddy)
	{
		ToolMaterial mat = toolMaterials.get(materialID);
		if (mat == null)
		{
			toolMaterials.put(materialID, new ToolMaterial(materialName, craftingTier, harvestLevel, durability, miningspeed, attack, handleModifier, reinforced, shoddy));
		}
		else
			throw new RuntimeException("TConstruct material ID " + materialID + " is already occupied by " + mat.materialName);
	}

	public static ToolMaterial getMaterial (int key)
	{
		return (toolMaterials.get(key));
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
