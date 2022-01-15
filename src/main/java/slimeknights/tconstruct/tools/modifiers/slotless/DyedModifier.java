package slimeknights.tconstruct.tools.modifiers.slotless;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.Objects;

public class DyedModifier extends SingleUseModifier {
  private static final String FORMAT_KEY = TConstruct.makeTranslationKey("modifier", "dyed.formatted");
  public DyedModifier() {
    super(Objects.requireNonNull(TextFormatting.GRAY.getColor()));
  }

  @Override
  public ITextComponent getDisplayName(IModifierToolStack tool, int level) {
    ModDataNBT persistentData = tool.getPersistentData();
    ResourceLocation key = getId();
    if (persistentData.contains(key, NBT.TAG_INT)) {
      int color = persistentData.getInt(key);
      return applyStyle(new TranslationTextComponent(FORMAT_KEY, String.format("%06X", color)));
    }
    return super.getDisplayName();
  }

  @Override
  public void onRemoved(IModifierToolStack tool) {
    tool.getPersistentData().remove(getId());
  }
}
