package slimeknights.tconstruct.tools.modifiers.upgrades.harvest;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import slimeknights.tconstruct.library.modifiers.SingleLevelModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorLootModifier;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class FortuneModifier extends SingleLevelModifier implements IArmorLootModifier {
  public FortuneModifier() {
    super(0xA982BC);
  }

  @Override
  public void applyHarvestEnchantments(IModifierToolStack tool, int level, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {
    consumer.accept(Enchantments.FORTUNE, level);
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorLootModifier.class, this);
  }
}
