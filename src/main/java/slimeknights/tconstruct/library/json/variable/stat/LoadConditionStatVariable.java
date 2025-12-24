package slimeknights.tconstruct.library.json.variable.stat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.data.loadable.mapping.ConditionalLoadable.ConditionalObject;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;

/**
 * Datagen helper for making conditional {@link ConditionalStatVariable}.
 * @param ifTrue      Variable to use if all conditions are true.
 * @param ifFalse     Variable to use if any condition is false.
 * @param conditions  Conditions to evaluate.
 */
@SuppressWarnings("unused") // API
public record LoadConditionStatVariable(ConditionalStatVariable ifTrue, ConditionalStatVariable ifFalse, ICondition... conditions) implements ConditionalStatVariable, ConditionalObject<ConditionalStatVariable> {
  @Override
  public float getValue(IToolStackView tool, @Nullable LivingEntity entity) {
    return (Util.testConditions(conditions) ? ifTrue : ifFalse).getValue(tool, entity);
  }

  @Override
  public RecordLoadable<? extends ConditionalStatVariable> getLoader() {
    return ConditionalStatVariable.LOADER.getConditionalLoader();
  }
}
