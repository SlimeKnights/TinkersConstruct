package slimeknights.tconstruct.library;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;

/**
 * Class containing any data that does not have a dedicated registry class. Mostly TiC registries backed by {@link IForgeRegistry}.
 */
public class TinkerRegistries {
  /** Resource location for empty objects */
  public static final ResourceLocation EMPTY = TConstruct.getResource("empty");

  /** Register for modifiers */
  public static final IForgeRegistry<Modifier> MODIFIERS = RegistryManager.ACTIVE.getRegistry(Modifier.class);
}
