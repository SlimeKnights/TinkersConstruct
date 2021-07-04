package slimeknights.tconstruct.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.world.TinkerWorld;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Mod.EventBusSubscriber(modid = TConstruct.MOD_ID)
public class CommonsEvents {

  // Slimy block jump stuff
  @SubscribeEvent
  static void onLivingJump(LivingEvent.LivingJumpEvent event) {
    if (event.getEntity() == null) {
      return;
    }

    // check if we jumped from a slime block
    BlockPos pos = new BlockPos(event.getEntity().getPosX(), event.getEntity().getPosY(), event.getEntity().getPosZ());
    if (event.getEntity().getEntityWorld().isAirBlock(pos)) {
      pos = pos.down();
    }
    BlockState state = event.getEntity().getEntityWorld().getBlockState(pos);
    Block block = state.getBlock();

    if (TinkerWorld.congealedSlime.contains(block)) {
      bounce(event.getEntity(), 0.25f);
    } else if (TinkerWorld.slimeDirt.contains(block) || TinkerWorld.vanillaSlimeGrass.contains(block) || TinkerWorld.earthSlimeGrass.contains(block) || TinkerWorld.skySlimeGrass.contains(block) || TinkerWorld.enderSlimeGrass.contains(block) || TinkerWorld.ichorSlimeGrass.contains(block)) {
      bounce(event.getEntity(), 0.06f);
    }
  }

  private static void bounce(Entity entity, float amount) {
    entity.setMotion(entity.getMotion().add(0.0D, (double) amount, 0.0D));
    entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 0.5f + amount, 1f);
  }

  /** Tag for players who have received the book */
  private static final String TAG_PLAYER_HAS_BOOK = TConstruct.prefix("spawned_book");

  @SubscribeEvent
  static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (Config.COMMON.shouldSpawnWithTinkersBook.get()) {
      CompoundNBT playerData = event.getPlayer().getPersistentData();
      CompoundNBT data = playerData.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
      if (!data.getBoolean(TAG_PLAYER_HAS_BOOK)) {
        ItemHandlerHelper.giveItemToPlayer(event.getPlayer(), new ItemStack(TinkerCommons.materialsAndYou.get()));
        data.putBoolean(TAG_PLAYER_HAS_BOOK, true);
        playerData.put(PlayerEntity.PERSISTED_NBT_TAG, data);
      }
    }
  }
}
