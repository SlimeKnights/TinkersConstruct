package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule.TooltipStyle;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.shared.TinkerAttributes;

/** @deprecated use {@link AttributeModule} with {@link TinkerAttributes#BOUNCY} */
@Deprecated(forRemoval = true)
public class BouncyModifier extends NoLevelsModifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(AttributeModule.builder(TinkerAttributes.BOUNCY, Operation.ADDITION).uniqueFrom(getId()).tooltipStyle(TooltipStyle.NONE).flat(1));
  }
}
