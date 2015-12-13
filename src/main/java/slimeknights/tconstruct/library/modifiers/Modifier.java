package slimeknights.tconstruct.library.modifiers;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;

import java.util.Arrays;
import java.util.List;

import slimeknights.mantle.util.RecipeMatchRegistry;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public abstract class Modifier extends RecipeMatchRegistry implements IModifier {

  public static final String LOC_Name = "modifier.%s.name";
  public static final String LOC_Desc = "modifier.%s.desc";

  public final String identifier;

  protected final List<ModifierAspect> aspects = Lists.newLinkedList();

  public Modifier(String identifier) {
    this.identifier = Util.sanitizeLocalizationString(identifier);

    TinkerRegistry.registerModifier(this);
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  protected void addAspects(ModifierAspect... aspects) {
    this.aspects.addAll(Arrays.asList(aspects));
  }

  @Override
  public final boolean canApply(ItemStack stack) throws TinkerGuiException {
    // aspects
    for(ModifierAspect aspect : aspects) {
      if(!aspect.canApply(stack)) {
        return false;
      }
    }

    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(stack);
    for(int i = 0; i < modifiers.tagCount(); i++) {
      String id = modifiers.getStringTagAt(i);
      IModifier mod = TinkerRegistry.getModifier(id);
      if(mod != null && !canApplyTogether(mod)) {
        return false;
      }
    }

    return canApplyCustom(stack);
  }

  public boolean canApplyTogether(IModifier otherModifier) {
    return true;
  }

  protected boolean canApplyCustom(ItemStack stack) {
    return true;
  }

  @Override
  public void apply(ItemStack stack) {
    NBTTagCompound root = TagUtil.getTagSafe(stack);
    apply(root);
    stack.setTagCompound(root);
  }

  @Override
  public void apply(NBTTagCompound root) {
    // add the modifier to its data
    NBTTagList tagList;

    // if the modifier wasn't present before, add it and safe it to the tool
    if(!TinkerUtil.hasModifier(root, getIdentifier())) {
      tagList = TagUtil.getBaseModifiersTagList(root);
      tagList.appendTag(new NBTTagString(getIdentifier()));
      TagUtil.setBaseModifiersTagList(root, tagList);
    }

    // have the modifier itself save its data
    NBTTagCompound modifierTag = new NBTTagCompound();
    tagList = TagUtil.getModifiersTagList(root);
    int index = TinkerUtil.getIndexInList(tagList, identifier);
    if(index >= 0) {
      modifierTag = tagList.getCompoundTagAt(index);
    }

    // update NBT through aspects
    for(ModifierAspect aspect : aspects) {
      aspect.updateNBT(root, modifierTag);
    }

    updateNBT(modifierTag);

    // some modifiers might not save data, don't save them
    if(!modifierTag.hasNoTags()) {
      // but if they do, ensure that the identifier is correct
      ModifierNBT data = ModifierNBT.readTag(modifierTag);
      if(!identifier.equals(data.identifier)) {
        data.identifier = identifier;
        data.write(modifierTag);
      }
    }

    // update the tools NBT
    if(index >= 0) {
      tagList.set(index, modifierTag);
    }
    else {
      tagList.appendTag(modifierTag);
    }

    TagUtil.setModifiersTagList(root, tagList);

    applyEffect(root, modifierTag);
  }

  @Override
  public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
    StringBuilder sb = new StringBuilder();

    ModifierNBT data = ModifierNBT.readTag(modifierTag);

    sb.append(getLocalizedName());
    if(data.level > 1) {
      sb.append(" ");
      sb.append(TinkerUtil.getRomanNumeral(data.level));
    }

    return sb.toString();
  }

  public String getLeveledTooltip(NBTTagCompound modifierTag, boolean detailed) {
    // the most important function in the whole file!
    ModifierNBT data = ModifierNBT.readInteger(modifierTag);

    String basic = getLocalizedName(); // backup

    for(int i = data.level; i > 1; i--) {
      if(StatCollector.canTranslate(String.format(LOC_Name + i, getIdentifier()))) {
        basic = StatCollector.translateToLocal(String.format(LOC_Name + i, getIdentifier()));
        break;
      }
    }

    if(detailed) {
      return basic + " " + data.extraInfo;
    }

    return basic;
  }

  @Override
  public String getLocalizedName() {
    return Util.translate(LOC_Name, getIdentifier());
  }

  @Override
  public String getLocalizedDesc() {
    return Util.translate(LOC_Desc, getIdentifier());
  }

  @Override
  public boolean hasTexturePerMaterial() {
    return false;
  }
}
