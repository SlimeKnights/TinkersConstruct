package slimeknights.tconstruct.library;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.tools.modifiers.EmptyModifier;

/**
 * Class containing any data that does not have a dedicated registry class. Mostly TiC registries backed by {@link Registry}.
 */
public class TinkerRegistries {

  /**
   * Resource location for empty objects
   */
  public static final Identifier EMPTY = Util.getResource("empty");

  /**
   * Register for modifiers
   */
  public static final RegistryKey<Registry<Modifier>> MODIFIERS_KEY = Registry.createRegistryKey("modifiers");
  public static final DefaultedRegistry<Modifier> MODIFIERS = Registry.create(MODIFIERS_KEY, "none", EmptyModifier::new);
}
