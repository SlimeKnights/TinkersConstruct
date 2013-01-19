package tinker.tconstruct.tools;

import tinker.tconstruct.AbilityHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class Mattock extends DualHarvestTool
{
	public Mattock(int itemID, String tex)
	{
		super(itemID, 3, tex);
		this.setItemName("InfiTool.Mattock");
	}

	@Override
	protected Material[] getEffectiveMaterials ()
	{
		return axeMaterials;
	}

	@Override
	protected Material[] getEffectiveSecondaryMaterials ()
	{
		return shovelMaterials;
	}

	@Override
	protected String getHarvestType ()
	{
		return "axe";
	}

	@Override
	protected String getSecondHarvestType ()
	{
		return "shovel";
	}
	
	static Material[] axeMaterials = { Material.wood, Material.circuits, Material.cactus, Material.pumpkin, Material.leaves };
	static Material[] shovelMaterials = { Material.grass, Material.ground, Material.sand, Material.snow, Material.craftedSnow, Material.clay };
	
	/* Mattock specific */
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        return AbilityHelper.hoeGround(stack, player, world, x, y, z, side, random);
    }
}