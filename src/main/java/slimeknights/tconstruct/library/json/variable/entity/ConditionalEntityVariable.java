package slimeknights.tconstruct.library.json.variable.entity;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.variable.ConditionalVariableLoader;
import slimeknights.tconstruct.library.json.variable.ConditionalVariableLoader.ConditionalVariable;

/**
 * Gets one of two entity properties based on the condition
 */
public record ConditionalEntityVariable(IJsonPredicate<LivingEntity> condition, EntityVariable ifTrue, EntityVariable ifFalse)
    implements EntityVariable, ConditionalVariable<IJsonPredicate<LivingEntity>,EntityVariable> {
  public static final IGenericLoader<ConditionalEntityVariable> LOADER = new ConditionalVariableLoader<>(LivingEntityPredicate.LOADER, EntityVariable.LOADER, ConditionalEntityVariable::new);

  public ConditionalEntityVariable(IJsonPredicate<LivingEntity> condition, float ifTrue, float ifFalse) {
    this(condition, new EntityVariable.Constant(ifTrue), new EntityVariable.Constant(ifFalse));
  }

  @Override
  public float getValue(LivingEntity entity) {
    return condition.matches(entity) ? ifTrue.getValue(entity) : ifFalse.getValue(entity);
  }

  @Override
  public IGenericLoader<? extends EntityVariable> getLoader() {
    return LOADER;
  }

}
