package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class EmbellishmentModifier extends NoLevelsModifier implements ModifierRemovalHook {
  private static final String FORMAT_KEY = TConstruct.makeTranslationKey("modifier", "embellishment.formatted");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.REMOVE);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, int level) {
    MaterialVariantId materialVariant = MaterialVariantId.tryParse(tool.getPersistentData().getString(getId()));
    if (materialVariant != null) {
      return Component.translatable(FORMAT_KEY, MaterialTooltipCache.getDisplayName(materialVariant)).withStyle(style -> style.withColor(MaterialTooltipCache.getColor(materialVariant)));
    }
    return super.getDisplayName();
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(getId());
    return null;
  }
}
