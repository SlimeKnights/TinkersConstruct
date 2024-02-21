package slimeknights.tconstruct.library.json.variable.entity;

import net.minecraft.util.ToFloatFunction;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry.ConstantLoader;

import static slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader.singleton;

/** Variable that fetches a property from an entity */
public interface EntityVariable extends IHaveLoader<EntityVariable> {
  GenericLoaderRegistry<EntityVariable> LOADER = new VariableLoaderRegistry<>(Constant.LOADER.constructor());

  /** Gets a value from the given entity */
  float getValue(LivingEntity entity);


  /* Singletons */

  /** Creates a new singleton variable getter */
  private static EntityVariable simple(ToFloatFunction<LivingEntity> getter) {
    return singleton(loader -> new EntityVariable() {
      @Override
      public float getValue(LivingEntity entity) {
        return getter.apply(entity);
      }

      @Override
      public IGenericLoader<? extends EntityVariable> getLoader() {
        return loader;
      }
    });
  }

  /** Gets the current health of the entity. For max health, see {@link AttributeEntityVariable} */
  EntityVariable HEALTH = simple(LivingEntity::getHealth);
  /** Gets the height of the entities feet */
  EntityVariable HEIGHT = simple(entity -> (float)entity.getY());


  /** Constant value instance for this object */
  record Constant(float value) implements VariableLoaderRegistry.ConstantFloat, EntityVariable {
    public static final ConstantLoader<Constant> LOADER = new ConstantLoader<>(Constant::new);

    @Override
    public float getValue(LivingEntity entity) {
      return value;
    }

    @Override
    public IGenericLoader<? extends EntityVariable> getLoader() {
      return LOADER;
    }
  }
}
