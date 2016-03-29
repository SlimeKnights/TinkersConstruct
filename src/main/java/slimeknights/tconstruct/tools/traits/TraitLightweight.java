package slimeknights.tconstruct.tools.traits;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitLightweight extends AbstractTrait {

  private final float bonus = 0.1f;

  public TraitLightweight() {
    super("lightweight", 0x00ff00);
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    // 10% bonus speed
    event.setNewSpeed(event.getNewSpeed() * (1 + bonus));
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getModifierIdentifier());

    return ImmutableList.of(Util.translateFormatted(loc, AbstractMaterialStats.dfPercent.format(bonus)));
  }
}
