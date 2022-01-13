package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class EmbellishmentModifier extends SingleUseModifier {
  private static final String FORMAT_KEY = TConstruct.makeTranslationKey("modifier", "embellishment.formatted");
  public EmbellishmentModifier() {
    super(-1);
  }

  @Override
  public ITextComponent getDisplayName(IModifierToolStack tool, int level) {
    MaterialId location = MaterialId.tryCreate(tool.getPersistentData().getString(getId()));
    if (location != null) {
      IMaterial material = MaterialRegistry.getMaterial(location);
      Color color = material.getColor();
      return new TranslationTextComponent(FORMAT_KEY, material.getDisplayName()).modifyStyle(style -> style.setColor(color));
    }
    return super.getDisplayName();
  }

  @Override
  public void onRemoved(IModifierToolStack tool) {
    tool.getPersistentData().remove(getId());
  }
}
