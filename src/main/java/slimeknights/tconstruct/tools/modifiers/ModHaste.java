package slimeknights.tconstruct.tools.modifiers;

import com.google.common.collect.ImmutableList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.AbstractMaterialStats;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;

public class ModHaste extends ToolModifier {

  private final int max;

  public ModHaste(int max) {
    super("haste", 0x910000);

    this.max = max;
    
    addAspects(new ModifierAspect.MultiAspect(this, 5, max, 1));
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    ModifierNBT.IntegerNBT modData = ModifierNBT.readInteger(modifierTag);

    boolean harvest = false;
    
    for(Category category : TagUtil.getCategories(rootCompound)) {
      if(category == Category.HARVEST) harvest = true;
    }

    ToolNBT data = TagUtil.getToolStats(rootCompound);
    int level = modData.current / max;
    
    // only boost mining speed if we have a harvest tool
    if(harvest) {
      float speed = data.speed;
      for(int count = modData.current; count > 0; count--) {
        if(speed <= 10f) {
          // linear scaling from 0.08 to 0.06 per piece till 10 miningspeed
          speed += 0.15f - 0.05f * speed / 10f;
        }
        else if(speed <= 20f) {
          speed += 0.1f - 0.05 * speed / 20f;
        }
        else {
          speed += 0.05;
        }
      }
  
      // each full level gives a flat 0.5 bonus, not influenced by dimishing returns
      speed += level * 0.5f;
  
      // save it to the tool
      data.speed = speed;
    }
    
    // attack speed: each total level adds 0.2 to the modifier, though individual redstone piece above the level add 0.004 each
    data.attackSpeedMultiplier += getSpeedBonus(modData);

    TagUtil.setToolTag(rootCompound, data.get());
  }

  protected float getSpeedBonus(ModifierNBT.IntegerNBT modData) {
    return 0.2f * modData.current / max;
  }


  // don't allow on projectiles
  @Override
  protected boolean canApplyCustom(ItemStack stack) throws TinkerGuiException {
    return !((ToolCore)stack.getItem()).hasCategory(Category.NO_MELEE);
  }

  @Override
  public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
    return getLeveledTooltip(modifierTag, detailed);
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getIdentifier());
    float bonus = getSpeedBonus(ModifierNBT.readInteger(modifierTag));
    return ImmutableList.of(Util.translateFormatted(loc, AbstractMaterialStats.dfPercent.format(bonus)));
  }
}
