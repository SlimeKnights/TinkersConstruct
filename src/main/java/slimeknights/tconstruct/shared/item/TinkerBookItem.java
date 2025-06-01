package slimeknights.tconstruct.shared.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.item.LecternBookItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.book.TinkerBook;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.library.tools.capability.inventory.InventorySlotMenuModule.isValidContainer;

public class TinkerBookItem extends LecternBookItem {
  private static final Component CLICK_TO_OPEN = TConstruct.makeTranslation("item", "book.click_to_open").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC);

  private final BookType bookType;
  public TinkerBookItem(Properties props, BookType bookType) {
    super(props);
    this.bookType = bookType;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
    // if the stack is in the player inventory, show the right click to open tooltip
    if (world != null && world.isClientSide) {
      Player player = SafeClientAccess.getPlayer();
      if (player != null && isValidContainer(player.containerMenu)) {
        Inventory inventory = player.getInventory();
        if (inventory.items.contains(stack) || inventory.offhand.contains(stack)) {
          tooltip.add(CLICK_TO_OPEN);
        }
      }
    }
    super.appendHoverText(stack, world, tooltip, flagIn);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (world.isClientSide) {
      TinkerBook.getBook(bookType).openGui(hand, stack);
    }
    return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
  }

  @Override
  public void openLecternScreenClient(BlockPos pos, ItemStack stack) {
    TinkerBook.getBook(bookType).openGui(pos, stack);
  }

  @Override
  public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack held, Slot slot, ClickAction action, Player player, SlotAccess access) {
    // on right-clicking the book with empty held, if this container allows we close and reopen the book page
    if (action == ClickAction.SECONDARY && held.isEmpty() && slot.container == player.getInventory() && slot.allowModification(player) && isValidContainer(player.containerMenu)) {
      if (player.level().isClientSide) {
        player.containerMenu.resumeRemoteUpdates();
        player.closeContainer();
        TinkerBook.getBook(bookType).openGui(slot.getSlotIndex(), stack);
      }
      return true;
    }
    return false;
  }

  /** Simple enum to allow selecting the book on the client */
  public enum BookType {
    MATERIALS_AND_YOU,
    PUNY_SMELTING,
    MIGHTY_SMELTING,
    TINKERS_GADGETRY,
    FANTASTIC_FOUNDRY,
    ENCYCLOPEDIA
  }
}
