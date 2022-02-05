package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.modifiers.impl.TankModifier;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipeLookup;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.particle.FluidParticleData;

/** Modifier to handle spilling recipes */
public class SpillingModifier extends TankModifier {
  public SpillingModifier() {
    super(FluidAttributes.BUCKET_VOLUME);
  }

  /** Spawns particles at the given entity */
  private static void spawnParticles(Entity target, FluidStack fluid) {
    if (target.level instanceof ServerLevel) {
      ((ServerLevel)target.level).sendParticles(new FluidParticleData(TinkerCommons.fluidParticle.get(), fluid), target.getX(), target.getY(0.5), target.getZ(), 10, 0.1, 0.2, 0.1, 0.2);
    }
  }

  @Override
  public void onAttacked(IToolStackView tool, int level, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    Entity attacker = source.getEntity();
    if (isDirectDamage && attacker != null) {
      // 25% chance of working per level
      if (RANDOM.nextInt(4) < level) {
        FluidStack fluid = getFluid(tool);
        if (!fluid.isEmpty()) {
          LivingEntity self = context.getEntity();
          Player player = self instanceof Player p ? p : null;
          SpillingRecipe recipe = SpillingRecipeLookup.findRecipe(self.level.getRecipeManager(), fluid.getFluid());
          if (recipe != null) {
            ToolAttackContext attackContext = new ToolAttackContext(self, player, InteractionHand.MAIN_HAND,
                                                                    attacker, attacker instanceof LivingEntity ? ((LivingEntity) attacker) : null,
                                                                    false, 1.0f, false);
            FluidStack remaining = recipe.applyEffects(fluid, level, attackContext);
            spawnParticles(attacker, fluid);
            if (player == null || !player.isCreative()) {
              setFluid(tool, remaining);
            }
          }
        }
      }
    }
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    if (damageDealt > 0 && context.isFullyCharged()) {
      FluidStack fluid = getFluid(tool);
      if (!fluid.isEmpty()) {
        SpillingRecipe recipe = SpillingRecipeLookup.findRecipe(context.getAttacker().level.getRecipeManager(), fluid.getFluid());
        if (recipe != null) {
          FluidStack remaining = recipe.applyEffects(fluid, level, context);
          spawnParticles(context.getTarget(), fluid);
          Player player = context.getPlayerAttacker();
          if (player == null || !player.isCreative()) {
            setFluid(tool, remaining);
          }
        }
      }
    }
    return 0;
  }
}
