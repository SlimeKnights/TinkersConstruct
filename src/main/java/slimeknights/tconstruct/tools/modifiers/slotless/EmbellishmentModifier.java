package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.cosmetic.EmbellishmentModule;

import javax.annotation.Nullable;

/** @deprecated use {@link EmbellishmentModule} */
@Deprecated(forRemoval = true)
public class EmbellishmentModifier extends NoLevelsModifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(EmbellishmentModule.INSTANCE);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    return EmbellishmentModule.INSTANCE.getDisplayName(tool, entry, super.getDisplayName(), access);
  }
}
