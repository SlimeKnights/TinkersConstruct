package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.world.item.enchantment.Enchantments;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.armor.SoulSpeedTooltipModule;

/** @deprecated use {@link SoulSpeedTooltipModule} and {@link EnchantmentModule} */
@Deprecated(forRemoval = true)
public class SoulSpeedModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(new EnchantmentModule.Constant(Enchantments.SOUL_SPEED, 1));
    hookBuilder.addModule(SoulSpeedTooltipModule.INSTANCE);
  }
}
