package slimeknights.tconstruct.gadgets.entity;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FancyArmorStandEntity.StandType;

import java.util.function.Consumer;

/** Dispenser behavior for fancy armor stand. Based on vanilla armor stand dispenser behavior */
@RequiredArgsConstructor
public class DispenseFancyArmorStand extends DefaultDispenseItemBehavior {
  private final StandType type;

  @Override
  protected ItemStack execute(BlockSource source, ItemStack stack) {
    Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
    BlockPos blockpos = source.getPos().relative(direction);
    ServerLevel server = source.getLevel();
    Consumer<FancyArmorStandEntity> consumer = EntityType.appendDefaultStackConfig(stand -> stand.setYRot(direction.toYRot()), server, stack, null);
    FancyArmorStandEntity stand = TinkerGadgets.armorStandEntity.get().spawn(server, stack.getTag(), consumer, blockpos, MobSpawnType.DISPENSER, false, false);
    if (stand != null) {
      stack.shrink(1);
      type.onPlace(stand);
    }
    return stack;
  }
}
