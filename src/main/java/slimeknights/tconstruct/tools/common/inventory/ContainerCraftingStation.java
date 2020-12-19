package slimeknights.tconstruct.tools.common.inventory;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.shared.inventory.InventoryCraftingPersistent;
import slimeknights.tconstruct.tools.common.network.LastRecipeMessage;
import slimeknights.tconstruct.tools.common.tileentity.TileCraftingStation;

// nearly the same as ContainerWorkbench but uses the TileEntities inventory
@Mod.EventBusSubscriber
public class ContainerCraftingStation extends ContainerTinkerStation<TileCraftingStation> {
  public static final Logger log = LogManager.getLogger("test");
  private static final int SLOT_RESULT = 0;
  private final EntityPlayer player;
  private final InventoryCraftingPersistent craftMatrix;
  private final InventoryCraftResult craftResult;

  private IRecipe lastRecipe;
  private IRecipe lastLastRecipe;

  @SubscribeEvent
  public static void onCraftingStationGuiOpened(PlayerContainerEvent.Open event) {
    // by default the container does not update after it has been opened.
    // we need it to check its recipe
    if(event.getContainer() instanceof ContainerCraftingStation) {
      ((ContainerCraftingStation) event.getContainer()).onCraftMatrixChanged();
    }
  }

  public ContainerCraftingStation(InventoryPlayer playerInventory, TileCraftingStation tile) {
    super(tile);

    craftResult = new InventoryCraftResult();
    craftMatrix = new InventoryCraftingPersistent(this, tile, 3, 3);
    player = playerInventory.player;

    this.addSlotToContainer(new SlotCraftingFastWorkbench(this, playerInventory.player, this.craftMatrix, this.craftResult, SLOT_RESULT, 124, 35));
    int i;
    int j;

    for(i = 0; i < 3; ++i) {
      for(j = 0; j < 3; ++j) {
        this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
      }
    }

    // detect te
    TileEntity inventoryTE = null;
    EnumFacing accessDir = null;
    for(EnumFacing dir : EnumFacing.HORIZONTALS) {
      BlockPos neighbor = pos.offset(dir);
      boolean stationPart = false;
      for(Pair<BlockPos, IBlockState> tinkerPos : tinkerStationBlocks) {
        if(tinkerPos.getLeft().equals(neighbor)) {
          stationPart = true;
          break;
        }
      }
      if(!stationPart) {
        TileEntity te = world.getTileEntity(neighbor);
        if(te != null && !(te instanceof TileCraftingStation)) {
          // if blacklisted, skip checks entirely
          if(blacklisted(te.getClass())) {
            continue;
          }
          if(te instanceof IInventory && !((IInventory) te).isUsableByPlayer(player)) {
            continue;
          }

          // try internal access first
          if(te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            if(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) instanceof IItemHandlerModifiable) {
              inventoryTE = te;
              accessDir = null;
              break;
            }
          }
          // try sided access else
          if(te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite())) {
            if(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()) instanceof IItemHandlerModifiable) {
              inventoryTE = te;
              accessDir = dir.getOpposite();
              break;
            }
          }
        }
      }
    }

    if(inventoryTE != null) {
      addSubContainer(new ContainerSideInventory(inventoryTE, accessDir, -6 - 18 * 6, 8, 6), false);
    }

    this.addPlayerInventory(playerInventory, 8, 84);
  }

  private boolean blacklisted(Class<? extends TileEntity> clazz) {
    if(Config.craftingStationBlacklist.isEmpty()) {
      return false;
    }

    // first, try registry name
    ResourceLocation registryName = TileEntity.getKey(clazz);
    if(registryName != null && Config.craftingStationBlacklist.contains(registryName.toString())) {
      return true;
    }

    // then try class name
    if(Config.craftingStationBlacklist.contains(clazz.getName())) {
      return true;
    }

    return false;
  }

  // update crafting
  @Override
  public void setAll(List<ItemStack> p_190896_1_) {
    craftMatrix.setDoNotCallUpdates(true);
    super.setAll(p_190896_1_);
    craftMatrix.setDoNotCallUpdates(false);
    craftMatrix.onCraftMatrixChanged();
  }

  public void onCraftMatrixChanged() {
    this.onCraftMatrixChanged(this.craftMatrix);
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    this.slotChangedCraftingGrid(this.world, this.player, this.craftMatrix, this.craftResult);
  }

  @Override
  protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting inv, InventoryCraftResult result) {
    ItemStack itemstack = ItemStack.EMPTY;

    // if the recipe is no longer valid, update it
    if(lastRecipe == null || !lastRecipe.matches(inv, world)) {
      lastRecipe = CraftingManager.findMatchingRecipe(inv, world);
    }

    // if we have a recipe, fetch its result
    if(lastRecipe != null) {
      itemstack = lastRecipe.getCraftingResult(inv);
    }
    // set the slot on both sides, client is for display/so the client knows about the recipe
    result.setInventorySlotContents(SLOT_RESULT, itemstack);

    // update recipe on server
    if(!world.isRemote) {
      EntityPlayerMP entityplayermp = (EntityPlayerMP) player;

      // we need to sync to all players currently in the inventory
      List<EntityPlayerMP> relevantPlayers = getAllPlayersWithThisContainerOpen(this, entityplayermp.getServerWorld());

      // sync result to all serverside inventories to prevent duplications/recipes being blocked
      // need to do this every time as otherwise taking items of the result causes desync
      syncResultToAllOpenWindows(itemstack, relevantPlayers);

      // if the recipe changed, update clients last recipe
      // this also updates the client side display when the recipe is added
      if(lastLastRecipe != lastRecipe) {
        syncRecipeToAllOpenWindows(lastRecipe, relevantPlayers);
        lastLastRecipe = lastRecipe;
      }
    }
  }

  private void syncResultToAllOpenWindows(final ItemStack stack, List<EntityPlayerMP> players) {
    players.forEach(otherPlayer -> {
      otherPlayer.openContainer.putStackInSlot(SLOT_RESULT, stack);
      //otherPlayer.connection.sendPacket(new SPacketSetSlot(otherPlayer.openContainer.windowId, SLOT_RESULT, stack));
    });
  }

  private void syncRecipeToAllOpenWindows(final IRecipe lastRecipe, List<EntityPlayerMP> players) {
    players.forEach(otherPlayer -> {
      // safe cast since hasSameContainerOpen does class checks
      ((ContainerCraftingStation)otherPlayer.openContainer).lastRecipe = lastRecipe;
      TinkerNetwork.sendTo(new LastRecipeMessage(lastRecipe), otherPlayer);
    });
  }

  // todo: move this to Mantle
  // server can be gotten from EntityPlayerMP
  private <T extends TileEntity> List<EntityPlayerMP> getAllPlayersWithThisContainerOpen(BaseContainer<T> container, WorldServer server) {
    return server.playerEntities.stream()
        .filter(player -> hasSameContainerOpen(container, player))
        .map(player -> (EntityPlayerMP)player)
        .collect(Collectors.toList());
  }

  private <T extends TileEntity> boolean hasSameContainerOpen(BaseContainer<T> container, EntityPlayer playerToCheck) {
    return playerToCheck instanceof EntityPlayerMP &&
           playerToCheck.openContainer.getClass().isAssignableFrom(container.getClass()) &&
        this.sameGui((BaseContainer<T>) playerToCheck.openContainer);
  }

  @Override
  public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
    return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(p_94530_1_, p_94530_2_);
  }

  protected TileEntity detectInventory() {
    for(EnumFacing dir : EnumFacing.HORIZONTALS) {
      BlockPos neighbor = pos.offset(dir);
      boolean stationPart = false;
      for(Pair<BlockPos, IBlockState> tinkerPos : tinkerStationBlocks) {
        if(tinkerPos.getLeft().equals(neighbor)) {
          stationPart = true;
          break;
        }
      }
      if(!stationPart) {
        TileEntity te = world.getTileEntity(neighbor);
        if(te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite())) {
          if(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()) instanceof IItemHandlerModifiable) {
            return te;
          }
        }
      }
    }

    return null;
  }

  /**
   * @return the starting slot for the player inventory. Present for usage in the JEI crafting station support
   */
  public int getPlayerInventoryStart() {
    return playerInventoryStart;
  }

  public InventoryCrafting getCraftMatrix() {
    return craftMatrix;
  }

  public void updateLastRecipeFromServer(IRecipe recipe) {
    lastRecipe = recipe;
    // if no recipe, set to empty to prevent ghost outputs when another player grabs the result
    this.craftResult.setInventorySlotContents(SLOT_RESULT, recipe != null ? recipe.getCraftingResult(craftMatrix) : ItemStack.EMPTY);
  }


  public NonNullList<ItemStack> getRemainingItems() {
    if(lastRecipe != null && lastRecipe.matches(craftMatrix, world)) {
      return lastRecipe.getRemainingItems(craftMatrix);
    }
    return craftMatrix.stackList;
  }
}
