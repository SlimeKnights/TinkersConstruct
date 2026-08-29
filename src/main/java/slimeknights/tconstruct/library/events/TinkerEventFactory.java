package slimeknights.tconstruct.library.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.Nullable;

/**
 * @author TT432
 */
public class TinkerEventFactory {
  private static final IEventBus BUS = MinecraftForge.EVENT_BUS;

  private static IEventBus bus() {
    return BUS;
  }

  public static ItemStack onTinkerToolCrafting(@Nullable Player player, ItemStack tool, boolean isClient) {
    TinkerToolCraftingEvent event = new TinkerToolCraftingEvent(player, tool, isClient);
    bus().post(event);
    return event.getTool();
  }
}
