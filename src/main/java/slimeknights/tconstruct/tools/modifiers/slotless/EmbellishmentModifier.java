package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EmbellishmentModifier extends SingleUseModifier {
  private static final String FORMAT_KEY = TConstruct.makeTranslationKey("modifier", "embellishment.formatted");

  @Override
  public Component getDisplayName(IToolStackView tool, int level) {
    MaterialVariantId materialVariant = MaterialVariantId.tryParse(tool.getPersistentData().getString(getId()));
    if (materialVariant != null) {
      return new TranslatableComponent(FORMAT_KEY, MaterialTooltipCache.getDisplayName(materialVariant)).withStyle(style -> style.withColor(MaterialTooltipCache.getColor(materialVariant)));
    }
    return super.getDisplayName();
  }

  @Override
  public void onRemoved(IToolStackView tool) {
    tool.getPersistentData().remove(getId());
  }
}
