package tconstruct.landmine.item;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.common.TContent;
import tconstruct.landmine.tileentity.TileEntityLandmine;

/**
 * 
 * @author fuj1n
 *
 */
public class ItemBlockLandmine extends ItemBlock{

	public ItemBlockLandmine(int par1) {
		super(par1);
		this.setHasSubtypes(true);
	}

	@Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		String interaction = null;
		
		switch(par1ItemStack.getItemDamage()){
		case 0:
			interaction = "everything";
			break;
		case 1:
			interaction = "mobs, players and redstone";
			break;
		case 2:
			interaction = "players and redstone";
			break;
		default:
			interaction = "redstone only";
			break;
		}
		
		par3List.add("Interacts with: " + interaction);
	}
	
	@Override
	public int getMetadata(int par1){
		return 0;
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		if (!world.setBlock(x, y, z, TContent.landmine.blockID, metadata, 3)) {
			return false;
		}

		if (world.getBlockId(x, y, z) == TContent.landmine.blockID) {
			TContent.landmine.onBlockPlacedBy(world, x, y, z, player, stack);
			
			TileEntityLandmine te = (TileEntityLandmine) world.getBlockTileEntity(x, y, z);
			if(te == null){
				te = (TileEntityLandmine) TContent.landmine.createTileEntity(world, metadata);
			}
			
			te.triggerType = stack.getItemDamage();
			world.setBlockTileEntity(x, y, z, te);
			
			TContent.landmine.onPostBlockPlaced(world, x, y, z, metadata);
		}

		return true;
	}
	
	public static Random getRandom(){
		return itemRand;
	}

}
