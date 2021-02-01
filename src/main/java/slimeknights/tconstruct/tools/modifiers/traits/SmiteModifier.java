package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import slimeknights.tconstruct.library.modifiers.Modifier;

import java.util.function.BiConsumer;

public class SmiteModifier extends Modifier {
  public SmiteModifier() {
    super(0xd1ecf6);
  }

  @Override
  public void addEnchantments(int level, BiConsumer<Enchantment, Integer> consumer) {
    consumer.accept(Enchantments.SMITE, level);
  }
}
