package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;

import java.util.function.BiConsumer;

public class SilkyModifier extends SingleUseModifier {
  public SilkyModifier() {
    super(0xfbe28b);
  }

  @Override
  public void addEnchantments(IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, BiConsumer<Enchantment,Integer> consumer) {
    consumer.accept(Enchantments.SILK_TOUCH, 1);
  }
}
