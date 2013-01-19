package tinker.tconstruct.tools;

import tinker.tconstruct.EnumMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

/* Base class for harvest tools with each head having a different purpose */

public abstract class DualHarvestTool extends HarvestTool
{
	public DualHarvestTool(int itemID, int baseDamage, String tex)
	{
		super(itemID, baseDamage, tex);
	}
	
	@Override
	public int getHeadType ()
	{
		return 3;
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player)
	{
		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		World world = player.worldObj;
		int bID = player.worldObj.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		Block block = Block.blocksList[bID];
		int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
		int shlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getSecondHarvestType());
		
		if (hlvl <= tags.getInteger("HarvestLevel") && shlvl <= tags.getInteger("HarvestLevel2"))
			return false;
		else
		{
			if (!player.capabilities.isCreativeMode)
				onBlockDestroyed(stack, world, bID, x, y, z, player);
			world.setBlockWithNotify(x, y, z, 0);
			if (!world.isRemote)
				world.playAuxSFX(2001, x, y, z, bID + (meta << 12));
			return true;
		}
	}

	@Override
	public float getStrVsBlock(ItemStack stack, Block block, int meta)
	{

		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		if (tags.getBoolean("Broken"))
			return 0.1f;
		
		Material[] materials = getEffectiveMaterials();
		for (int i = 0; i < materials.length; i++)
		{
			if (materials[i] == block.blockMaterial )
			{				
				float speed = tags.getInteger("MiningSpeed");
				speed /= 100f;
				int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
				int durability = tags.getInteger("Damage");
				
				float shoddy = tags.getFloat("Shoddy");
				speed += shoddy*durability/100f;
				
				if (hlvl <= tags.getInteger("HarvestLevel"))
					return speed;
				return 0.1f;
			}
		}
		materials = getEffectiveSecondaryMaterials();
		for (int i = 0; i < materials.length; i++)
		{
			if (materials[i] == block.blockMaterial )
			{				
				float speed = tags.getInteger("MiningSpeed2");
				speed /= 100f;
				int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
				int durability = tags.getInteger("Damage");
				
				float shoddy = tags.getFloat("Shoddy");
				speed += shoddy*durability/100f;
				
				if (hlvl <= tags.getInteger("HarvestLevel2"))
					return speed;
				return 0.1f;
			}
		}
		return super.getStrVsBlock(stack, block, meta);
	}
	
	public boolean canHarvestBlock(Block block)
    {
		return true;
    }
	
	protected abstract Material[] getEffectiveSecondaryMaterials();
	protected abstract String getSecondHarvestType();
}
