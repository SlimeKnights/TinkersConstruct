package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.cosmetic.TrimModule;

import javax.annotation.Nullable;

/** @deprecated use {@link slimeknights.tconstruct.tools.modules.cosmetic.TrimModule} */
@Deprecated(forRemoval = true)
public class TrimModifier extends NoLevelsModifier {
  private static final TrimModule TRIM = new TrimModule();
  /** @deprecated use {@link TrimModule#patternKey(ModifierId)} */
  @Deprecated(forRemoval = true)
  public static final ResourceLocation TRIM_PATTERN = TConstruct.getResource("trim_pattern");
  /** @deprecated use {@link TrimModule#materialKey(ModifierId)} */
  @Deprecated(forRemoval = true)
  public static final ResourceLocation TRIM_MATERIAL = TConstruct.getResource("trim_material");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(TRIM);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    return TRIM.getDisplayName(tool, entry, getDisplayName(), access);
  }
}
