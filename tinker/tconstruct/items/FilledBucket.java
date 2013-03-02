package tinker.tconstruct.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import tinker.tconstruct.TContent;
import tinker.tconstruct.logic.LiquidTextureLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FilledBucket extends ItemBucket
{

	public FilledBucket(int id)
	{
		super(id, 0);
		setTextureFile(TContent.craftingTexture);
		setIconIndex(224);
		setItemName("tconstruct.bucket");
	}

	public ItemStack onItemRightClick (ItemStack stack, World world, EntityPlayer player)
	{
		float var4 = 1.0F;
		double trueX = player.prevPosX + (player.posX - player.prevPosX) * (double) var4;
		double trueY = player.prevPosY + (player.posY - player.prevPosY) * (double) var4 + 1.62D - (double) player.yOffset;
		double trueZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) var4;
		boolean wannabeFull = false;
		MovingObjectPosition position = this.getMovingObjectPositionFromPlayer(world, player, wannabeFull);

		if (position == null)
		{
			return stack;
		}
		else
		{
			/*FillBucketEvent event = new FillBucketEvent(player, stack, world, position);
			if (MinecraftForge.EVENT_BUS.post(event))
			{
				return stack;
			}

			if (event.getResult() == Event.Result.ALLOW)
			{
				if (player.capabilities.isCreativeMode)
				{
					return stack;
				}

				if (--stack.stackSize <= 0)
				{
					return event.result;
				}

				if (!player.inventory.addItemStackToInventory(event.result))
				{
					player.dropPlayerItem(event.result);
				}

				return stack;
			}*/

			if (position.typeOfHit == EnumMovingObjectType.TILE)
			{
				int clickX = position.blockX;
				int clickY = position.blockY;
				int clickZ = position.blockZ;

				if (!world.canMineBlock(player, clickX, clickY, clickZ))
				{
					return stack;
				}

				if (position.sideHit == 0)
				{
					--clickY;
				}

				if (position.sideHit == 1)
				{
					++clickY;
				}

				if (position.sideHit == 2)
				{
					--clickZ;
				}

				if (position.sideHit == 3)
				{
					++clickZ;
				}

				if (position.sideHit == 4)
				{
					--clickX;
				}

				if (position.sideHit == 5)
				{
					++clickX;
				}

				if (!player.canPlayerEdit(clickX, clickY, clickZ, position.sideHit, stack))
				{
					return stack;
				}

				if (this.tryPlaceContainedLiquid(world, clickX, clickY, clickZ, stack.getItemDamage()) && !player.capabilities.isCreativeMode)
				{
					return new ItemStack(Item.bucketEmpty);
				}
			}

			return stack;
		}
	}

	public boolean tryPlaceContainedLiquid(World world, int clickX, int clickY, int clickZ, int meta)
    {
        if (!world.isAirBlock(clickX, clickY, clickZ) && world.getBlockMaterial(clickX, clickY, clickZ).isSolid())
        {
            return false;
        }
        else
        {
        	world.setBlockWithNotify(clickX, clickY, clickZ, TContent.liquidMetalStill.blockID);
        	LiquidTextureLogic logic = (LiquidTextureLogic) world.getBlockTileEntity(clickX, clickY, clickZ);
        	logic.setTexturePos(meta);

            return true;
        }
    }
	
	public int getSourceBlock(int meta)
	{
		switch(meta)
		{
		/*case 0: return TContent.ironStill.blockID;
		case 1: return TContent.goldStill.blockID;
		case 2: return TContent.copperStill.blockID;
		case 3: return TContent.tinStill.blockID;
		case 4: return TContent.aluminumStill.blockID;
		case 5: return TContent.cobaltStill.blockID;
		case 6: return TContent.arditeStill.blockID;
		case 7: return TContent.bronzeStill.blockID;
		case 8: return TContent.alBrassStill.blockID;
		case 9: return TContent.manyullynStill.blockID;
		case 10: return TContent.alumiteStill.blockID;
		case 11: return TContent.obsidianStill.blockID;
		case 12: return TContent.steelStill.blockID;*/
		//case 13: return TContent.ironStill.blockID;
		default: return 0;
		}
	}
	
	@Override
	public void getSubItems(int id, CreativeTabs tab, List list)
    {
		for (int i = 0; i < 17; i++)
			list.add(new ItemStack(id, 1, i));
    }
	
	@SideOnly(Side.CLIENT)
	public int getIconFromDamage(int meta)
	{
		return this.iconIndex + meta;
	}
	
	public String getItemNameIS(ItemStack stack)
	{
		int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, materialNames.length);
		return getItemName() + "." +materialNames[arr];
	}
	
	public static final String[] materialNames = new String[] { 
		"Iron", "Gold", "Copper", "Tin", "Aluminum", "Cobalt", "Ardite", "Bronze", "AlBrass", "Manyullyn", "Alumite", "Obsidian", "Steel",
		"Manganese", "Heptazion", "DSteel", "Angmallen"};
}
