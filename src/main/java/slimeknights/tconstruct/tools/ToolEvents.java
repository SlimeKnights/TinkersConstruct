package slimeknights.tconstruct.tools;

import com.google.common.collect.Sets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.KilledByPlayer;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChanceWithLooting;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Random;
import java.util.Set;

import slimeknights.tconstruct.library.capability.projectile.CapabilityTinkerProjectile;
import slimeknights.tconstruct.library.capability.projectile.ITinkerProjectile;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.tools.modifiers.ModMendingMoss;

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
  public void onLootTableLoad(LootTableLoadEvent event) {
    // wither skellies drop necrotic bones
    if(event.getName().equals(LootTableList.ENTITIES_WITHER_SKELETON)) {

      LootCondition[] lootConditions = new LootCondition[0];

      LootEntry entry = new LootEntryItem(TinkerCommons.matNecroticBone.getItem(),
                                          1,
                                          0,
                                          new LootFunction[]{new SetMetadata(lootConditions, new RandomValueRange(TinkerCommons.matNecroticBone.getMetadata()))},
                                          lootConditions,
                                          "necrotic_bone");
      event.getTable().addPool(new LootPool(new LootEntry[]{entry},
                                            new LootCondition[]{
                                                new KilledByPlayer(false),
                                                new RandomChanceWithLooting(0.07f, 0.05f)
                                            },
                                            new RandomValueRange(1),
                                            new RandomValueRange(0),
                                            "necrotic_bone"));
    }
  }

  @SubscribeEvent
  public void onInteract(PlayerInteractEvent.RightClickBlock event) {
    // does the player clicks on an echanting table with moss with 5 levels?
    if(ItemStack.areItemsEqual(event.getItemStack(), TinkerCommons.matMoss)) {
      if(event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.BOOKSHELF) {
        if(event.getEntityPlayer().experienceLevel >= ModMendingMoss.MENDING_MOSS_LEVELS) {
          // convert moss to mending moss
          EntityPlayer player = event.getEntityPlayer();
          player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);

          if(!event.getWorld().isRemote) {
            event.getItemStack().stackSize--;
            player.removeExperienceLevel(ModMendingMoss.MENDING_MOSS_LEVELS);
            ItemHandlerHelper.giveItemToPlayer(player, TinkerCommons.matMendingMoss.copy());

            event.setUseBlock(Event.Result.DENY);
            event.setUseItem(Event.Result.DENY);
            event.setCanceled(true);
          }
        }
      }
    }
  }

  @SubscribeEvent
  public void onLooting(LootingLevelEvent event) {
    // ensure looting is taken into account for projectiles
    Entity projectile = event.getDamageSource().getEntity();
    if(projectile == null || !projectile.hasCapability(CapabilityTinkerProjectile.PROJECTILE_CAPABILITY, null)) {
      return;
    }

    ITinkerProjectile tinkerProjectile = projectile.getCapability(CapabilityTinkerProjectile.PROJECTILE_CAPABILITY, null);
    ItemStack item = tinkerProjectile.getItemStack();
    if(item != null) {
      int level = TinkerTools.modLuck.getLuckLevel(item);
      if(level > 0) {
        event.setLootingLevel(level);
      }
    }
  }
}
