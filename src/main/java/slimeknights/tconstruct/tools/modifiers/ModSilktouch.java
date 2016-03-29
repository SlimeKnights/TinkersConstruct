package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;

public class ModSilktouch extends Modifier {

  public ModSilktouch() {
    super("silktouch");

    addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this, 0xfbe28b), ModifierAspect.freeModifier);
  }

  @Override
  public boolean canApplyTogether(Enchantment enchantment) {
    return !(enchantment == Enchantments.silkTouch || enchantment == Enchantments.looting || enchantment == Enchantments.fortune);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    ToolBuilder.addEnchantment(rootCompound, Enchantments.silkTouch);

    ToolNBT toolData = TagUtil.getToolStats(rootCompound);
    toolData.speed = Math.max(1f, toolData.speed - 3f);
    toolData.attack = Math.max(1f, toolData.attack - 3f);

    TagUtil.setToolTag(rootCompound, toolData.get());
  }
}
