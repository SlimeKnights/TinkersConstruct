package slimeknights.tconstruct.tools.modifiers.defense;

import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorStatModule.TooltipStyle;
import slimeknights.tconstruct.library.modifiers.modules.technical.MaxArmorStatModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;

/** @deprecated use {@link MaxArmorStatModule}, {@link TinkerDataKeys#CROUCH_DAMAGE}, and {@link ProtectionModule} */
@Deprecated
public class ShulkingModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(MaxArmorStatModule.builder(TinkerDataKeys.CROUCH_DAMAGE).heldTag(TinkerTags.Items.HELD).tooltipStyle(TooltipStyle.PERCENT).eachLevel(-0.1f));
    hookBuilder.addModule(ProtectionModule.builder().sources(DamageSourcePredicate.CAN_PROTECT).entity(LivingEntityPredicate.CROUCHING).eachLevel(2.5f));
  }
}
