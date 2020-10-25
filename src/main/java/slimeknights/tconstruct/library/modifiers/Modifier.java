package slimeknights.tconstruct.library.modifiers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.GameData;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.aspects.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ModifierListNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public abstract class Modifier implements IModifier {

  public static final String NAME_LOCALIZATION = "modifier.%s.%s";
  public static final String DESCRIPTION_LOCALIZATION = "modifier.%s.%s.description";
  public static final String EXTRA_LOCALIZATION = "modifier.%s.%s.extra";

  protected final List<ModifierAspect> aspects = Lists.newLinkedList();

  private ModifierId registryName = null;

  private StatsNBT originalStats = null;

  protected void addAspects(ModifierAspect... aspects) {
    this.aspects.addAll(Arrays.asList(aspects));
  }

  @Override
  public ItemStack apply(ItemStack stack) {
    ToolData toolData = ToolData.from(stack);

    originalStats = ((ToolCore) stack.getItem()).buildToolStats(toolData.getMaterials());

    assert getRegistryName() != null;

    ModifiedToolStatsBuilder builder = ModifiedToolStatsBuilder.from(toolData);

    ModifierNBT modifierNBT = toolData.getModifiers().getOrCreateModifier(new ModifierId(this.getRegistryName()));

    // update NBT through aspects
    for (ModifierAspect aspect : aspects) {
      aspect.editStats(builder);
      modifierNBT = aspect.editNBT(modifierNBT);
    }

    applyStats(builder, modifierNBT);

    ModifierListNBT modifierListNBT = toolData.getModifiers().addOrReplaceModifier(modifierNBT);

    toolData.createNewDataWithStatsAndModifiers(builder.buildNewStats(), modifierListNBT).updateStack(stack);

    return stack;
  }

  public StatsNBT getOriginalStats() {
    return originalStats;
  }

  @Override
  public void updateNBT(ModifierNBT modifierNBT) {
    // nothing to do in most cases, aspects handle the updating for most modifier
  }

  @Override
  public ITextComponent getTooltip(ModifierNBT modifierNBT, boolean detailed) {
    IFormattableTextComponent textComponent = getLocalizedName().deepCopy();

    if (modifierNBT.level > 1) {
      textComponent.appendString(" ").appendString(Util.getRomanNumeral(modifierNBT.level));
    }

    return textComponent.modifyStyle(style -> style.setColor(getTextColor()));
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
    return new TranslationTextComponent(String.format(NAME_LOCALIZATION, getRegistryName().getNamespace(), getRegistryName().getPath()));
  }

  @Override
  public ITextComponent getLocalizedDescription() {
    return new TranslationTextComponent(String.format(DESCRIPTION_LOCALIZATION, getRegistryName().getNamespace(), getRegistryName().getPath())).modifyStyle(style -> style.setColor(getTextColor()));
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

  @Nullable
  public Color getTextColor() {
    if (getColorIndex() != 0) {
      int color = getColorIndex();

      if ((color & 0xFF000000) == 0) {
        color |= 0xFF000000;
      }

      return Color.fromInt(color);
    }

    return null;
  }

  @Override
  public IModifier setRegistryName(ResourceLocation name) {
    if (getRegistryName() != null) { throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName()); }

    registryName = new ModifierId(GameData.checkPrefix(name.toString(), true));
    return this;
  }

  @Nullable
  @Override
  public ModifierId getRegistryName() {
    return registryName;
  }
}
