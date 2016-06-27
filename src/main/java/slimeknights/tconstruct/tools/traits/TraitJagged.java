package slimeknights.tconstruct.tools.traits;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitJagged extends AbstractTrait {

  public TraitJagged() {
    super("jagged", TextFormatting.AQUA);
  }

  private double calcBonus(ItemStack tool) {
    int durability = ToolHelper.getCurrentDurability(tool);
    int maxDurability = ToolHelper.getMaxDurability(tool);

    // old tcon jagged formula
    return Math.log((maxDurability - durability) / 72d + 1d) * 2;
  }

  @Override
  public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    newDamage += calcBonus(tool);

    return super.damage(tool, player, target, damage, newDamage, isCritical);
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getModifierIdentifier());

    return ImmutableList.of(Util.translateFormatted(loc, Util.df.format(calcBonus(tool))));
  }
}
