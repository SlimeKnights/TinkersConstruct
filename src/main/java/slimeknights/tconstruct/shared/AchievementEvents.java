package slimeknights.tconstruct.shared;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.common.entity.EntityArrow;
import slimeknights.tconstruct.tools.tools.Pickaxe;

public class AchievementEvents {

  private static final String ADVANCEMENT_STORY_ROOT = "minecraft:story/root";
  private static final String ADVANCEMENT_STONE_PICK = "minecraft:story/upgrade_tools";
  private static final String ADVANCEMENT_IRON_PICK = "minecraft:story/iron_tools";
  public static final String ADVANCEMENT_SHOOT_ARROW = "minecraft:adventure/shoot_arrow";

  @SubscribeEvent
  public void onCraft(PlayerEvent.ItemCraftedEvent event) {
    if(event.player == null ||
       event.player instanceof FakePlayer  ||
       !(event.player instanceof EntityPlayerMP) ||
       event.crafting.isEmpty()) {
      return;
    }
    EntityPlayerMP playerMP = (EntityPlayerMP) event.player;
    Item item = event.crafting.getItem();
    if(item instanceof ItemBlock && ((ItemBlock) item).getBlock() == Blocks.CRAFTING_TABLE) {
      grantAdvancement(playerMP, ADVANCEMENT_STORY_ROOT);
    }
    // fire vanilla pickaxe crafting when crafting tinkers picks (hammers also count for completeness sake)
    if(item instanceof Pickaxe) {
      int harvestLevel = TagUtil.getToolStats(event.crafting).harvestLevel;
      if(harvestLevel > 0) {
        grantAdvancement(playerMP, ADVANCEMENT_STONE_PICK);
      }
      if(harvestLevel > 1) {
        grantAdvancement(playerMP, ADVANCEMENT_IRON_PICK);
      }
    }
  }

  @SubscribeEvent
  public void onDamageEntity(LivingHurtEvent event) {
    DamageSource source = event.getSource();
    if(source.isProjectile()
       && !(source.getTrueSource() instanceof FakePlayer)
       && source.getTrueSource() instanceof EntityPlayerMP
       && source.getImmediateSource() instanceof EntityArrow) {
        grantAdvancement((EntityPlayerMP) source.getTrueSource(), ADVANCEMENT_SHOOT_ARROW);
    }
  }

  private void grantAdvancement(EntityPlayerMP playerMP, String advancementResource) {
    Advancement advancement = playerMP.getServer().getAdvancementManager().getAdvancement(new ResourceLocation(advancementResource));
    if(advancement != null) {
      AdvancementProgress advancementProgress = playerMP.getAdvancements().getProgress(advancement);
      if(!advancementProgress.isDone()) {
        // we use playerAdvancements.grantCriterion instead of progress.grantCriterion for the visibility stuff and toasts
        advancementProgress.getRemaningCriteria().forEach(criterion -> playerMP.getAdvancements().grantCriterion(advancement, criterion));
      }
    }
  }
}
