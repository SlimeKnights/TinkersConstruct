package tconstruct.library.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.traits.ITrait;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.TinkerUtil;

public class TraitModifier extends Modifier {
  private final ITrait trait;
  private final EnumChatFormatting color;

  public TraitModifier(ITrait trait, EnumChatFormatting color) {
    super(trait.getIdentifier());

    this.trait = trait;
    this.color = color;

    // we assume traits can only be applied once.
    // If you want stacking traits you'll have to do that stuff yourself :P
    this.addAspects(new ModifierAspect.SingleAspect(this));
  }

  @Override
  public boolean canApplyCustom(ItemStack stack) {
    // can only apply if the trait isn't present already
    NBTTagList tagList = TagUtil.getTraitsTagList(stack);
    int index = TinkerUtil.getIndexInList(tagList, trait.getIdentifier());

    // not present yet
    return index < 0;
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    updateNBTWithColor(modifierTag, color);
  }

  public void updateNBTWithColor(NBTTagCompound modifierTag, EnumChatFormatting newColor) {
    ModifierNBT data = ModifierNBT.readTag(modifierTag);
    data.identifier = identifier;
    data.color = newColor;
    // we ensure at least lvl1 for compatibility with the level-aspect
    if(data.level == 0) {
      data.level = 1;
    }
    data.write(modifierTag);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    // add the trait to the traitlist so it gets processed
    NBTTagList traits = TagUtil.getTraitsTagList(rootCompound);
    // if it's not already present
    for(int i = 0; i < traits.tagCount(); i++) {
      if(identifier.equals(traits.getStringTagAt(i))) {
        return;
      }
    }

    traits.appendTag(new NBTTagString(identifier));
    TagUtil.setTraitsTagList(rootCompound, traits);
  }
}
