package slimeknights.tconstruct.gadgets.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import slimeknights.mantle.inventory.EmptyItemHandler;

public class DropperRailBlock extends RailBlock {

  public DropperRailBlock(Properties properties) {
    super(properties);
  }

  @Override
  public void onMinecartPass(BlockState state, Level world, BlockPos pos, AbstractMinecart cart) {
    if (cart.getCapability(Capabilities.ItemHandler.ENTITY_AUTOMATION, Direction.DOWN) == null || !(cart instanceof Hopper)) {
      return;
    }
    BlockEntity tileEntity = world.getBlockEntity(pos.below());
    if (tileEntity == null || world.getCapability(Capabilities.ItemHandler.BLOCK, pos.below(), Direction.DOWN) == null) {
      return;
    }

    // todo: fix this optional usage
    IItemHandler itemHandlerCart = cart.getCapability(Capabilities.ItemHandler.ENTITY_AUTOMATION, Direction.UP);
    if (itemHandlerCart == null) {
      itemHandlerCart = EmptyItemHandler.INSTANCE;
    }
    IItemHandler itemHandlerTE = world.getCapability(Capabilities.ItemHandler.BLOCK, pos.below(), Direction.UP);
    if (itemHandlerTE == null) {
      itemHandlerTE = EmptyItemHandler.INSTANCE;
    }

    for (int i = 0; i < itemHandlerCart.getSlots(); i++) {
      ItemStack itemStack = itemHandlerCart.extractItem(i, 1, true);
      if (itemStack.isEmpty()) {
        continue;
      }
      if (ItemHandlerHelper.insertItem(itemHandlerTE, itemStack, true).isEmpty()) {
        itemStack = itemHandlerCart.extractItem(i, 1, false);
        ItemHandlerHelper.insertItem(itemHandlerTE, itemStack, false);
        break;
      }
    }
  }

}
