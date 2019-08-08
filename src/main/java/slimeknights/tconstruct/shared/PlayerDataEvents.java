package slimeknights.tconstruct.shared;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.utils.TagUtil;

public class PlayerDataEvents {

  public static final String TAG_PLAYER_HAS_BOOK = Util.prefix("spawned_book");

  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (Config.spawnWithBook) {
      CompoundNBT playerData = event.getPlayer().getEntityData();
      CompoundNBT data = TagUtil.getTagSafe(playerData, PlayerEntity.PERSISTED_NBT_TAG);

      if (!data.getBoolean(TAG_PLAYER_HAS_BOOK)) {
        ItemHandlerHelper.giveItemToPlayer(event.getPlayer(), new ItemStack(TinkerCommons.book));
        data.putBoolean(TAG_PLAYER_HAS_BOOK, true);
        playerData.put(PlayerEntity.PERSISTED_NBT_TAG, data);
      }
    }
  }
}
