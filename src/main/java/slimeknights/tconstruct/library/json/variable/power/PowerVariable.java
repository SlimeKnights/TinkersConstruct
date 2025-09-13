package slimeknights.tconstruct.library.json.variable.power;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.VariableLoaderRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

/** Variable for use in {@link slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalPowerModule} */
public interface PowerVariable extends IHaveLoader {
  GenericLoaderRegistry<PowerVariable> LOADER = new VariableLoaderRegistry<>("Power Variable", Constant::new);

  @Override
  RecordLoadable<? extends PowerVariable> getLoader();

  /**
   * Gets the value of this variable
   * @param modifiers       Modifiers from the tool firing this arrow
   * @param persistentData  Persistent data on the entity
   * @param modifier        Modifier triggering this hook
   * @param projectile      Projectile that hit the entity
   * @param hit             Hit result
   * @param attacker        Living entity who fired the projectile, null if non-living or not fired
   * @param target          Living target, will be null if not living
   * @return  Variable value
   */
  float getValue(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, @Nullable Projectile projectile, @Nullable EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target);


  /** Constant value instance for this object */
  record Constant(float value) implements VariableLoaderRegistry.ConstantFloat, PowerVariable {
    public static final RecordLoadable<Constant> LOADER = VariableLoaderRegistry.constantLoader(Constant::new);

    @Override
    public float getValue(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, @Nullable Projectile projectile, @Nullable EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
      return value;
    }

    @Override
    public RecordLoadable<Constant> getLoader() {
      return LOADER;
    }
  }
}
