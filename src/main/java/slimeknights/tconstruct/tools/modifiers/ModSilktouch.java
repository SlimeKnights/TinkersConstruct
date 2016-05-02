package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolBuilder;

public class ModSilktouch extends ToolModifier {

  public ModSilktouch() {
    super("silktouch", 0xfbe28b);

    addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this), ModifierAspect.freeModifier);
  }

  @Override
  public boolean canApplyTogether(Enchantment enchantment) {
    return !(enchantment == Enchantments.SILK_TOUCH || enchantment == Enchantments.LOOTING || enchantment == Enchantments.FORTUNE);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    ToolBuilder.addEnchantment(rootCompound, Enchantments.SILK_TOUCH);

    ToolNBT toolData = TagUtil.getToolStats(rootCompound);
    toolData.speed = Math.max(1f, toolData.speed - 3f);
    toolData.attack = Math.max(1f, toolData.attack - 3f);

    TagUtil.setToolTag(rootCompound, toolData.get());
  }
}
