package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.modifiers.impl.TankModifier;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipeLookup;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.particle.FluidParticleData;

import javax.annotation.Nullable;

/** Modifier to handle spilling recipes on helmets */
public class SlurpingModifier extends TankModifier implements IArmorInteractModifier {
  private static final float DEGREE_TO_RADIANS = (float)Math.PI / 180F;
  private static final TinkerDataKey<SlurpingInfo> SLURP_FINISH_TIME = TConstruct.createKey("slurping_finish");
  public SlurpingModifier() {
    super(FluidAttributes.BUCKET_VOLUME);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerTickEvent.class, this::playerTick);
  }

  @Override
  public boolean startArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot slot) {
    if (!player.isShiftKeyDown()) {
      FluidStack fluid = getFluid(tool);
      if (!fluid.isEmpty()) {
        // if we have a recipe, start drinking
        SpillingRecipe recipe = SpillingRecipeLookup.findRecipe(player.getCommandSenderWorld().getRecipeManager(), fluid.getFluid());
        if (recipe != null) {
          player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.put(SLURP_FINISH_TIME, new SlurpingInfo(fluid, player.tickCount + 20)));
          return true;
        }
      }
    }
    return false;
  }

  /** Adds the given number of fluid particles */
  private static void addFluidParticles(Player player, FluidStack fluid, int count) {
    for(int i = 0; i < count; ++i) {
      Vec3 motion = new Vec3((RANDOM.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
      motion = motion.xRot(-player.getXRot() * DEGREE_TO_RADIANS);
      motion = motion.yRot(-player.getYRot() * DEGREE_TO_RADIANS);
      Vec3 position = new Vec3((RANDOM.nextFloat() - 0.5D) * 0.3D, (-RANDOM.nextFloat()) * 0.6D - 0.3D, 0.6D);
      position = position.xRot(-player.getXRot() * DEGREE_TO_RADIANS);
      position = position.yRot(-player.getYRot() * DEGREE_TO_RADIANS);
      position = position.add(player.getX(), player.getEyeY(), player.getZ());
      FluidParticleData data = new FluidParticleData(TinkerCommons.fluidParticle.get(), fluid);
      if (player.level instanceof ServerLevel) {
        ((ServerLevel)player.level).sendParticles(data, position.x, position.y, position.z, 1, motion.x, motion.y + 0.05D, motion.z, 0.0D);
      } else {
        player.level.addParticle(data, position.x, position.y, position.z, motion.x, motion.y + 0.05D, motion.z);
      }
    }
  }

  /** Called on player tick to update drinking */
  private void playerTick(PlayerTickEvent event) {
    Player player = event.player;
    if (player.isSpectator()) {
      return;
    }
    player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      // if drinking
      SlurpingInfo info = data.get(SLURP_FINISH_TIME);
      if (info != null) {
        // how long we have left?
        int timeLeft = info.finishTime - player.tickCount;
        if (timeLeft < 0) {
          // particles a bit stronger
          player.playSound(SoundEvents.GENERIC_DRINK, 0.5F, RANDOM.nextFloat() * 0.1f + 0.9f);
          addFluidParticles(player, info.fluid, 16);

          // only server needs to drink
          if (!player.getCommandSenderWorld().isClientSide) {
            ToolStack tool = ToolStack.from(player.getItemBySlot(EquipmentSlot.HEAD));
            FluidStack fluid = getFluid(tool);
            if (!fluid.isEmpty()) {
              // find the recipe
              SpillingRecipe recipe = SpillingRecipeLookup.findRecipe(player.getCommandSenderWorld().getRecipeManager(), fluid.getFluid());
              if (recipe != null) {
                ToolAttackContext context = new ToolAttackContext(player, player, InteractionHand.MAIN_HAND, player, player, false, 1.0f, false);
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
          player.playSound(SoundEvents.GENERIC_DRINK, 0.5F, RANDOM.nextFloat() * 0.1f + 0.9f);
          addFluidParticles(player, info.fluid, 5);
        }
      }
    });
  }

  @Override
  public void stopArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot slot) {
    player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.remove(SLURP_FINISH_TIME));
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    if (type == IArmorInteractModifier.class) {
      return (T) this;
    }
    return super.getModule(type);
  }

  private record SlurpingInfo(FluidStack fluid, int finishTime) {}
}
