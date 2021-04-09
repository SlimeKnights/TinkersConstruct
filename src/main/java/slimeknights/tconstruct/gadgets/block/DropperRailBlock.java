package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.RailBlock;

public class DropperRailBlock extends RailBlock {

  public DropperRailBlock(Settings properties) {
    super(properties);
  }

//  @Override
//  public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
//    if (!cart.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).isPresent() || !(cart instanceof Hopper)) {
//      return;
//    }
//    BlockEntity tileEntity = world.getBlockEntity(pos.down());
//    if (tileEntity == null || !tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).isPresent()) {
//      return;
//    }
//
//    // todo: fix this optional usage
//    IItemHandler itemHandlerCart = cart.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null);
//    IItemHandler itemHandlerTE = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElse(null);
//
//    assert itemHandlerCart != null;
//    for (int i = 0; i < itemHandlerCart.getSlots(); i++) {
//      ItemStack itemStack = itemHandlerCart.extractItem(i, 1, true);
//      if (itemStack.isEmpty()) {
//        continue;
//      }
//      if (ItemHandlerHelper.insertItem(itemHandlerTE, itemStack, true).isEmpty()) {
//        itemStack = itemHandlerCart.extractItem(i, 1, false);
//        ItemHandlerHelper.insertItem(itemHandlerTE, itemStack, false);
//        break;
//      }
//    }
//  }

}
