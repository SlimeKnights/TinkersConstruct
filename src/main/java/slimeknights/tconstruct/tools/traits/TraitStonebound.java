package slimeknights.tconstruct.tools.traits;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitStonebound extends AbstractTrait {

  public TraitStonebound() {
    super("stonebound", EnumChatFormatting.DARK_GRAY);
  }

  private double calcBonus(ItemStack tool) {
    int durability = ToolHelper.getCurrentDurability(tool);
    int maxDurability = ToolHelper.getDurabilityStat(tool);

    // old tcon stonebound formula
    return Math.log((maxDurability - durability) / 72d + 1d) * 2;
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    if(ToolHelper.isToolEffective2(tool, event.state)) {

      event.newSpeed += calcBonus(tool);
    }
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getModifierIdentifier());

    return ImmutableList.of(Util.translateFormatted(loc, AbstractMaterialStats.df.format(calcBonus(tool))));
  }
}
