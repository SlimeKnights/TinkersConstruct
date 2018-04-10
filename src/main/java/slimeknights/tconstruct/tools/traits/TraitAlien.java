package slimeknights.tconstruct.tools.traits;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import java.util.List;

import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;

/**
 * Gives the tool bonus stats on crafting.
 * The bonus stats are distributed over time and are more or less random.
 * The stats that will be rewarded are already designated on the first time the tool is crafted
 */
public class TraitAlien extends TraitProgressiveStats {

  protected static int TICK_PER_STAT = 72;

  protected static int DURABILITY_STEP = 1;
  protected static float SPEED_STEP = 0.007f;
  protected static float ATTACK_STEP = 0.005f;

  public TraitAlien() {
    super("alien", TextFormatting.YELLOW);
  }

  @Override
  public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
    if(entity instanceof FakePlayer || entity.getEntityWorld().isRemote) {
      return;
    }
    // every 3.6 seconds we distribute one stat. This means 1h = 1000 applications
    if(entity.ticksExisted % TICK_PER_STAT > 0) {
      return;
    }

    // we don't update if the player is currently breaking a block because that'd reset it
    if(playerIsBreakingBlock(entity) || (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getActiveItemStack() == tool)) {
      return;
    }

    NBTTagCompound root = TagUtil.getTagSafe(tool);
    StatNBT pool = getPoolLazily(root);
    StatNBT distributed = getBonus(root);
    ToolNBT data = TagUtil.getToolStats(tool);

    // attack
    if(entity.ticksExisted % (TICK_PER_STAT * 3) == 0) {
      if(distributed.attack < pool.attack) {
        data.attack += ATTACK_STEP;
        distributed.attack += ATTACK_STEP;
      }
    }
    // speed
    else if(entity.ticksExisted % (TICK_PER_STAT * 2) == 0) {
      if(distributed.speed < pool.speed) {
        data.speed += SPEED_STEP;
        distributed.speed += SPEED_STEP;
      }
    }
    // durability
    else {
      if(distributed.durability < pool.durability) {
        data.durability += DURABILITY_STEP;
        distributed.durability += DURABILITY_STEP;
      }
    }

    // update tool stats
    TagUtil.setToolTag(root, data.get());
    // update statistics on distributed stats
    setBonus(root, distributed);
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    StatNBT pool = getBonus(TagUtil.getTagSafe(tool));

    return ImmutableList.of(HeadMaterialStats.formatDurability(pool.durability),
                            HeadMaterialStats.formatMiningSpeed(pool.speed),
                            HeadMaterialStats.formatAttack(pool.attack));
  }

  private StatNBT getPoolLazily(NBTTagCompound rootCompound) {
    if(!hasPool(rootCompound)) {
      // ok, we need new stats. Let the fun begin!
      StatNBT data = new StatNBT();

      int statPoints = 800; // we distribute a whopping X points worth of stats!
      for(; statPoints > 0; statPoints--) {
        switch(random.nextInt(3)) {
          // durability
          case 0:
            data.durability += DURABILITY_STEP;
            break;
          // speed
          case 1:
            data.speed += SPEED_STEP;
            break;
          // attack
          case 2:
            data.attack += ATTACK_STEP;
            break;
        }
      }

      setPool(rootCompound, data);
    }
    return getPool(rootCompound);
  }
}
