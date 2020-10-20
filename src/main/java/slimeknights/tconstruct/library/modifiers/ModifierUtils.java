package slimeknights.tconstruct.library.modifiers;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModifierUtils {

  private static IForgeRegistry<IModifier> REGISTRY;

  @Nonnull
  public static IForgeRegistry<IModifier> getRegistry() {
    if (REGISTRY == null) {
      REGISTRY = RegistryManager.ACTIVE.getRegistry(IModifier.class);
    }

    return REGISTRY;
  }

  @Nullable
  public static IModifier getModifier(ModifierId modifierId) {
    return getRegistry().getValue(modifierId);
  }
}
