package slimeknights.tconstruct.library.json.variable.power;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.variable.entity.EntityVariable;
import slimeknights.tconstruct.library.json.variable.melee.EntityMeleeVariable.WhichEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import javax.annotation.Nullable;

/**
 * Reads an entity variable from power content
 * @param entity   Entity variable
 * @param which    Determines whether to read the attacker or the target
 * @param fallback Fallback if the entity is not found
 */
public record EntityPowerVariable(EntityVariable entity, WhichEntity which, float fallback) implements PowerVariable {
  public static final RecordLoadable<EntityPowerVariable> LOADER = RecordLoadable.create(
    EntityVariable.LOADER.directField("entity_type", EntityPowerVariable::entity),
    WhichEntity.LOADER.requiredField("which", EntityPowerVariable::which),
    FloatLoadable.ANY.requiredField("fallback", EntityPowerVariable::fallback),
    EntityPowerVariable::new);

  @Override
  public float getValue(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, @Nullable Projectile projectile, @Nullable EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    LivingEntity entity = which == WhichEntity.ATTACKER ? attacker : target;
    if (entity != null) {
      return this.entity.getValue(entity);
    }
    return fallback;
  }

  @Override
  public RecordLoadable<EntityPowerVariable> getLoader() {
    return LOADER;
  }
}
