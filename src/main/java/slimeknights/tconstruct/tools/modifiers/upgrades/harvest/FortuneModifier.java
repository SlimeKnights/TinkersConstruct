package slimeknights.tconstruct.tools.modifiers.upgrades.harvest;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorLootModifier;
import slimeknights.tconstruct.library.modifiers.impl.SingleLevelModifier;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class FortuneModifier extends SingleLevelModifier implements IArmorLootModifier {
  @Override
  public void applyHarvestEnchantments(IToolStackView tool, int level, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {
    consumer.accept(Enchantments.BLOCK_FORTUNE, level);
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorLootModifier.class, this);
  }
}
