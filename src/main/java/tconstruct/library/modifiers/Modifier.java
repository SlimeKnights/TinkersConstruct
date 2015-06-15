package tconstruct.library.modifiers;

import com.google.common.collect.Lists;

import com.sun.istack.internal.NotNull;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;

import java.util.Arrays;
import java.util.List;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.TinkerUtil;

public abstract class Modifier implements IModifier {

  public static final String LOCALIZATION_STRING = "modifier.%s.name";

  public final String identifier;
  public int requiredModifiers = 1;

  // A mapping of oredict-entries to how often the item can be applied with this item
  protected final List<RecipeMatch> modifierItems = Lists.newLinkedList();
  protected final List<ModifierAspect> aspects = Lists.newLinkedList();

  public Modifier(@NotNull String identifier) {
    this.identifier = identifier;

    TinkerRegistry.registerModifier(this);
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  public void addItem(String oredictItem, int count) {
    modifierItems.add(new RecipeMatch.Oredict(oredictItem, count));
  }

  public void addItem(String oredictItem) {
    addItem(oredictItem, 1);
  }

  public void addItem(Item item, int count) {
    modifierItems.add(new RecipeMatch.Item(new ItemStack(item), count));
  }

  public void addItem(Block block, int count) {
    modifierItems.add(new RecipeMatch.Item(new ItemStack(block), count));
  }

  public void addItem(Item item) {
    addItem(item, 1);
  }

  protected void addAspects(ModifierAspect... aspects) {
    this.aspects.addAll(Arrays.asList(aspects));
  }

  @Override
  public RecipeMatch.Match matches(ItemStack[] stacks) {
    for(RecipeMatch recipe : modifierItems) {
      RecipeMatch.Match match = recipe.matches(stacks);
      if(match != null) {
        return match;
      }
    }

    return null;
  }

  @Override
  public final boolean canApply(ItemStack stack) throws ModifyException {
    // aspects
    for(ModifierAspect aspect : aspects) {
      if(!aspect.canApply(stack)) {
        return false;
      }
    }

    return canApplyCustom(stack);
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
      tagList = TagUtil.getBaseModifiersTagList(root);;
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
  public String getTooltip(NBTTagCompound modifierTag) {
    StringBuilder sb = new StringBuilder();

    ModifierNBT data = ModifierNBT.readTag(modifierTag);

    sb.append(getLocalizedName());
    if(data.level > 1) {
      sb.append(" ");
      sb.append(TinkerUtil.getRomanNumeral(data.level));
    }

    return sb.toString();
  }

  @Override
  public String getLocalizedName() {
    return StatCollector.translateToLocalFormatted(LOCALIZATION_STRING, identifier);
  }

  @Override
  public boolean hasTexturePerMaterial() {
    return false;
  }
}
