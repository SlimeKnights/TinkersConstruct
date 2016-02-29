package slimeknights.tconstruct.tools.traits;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

import java.util.ListIterator;

import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitAutosmelt extends AbstractTrait {

  public TraitAutosmelt() {
    super("autosmelt", 0xff5500);
  }

  @Override
  public boolean canApplyTogether(Enchantment enchantment) {
    return enchantment != Enchantment.silkTouch;
  }

  @Override
  public void blockHarvestDrops(ItemStack tool, BlockEvent.HarvestDropsEvent event) {
    if(ToolHelper.isToolEffective(tool, event.state)) {
      // go through the drops and replace them with their furnace'd variant if applicable
      ListIterator<ItemStack> iter = event.drops.listIterator();
      while(iter.hasNext()) {
        ItemStack drop = iter.next();
        ItemStack smelted = FurnaceRecipes.instance().getSmeltingResult(drop);
        if(smelted != null) {
          smelted = smelted.copy();
          smelted.stackSize = drop.stackSize;
          int fortune = EnchantmentHelper.getFortuneModifier(event.harvester);
          if(Config.autosmeltlapis && fortune > 0) {
            smelted.stackSize *= random.nextInt(fortune + 1) + 1;
          }
          iter.set(smelted);
        }
      }
    }
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    if(world.isRemote && wasEffective) {
      for(int i = 0; i < 3; i++) {
        world.spawnParticle(EnumParticleTypes.FLAME,
                            pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(),
                            0.0D, 0.0D, 0.0D);
      }
    }
  }
}
