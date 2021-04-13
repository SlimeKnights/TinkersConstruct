package slimeknights.tconstruct.shared;

import slimeknights.tconstruct.common.config.TConfig;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.utils.TagUtil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class PlayerDataEvents {

  private static final String TAG_PLAYER_HAS_BOOK = Util.prefix("spawned_book");

  public static void onPlayerLoggedIn(PlayerEntity player) {
    if (TConfig.common.shouldSpawnWithTinkersBook) {
      throw new RuntimeException("CRAB!");
      // FIXME: PORT
//      CompoundTag playerData = player.getPersistentData(); //TODO: Cardinal Components
//      CompoundTag data = TagUtil.getTagSafe(playerData, "PlayerPersisted");
//
//      if (!data.getBoolean(TAG_PLAYER_HAS_BOOK)) {
//        player.giveItemStack(new ItemStack(TinkerCommons.book.get()));
//        ItemHandlerHelper.giveItemToPlayer(event.getPlayer(), new ItemStack(TinkerCommons.book.get()));
//        data.putBoolean(TAG_PLAYER_HAS_BOOK, true);
//        playerData.put("PlayerPersisted", data);
//      }
    }
  }
}
