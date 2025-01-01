package slimeknights.tconstruct.library.json.variable.entity;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.ToFloatFunction;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry;

import static slimeknights.mantle.data.loadable.record.SingletonLoader.singleton;

/** Variable that fetches a property from an entity */
public interface EntityVariable extends IHaveLoader {
  GenericLoaderRegistry<EntityVariable> LOADER = new VariableLoaderRegistry<>("Entity Variable", Constant::new);

  /** Gets a value from the given entity */
  float getValue(LivingEntity entity);

  @Override
  RecordLoadable<? extends EntityVariable> getLoader();

  /* Singletons */

  /** Creates a new singleton variable getter */
  static EntityVariable simple(ToFloatFunction<LivingEntity> getter) {
    return singleton(loader -> new EntityVariable() {
      @Override
      public float getValue(LivingEntity entity) {
        return getter.apply(entity);
      }

      @Override
      public RecordLoadable<? extends EntityVariable> getLoader() {
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
    public static final RecordLoadable<Constant> LOADER = VariableLoaderRegistry.constantLoader(Constant::new);

    @Override
    public float getValue(LivingEntity entity) {
      return value;
    }

    @Override
    public RecordLoadable<Constant> getLoader() {
      return LOADER;
    }
  }
}
