package slimeknights.tconstruct.library.json.variable.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.data.loadable.mapping.ConditionalLoadable.ConditionalObject;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.variable.tool.ToolVariable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

/**
 * Datagen helper for making conditional {@link EntityVariable}.
 * @param ifTrue      Variable to use if all conditions are true.
 * @param ifFalse     Variable to use if any condition is false.
 * @param conditions  Conditions to evaluate.
 */
@SuppressWarnings("unused") // API
public record LoadConditionEntityVariable(EntityVariable ifTrue, EntityVariable ifFalse, ICondition... conditions) implements EntityVariable, ConditionalObject<EntityVariable> {
  @Override
  public float getValue(LivingEntity entity) {
    return (Util.testConditions(conditions) ? ifTrue : ifFalse).getValue(entity);
  }

  @Override
  public RecordLoadable<? extends EntityVariable> getLoader() {
    return EntityVariable.LOADER.getConditionalLoader();
  }
}
