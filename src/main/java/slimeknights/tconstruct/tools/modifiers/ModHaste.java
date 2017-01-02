package slimeknights.tconstruct.tools.modifiers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.Set;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
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

    Set<Category> categories = ImmutableSet.copyOf(TagUtil.getCategories(rootCompound));
    boolean harvest = categories.contains(Category.HARVEST);
    boolean weapon = categories.contains(Category.WEAPON);
    boolean launcher = categories.contains(Category.LAUNCHER);

    ToolNBT data = TagUtil.getToolStats(rootCompound);
    int level = modData.current / max;

    // only boost mining speed if we have a harvest tool
    if(harvest) {
      applyHarvestBoost(modData, data, level);
    }

    // attack speed: each total level adds 0.2 to the modifier, though individual redstone piece above the level add 0.004 each
    // so in short: 0.004 per redstone
    if(weapon) {
      data.attackSpeedMultiplier += getSpeedBonus(modData);
    }

    TagUtil.setToolTag(rootCompound, data.get());

    // bow speed:
    if(launcher) {
      ProjectileLauncherNBT launcherData = new ProjectileLauncherNBT(TagUtil.getToolTag(rootCompound));
      launcherData.drawSpeed += launcherData.drawSpeed * getDrawspeedBonus(modData);
      TagUtil.setToolTag(rootCompound, launcherData.get());
    }
  }

  protected void applyHarvestBoost(ModifierNBT.IntegerNBT modData, ToolNBT data, int level) {
    float speed = data.speed;
    final float step1 = 15f;
    final float step2 = 25f;
    for(int count = modData.current; count > 0; count--) {
      if(speed <= step1) {
        speed += 0.15f - 0.05f * speed / step1;
      }
      else if(speed <= step2) {
        speed += 0.1f - 0.05 * (speed-step1) / (step2-step1);
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

  protected float getSpeedBonus(ModifierNBT.IntegerNBT modData) {
    return 0.2f * modData.current / max;
  }

  protected float getDrawspeedBonus(ModifierNBT.IntegerNBT modData) {
    return 0.1f * modData.current / max;
  }

  // don't allow on projectiles
  @Override
  protected boolean canApplyCustom(ItemStack stack) throws TinkerGuiException {
    return !((ToolCore) stack.getItem()).hasCategory(Category.NO_MELEE);
  }

  @Override
  public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
    return getLeveledTooltip(modifierTag, detailed);
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getIdentifier());

    Set<Category> categories = ImmutableSet.copyOf(TagUtil.getCategories(TagUtil.getTagSafe(tool)));
    boolean weapon = categories.contains(Category.WEAPON);
    boolean launcher = categories.contains(Category.LAUNCHER);

    ImmutableList.Builder<String> builder = ImmutableList.builder();

    if(weapon) {
      float bonus = getSpeedBonus(ModifierNBT.readInteger(modifierTag));
      builder.add(Util.translateFormatted(loc, Util.dfPercent.format(bonus)));
    }
    if(launcher) {
      float bonus = getDrawspeedBonus(ModifierNBT.readInteger(modifierTag));
      builder.add(Util.translateFormatted(loc, Util.dfPercent.format(bonus)));
    }
    return builder.build();
  }
}
