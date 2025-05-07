package slimeknights.tconstruct.tools.logic;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.shared.TinkerAttributes;

/** Logic to run the double jump attribute */
@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Bus.FORGE)
public class DoubleJumpHandler {
  private static final ResourceLocation JUMPS = TConstruct.getResource("jumps");

  private DoubleJumpHandler() {}

  /** Event handler to reset the number of times we have jumped in mid-air */
  @SubscribeEvent
  static void onJump(LivingJumpEvent event) {
    LivingEntity living = event.getEntity();
    if (living.onGround() || (living.verticalCollision && !living.verticalCollisionBelow && living.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()) < 0)) {
      living.getCapability(PersistentDataCapability.CAPABILITY).ifPresent(data -> data.remove(JUMPS));
    }
  }

  /** Event handler to reset the number of times we have jumped in mid air */
  @SubscribeEvent
  static void onLand(LivingFallEvent event) {
    event.getEntity().getCapability(PersistentDataCapability.CAPABILITY).ifPresent(data -> data.remove(JUMPS));
  }

  /**
   * Causes the player to jump an extra time, if possible
   * @param entity  Entity instance who wishes to jump again
   * @return  True if the entity jumpped, false if not
   */
  public static boolean extraJump(Player entity) {
    // validate preconditions, no using when swimming, elytra, or on the ground
    if (!entity.onGround() && !entity.onClimbable() && !entity.isInWaterOrBubble()) {
      // determine max jumps
      int extraJumps = Mth.floor(entity.getAttributeValue(TinkerAttributes.JUMP_COUNT.get())) - 1;
      if (extraJumps > 0) {
        // check that we can take more jumps
        ModDataNBT data = PersistentDataCapability.getOrWarn(entity);
        int jumps = data.getInt(JUMPS);
        if (jumps < extraJumps) {
          // actually jump, this method is nice enough to work in air
          entity.jumpFromGround();
          RandomSource random = entity.getCommandSenderWorld().getRandom();
          for (int i = 0; i < 4; i++) {
            entity.getCommandSenderWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, entity.getX() - 0.25f + random.nextFloat() * 0.5f, entity.getY(), entity.getZ() - 0.25f + random.nextFloat() * 0.5f, 0, 0, 0);
          }
          entity.playSound(Sounds.EXTRA_JUMP.getSound(), 0.5f, 0.5f);
          data.putInt(JUMPS, jumps + 1);
          return true;
        }
      }
    }
    return false;
  }
}
