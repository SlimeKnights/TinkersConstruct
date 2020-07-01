package slimeknights.tconstruct.shared.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.mantle.block.InventoryBlock;
import slimeknights.mantle.inventory.BaseContainer;

// TODO: move to mantle, replacing the method in Inventory block
public abstract class GuiInventoryBlock extends InventoryBlock {
  protected GuiInventoryBlock(Properties builder) {
    super(builder);
  }

  @Override
  protected boolean openGui(PlayerEntity player, World world, BlockPos pos) {
    if (!world.isRemote()) {
      INamedContainerProvider container = this.getContainer(world.getBlockState(pos), world, pos);
      if (container != null && player instanceof ServerPlayerEntity) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        NetworkHooks.openGui(serverPlayer, container, pos);
        if (player.openContainer instanceof BaseContainer<?>) {
          ((BaseContainer<?>) player.openContainer).syncOnOpen(serverPlayer);
        }
      }
    }

    return true;
  }
}
