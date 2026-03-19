package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.shared.TinkerAttributes;
import slimeknights.tconstruct.tools.modules.ReduceEffectOnUnequipModule;

/** @deprecated use {@link TinkerAttributes#GOOD_EFFECT_DURATION} and {@link ReduceEffectOnUnequipModule} */
@Deprecated(forRemoval = true)
public class BoonOfSssssModifier extends NoLevelsModifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(AttributeModule.builder(TinkerAttributes.GOOD_EFFECT_DURATION, Operation.MULTIPLY_BASE).eachLevel(0.25f));
    hookBuilder.addModule(new ReduceEffectOnUnequipModule(MobEffectCategory.BENEFICIAL, LevelingValue.eachLevel(0.2f), ModifierCondition.ANY_TOOL));
  }
}
