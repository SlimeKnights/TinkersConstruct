package slimeknights.tconstruct.tools.traits;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitLightweight extends AbstractTrait {

  private final float bonus = 0.1f;

  public TraitLightweight() {
    super("lightweight", 0x00ff00);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    super.applyEffect(rootCompound, modifierTag);

    // add the attack speed boost
    ToolNBT data = TagUtil.getToolStats(rootCompound);

    // apply using the base value of 1.0f, as otherwise this will apply twice on tools with multiple lightweight parts
    data.attackSpeedMultiplier = 1.0f + bonus;

    TagUtil.setToolTag(rootCompound, data.get());


    if(TinkerUtil.hasCategory(rootCompound, Category.LAUNCHER)) {
      ProjectileLauncherNBT launcherData = new ProjectileLauncherNBT(TagUtil.getToolTag(rootCompound));
      launcherData.drawSpeed += launcherData.drawSpeed * bonus;
      TagUtil.setToolTag(rootCompound, launcherData.get());
    }
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    // 10% bonus speed
    event.setNewSpeed(event.getNewSpeed() * (1 + bonus));
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getModifierIdentifier());

    return ImmutableList.of(Util.translateFormatted(loc, Util.dfPercent.format(bonus)));
  }
}
