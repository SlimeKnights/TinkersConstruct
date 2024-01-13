package slimeknights.tconstruct.library.json.variable.stat;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.variable.NestedFallbackLoader;
import slimeknights.tconstruct.library.json.variable.NestedFallbackLoader.NestedFallback;
import slimeknights.tconstruct.library.json.variable.entity.EntityVariable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/**
 * Gets a variable from the entity in the variable context
 * @param entity    Entity variable getter
 * @param fallback  Fallback if entity is null (happens when the tooltip is called serverside mainly)
 */
public record EntityConditionalStatVariable(EntityVariable entity, float fallback) implements ConditionalStatVariable, NestedFallback<EntityVariable> {
  public static final IGenericLoader<EntityConditionalStatVariable> LOADER = new NestedFallbackLoader<>("entity_type", EntityVariable.LOADER, EntityConditionalStatVariable::new);

  @Override
  public float getValue(IToolStackView tool, @Nullable LivingEntity entity) {
    if (entity != null) {
      return this.entity.getValue(entity);
    }
    return fallback;
  }

  @Override
  public EntityVariable nested() {
    return entity;
  }

  @Override
  public IGenericLoader<? extends EntityConditionalStatVariable> getLoader() {
    return LOADER;
  }
}
