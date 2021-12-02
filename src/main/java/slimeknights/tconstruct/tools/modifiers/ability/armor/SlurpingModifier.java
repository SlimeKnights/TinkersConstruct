package slimeknights.tconstruct.tools.modifiers.ability.armor;

import lombok.Data;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.TankModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IHelmetInteractModifier;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipeLookup;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.particle.FluidParticleData;

import javax.annotation.Nullable;

/** Modifier to handle spilling recipes on helmets */
public class SlurpingModifier extends TankModifier implements IHelmetInteractModifier {
  private static final float DEGREE_TO_RADIANS = (float)Math.PI / 180F;
  private static final TinkerDataKey<SlurpingInfo> SLURP_FINISH_TIME = TConstruct.createKey("slurping_finish");
  public SlurpingModifier() {
    super(0xF98648, FluidAttributes.BUCKET_VOLUME);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerTickEvent.class, this::playerTick);
  }

  @Override
  public boolean startHelmetInteract(IModifierToolStack tool, int level, PlayerEntity player) {
    if (!player.isSneaking()) {
      FluidStack fluid = getFluid(tool);
      if (!fluid.isEmpty()) {
        // if we have a recipe, start drinking
        SpillingRecipe recipe = SpillingRecipeLookup.findRecipe(player.getEntityWorld().getRecipeManager(), fluid.getFluid());
        if (recipe != null) {
          player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.put(SLURP_FINISH_TIME, new SlurpingInfo(fluid, player.ticksExisted + 32)));
          return true;
        }
      }
    }
    return false;
  }

  /** Adds the given number of fluid particles */
  private static void addFluidParticles(PlayerEntity player, FluidStack fluid, int count) {
    for(int i = 0; i < count; ++i) {
      Vector3d motion = new Vector3d((RANDOM.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
      motion = motion.rotatePitch(-player.rotationPitch * DEGREE_TO_RADIANS);
      motion = motion.rotateYaw(-player.rotationYaw * DEGREE_TO_RADIANS);
      Vector3d position = new Vector3d((RANDOM.nextFloat() - 0.5D) * 0.3D, (-RANDOM.nextFloat()) * 0.6D - 0.3D, 0.6D);
      position = position.rotatePitch(-player.rotationPitch * DEGREE_TO_RADIANS);
      position = position.rotateYaw(-player.rotationYaw * DEGREE_TO_RADIANS);
      position = position.add(player.getPosX(), player.getPosYEye(), player.getPosZ());
      FluidParticleData data = new FluidParticleData(TinkerCommons.fluidParticle.get(), fluid);
      if (player.world instanceof ServerWorld) {
        ((ServerWorld)player.world).spawnParticle(data, position.x, position.y, position.z, 1, motion.x, motion.y + 0.05D, motion.z, 0.0D);
      } else {
        player.world.addParticle(data, position.x, position.y, position.z, motion.x, motion.y + 0.05D, motion.z);
      }
    }
  }

  /** Called on player tick to update drinking */
  private void playerTick(PlayerTickEvent event) {
    PlayerEntity player = event.player;
    player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      // if drinking
      SlurpingInfo info = data.get(SLURP_FINISH_TIME);
      if (info != null) {
        // how long we have left?
        int timeLeft = info.finishTime - player.ticksExisted;
        if (timeLeft < 0) {
          // particles a bit stronger
          player.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5F, RANDOM.nextFloat() * 0.1f + 0.9f);
          addFluidParticles(player, info.fluid, 16);

          // only server needs to drink
          if (!player.getEntityWorld().isRemote) {
            ToolStack tool = ToolStack.from(player.getItemStackFromSlot(EquipmentSlotType.HEAD));
            FluidStack fluid = getFluid(tool);
            if (!fluid.isEmpty()) {
              // find the recipe
              SpillingRecipe recipe = SpillingRecipeLookup.findRecipe(player.getEntityWorld().getRecipeManager(), fluid.getFluid());
              if (recipe != null) {
                ToolAttackContext context = new ToolAttackContext(player, player, Hand.MAIN_HAND, player, player, false, 1.0f, false);
                FluidStack remaining = recipe.applyEffects(fluid, tool.getModifierLevel(this), context);
                if (!player.isCreative()) {
                  setFluid(tool, remaining);
                }
              }
            }
          }

          // stop drinking
          data.remove(SLURP_FINISH_TIME);
        }
        // sound is only every 4 ticks
        else if (timeLeft % 4 == 0) {
          player.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5F, RANDOM.nextFloat() * 0.1f + 0.9f);
          addFluidParticles(player, info.fluid, 5);
        }
      }
    });
  }

  @Override
  public void stopHelmetInteract(IModifierToolStack tool, int level, PlayerEntity player) {
    player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.remove(SLURP_FINISH_TIME));
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    if (type == IHelmetInteractModifier.class) {
      return (T) this;
    }
    return super.getModule(type);
  }

  @Data
  private static class SlurpingInfo {
    private final FluidStack fluid;
    private final int finishTime;
  }
}
