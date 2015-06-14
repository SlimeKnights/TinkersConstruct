package tconstruct.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.modifiers.Modifier;
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
    // aspects
    for(ModifierAspect aspect : aspects) {
      if(!aspect.canApply(stack)) {
        return false;
      }
    }

    return true;
  }
}
