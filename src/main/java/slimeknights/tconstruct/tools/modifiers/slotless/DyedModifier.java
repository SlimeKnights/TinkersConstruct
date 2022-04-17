package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class DyedModifier extends NoLevelsModifier {
  private static final String FORMAT_KEY = TConstruct.makeTranslationKey("modifier", "dyed.formatted");

  @Override
  public Component getDisplayName(IToolStackView tool, int level) {
    ModDataNBT persistentData = tool.getPersistentData();
    ResourceLocation key = getId();
    if (persistentData.contains(key, Tag.TAG_INT)) {
      int color = persistentData.getInt(key);
      return applyStyle(new TranslatableComponent(FORMAT_KEY, String.format("%06X", color)));
    }
    return super.getDisplayName();
  }

  @Override
  public void onRemoved(IToolStackView tool) {
    tool.getPersistentData().remove(getId());
  }
}
