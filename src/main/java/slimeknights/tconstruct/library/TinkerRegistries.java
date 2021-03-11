package slimeknights.tconstruct.library;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import slimeknights.tconstruct.library.modifiers.Modifier;

/**
 * Class containing any data that does not have a dedicated registry class. Mostly TiC registries backed by {@link IForgeRegistry}.
 */
public class TinkerRegistries {
  /** Resource location for empty objects */
  public static final ResourceLocation EMPTY = Util.getResource("empty");

  /** Register for modifiers */
  public static final IForgeRegistry<Modifier> MODIFIERS = RegistryManager.ACTIVE.getRegistry(Modifier.class);
}
