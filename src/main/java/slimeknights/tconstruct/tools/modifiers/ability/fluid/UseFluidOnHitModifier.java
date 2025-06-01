package slimeknights.tconstruct.tools.modifiers.ability.fluid;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.particle.FluidParticleData;
import slimeknights.tconstruct.tools.modules.armor.CounterModule;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

/** Modifier to handle spilling recipes onto self when attacked */
public abstract class UseFluidOnHitModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ToolTankHelper.TANK_HANDLER);
    hookBuilder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME));
  }

  /** Spawns particles at the given entity */
  public static void spawnParticles(Entity target, FluidStack fluid) {
    if (target.level() instanceof ServerLevel serverLevel) {
      serverLevel.sendParticles(new FluidParticleData(TinkerCommons.fluidParticle.get(), fluid), target.getX(), target.getY(0.5), target.getZ(), 10, 0.1, 0.2, 0.1, 0.2);
    }
  }

  /** Overridable method to create the attack context and spawn particles */
  public abstract FluidEffectContext.Entity createContext(LivingEntity self, @Nullable Player player, @Nullable Entity attacker);

  /** Logic for using the fluid */
  protected void useFluid(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source) {
    LivingEntity self = context.getEntity();
    float level = CounterModule.getLevel(tool, modifier, slotType, self);
    // 25% chance per level, though a blocking shield doubles it
    if (RANDOM.nextFloat() < 0.25f * level) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (!fluid.isEmpty()) {
        Player player = self instanceof Player p ? p : null;
        FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
        if (recipe.hasEffects()) {
          FluidEffectContext.Entity fluidContext = createContext(self, player, source.getEntity());
          // always applies at level 1, for consistency with other counterattack modules. It's the chance that changes
          int consumed = recipe.applyToEntity(fluid, 1, fluidContext, FluidAction.EXECUTE);
          if (consumed > 0) {
            spawnParticles(fluidContext.getTarget(), fluid);
            if (player == null || !player.isCreative()) {
              fluid.shrink(consumed);
              TANK_HELPER.setFluid(tool, fluid);
            }
          }
        }
      }
    }
  }
}
