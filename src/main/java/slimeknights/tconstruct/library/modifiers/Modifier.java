package slimeknights.tconstruct.library.modifiers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.aspects.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierListNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

import java.util.Arrays;
import java.util.List;

public abstract class Modifier extends ForgeRegistryEntry<IModifier> implements IModifier {

  public static final String NAME_LOCALIZATION = "modifier.%s.%s.name";
  public static final String DESCRIPTION_LOCALIZATION = "modifier.%s.%s.desc";
  public static final String EXTRA_LOCALIZATION = "modifier.%s.%s.extra";

  protected final List<ModifierAspect> aspects = Lists.newLinkedList();

  protected void addAspects(ModifierAspect... aspects) {
    this.aspects.addAll(Arrays.asList(aspects));
  }

  @Override
  public ItemStack apply(ItemStack stack) {
    ToolData toolData = ToolData.from(stack);

    assert this.getRegistryName() != null;

    ModifiedToolStatsBuilder builder = ModifiedToolStatsBuilder.from(toolData);

    ModifierNBT modifierNBT = toolData.getModifiers().getOrCreateModifier(new ModifierId(this.getRegistryName()));

    // update NBT through aspects
    for (ModifierAspect aspect : aspects) {
      aspect.editStats(builder);
      modifierNBT = aspect.editNBT(modifierNBT);
    }

    this.applyStats(builder, modifierNBT);

    ModifierListNBT modifierListNBT = toolData.getModifiers().addOrReplaceModifier(modifierNBT);

    toolData.createNewDataWithStatsAndModifiers(builder.buildNewStats(), modifierListNBT).updateStack(stack);

    return stack;
  }

  @Override
  public void updateNBT(ModifierNBT modifierNBT) {
    // nothing to do in most cases, aspects handle the updating for most modifier
  }

  @Override
  public ITextComponent getTooltip(ModifierNBT modifierNBT, boolean detailed) {
    IFormattableTextComponent textComponent = this.getLocalizedName().deepCopy();

    if (modifierNBT.level > 1) {
      textComponent.appendString(" ").appendString(Util.getRomanNumeral(modifierNBT.level));
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
  public boolean canApplyTogether(IModifier otherModifier) {
    return true;
  }

  @Override
  public ITextComponent getLocalizedName() {
    return new TranslationTextComponent(String.format(NAME_LOCALIZATION, this.getRegistryName().getNamespace(), this.getRegistryName().getPath()));
  }

  @Override
  public ITextComponent getLocalizedDescription() {
    return new TranslationTextComponent(String.format(DESCRIPTION_LOCALIZATION, this.getRegistryName().getNamespace(), this.getRegistryName().getPath()));
  }

  @Override
  public List<ITextComponent> getExtraInfo(ItemStack tool, ModifierNBT modifierNBT) {
    return ImmutableList.of();
  }

  @Override
  public boolean hasTexturePerMaterial() {
    return false;
  }

  @Override
  public boolean equalModifier(ModifierNBT modifierNBT1, ModifierNBT modifierNBT2) {
    return modifierNBT1.identifier.equals(modifierNBT2.identifier) && modifierNBT1.level == modifierNBT2.level;
  }
}
