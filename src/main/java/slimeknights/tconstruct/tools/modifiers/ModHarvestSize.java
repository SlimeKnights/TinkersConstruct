package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Locale;

import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tools.IAoeTool;

public class ModHarvestSize extends ToolModifier {

  public ModHarvestSize(String name) {
    super("harvest" + name.toLowerCase(Locale.US), 0xcaf6a2);

    addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this), ModifierAspect.harvestOnly, ModifierAspect.freeModifier);
  }

  @Override
  protected boolean canApplyCustom(ItemStack stack) throws TinkerGuiException {
    // we can only apply this to AOE tools
    if(!(stack.getItem() instanceof IAoeTool)) {
      return false;
    }

    return super.canApplyCustom(stack);
  }


  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    // no extra data needed
  }
}
