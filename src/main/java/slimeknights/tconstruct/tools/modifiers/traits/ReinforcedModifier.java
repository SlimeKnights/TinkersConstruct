package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import slimeknights.tconstruct.library.modifiers.Modifier;

import java.util.function.BiConsumer;

public class ReinforcedModifier extends Modifier {
  public ReinforcedModifier() {
    super(0xcacaca);
  }

  @Override
  public void addEnchantments(int level, BiConsumer<Enchantment, Integer> consumer) {
    // FIXME: temporary solution until damage hook is implemented
    consumer.accept(Enchantments.UNBREAKING, level);
  }
}
