package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.function.BiConsumer;

public class SilkyModifier extends SingleUseModifier {
  public SilkyModifier() {
    super(0xF7CDBB);
  }

  @Override
  public void applyHarvestEnchantments(IModifierToolStack tool, int level, PlayerEntity player, BlockState state, BlockPos pos, Direction sideHit, BiConsumer<Enchantment,Integer> consumer) {
    consumer.accept(Enchantments.SILK_TOUCH, 1);
  }
}
