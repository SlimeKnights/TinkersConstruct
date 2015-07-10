package tconstruct.tools.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.modifiers.Modifier;
import tconstruct.library.modifiers.ModifierAspect;
import tconstruct.library.modifiers.TraitModifier;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.ToolTagUtil;
import tconstruct.tools.TinkerMaterials;

// Only for test purposes
public class StoneboundModifier extends TraitModifier {

  public StoneboundModifier() {
    super(TinkerMaterials.stonebound, EnumChatFormatting.DARK_GRAY);

    addItem("cobblestone");
    aspects.clear(); // remove traitModifiers aspects
    addAspects(new ModifierAspect.LevelAspect(this, 3), ModifierAspect.freeModifier);
  }

  @Override
  public boolean canApplyCustom(ItemStack stack) {
    return true;
  }
}
