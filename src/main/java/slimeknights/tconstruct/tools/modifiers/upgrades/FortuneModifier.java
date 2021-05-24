package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.function.BiConsumer;

public class FortuneModifier extends Modifier {
  public FortuneModifier() {
    super(0xA982BC);
  }

  @Override
  public void applyHarvestEnchantments(IModifierToolStack tool, int level, PlayerEntity player, BlockState state, BlockPos pos, Direction sideHit, BiConsumer<Enchantment,Integer> consumer) {
    consumer.accept(Enchantments.FORTUNE, level);
  }
}
