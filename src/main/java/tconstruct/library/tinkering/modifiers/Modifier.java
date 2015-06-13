package tconstruct.library.tinkering.modifiers;

import com.google.common.collect.Lists;

import com.sun.istack.internal.NotNull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;

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
  private final List<RecipeMatch> modifierItems = Lists.newLinkedList();

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
    NBTTagCompound toolTag = TagUtil.getToolTagSafe(stack);
    if (ToolTagUtil.getFreeModifiers(toolTag) < requiredModifiers) {
      // also returns false if the tooltag is missing
      return false;
    }

    return true;
  }

  @Override
  public void apply(ItemStack stack) {
    // add the modifier to its data
    NBTTagList tagList = TagUtil.getModifiersBaseTag(stack);

    // if the modifier hasn't been on the tool already, add it
    boolean alreadyPresent = false;
    for (int i = 0; i < tagList.tagCount(); i++) {
      if (getIdentifier().equals(tagList.getStringTagAt(i))) {
        alreadyPresent = true;
        break;
      }
    }

    if (!alreadyPresent) {
      tagList.appendTag(new NBTTagString(getIdentifier()));
    }


    NBTTagCompound base = TagUtil.getBaseTagSafe(stack);
    base.setTag(Tags.BASE_MODIFIERS, tagList);

    // update the itemstacks NBT
    TagUtil.setBaseTag(stack, base);


    // substract the modifiers
    NBTTagCompound tag = TagUtil.getToolTagSafe(stack);
    int modifiers = ToolTagUtil.getFreeModifiers(tag) - requiredModifiers;
    tag.setInteger(Tags.FREE_MODIFIERS, Math.max(0, modifiers));

    TagUtil.setToolTag(stack, tag);


    // have the modifier itself save its data
    tag = new NBTTagCompound();
    tagList = TagUtil.getModifiersTag(stack);
    int index = TinkerUtil.getIndexInList(tagList, identifier);
    if (index >= 0) {
      tag = tagList.getCompoundTagAt(index);
    }

    // some modifiers might not save data, don't save them
    if (!tag.hasNoTags()) {
      // but if they do, ensure that the identifier is correct
      ModifierNBT data = ModifierNBT.readTag(tag);
      if (!identifier.equals(data.identifier)) {
        data.identifier = identifier;
        data.write(tag);
      }
      updateNBT(tag);
    }

    if (index >= 0) {
      tagList.set(index, tag);
    } else {
      tagList.appendTag(tag);
    }

    TagUtil.setModifiersTag(stack, tag);

    // have the modifier apply its effect based on the nbt data
    NBTTagCompound rootCompound = stack.getTagCompound();
    applyEffect(rootCompound, tag);
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
}
