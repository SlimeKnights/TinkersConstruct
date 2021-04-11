package slimeknights.tconstruct.shared;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
//import slimeknights.tconstruct.library.utils.TagUtil;
//import slimeknights.tconstruct.tools.common.entity.EntityArrow;
//import slimeknights.tconstruct.tools.tools.Pickaxe;

public final class AchievementEvents {

  private static final String ADVANCEMENT_STORY_ROOT = "minecraft:story/root";
  private static final String ADVANCEMENT_STONE_PICK = "minecraft:story/upgrade_tools";
  private static final String ADVANCEMENT_IRON_PICK = "minecraft:story/iron_tools";
  private static final String ADVANCEMENT_SHOOT_ARROW = "minecraft:adventure/shoot_arrow";

  public static void onCraft(PlayerEntity player, ItemStack crafting) {
    if (player == null || !(player instanceof ServerPlayerEntity) || crafting.isEmpty()) {
      return;
    }
    ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
    Item item = crafting.getItem();
    if (item instanceof BlockItem && ((BlockItem) item).getBlock() == Blocks.CRAFTING_TABLE) {
      grantAdvancement(playerMP, ADVANCEMENT_STORY_ROOT);
    }
    // fire vanilla pickaxe crafting when crafting tinkers picks (hammers also count for completeness sake)
    /*if (item instanceof Pickaxe) {
      int harvestLevel = TagUtil.getToolStats(event.getCrafting()).harvestLevel;
      if (harvestLevel > 0) {
        grantAdvancement(playerMP, ADVANCEMENT_STONE_PICK);
      }
      if (harvestLevel > 1) {
        grantAdvancement(playerMP, ADVANCEMENT_IRON_PICK);
      }
    }*/
  }

  //TODO: do this (AlphaMode)
  public static void onDamageEntity(DamageSource source) {
    if (source.isProjectile()) {// && source.getImmediateSource() instanceof EntityArrow) {
      grantAdvancement((ServerPlayerEntity) source.getAttacker(), ADVANCEMENT_SHOOT_ARROW);
    }
  }

  private static void grantAdvancement(ServerPlayerEntity playerMP, String advancementResource) {
    Advancement advancement = playerMP.getServer().getAdvancementLoader().get(new Identifier(advancementResource));
    if (advancement != null) {
      AdvancementProgress advancementProgress = playerMP.getAdvancementTracker().getProgress(advancement);
      if (!advancementProgress.isDone()) {
        // we use playerAdvancements.grantCriterion instead of progress.grantCriterion for the visibility stuff and toasts
        advancementProgress.getUnobtainedCriteria().forEach(criterion -> playerMP.getAdvancementTracker().grantCriterion(advancement, criterion));
      }
    }
  }
}
