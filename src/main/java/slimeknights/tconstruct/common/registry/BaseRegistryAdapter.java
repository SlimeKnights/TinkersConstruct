package slimeknights.tconstruct.common.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

// MANTLE
// TODO: move to mantle
/**
 * A convenience wrapper for forge registries, to be used in combination with the {@link net.minecraftforge.event.RegistryEvent.Register} event.
 * Simply put it allows you to register things by passing (thing, name) instead of having to set the name inline.
 * There also is a convenience variant for items and itemblocks, see {@link ItemRegistryAdapter}.
 */
public class BaseRegistryAdapter<T extends IForgeRegistryEntry<T>> {

  protected final IForgeRegistry<T> registry;
  private final String modId;

  /**
   * Automatically creates determines the modid from the currently loading mod.
   * If this results in the wrong namespace, use the other constructor where you can provide the modid.
   * The modid is used as the namespace for resource locations, so if your mods id is "foo" it will register an item "bar" as "foo:bar".
   */
  public BaseRegistryAdapter(IForgeRegistry<T> registry) {
    this(registry, ModLoadingContext.get().getActiveContainer().getModId());
  }

  /**
   * Backup Constructor that allows setting the modid used when registering manually.
   * The modid is used as the namespace for resource locations.
   */
  public BaseRegistryAdapter(IForgeRegistry<T> registry, String modId) {
    this.registry = registry;
    this.modId = modId;
  }

  /**
   * Construct a resource location that belongs to the given namespace. Usually your mod.
   */
  public ResourceLocation getResource(String name) {
    return new ResourceLocation(modId, name);
  }

  /**
   * General purpose registration method. Just pass the string you want your thing registered as.
   */
  public <I extends T> I register(I forgeRegitryEntry, String name) {
    return this.register(forgeRegitryEntry, this.getResource(name));
  }

  /**
   * General purpose backup registration method. In case you want to set a very specific resource location.
   * You should probably use the special purpose methods instead of this.
   *
   * Note: changes the things registry name. Do not call this with already registered objects!
   */
  public <I extends T> I register(I forgeRegistryEntry, ResourceLocation location) {
    forgeRegistryEntry.setRegistryName(location);
    registry.register(forgeRegistryEntry);
    return forgeRegistryEntry;
  }
}
