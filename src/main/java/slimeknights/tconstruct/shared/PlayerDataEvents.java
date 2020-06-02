package slimeknights.tconstruct.shared;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.items.CommonItems;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.utils.TagUtil;

@Mod.EventBusSubscriber(modid = TConstruct.modID)
public class PlayerDataEvents {

  private static final String TAG_PLAYER_HAS_BOOK = Util.prefix("spawned_book");

  @SubscribeEvent
  public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (Config.COMMON.shouldSpawnWithTinkersBook.get()) {
      CompoundNBT playerData = event.getPlayer().getPersistentData();
      CompoundNBT data = TagUtil.getTagSafe(playerData, PlayerEntity.PERSISTED_NBT_TAG);

      if (!data.getBoolean(TAG_PLAYER_HAS_BOOK)) {
        ItemHandlerHelper.giveItemToPlayer(event.getPlayer(), new ItemStack(CommonItems.book.get()));
        data.putBoolean(TAG_PLAYER_HAS_BOOK, true);
        playerData.put(PlayerEntity.PERSISTED_NBT_TAG, data);
      }
    }
  }

  private PlayerDataEvents() {}
}
