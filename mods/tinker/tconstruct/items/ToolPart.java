package mods.tinker.tconstruct.items;


import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.tinker.tconstruct.library.IToolPart;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;

public class ToolPart extends CraftingItem
	implements IToolPart
{
	public ToolPart(int id, String partType, String textureType)
	{
		super(id, toolMaterialNames, buildTextureNames(textureType), "parts/");
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	private static String[] buildTextureNames (String textureType)
	{
		String[] names = new String[toolMaterialNames.length];
		for (int i = 0; i < toolMaterialNames.length; i++)
			names[i] = toolTextureNames[i]+textureType;
		return names;
	}
	
	/*@SideOnly(Side.CLIENT)
    public void updateIcons(IconRegister iconRegister)
    {
		this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:parts/"+textureNames[i]);
        }
    }*/
	
	public static final String[] toolMaterialNames = new String[] { 
		"Wood", "Stone", "Iron", "Flint", "Cactus", "Bone", "Obsidian", "Netherrack", "Slime", "Paper", "Cobalt", "Ardite", "Manyullyn", "Copper", "Bronze", "Alumite", "Steel", "Blue Slime" };
	
	public static final String[] toolTextureNames = new String[] { 
		"wood", "stone", "iron", "flint", "cactus", "bone", "obsidian", "netherrack", "slime", "paper", "cobalt", "ardite", "manyullyn", "copper", "bronze", "alumite", "steel", "blueslime" };

	@Override
	public int getMaterialID (ItemStack stack)
	{
		return stack.getItemDamage();
	}
}
