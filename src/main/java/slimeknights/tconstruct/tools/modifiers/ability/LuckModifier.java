package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class LuckModifier extends Modifier {
  public LuckModifier() {
    super(0x345EC3);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    // displays special names for the 3 levels
    if (level <= 3) {
      return applyStyle(new TranslationTextComponent(getTranslationKey() + "." + level));
    }
    return super.getDisplayName(level);
  }

  @Override
  public void applyHarvestEnchantments(IModifierToolStack tool, int level, PlayerEntity player, BlockState state, BlockPos pos, Direction sideHit, BiConsumer<Enchantment,Integer> consumer) {
    consumer.accept(Enchantments.FORTUNE, level);
  }

  @Override
  public int getLootingValue(IModifierToolStack tool, int level, LivingEntity holder, @Nullable LivingEntity target, @Nullable DamageSource damageSource, int looting) {
    return looting + level;
  }
}
