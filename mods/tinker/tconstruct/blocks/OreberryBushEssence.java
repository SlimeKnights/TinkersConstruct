package mods.tinker.tconstruct.blocks;

import java.util.List;
import java.util.Random;

import mods.tinker.tconstruct.client.block.OreberryRender;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.tools.AbilityHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeavesBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class OreberryBushEssence extends OreberryBush implements IPlantable
{
	public OreberryBushEssence(int id, String[] textureNames, int meta, int sub, String[] oreTypes)
	{
		super(id, textureNames, meta, sub, oreTypes);
	}

	/* Bush growth */

	@Override
	public void updateTick (World world, int x, int y, int z, Random random1)
	{
		if (world.isRemote)
		{
			return;
		}

		if (random1.nextInt(20) == 0)// && world.getBlockLightValue(x, y, z) <= 8)
		{
			int meta = world.getBlockMetadata(x, y, z);
			if (world.getFullBlockLightValue(x, y, z) < 10 || meta % 4 == 1)
			{
				if (meta < 12)
				{
					world.setBlock(x, y, z, blockID, meta + 4, 3);
				}
			}
			/*else if (meta < 8)
			{
				world.setBlock(x, y, z, blockID, meta + 4, 3);
			}*/
		}
	}

	public boolean canPlaceBlockAt (World world, int x, int y, int z)
	{
	    return true;
		/*if (world.getFullBlockLightValue(x, y, z) < 13)
			return super.canPlaceBlockAt(world, x, y, z);
		return false;*/
	}
}
