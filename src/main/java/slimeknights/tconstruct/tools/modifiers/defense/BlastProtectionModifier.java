package slimeknights.tconstruct.tools.modifiers.defense;

import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorStatModule.TooltipStyle;
import slimeknights.tconstruct.library.modifiers.modules.technical.MaxArmorStatModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;

/** @deprecated use {@link MaxArmorStatModule}, {@link TinkerDataKeys#EXPLOSION_KNOCKBACK}, and {@link ProtectionModule} */
@Deprecated(forRemoval = true)
public class BlastProtectionModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(MaxArmorStatModule.builder(TinkerDataKeys.EXPLOSION_KNOCKBACK).heldTag(TinkerTags.Items.HELD).tooltipStyle(TooltipStyle.PERCENT).eachLevel(-0.15f));
    hookBuilder.addModule(ProtectionModule.builder().sources(DamageSourcePredicate.CAN_PROTECT, DamageSourcePredicate.EXPLOSION).eachLevel(2.5f));
  }
}
