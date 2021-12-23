package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Lazy;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import java.util.Arrays;
import java.util.List;

public class OffhandedModifier extends Modifier {
  private final Lazy<ITextComponent> noHandedName = Lazy.of(() -> applyStyle(new TranslationTextComponent(getTranslationKey() + ".2")));
  private final Lazy<List<ITextComponent>> noHandedDescription = Lazy.of(() -> Arrays.asList(
    new TranslationTextComponent(getTranslationKey() + ".flavor").mergeStyle(TextFormatting.ITALIC),
    new TranslationTextComponent(getTranslationKey() + ".description.2")));

  public OffhandedModifier() {
    super(0x7E627B);
  }

  @Override
  public void addVolatileData(Item item, ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(IModifiable.DEFER_OFFHAND, true);
    if (level > 1) {
      volatileData.putBoolean(IModifiable.NO_INTERACTION, true);
    }
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    if (level > 1) {
      return noHandedName.get();
    }
    return super.getDisplayName();
  }

  @Override
  public List<ITextComponent> getDescriptionList(int level) {
    if (level > 1) {
      return noHandedDescription.get();
    }
    return super.getDescriptionList();
  }
}
