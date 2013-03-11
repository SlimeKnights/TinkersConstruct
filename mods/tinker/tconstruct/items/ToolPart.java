package mods.tinker.tconstruct.items;


import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import mods.tinker.common.IToolPart;
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
		super(id, getNames(partType), buildTextureNames(textureType));
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	private static String[] getNames (String partType)
	{
		String[] names = new String[toolMaterialNames.length];
		for (int i = 0; i < toolMaterialNames.length; i++)
			names[i] = partType + toolMaterialNames[i];
		return names;
	}
	

	private static String[] buildTextureNames (String textureType)
	{
		String[] names = new String[toolMaterialNames.length];
		for (int i = 0; i < toolMaterialNames.length; i++)
			names[i] = toolTextureNames[i]+textureType;
		return names;
	}
	
	@SideOnly(Side.CLIENT)
    public void func_94581_a(IconRegister iconRegister)
    {
		this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.func_94245_a("tinker:parts/"+textureNames[i]);
        }
    }
	
	public static final String[] toolMaterialNames = new String[] { 
		"Wood", "Stone", "Iron", "Flint", "Cactus", "Bone", "Obsidian", "Netherrack", "Slime", "Paper", "Cobalt", "Ardite", "Manyullyn", "Copper", "Bronze", "Alumite", "Steel" };
	
	public static final String[] toolTextureNames = new String[] { 
		"wood", "stone", "iron", "flint", "cactus", "bone", "obsidian", "netherrack", "slime", "paper", "cobalt", "ardite", "manyullyn", "copper", "bronze", "alumite", "steel" };

	@Override
	public int getMaterialID (ItemStack stack)
	{
		return stack.getItemDamage();
	}
}
