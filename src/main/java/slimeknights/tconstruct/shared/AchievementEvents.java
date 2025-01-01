package slimeknights.tconstruct.shared;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;
//import slimeknights.tconstruct.library.utils.TagUtil;
//import slimeknights.tconstruct.tools.common.entity.EntityArrow;
//import slimeknights.tconstruct.tools.tools.Pickaxe;

// TODO: reevaluate
@Mod.EventBusSubscriber(modid = TConstruct.MOD_ID)
public final class AchievementEvents {

  private static final String ADVANCEMENT_STORY_ROOT = "minecraft:story/root";
  private static final String ADVANCEMENT_STONE_PICK = "minecraft:story/upgrade_tools";
  private static final String ADVANCEMENT_IRON_PICK = "minecraft:story/iron_tools";
  private static final String ADVANCEMENT_SHOOT_ARROW = "minecraft:adventure/shoot_arrow";

  @SubscribeEvent
  public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
    if (event.getEntity() == null || event.getEntity() instanceof FakePlayer || !(event.getEntity() instanceof ServerPlayer playerMP) || event.getCrafting().isEmpty()) {
      return;
    }
    Item item = event.getCrafting().getItem();
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

  @SubscribeEvent
  public static void onDamageEntity(LivingHurtEvent event) {
    DamageSource source = event.getSource();
    if (source.is(DamageTypeTags.IS_PROJECTILE) && source.getEntity() instanceof ServerPlayer player && !(source.getEntity() instanceof FakePlayer)) {// && source.getImmediateSource() instanceof EntityArrow) {
      grantAdvancement(player, ADVANCEMENT_SHOOT_ARROW);
    }
  }

  private static void grantAdvancement(ServerPlayer playerMP, String advancementResource) {
    MinecraftServer server = playerMP.getServer();
    if (server != null) {
      Advancement advancement = server.getAdvancements().getAdvancement(new ResourceLocation(advancementResource));
      if (advancement != null) {
        AdvancementProgress advancementProgress = playerMP.getAdvancements().getOrStartProgress(advancement);
        if (!advancementProgress.isDone()) {
          // we use playerAdvancements.grantCriterion instead of progress.grantCriterion for the visibility stuff and toasts
          advancementProgress.getRemainingCriteria().forEach(criterion -> playerMP.getAdvancements().award(advancement, criterion));
        }
      }
    }
  }

  private AchievementEvents() {}
}
