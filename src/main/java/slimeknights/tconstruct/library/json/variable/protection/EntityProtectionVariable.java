package slimeknights.tconstruct.library.json.variable.protection;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.json.variable.entity.EntityVariable;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Variable fetching data from an entity in the protection context */
public record EntityProtectionVariable(EntityVariable entity, WhichEntity which, float fallback) implements ProtectionVariable {
  public static final RecordLoadable<EntityProtectionVariable> LOADER = RecordLoadable.create(
    EntityVariable.LOADER.directField("entity_type", EntityProtectionVariable::entity),
    new EnumLoadable<>(WhichEntity.class).requiredField("which", EntityProtectionVariable::which),
    FloatLoadable.ANY.requiredField("fallback", EntityProtectionVariable::fallback),
    EntityProtectionVariable::new);

  @Override
  public float getValue(IToolStackView tool, @Nullable EquipmentContext context, @Nullable LivingEntity target, @Nullable DamageSource source, @Nullable EquipmentSlot slotType) {
    LivingEntity entity = null;
    if (which == WhichEntity.TARGET)
      entity = target;
    else if (source != null && source.getEntity() instanceof LivingEntity living) {
      entity = living;
    }
    if (entity != null) {
      return this.entity.getValue(entity);
    }
    return fallback;
  }

  @Override
  public RecordLoadable<? extends IHaveLoader> getLoader() {
    return LOADER;
  }

  // TODO 1.21: add an entity predicate and add direct variable
  public enum WhichEntity { TARGET, ATTACKER }
}
