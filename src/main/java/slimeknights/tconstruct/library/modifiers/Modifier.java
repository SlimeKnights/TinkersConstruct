package slimeknights.tconstruct.library.modifiers;

import com.google.common.collect.ImmutableList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

import java.util.List;

public abstract class Modifier extends ForgeRegistryEntry<Modifier> implements IModifier {

  public static final String NAME_LOCALIZATION = "modifier.%s.%s.name";
  public static final String DESCRIPTION_LOCALIZATION = "modifier.%s.%s.desc";
  public static final String EXTRA_LOCALIZATION = "modifier.%s.%s.extra";

  @Override
  public void apply(ItemStack stack) {
    ToolData toolData = ToolData.from(stack);

    ModifierNBT modifierNBT = new ModifierNBT();

    ModifierToolStatsBuilder builder = ModifierToolStatsBuilder.from(toolData);

    this.applyEffectToStats(builder, null);

    toolData.createNewDataWithStats(builder.buildNewStats()).updateStack(stack);
  }

  @Override
  public void updateNBT(CompoundNBT modifierTag) {
    // nothing to do in most cases, aspects handle the updating for most modifier
  }

  @Override
  public IFormattableTextComponent getTooltip(CompoundNBT modifierTag, boolean detailed) {
    IFormattableTextComponent textComponent = this.getLocalizedName().deepCopy();

    ModifierNBT data = ModifierNBT.readFromNBT(modifierTag);

    if (data.level > 1) {
      textComponent.appendString(" ").appendString(Util.getRomanNumeral(data.level));
    }

    return textComponent;
  }

  @Override
  public boolean isHidden() {
    return false;
  }

  @Override
  public boolean canApplyTogether(Enchantment enchantment) {
    return true;
  }

  @Override
  public boolean canApplyTogether(IToolMod otherModifier) {
    return true;
  }

  @Override
  public IFormattableTextComponent getLocalizedName() {
    return new TranslationTextComponent(String.format(NAME_LOCALIZATION, this.getRegistryName().getNamespace(), this.getRegistryName().getPath()));
  }

  @Override
  public IFormattableTextComponent getLocalizedDescription() {
    return new TranslationTextComponent(String.format(DESCRIPTION_LOCALIZATION, this.getRegistryName().getNamespace(), this.getRegistryName().getPath()));
  }

  @Override
  public List<IFormattableTextComponent> getExtraInfo(ItemStack tool, CompoundNBT modifierTag) {
    return ImmutableList.of();
  }

  @Override
  public boolean hasTexturePerMaterial() {
    return false;
  }

  @Override
  public boolean equalModifier(CompoundNBT modifierTag1, CompoundNBT modifierTag2) {
    ModifierNBT data1 = ModifierNBT.readFromNBT(modifierTag1);
    ModifierNBT data2 = ModifierNBT.readFromNBT(modifierTag2);

    return data1.identifier.equals(data2.identifier) && data1.level == data2.level;
  }

}
