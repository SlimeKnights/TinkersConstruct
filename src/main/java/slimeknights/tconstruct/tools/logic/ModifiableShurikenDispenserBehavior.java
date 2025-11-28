package slimeknights.tconstruct.tools.logic;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.entity.ThrownShuriken;

/** Dispenser behavior for a modifiable shuriken item */
public class ModifiableShurikenDispenserBehavior extends DefaultDispenseItemBehavior {
  public static final ModifiableShurikenDispenserBehavior INSTANCE = new ModifiableShurikenDispenserBehavior();

  private ModifiableShurikenDispenserBehavior() {}

  @Override
  public ItemStack execute(BlockSource source, ItemStack stack) {
    Level level = source.getLevel();
    Position position = DispenserBlock.getDispensePosition(source);
    Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
    ThrownShuriken shuriken = new ThrownShuriken(level, position.x(), position.y(), position.z());
    IToolStackView tool = shuriken.onCreate(stack, null);
    shuriken.shoot(direction.getStepX(), direction.getStepY() + 0.1F, direction.getStepZ(), tool.getStats().get(ToolStats.VELOCITY), 6);
    level.addFreshEntity(shuriken);
    stack.shrink(1);
    return stack;
  }
}
