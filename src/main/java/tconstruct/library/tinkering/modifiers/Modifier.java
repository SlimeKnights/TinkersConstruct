package tconstruct.library.tinkering.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;
import tconstruct.library.utils.ToolTagUtil;

public abstract class Modifier implements IModifier {

  public static final String LOCALIZATION_STRING = "modifier.%s.name";

  public final String identifier;
  public int requiredModifiers = 1;

  public Modifier(String identifier) {
    this.identifier = identifier;

    TinkerRegistry.registerModifier(this);
  }

  @Override
  public String getIdentifier() {
    return identifier;
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
    NBTTagCompound tag = TagUtil.getModifiersBaseTag(stack);

    int i = 0;
    while (tag.hasKey(String.valueOf(i))) {
      i++;
    }

    tag.setString(String.valueOf(i), getIdentifier());

    NBTTagCompound base = TagUtil.getBaseTagSafe(stack);
    base.setTag(Tags.BASE_MODIFIERS, tag);

    // update the itemstacks NBT
    TagUtil.setBaseTag(stack, base);

    // substract the modifiers
    tag = TagUtil.getToolTagSafe(stack);
    int modifiers = ToolTagUtil.getFreeModifiers(tag) - requiredModifiers;
    tag.setInteger(Tags.FREE_MODIFIERS, Math.max(0, modifiers));

    TagUtil.setToolTag(stack, tag);

    // have the modifier itself save its data
    tag = TagUtil.getModifiersTag(stack);
    updateNBT(tag);
    TagUtil.setModifiersTag(stack, tag);

    // have the modifier apply its effect based on the nbt data
    NBTTagCompound rootCompound = stack.getTagCompound();
    applyEffect(rootCompound, tag);
    stack.setTagCompound(rootCompound);
  }

  public String getLocalizedName() {
    return StatCollector.translateToLocalFormatted(LOCALIZATION_STRING, identifier);
  }
}
