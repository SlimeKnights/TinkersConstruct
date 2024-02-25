package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.TankModifier;
import slimeknights.tconstruct.library.modifiers.spilling.SpillingFluid;
import slimeknights.tconstruct.library.modifiers.spilling.SpillingFluidManager;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.particle.FluidParticleData;

import javax.annotation.Nullable;

/** Modifier to handle spilling recipes onto self when attacked */
public abstract class UseFluidOnHitModifier extends TankModifier {
  public UseFluidOnHitModifier() {
    super(FluidAttributes.BUCKET_VOLUME);
  }

  /** Spawns particles at the given entity */
  public static void spawnParticles(Entity target, FluidStack fluid) {
    if (target.level instanceof ServerLevel) {
      ((ServerLevel)target.level).sendParticles(new FluidParticleData(TinkerCommons.fluidParticle.get(), fluid), target.getX(), target.getY(0.5), target.getZ(), 10, 0.1, 0.2, 0.1, 0.2);
    }
  }

  /** Overridable method to create the attack context and spawn particles */
  public abstract ToolAttackContext createContext(LivingEntity self, @Nullable Player player, @Nullable Entity attacker, FluidStack fluid);

  /**
   * Checks if the modifier triggers
   * TODO 1.19: can ditch this method in favor of just moving the logic to where calls {@link #useFluid(IToolStackView, ModifierEntry, EquipmentContext, EquipmentSlot, DamageSource, boolean)}, keeping just in case an addon uses.
   */
  protected abstract boolean doesTrigger(DamageSource source, boolean isDirectDamage);

  /** Logic for using the fluid */
  protected void useFluid(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, boolean isDirectDamage) {
    if (doesTrigger(source, isDirectDamage)) {
      // 25% chance of working per level, 50% per level on shields
      int level = modifier.getLevel();
      if (RANDOM.nextInt(slotType.getType() == Type.HAND ? 2 : 4) < level) {
        FluidStack fluid = getFluid(tool);
        if (!fluid.isEmpty()) {
          LivingEntity self = context.getEntity();
          Player player = self instanceof Player p ? p : null;
          SpillingFluid recipe = SpillingFluidManager.INSTANCE.find(fluid.getFluid());
          if (recipe.hasEffects()) {
            FluidStack remaining = recipe.applyEffects(fluid, level, createContext(self, player, source.getEntity(), fluid));
            if (player == null || !player.isCreative()) {
              setFluid(tool, remaining);
            }
          }
        }
      }
    }
  }
}
