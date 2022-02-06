package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class EmbellishmentModifier extends SingleUseModifier {
  private static final String FORMAT_KEY = TConstruct.makeTranslationKey("modifier", "embellishment.formatted");

  @Override
  public Component getDisplayName(IToolStackView tool, int level) {
    MaterialId location = MaterialId.tryParse(tool.getPersistentData().getString(getId()));
    if (location != null) {
      IMaterial material = MaterialRegistry.getMaterial(location);
      TextColor color = material.getColor();
      return new TranslatableComponent(FORMAT_KEY, material.getDisplayName()).withStyle(style -> style.withColor(color));
    }
    return super.getDisplayName();
  }

  @Override
  public void onRemoved(IToolStackView tool) {
    tool.getPersistentData().remove(getId());
  }
}
