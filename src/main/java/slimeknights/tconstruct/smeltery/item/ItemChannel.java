package slimeknights.tconstruct.smeltery.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tconstruct.smeltery.tileentity.TileChannel;

public class ItemChannel extends ItemBlockMeta {

  public ItemChannel(Block block) {
    super(block);
  }

  // this is all because mojang does not pass side hit into onBlockPlacedBy. This is a bit easier than calculating that after the fact and less hacky that storing it between functions
  @Override
  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
    //Ceramics.log.info("test");
    boolean result = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    if(result) {
      TileEntity te = world.getTileEntity(pos);
      // if we have a channel, update it sensitive to our sneaking and the side hit
      if(te instanceof TileChannel) {
        ((TileChannel) te).onPlaceBlock(side, player.isSneaking());
      }
    }

    return result;
  }

}
