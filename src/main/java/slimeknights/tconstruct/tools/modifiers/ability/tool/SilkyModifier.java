package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.modifiers.hooks.ILeggingLootModifier;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

public class SilkyModifier extends SingleUseModifier implements ILeggingLootModifier {
  public SilkyModifier() {
    super(0xF7CDBB);
  }

  @Override
  public void applyHarvestEnchantments(IModifierToolStack tool, int level, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {
    consumer.accept(Enchantments.SILK_TOUCH, 1);
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, ILeggingLootModifier.class, this);
  }
}
