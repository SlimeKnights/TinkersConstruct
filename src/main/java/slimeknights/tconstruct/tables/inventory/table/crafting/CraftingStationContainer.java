package slimeknights.tconstruct.tables.inventory.table.crafting;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.containers.TableContainerTypes;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.shared.inventory.PersistentCraftingInventory;
import slimeknights.tconstruct.tables.inventory.SideInventoryContainer;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.network.LastRecipePacket;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CraftingStationContainer extends TinkerStationContainer<CraftingStationTileEntity> {

  public static final Logger log = LogManager.getLogger("test");
  private static final int SLOT_RESULT = 0;
  private final PlayerEntity player;
  private final PersistentCraftingInventory craftMatrix;
  private final CraftResultInventory craftResult;

  private ICraftingRecipe lastRecipe;
  private ICraftingRecipe lastLastRecipe;

  public CraftingStationContainer(int id, PlayerInventory inv, CraftingStationTileEntity tileEntity) {
    super(TableContainerTypes.crafting_station.get(), id, inv, tileEntity);

    this.craftResult = new CraftResultInventory();
    this.craftMatrix = new PersistentCraftingInventory(this, tileEntity, 3, 3);
    this.player = inv.player;

    this.addSlot(new FastCraftingResultSlot(this, inv.player, this.craftMatrix, this.craftResult, SLOT_RESULT, 124, 35));
    int i;
    int j;

    for (i = 0; i < 3; ++i) {
      for (j = 0; j < 3; ++j) {
        this.addSlot(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
      }
    }

    // detect te
    TileEntity inventoryTE = null;
    Direction accessDir = null;

    if (this.tile != null) {
      for (Direction dir : Direction.Plane.HORIZONTAL) {
        BlockPos neighbor = this.tile.getPos().offset(dir);
        boolean stationPart = false;

        for (Pair<BlockPos, BlockState> tinkerPos : this.tinkerStationBlocks) {
          if (tinkerPos.getLeft().equals(neighbor)) {
            stationPart = true;
            break;
          }
        }

        if (!stationPart) {
          TileEntity te = this.tile.getWorld().getTileEntity(neighbor);

          if (te != null && !(te instanceof CraftingStationTileEntity)) {
            // if blacklisted, skip checks entirely
            if (blacklisted(te)) {
              continue;
            }

            if (te instanceof IInventory && !((IInventory) te).isUsableByPlayer(player)) {
              continue;
            }

            // try internal access first
            if (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).isPresent()) {
              if (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElseGet(EmptyHandler::new) instanceof IItemHandlerModifiable) {
                inventoryTE = te;
                accessDir = null;
                break;
              }
            }

            // try sided access else
            if (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()).isPresent()) {
              if (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()).orElseGet(EmptyHandler::new) instanceof IItemHandlerModifiable) {
                inventoryTE = te;
                accessDir = dir.getOpposite();
                break;
              }
            }
          }
        }
      }

      if (inventoryTE != null) {
        this.addSubContainer(new SideInventoryContainer(TableContainerTypes.crafting_station.get(), id, inv, inventoryTE, accessDir, -6 - 18 * 6, 8, 6), false);
      }
    }

    this.addInventorySlots();
  }

  public CraftingStationContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, CraftingStationTileEntity.class));
  }

  private boolean blacklisted(TileEntity tileEntity) {
    if (Config.COMMON.craftingStationBlacklist.get().isEmpty()) {
      return false;
    }

    // first, try registry name
    ResourceLocation registryName = TileEntityType.getId(tileEntity.getType());
    if (registryName != null && Config.COMMON.craftingStationBlacklist.get().contains(registryName.toString())) {
      return true;
    }

    // then try class name
    return Config.COMMON.craftingStationBlacklist.get().contains(tileEntity.getClass().getName());
  }

  @Override
  public void setAll(List<ItemStack> p_190896_1_) {
    this.craftMatrix.setDoNotCallUpdates(true);
    super.setAll(p_190896_1_);
    this.craftMatrix.setDoNotCallUpdates(false);
    this.craftMatrix.onCraftMatrixChanged();
  }

  protected void updateCrafting(int windowId, World world, PlayerEntity playerEntity, CraftingInventory craftingInventory, CraftResultInventory resultInventory) {
    if (!world.isRemote) {
      ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) playerEntity;
      ItemStack itemstack = ItemStack.EMPTY;

      Optional<ICraftingRecipe> optional = world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftingInventory, world);

      // if the recipe is no longer valid, update it
      if (this.lastRecipe == null || !this.lastRecipe.matches(craftingInventory, world)) {
        this.lastRecipe = optional.orElse(null);
      }

      // if we have a recipe, fetch its result
      if (this.lastRecipe != null) {
        if (resultInventory.canUseRecipe(world, serverPlayerEntity, this.lastRecipe)) {
          itemstack = this.lastRecipe.getCraftingResult(craftingInventory);
        }
      }

      // set the slot on both sides, client is for display/so the client knows about the recipe
      resultInventory.setInventorySlotContents(SLOT_RESULT, itemstack);

      // update recipe on server
      // we need to sync to all players currently in the inventory
      List<ServerPlayerEntity> relevantPlayers = this.getAllPlayersWithThisContainerOpen(this, serverPlayerEntity.getServerWorld());

      // sync result to all serverside inventories to prevent duplications/recipes being blocked
      // need to do this every time as otherwise taking items of the result causes desync
      this.syncResultToAllOpenWindows(itemstack, relevantPlayers);

      // if the recipe changed, update clients last recipe
      // this also updates the client side display when the recipe is added
      if (this.lastLastRecipe != this.lastRecipe) {
        this.syncRecipeToAllOpenWindows(this.lastRecipe, relevantPlayers);
        this.lastLastRecipe = this.lastRecipe;
      }
    }
  }

  public void onCraftMatrixChanged() {
    this.onCraftMatrixChanged(this.craftMatrix);
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    this.updateCrafting(this.windowId, this.player.world, this.player, this.craftMatrix, this.craftResult);
  }

  private void syncResultToAllOpenWindows(final ItemStack stack, List<ServerPlayerEntity> players) {
    players.forEach(otherPlayer -> {
      otherPlayer.openContainer.putStackInSlot(SLOT_RESULT, stack);
    });
  }

  private void syncRecipeToAllOpenWindows(@Nullable final ICraftingRecipe lastRecipe, List<ServerPlayerEntity> players) {
    players.forEach(otherPlayer -> {
      // safe cast since hasSameContainerOpen does class checks
      ((CraftingStationContainer) otherPlayer.openContainer).lastRecipe = lastRecipe;

      if (lastRecipe != null) {
        TinkerNetwork.getInstance().sendTo(new LastRecipePacket(lastRecipe.getId()), otherPlayer);
      } else {
        TinkerNetwork.getInstance().sendTo(new LastRecipePacket(LastRecipePacket.NO_RECIPE), otherPlayer);
      }
    });
  }

  // server can be gotten from ServerPlayerEntity
  private <T extends TileEntity> List<ServerPlayerEntity> getAllPlayersWithThisContainerOpen(BaseContainer<T> container, ServerWorld server) {
    return server.getPlayers().stream()
      .filter(player -> this.hasSameContainerOpen(container, player))
      .map(player -> (ServerPlayerEntity) player)
      .collect(Collectors.toList());
  }

  private <T extends TileEntity> boolean hasSameContainerOpen(BaseContainer<T> container, PlayerEntity playerToCheck) {
    return playerToCheck instanceof ServerPlayerEntity && playerToCheck.openContainer.getClass().isAssignableFrom(container.getClass()) && this.sameGui((BaseContainer<T>) playerToCheck.openContainer);
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slot) {
    return slot.inventory != this.craftResult && super.canMergeSlot(stack, slot);
  }

  public void updateLastRecipeFromServer(@Nullable ICraftingRecipe recipe) {
    this.lastRecipe = recipe;
    // if no recipe, set to empty to prevent ghost outputs when another player grabs the result
    this.craftResult.setInventorySlotContents(SLOT_RESULT, recipe != null ? recipe.getCraftingResult(craftMatrix) : ItemStack.EMPTY);
  }

  /**
   * @return the starting slot for the player inventory. Present for usage in the JEI crafting station support
   */
  public int getPlayerInventoryStart() {
    return this.playerInventoryStart;
  }

  public CraftingInventory getCraftMatrix() {
    return this.craftMatrix;
  }

  public NonNullList<ItemStack> getRemainingItems() {
    if (this.tile == null) {
      return this.craftMatrix.stackList;
    }

    if (this.lastRecipe != null && this.lastRecipe.matches(this.craftMatrix, this.tile.getWorld())) {
      return this.lastRecipe.getRemainingItems(craftMatrix);
    }

    return this.craftMatrix.stackList;
  }

  @SubscribeEvent
  public static void onCraftingStationGuiOpened(PlayerContainerEvent.Open event) {
    // by default the container does not update after it has been opened.
    // we need it to check its recipe
    if (event.getContainer() instanceof CraftingStationContainer) {
      ((CraftingStationContainer) event.getContainer()).onCraftMatrixChanged();
    }
  }
}
