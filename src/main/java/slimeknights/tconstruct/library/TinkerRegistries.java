package slimeknights.tconstruct.library;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;

import java.util.function.Supplier;

/**
 * Class containing any data that does not have a dedicated registry class. Mostly TiC registries backed by {@link IForgeRegistry}.
 */
public class TinkerRegistries {
  /** Resource location for empty objects */
  public static final ResourceLocation EMPTY = TConstruct.getResource("empty");

  /** Registry key for the modifiers */
  public static final ResourceKey<Registry<Modifier>> MODIFIER_REGISTRY = ResourceKey.createRegistryKey(TConstruct.getResource("modifiers"));

  /** Register for modifiers */
  public static final Supplier<IForgeRegistry<Modifier>> MODIFIERS = Lazy.of(() -> RegistryManager.ACTIVE.getRegistry(MODIFIER_REGISTRY));
}
