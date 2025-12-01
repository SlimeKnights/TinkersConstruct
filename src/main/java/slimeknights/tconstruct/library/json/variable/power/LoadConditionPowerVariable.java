package slimeknights.tconstruct.library.json.variable.power;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.data.loadable.mapping.ConditionalLoadable.ConditionalObject;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;

/**
 * Datagen helper for making conditional {@link PowerVariable}.
 * @param ifTrue      Variable to use if all conditions are true.
 * @param ifFalse     Variable to use if any condition is false.
 * @param conditions  Conditions to evaluate.
 */
@SuppressWarnings("unused") // API
public record LoadConditionPowerVariable(PowerVariable ifTrue, PowerVariable ifFalse, ICondition... conditions) implements PowerVariable, ConditionalObject<PowerVariable> {
  @Override
  public float getValue(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, @Nullable Projectile projectile, @Nullable EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    return (Util.testConditions(conditions) ? ifTrue : ifFalse).getValue(modifiers, persistentData, modifier, projectile, hit, attacker, target);
  }

  @Override
  public RecordLoadable<? extends PowerVariable> getLoader() {
    return PowerVariable.LOADER.getConditionalLoader();
  }
}
