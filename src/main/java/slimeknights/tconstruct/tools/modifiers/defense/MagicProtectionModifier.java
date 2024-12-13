package slimeknights.tconstruct.tools.modifiers.defense;

import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorStatModule.TooltipStyle;
import slimeknights.tconstruct.library.modifiers.modules.technical.MaxArmorStatModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;

/** @deprecated use {@link slimeknights.tconstruct.library.modifiers.modules.technical.MaxArmorStatModule}, {@link slimeknights.tconstruct.library.tools.capability.TinkerDataKeys#BAD_EFFECT_DURATION}, and {@link ProtectionModule} */
@Deprecated(forRemoval = true)
public class MagicProtectionModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(MaxArmorStatModule.builder(TinkerDataKeys.BAD_EFFECT_DURATION).heldTag(TinkerTags.Items.HELD).tooltipStyle(TooltipStyle.PERCENT).eachLevel(-0.05f));
    hookBuilder.addModule(ProtectionModule.builder().sources(DamageSourcePredicate.CAN_PROTECT, DamageSourcePredicate.MAGIC).eachLevel(2.5f));
  }
}
