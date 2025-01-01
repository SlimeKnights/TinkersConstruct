package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;

public class DoubleJumpModifier extends Modifier {
  private static final ResourceLocation JUMPS = TConstruct.getResource("jumps");
  private static final TinkerDataKey<Integer> EXTRA_JUMPS = TConstruct.createKey("extra_jumps");

  private Component levelOneName = null;
  private Component levelTwoName = null;

  public DoubleJumpModifier() {
    // TODO: move this out of constructor to generalized logic
    MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, DoubleJumpModifier::onLand);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(new ArmorLevelModule(EXTRA_JUMPS, false, null));
  }

  @Override
  public Component getDisplayName(int level) {
    if (level == 1) {
      if (levelOneName == null) {
        levelOneName = applyStyle(Component.translatable(getTranslationKey() + ".double"));
      }
      return levelOneName;
    }
    if (level == 2) {
      if (levelTwoName == null) {
        levelTwoName = applyStyle(Component.translatable(getTranslationKey() + ".triple"));
      }
      return levelTwoName;
    }
    return super.getDisplayName(level);
  }

  /**
   * Causes the player to jump an extra time, if possible
   * @param entity  Entity instance who wishes to jump again
   * @return  True if the entity jumpped, false if not
   */
  public static boolean extraJump(Player entity) {
    // validate preconditions, no using when swimming, elytra, or on the ground
    if (!entity.onGround() && !entity.onClimbable() && !entity.isInWaterOrBubble()) {
      // determine modifier level
      int maxJumps = entity.getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(EXTRA_JUMPS)).orElse(0);
      if (maxJumps > 0) {
        return entity.getCapability(PersistentDataCapability.CAPABILITY).filter(data -> {
          int jumps = data.getInt(JUMPS);
          if (jumps < maxJumps) {
            entity.jumpFromGround();
            RandomSource random = entity.getCommandSenderWorld().getRandom();
            for (int i = 0; i < 4; i++) {
              entity.getCommandSenderWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, entity.getX() - 0.25f + random.nextFloat() * 0.5f, entity.getY(), entity.getZ() - 0.25f + random.nextFloat() * 0.5f, 0, 0, 0);
            }
            entity.playSound(Sounds.EXTRA_JUMP.getSound(), 0.5f, 0.5f);
            data.putInt(JUMPS, jumps + 1);
            return true;
          }
          return false;
        }).isPresent();
      }
    }
    return false;
  }

  /** Event handler to reset the number of times we have jumpped in mid air */
  private static void onLand(LivingFallEvent event) {
    event.getEntity().getCapability(PersistentDataCapability.CAPABILITY).ifPresent(data -> data.remove(JUMPS));
  }
}
