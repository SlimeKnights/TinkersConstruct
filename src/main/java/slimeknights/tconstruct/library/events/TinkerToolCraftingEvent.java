package slimeknights.tconstruct.library.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.tables.block.entity.table.TinkerStationBlockEntity;

/**
 * fire on forge bus
 *
 * @see TinkerStationBlockEntity#calcResult(Player)
 * @author TT432
 */
@AllArgsConstructor
@Getter
public class TinkerToolCraftingEvent extends Event {
  /** crafting player */
  private @Nullable Player player;
  /** crafting tool */
  private ItemStack tool;
  private boolean isClient;
}
