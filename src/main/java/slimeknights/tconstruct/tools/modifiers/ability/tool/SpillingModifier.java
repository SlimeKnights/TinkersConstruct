package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.modifiers.TankModifier;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipeLookup;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.particle.FluidParticleData;

/** Modifier to handle spilling recipes */
public class SpillingModifier extends TankModifier {
  public SpillingModifier() {
    super(0xF98648, FluidAttributes.BUCKET_VOLUME);
  }

  /** Spawns particles at the given entity */
  private static void spawnParticles(Entity target, FluidStack fluid) {
    if (target.world instanceof ServerWorld) {
      ((ServerWorld)target.world).spawnParticle(new FluidParticleData(TinkerCommons.fluidParticle.get(), fluid), target.getPosX(), target.getPosYHeight(0.5), target.getPosZ(), 10, 0.1, 0.2, 0.1, 0.2);
    }
  }

  @Override
  public void onAttacked(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float amount, boolean isDirectDamage) {
    Entity attacker = source.getTrueSource();
    if (isDirectDamage && attacker != null) {
      // 25% chance of working per level
      if (RANDOM.nextInt(4) < level) {
        FluidStack fluid = getFluid(tool);
        if (!fluid.isEmpty()) {
          LivingEntity self = context.getEntity();
          PlayerEntity player = self instanceof PlayerEntity ? ((PlayerEntity) self) : null;
          SpillingRecipe recipe = SpillingRecipeLookup.findRecipe(self.getEntityWorld().getRecipeManager(), fluid.getFluid());
          if (recipe != null) {
            ToolAttackContext attackContext = new ToolAttackContext( self, player, Hand.MAIN_HAND,
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
  public int afterEntityHit(IModifierToolStack tool, int level, ToolAttackContext context, float damageDealt) {
    if (damageDealt > 0 && context.isFullyCharged()) {
      FluidStack fluid = getFluid(tool);
      if (!fluid.isEmpty()) {
        SpillingRecipe recipe = SpillingRecipeLookup.findRecipe(context.getAttacker().getEntityWorld().getRecipeManager(), fluid.getFluid());
        if (recipe != null) {
          FluidStack remaining = recipe.applyEffects(fluid, level, context);
          spawnParticles(context.getTarget(), fluid);
          PlayerEntity player = context.getPlayerAttacker();
          if (player == null || !player.isCreative()) {
            setFluid(tool, remaining);
          }
        }
      }
    }
    return 0;
  }
}
