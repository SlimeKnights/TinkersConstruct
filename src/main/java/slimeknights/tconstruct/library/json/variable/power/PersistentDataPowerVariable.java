package slimeknights.tconstruct.library.json.variable.power;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

/** Variable that fetches a number from projectile persistent data data */
public record PersistentDataPowerVariable(ResourceLocation key, float fallback) implements PowerVariable {
  public static final RecordLoadable<PersistentDataPowerVariable> LOADER = RecordLoadable.create(
    Loadables.RESOURCE_LOCATION.requiredField("key", PersistentDataPowerVariable::key),
    FloatLoadable.ANY.requiredField("fallback", PersistentDataPowerVariable::fallback),
    PersistentDataPowerVariable::new);

  @Override
  public float getValue(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, @Nullable Projectile projectile, @Nullable EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (projectile != null) {
      return persistentData.getFloat(key);
    }
    return fallback;
  }

  @Override
  public RecordLoadable<PersistentDataPowerVariable> getLoader() {
    return LOADER;
  }

}
