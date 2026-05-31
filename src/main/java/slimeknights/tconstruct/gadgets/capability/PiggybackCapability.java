package slimeknights.tconstruct.gadgets.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.compat.neoforged.neoforge.capabilities.Capability;
import slimeknights.tconstruct.compat.neoforged.neoforge.capabilities.CapabilityManager;
import slimeknights.tconstruct.compat.neoforged.neoforge.capabilities.CapabilityToken;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import slimeknights.mantle.compat.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.bus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;

import java.util.Map;
import java.util.WeakHashMap;

/** Capability logic */
public class PiggybackCapability {
  private static final ResourceLocation ID = TConstruct.getResource("piggyback");
  public static final Capability<PiggybackHandler> PIGGYBACK = CapabilityManager.get(new CapabilityToken<>() {});
  private static final Map<Player, PiggybackHandler> DATA = new WeakHashMap<>();

  private PiggybackCapability() {}

  /** Registers this capability */
  public static void register() {
    slimeknights.tconstruct.TConstruct.getModBus().addListener(EventPriority.NORMAL, false, RegisterCapabilitiesEvent.class, PiggybackCapability::register);
  }

  /** Registers the capability with the event bus */
  private static void register(RegisterCapabilitiesEvent event) {
    // Piggyback state is transient and stored per-player in this 1.21 port.
  }

  /** Gets the piggyback handler for a player-like entity. */
  public static LazyOptional<PiggybackHandler> getCapability(Entity entity) {
    if (entity instanceof Player player) {
      return LazyOptional.of(() -> DATA.computeIfAbsent(player, PiggybackHandler::new));
    }
    return LazyOptional.empty();
  }
}
