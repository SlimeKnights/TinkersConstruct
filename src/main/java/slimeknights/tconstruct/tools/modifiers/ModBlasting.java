package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.ListIterator;

import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class ModBlasting extends ModifierTrait {

  public ModBlasting() {
    super("blasting", 0xffaa23, 3, 0);

    ListIterator<ModifierAspect> iter = aspects.listIterator();
    while(iter.hasNext()) {
      if(iter.next() == ModifierAspect.freeModifier) {
        iter.set(new ModifierAspect.FreeFirstModifierAspect(this, 1));
      }
    }

    MinecraftForge.EVENT_BUS.register(this);
  }

  private int getLevel(ItemStack tool) {
    return ModifierNBT.readInteger(TinkerUtil.getModifierTag(tool, getModifierIdentifier())).level;
  }

  @Override
  public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
    World world = event.getEntityPlayer().getEntityWorld();
    // target speed
    float speed = ToolHelper.getActualMiningSpeed(tool);
    int level = getLevel(tool);

    // mitigate block hardness
    float hardness = event.getState().getBlockHardness(world, event.getPos());
    speed *= hardness;

    if(level > 2) {
      speed /= 1.5f;
    }
    else if(level > 1) {
      speed /= 5f;
    }
    else {
      speed /= 10f;
    }

    float weight1 = (float)level/(float)maxLevel;
    float weight2 = 1f - (float)level/(float)maxLevel;

    // we weight the speed depending on how much the current level is. So 0 = full old speed, 10 = full new speed, 5 = in the middle
    float totalSpeed = speed * weight1 + event.getOriginalSpeed() * weight2;


    event.setNewSpeed(totalSpeed);
  }

  private float getBlockDestroyChange(ItemStack tool) {
    float level = getLevel(tool);
    float chancePerLevel = 1f/(float)maxLevel;
    return 1f - level * chancePerLevel;
  }

  @Override
  public void blockHarvestDrops(ItemStack tool, BlockEvent.HarvestDropsEvent event) {
    float chance = getBlockDestroyChange(tool);
    event.setDropChance(event.getDropChance() * chance);
  }
}
