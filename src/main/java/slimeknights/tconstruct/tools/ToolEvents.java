package slimeknights.tconstruct.tools;

import com.google.common.collect.Sets;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;
import java.util.Set;

import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.tools.events.TinkerToolEvent;

public class ToolEvents {

  private static final Random random = new Random();

  public final static Set<ToolCore> smallTools = Sets.newHashSet();

  // Extra width/height modifier management
  @SubscribeEvent
  public void onExtraBlockBreak(TinkerToolEvent.ExtraBlockBreak event) {
    if(TinkerTools.modHarvestWidth == null || TinkerTools.modHarvestHeight == null) {
      return;
    }

    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(event.itemStack);
    boolean width = false;
    boolean height = false;
    for(int i = 0; i < modifiers.tagCount(); i++) {
      String modId = modifiers.getStringTagAt(i);
      if(modId.equals(TinkerTools.modHarvestWidth.getIdentifier())) {
        width = true;
      }
      else if(modId.equals(TinkerTools.modHarvestHeight.getIdentifier())) {
        height = true;
      }
    }

    if(!width && !height) {
      return;
    }

    if(event.tool == TinkerTools.pickaxe ||
       event.tool == TinkerTools.hatchet ||
       event.tool == TinkerTools.shovel) {
      event.width += width ? 1 : 0;
      event.height += height ? 1 : 0;
    }
    else if(event.tool == TinkerTools.mattock) {
      int c = 0;
      if(width) {
        c++;
      }
      if(height) {
        c++;
      }
      event.width += c;
      event.height += c;
    }
    else if(event.tool == TinkerTools.hammer ||
            event.tool == TinkerTools.excavator ||
            event.tool == TinkerTools.lumberAxe) {
      event.width += width ? 2 : 0;
      event.height += height ? 2 : 0;
      //event.distance = 1 + (width ? 1 : 0) + (height ? 1 : 0);
      event.distance = 3;
    }
  }

  @SubscribeEvent
  public void onLivingDrop(LivingDropsEvent event) {
    if(event.getEntityLiving() instanceof EntitySkeleton && event.getSource().getEntity() instanceof EntityPlayer) {
      if(((EntitySkeleton) event.getEntityLiving()).getSkeletonType() == 1) {
        float chance = 0.10f;
        chance += 0.05f + EnchantmentHelper.getLootingModifier((EntityLivingBase) event.getSource().getEntity());
        if(random.nextFloat() < chance) {
          EntityItem entityitem = new EntityItem(event.getEntityLiving().worldObj,
                                                 event.getEntityLiving().posX,
                                                 event.getEntityLiving().posY,
                                                 event.getEntityLiving().posZ,
                                                 TinkerCommons.matNecroticBone.copy());
          entityitem.setDefaultPickupDelay();
          event.getDrops().add(entityitem);
        }
      }
    }
  }
}
