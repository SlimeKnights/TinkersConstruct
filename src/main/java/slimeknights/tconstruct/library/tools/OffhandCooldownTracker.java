package slimeknights.tconstruct.library.tools;

import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;

/**
 * Logic to handle offhand having its own cooldown
 */
@RequiredArgsConstructor
public class OffhandCooldownTracker implements ICapabilityProvider {
  public static final ResourceLocation KEY = Util.getResource("offhand_cooldown");
  private static final NonNullFunction<OffhandCooldownTracker,Float> COOLDOWN_TRACKER = OffhandCooldownTracker::getCooldown;
  private static final NonNullFunction<OffhandCooldownTracker,Boolean> ATTACK_READY = OffhandCooldownTracker::isAttackReady;

  /**
   * Capability instance for dimension compasses
   */
  @CapabilityInject(OffhandCooldownTracker.class)
  public static Capability<OffhandCooldownTracker> CAPABILITY = null;

  /** Registers the capability and subscribes to event listeners */
  public static void register() {
    // register a bunch of dumb unused things because I need to register one actually useful thing
    CapabilityManager.INSTANCE.register(OffhandCooldownTracker.class, new IStorage<OffhandCooldownTracker>() {
      @Nullable
      @Override
      public INBT writeNBT(Capability<OffhandCooldownTracker> capability, OffhandCooldownTracker instance, Direction side) {
        return null;
      }

      @Override
      public void readNBT(Capability<OffhandCooldownTracker> capability, OffhandCooldownTracker instance, Direction side, INBT nbt) {}
    }, () -> new OffhandCooldownTracker(null));

    MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, OffhandCooldownTracker::attachCapability);
  }

  /**
   * Called to add the capability handler to all players
   * @param event  Event
   */
  private static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
    Entity entity = event.getObject();
    if (entity instanceof PlayerEntity) {
      event.addCapability(KEY, new OffhandCooldownTracker((PlayerEntity) entity));
    }
  }

  /** Lazy optional of self for capability requirements */
  private final LazyOptional<OffhandCooldownTracker> capabilityInstance = LazyOptional.of(() -> this);
  /** Player receiving cooldowns */
  @Nullable
  private final PlayerEntity player;
  /** Scale of the last cooldown */
  private int lastCooldown = 0;
  /** Time in ticks when the player can next attack for full power */
  private int attackReady = 0;

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    return cap == CAPABILITY ? this.capabilityInstance.cast() : LazyOptional.empty();
  }

  /** Null safe way to get the player's ticks existed */
  private int getTicksExisted() {
    if (player == null) {
      return 0;
    }
    return player.ticksExisted;
  }

  /**
   * Applies the given amount of cooldown
   * @param cooldown  Coolddown amount
   */
  public void applyCooldown(int cooldown) {
    this.lastCooldown = cooldown;
    this.attackReady = getTicksExisted() + cooldown;
  }

  /**
   * Returns a number from 0 to 1 denoting the current cooldown amount, akin to {@link PlayerEntity#getCooledAttackStrength(float)}
   * @return  number from 0 to 1, with 1 being no cooldown
   */
  public float getCooldown() {
    int ticksExisted = getTicksExisted();
    if (ticksExisted > this.attackReady || this.lastCooldown == 0) {
      return 1.0f;
    }
    return MathHelper.clamp((this.lastCooldown + ticksExisted - this.attackReady) / (float) this.lastCooldown, 0f, 1f);
  }

  /**
   * Checks if we can perform another attack yet.
   * This counteracts rapid attacks via click macros, in a similar way to vanilla by limiting to once every 10 ticks
   */
  public boolean isAttackReady() {
    return getTicksExisted() + this.lastCooldown > this.attackReady;
  }


  /* Helpers */

  /**
   * Gets the offhand cooldown for the given player
   * @param player  Player
   * @return  Offhand cooldown
   */
  public static float getCooldown(PlayerEntity player) {
    return player.getCapability(CAPABILITY).map(COOLDOWN_TRACKER).orElse(1.0f);
  }

  /**
   * Applies cooldown to the given player
   * @param player  Player
   * @param cooldown  Cooldown to apply
   */
  public static void applyCooldown(PlayerEntity player, int cooldown) {
    player.getCapability(CAPABILITY).ifPresent(cap -> cap.applyCooldown(cooldown));
  }

  /**
   * Applies cooldown to the given player
   * @param player  Player
   */
  public static boolean isAttackReady(PlayerEntity player) {
    return player.getCapability(CAPABILITY).map(ATTACK_READY).orElse(true);
  }

  /**
   * Applies cooldown using a tool and its attack speed stat
   * @param toolStack     Tool instance
   * @param cooldownTime  Relative cooldown time for the given source, 20 is vanilla
   */
  public static void applyCooldown(PlayerEntity player, IModifierToolStack toolStack, int cooldownTime) {
    applyCooldown(player, Math.round(cooldownTime / toolStack.getStats().getFloat(ToolStats.ATTACK_SPEED)));
  }
}
