package slimeknights.tconstruct.tables.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import slimeknights.mantle.inventory.BaseContainer;

public abstract class TinkerTableBlock extends TableBlock implements ITinkerStationBlock {

  public TinkerTableBlock(Properties builder) {
    super(builder);
  }

  @Override
  public boolean openGui(PlayerEntity player, World world, BlockPos pos) {
    if (!world.isRemote) {
      INamedContainerProvider inamedcontainerprovider = this.getContainer(world.getBlockState(pos), world, pos);

      if (inamedcontainerprovider != null) {
        NetworkHooks.openGui((ServerPlayerEntity) player, inamedcontainerprovider, pos);

        if (player.openContainer instanceof BaseContainer) {
          ((BaseContainer) player.openContainer).syncOnOpen((ServerPlayerEntity) player);
        }
      }
    }
    return true;
  }

  @Override
  public boolean isMaster() {
    return false;
  }
}
