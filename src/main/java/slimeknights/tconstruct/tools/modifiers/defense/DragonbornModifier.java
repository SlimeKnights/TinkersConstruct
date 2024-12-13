package slimeknights.tconstruct.tools.modifiers.defense;

import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorStatModule.TooltipStyle;
import slimeknights.tconstruct.library.modifiers.modules.technical.MaxArmorStatModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;

/** @deprecated use {@link MaxArmorStatModule}, {@link TinkerDataKeys#CRITICAL_DAMAGE}, and {@link ProtectionModule} */
@Deprecated(forRemoval = true)
public class DragonbornModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(MaxArmorStatModule.builder(TinkerDataKeys.CRITICAL_DAMAGE).heldTag(TinkerTags.Items.HELD).tooltipStyle(TooltipStyle.PERCENT).eachLevel(0.05f));
    hookBuilder.addModule(ProtectionModule.builder().entity(TinkerPredicate.AIRBORNE).eachLevel(2.5f));
  }
}
