package tinker.tconstruct.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tinker.common.InventoryBlock;
import tinker.tconstruct.TConstructGuiHandler;
import tinker.tconstruct.TConstruct;
import tinker.tconstruct.logic.FrypanLogic;

public class EquipBlock extends InventoryBlock
{

	public EquipBlock(int id, Material material)
	{
		super(id, material);
		this.setHardness(2f);
		this.setBlockBounds(0, 0, 0, 1, 0.25f, 1);
		//this.setCreativeTab(ToolConstruct.materialTab);
	}

	/*public String getTextureFile()
	{
	    return "/tinkertextures/ConstructBlocks.png";
	}*/

	public int getBlockTextureFromSideAndMetadata (int side, int meta)
	{
		//return 22 + meta*6 + side;
		return 22;
	}

	@Override
	public int idDropped (int par1, Random par2Random, int par3)
	{
		return 0;
	}

	public TileEntity createNewTileEntity (World world, int metadata)
	{
		return new FrypanLogic();
	}

	/*@Override
	public void breakBlock (World par1World, int x, int y, int z, int par5, int par6)
	{
		EquipLogic logic = (EquipLogic) par1World.getBlockTileEntity(x, y, z);

		if (logic != null)
		{
			ItemStack equip = logic.getEquipmentItem();

			if (equip != null)
			{
				float jumpX = ToolConstruct.tRand.nextFloat() * 0.8F + 0.1F;
				float jumpY = ToolConstruct.tRand.nextFloat() * 0.8F + 0.1F;
				float jumpZ = ToolConstruct.tRand.nextFloat() * 0.8F + 0.1F;

				while (equip.stackSize > 0)
				{
					int itemSize = ToolConstruct.tRand.nextInt(21) + 10;

					if (itemSize > equip.stackSize)
					{
						itemSize = equip.stackSize;
					}

					equip.stackSize -= itemSize;
					EntityItem entityitem = new EntityItem(par1World, (double) ((float) x + jumpX), (double) ((float) y + jumpY), (double) ((float) z + jumpZ), new ItemStack(equip.itemID, itemSize, equip.getItemDamage()));

					if (equip.hasTagCompound())
					{
						entityitem.func_92014_d().setTagCompound((NBTTagCompound) equip.getTagCompound().copy());
					}

					float offset = 0.05F;
					entityitem.motionX = (double) ((float) ToolConstruct.tRand.nextGaussian() * offset);
					entityitem.motionY = (double) ((float) ToolConstruct.tRand.nextGaussian() * offset + 0.2F);
					entityitem.motionZ = (double) ((float) ToolConstruct.tRand.nextGaussian() * offset);
					par1World.spawnEntityInWorld(entityitem);
				}
			}

		}
		super.breakBlock(par1World, x, y, z, par5, par6);
	}*/

	@Override
	public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
	{
		//int md = world.getBlockMetadata(x, y, z);
		return TConstructGuiHandler.frypanID;
	}

	@Override
	public Object getModInstance ()
	{
		return TConstruct.instance;
	}
}
