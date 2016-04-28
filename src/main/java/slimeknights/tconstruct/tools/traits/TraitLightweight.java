package slimeknights.tconstruct.tools.traits;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

public class TraitLightweight extends AbstractTrait {

  private final float bonus = 0.1f;

  public TraitLightweight() {
    super("lightweight", 0x00ff00);
  }
  
  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    super.applyEffect(rootCompound, modifierTag);

    // add the attack speed boost
    float speedBoost = 1 + bonus;
    ToolNBT toolData = TagUtil.getOriginalToolStats(rootCompound);
    
    // make sure the original speed is preserved, in case we want tool based speed boost
    speedBoost *= toolData.attackSpeed;

    NBTTagCompound tag = TagUtil.getToolTag(rootCompound);
    tag.setFloat(Tags.ATTACKSPEED, speedBoost);
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
