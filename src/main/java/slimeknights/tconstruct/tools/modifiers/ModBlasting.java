package slimeknights.tconstruct.tools.modifiers;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.List;
import java.util.ListIterator;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.IToolMod;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTraits;

public class ModBlasting extends ModifierTrait {

  public ModBlasting() {
    super("blasting", 0xffaa23, 3, 0);

    ListIterator<ModifierAspect> iter = aspects.listIterator();
    while(iter.hasNext()) {
      if(iter.next() == ModifierAspect.freeModifier) {
        iter.set(new ModifierAspect.FreeFirstModifierAspect(this, 1));
      }
    }

    addAspects(ModifierAspect.harvestOnly);

    MinecraftForge.EVENT_BUS.register(this);
  }

  @Override
  public boolean canApplyTogether(Enchantment enchantment) {
    return enchantment != Enchantments.SILK_TOUCH
           && enchantment != Enchantments.LOOTING
           && enchantment != Enchantments.FORTUNE;
  }

  @Override
  public boolean canApplyTogether(IToolMod toolmod) {
    String id = toolmod.getIdentifier();
    return !id.equals(TinkerModifiers.modLuck.getModifierIdentifier())
           && !id.equals(TinkerModifiers.modSilktouch.getIdentifier())
           && !id.equals(TinkerTraits.squeaky.getIdentifier())
           && !id.equals(TinkerTraits.autosmelt.getIdentifier());
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
    if(hardness <= 0f) {
      // hardness 0 -> already instabreaks. otherwise we set speed to 0
      return;
    }

    speed *= hardness;

    if(level > 2) {
      speed /= 1.1f;
    }
    else if(level > 1) {
      speed /= 5f;
    }
    else {
      speed /= 10f;
    }

    float weight1 = (float) level / (float) maxLevel;
    float weight2 = 1f - (float) level / (float) maxLevel;

    // we weight the speed depending on how much the current level is. So 0 = full old speed, 10 = full new speed, 5 = in the middle
    float totalSpeed = speed * weight1 + event.getOriginalSpeed() * weight2;

    event.setNewSpeed(totalSpeed);
  }

  private float getBlockDestroyChange(ItemStack tool) {
    float level = getLevel(tool);
    float chancePerLevel = 1f / maxLevel;
    return level * chancePerLevel;
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    EnumParticleTypes particleType = random.nextInt(20) == 0 ? EnumParticleTypes.EXPLOSION_LARGE : EnumParticleTypes.EXPLOSION_NORMAL;
    world.spawnParticle(particleType, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
  }

  @Override
  public void blockHarvestDrops(ItemStack tool, BlockEvent.HarvestDropsEvent event) {
    float chance = 1f - getBlockDestroyChange(tool);
    event.setDropChance(event.getDropChance() * chance);
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getIdentifier());
    float chance = getBlockDestroyChange(tool);

    return ImmutableList.of(Util.translateFormatted(loc, Util.dfPercent.format(chance)));
  }

  @Override
  public int getPriority() {
    // blasting destroys all the things, higher priority
    return 200;
  }
}
