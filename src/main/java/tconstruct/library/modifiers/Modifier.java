package tconstruct.library.modifiers;

import com.google.common.collect.Lists;

import com.sun.istack.internal.NotNull;

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
import tconstruct.library.utils.Tags;
import tconstruct.library.utils.TinkerUtil;
import tconstruct.library.utils.ToolTagUtil;

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

  public void addItem(Item item) {
    addItem(item, 1);
  }

  protected void addAspects(ModifierAspect... aspects) {
    this.aspects.addAll(Arrays.asList(aspects));
  }

  @Override
  public RecipeMatch.Match matches(ItemStack[] stacks) {
    for (RecipeMatch recipe : modifierItems) {
      RecipeMatch.Match match = recipe.matches(stacks);
      if (match != null) {
        return match;
      }
    }

    return null;
  }

  @Override
  public boolean canApply(ItemStack stack) {
    // requires free modifiers
    NBTTagCompound toolTag = TagUtil.getToolTag(stack);
    if (ToolTagUtil.getFreeModifiers(toolTag) < requiredModifiers) {
      // also returns false if the tooltag is missing
      return false;
    }

    // aspects
    for (ModifierAspect aspect : aspects) {
      if(!aspect.canApply(stack)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public void apply(ItemStack stack) {
    // add the modifier to its data
    NBTTagList tagList = TagUtil.getBaseModifiersTagList(stack);

    // if the modifier hasn't been on the tool already, add it
    boolean alreadyPresent = false;
    for (int i = 0; i < tagList.tagCount(); i++) {
      if (getIdentifier().equals(tagList.getStringTagAt(i))) {
        alreadyPresent = true;
        break;
      }
    }

    // if the modifier wasn't present before, add it and safe it to the tool
    if (!alreadyPresent) {
      tagList.appendTag(new NBTTagString(getIdentifier()));
      TagUtil.setBaseModifiersTagList(stack, tagList);
    }


    // substract the modifiers
    NBTTagCompound toolTag = TagUtil.getToolTag(stack);
    int modifiers = ToolTagUtil.getFreeModifiers(toolTag) - requiredModifiers;
    toolTag.setInteger(Tags.FREE_MODIFIERS, Math.max(0, modifiers));

    TagUtil.setToolTag(stack, toolTag);


    // have the modifier itself save its data
    NBTTagCompound modifierTag = new NBTTagCompound();
    tagList = TagUtil.getModifiersTagList(stack);
    int index = TinkerUtil.getIndexInList(tagList, identifier);
    if (index >= 0) {
      modifierTag = tagList.getCompoundTagAt(index);
    }

    // update NBT through aspects
    for (ModifierAspect aspect : aspects) {
      aspect.updateNBT(modifierTag);
    }

    updateNBT(modifierTag);

    // some modifiers might not save data, don't save them
    if (!modifierTag.hasNoTags()) {
      // but if they do, ensure that the identifier is correct
      ModifierNBT data = ModifierNBT.readTag(modifierTag);
      if (!identifier.equals(data.identifier)) {
        data.identifier = identifier;
        data.write(modifierTag);
      }
    }

    // update the tools NBT
    if (index >= 0) {
      tagList.set(index, modifierTag);
    } else {
      tagList.appendTag(modifierTag);
    }

    TagUtil.setModifiersTagList(stack, tagList);

    // have the modifier apply its effect based on the nbt data
    NBTTagCompound rootCompound = stack.getTagCompound();
    applyEffect(rootCompound, modifierTag);
    stack.setTagCompound(rootCompound);
  }

  @Override
  public String getTooltip(NBTTagCompound modifierTag) {
    StringBuilder sb = new StringBuilder();

    ModifierNBT data = ModifierNBT.readTag(modifierTag);

    sb.append(getLocalizedName());
    if (data.level > 1) {
      sb.append(" ");
      sb.append(TinkerUtil.getRomanNumeral(data.level));
    }

    return sb.toString();
  }

  public String getLocalizedName() {
    return StatCollector.translateToLocalFormatted(LOCALIZATION_STRING, identifier);
  }

  @Override
  public boolean hasTexturePerMaterial() {
    return false;
  }
}
