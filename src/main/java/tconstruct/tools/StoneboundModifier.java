package tconstruct.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.modifiers.ModifierAspect;
import tconstruct.library.modifiers.TraitModifier;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.ToolTagUtil;

// Only for test purposes
public class StoneboundModifier extends TraitModifier {

  public StoneboundModifier() {
    super(TinkerMaterials.stonebound, EnumChatFormatting.DARK_GRAY);

    addItem("cobblestone");
    aspects.clear();
    addAspects(new ModifierAspect.LevelAspect(this, 3), ModifierAspect.freeModifier);
  }

  @Override
  public boolean canApply(ItemStack stack) {
    // requires free modifiers
    NBTTagCompound toolTag = TagUtil.getToolTag(stack);
    if(ToolTagUtil.getFreeModifiers(toolTag) < requiredModifiers) {
      // also returns false if the tooltag is missing
      return false;
    }

    // aspects
    for(ModifierAspect aspect : aspects) {
      if(!aspect.canApply(stack)) {
        return false;
      }
    }

    return true;
  }
}
