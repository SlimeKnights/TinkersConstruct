package tconstruct.library.tinkering.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.tinkering.traits.ITrait;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.TinkerUtil;

public class TraitModifier extends Modifier {
  private final ITrait trait;
  private final EnumChatFormatting color;

  public TraitModifier(ITrait trait, EnumChatFormatting color) {
    super(trait.getIdentifier());

    this.trait = trait;
    this.color = color;
  }

  @Override
  public boolean canApply(ItemStack stack) {
    // trait data is saved as a modifier
    NBTTagList tagList = TagUtil.getModifiersTag(stack);

    int index = TinkerUtil.getIndexInList(tagList, trait.getIdentifier());

    // if the trait is already present, it can only be applied if the max count has not been reached yet
    if (index >= 0) {
      ModifierNBT data = ModifierNBT.readTag(tagList.getCompoundTagAt(index));
      return data.level < trait.getMaxCount();
    }


    return super.canApply(stack);
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    ModifierNBT data = new ModifierNBT(this);
    data.color = color;
    data.write(modifierTag);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    // add the trait to the traitlist so it gets processed
    NBTTagList traits = TagUtil.getTraitsTag(rootCompound);
    // if it's not already present
    for (int i = 0; i < traits.tagCount(); i++) {
      if (identifier.equals(traits.getStringTagAt(i))) {
        return;
      }
    }

    traits.appendTag(new NBTTagString(identifier));
  }

  @Override
  public boolean hasTexturePerMaterial() {
    return false;
  }
}
