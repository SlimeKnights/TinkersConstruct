package tconstruct.library.tinkering.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.tinkering.traits.ITrait;
import tconstruct.library.tinkering.traits.TraitNBTData;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.ToolBuilder;
import tconstruct.tools.TinkerMaterials;

public class TraitModifier extends Modifier {
  private final ITrait trait;

  public TraitModifier(String identifier, ITrait trait) {
    super(identifier);

    this.trait = trait;
  }

  @Override
  public boolean canApply(ItemStack stack) {
    NBTTagCompound traitsTag = TagUtil.getTraitsTag(stack);

    // if the trait is already present, it can only be applied if the max count has not been reached yet
    for (int i = 0; traitsTag.hasKey(String.valueOf(i)); i++) {
      TraitNBTData data = TraitNBTData.read(traitsTag, String.valueOf(i));
      if(trait.getIdentifier().equals(data.identifier)) {
        return data.level < trait.getMaxCount();
      }
    }

    return super.canApply(stack);
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    ModifierNBT.Boolean.write(true, modifierTag, identifier);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    NBTTagCompound traits = TagUtil.getTraitsTag(rootCompound);
    ToolBuilder.addTrait(traits, TinkerMaterials.stonebound, EnumChatFormatting.DARK_GRAY);
  }

  @Override
  public boolean hasTexturePerMaterial() {
    return false;
  }
}
