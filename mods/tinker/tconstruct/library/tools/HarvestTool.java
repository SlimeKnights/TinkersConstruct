package mods.tinker.tconstruct.library.tools;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.ActiveToolMod;
import mods.tinker.tconstruct.library.TConstructRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/* Base class for tools that should be harvesting blocks */

public abstract class HarvestTool extends ToolCore
{

	public HarvestTool(int itemID, int baseDamage)
	{
		super(itemID, baseDamage);
	}

	@Override
	public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
	{
		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		World world = player.worldObj;
		int bID = player.worldObj.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		Block block = Block.blocksList[bID];
		if (block == null || bID < 1)
			return false;
		int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());

		if (hlvl <= tags.getInteger("HarvestLevel"))
		{
			boolean cancelHarvest = false;
			for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
			{
				if (mod.beforeBlockBreak(this, stack, x, y, z, player))
					cancelHarvest = true;
			}
			return cancelHarvest;
		}
		else
		{
			world.setBlockToAir(x, y, z);
			if (!player.capabilities.isCreativeMode)
				onBlockDestroyed(stack, world, bID, x, y, z, player);
			if (!world.isRemote)
				world.playAuxSFX(2001, x, y, z, bID + (meta << 12));
			return true;
		}
	}

	@Override
	public float getStrVsBlock (ItemStack stack, Block block, int meta)
	{

		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		if (tags.getBoolean("Broken"))
			return 0.1f;

		Material[] materials = getEffectiveMaterials();
		for (int i = 0; i < materials.length; i++)
		{
			if (materials[i] == block.blockMaterial)
			{
				float speed = tags.getInteger("MiningSpeed");
				speed /= 100f;
				int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
				int durability = tags.getInteger("Damage");

				float shoddy = tags.getFloat("Shoddy");
				speed += shoddy * durability / 100f;

				if (hlvl <= tags.getInteger("HarvestLevel"))
					return speed;
				return 0.1f;
			}
		}
		return super.getStrVsBlock(stack, block, meta);
	}

	public boolean canHarvestBlock (Block block)
	{
		if (block.blockMaterial.isToolNotRequired())
		{
			return true;
		}
		for (Material m : getEffectiveMaterials())
		{
			if (m == block.blockMaterial)
				return true;
		}
		return false;
	}

	@Override
	public String[] toolCategories ()
	{
		return new String[] { "harvest" };
	}

	protected abstract Material[] getEffectiveMaterials ();

	protected abstract String getHarvestType ();
}
