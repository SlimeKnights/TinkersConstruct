package slimeknights.tconstruct.tables.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.mantle.inventory.EmptyItemHandler;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.inventory.TriggeringMultiModuleContainerMenu;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.block.ITabbedBlock;
import slimeknights.tconstruct.tables.client.inventory.BaseTabbedScreen;
import slimeknights.tconstruct.tables.menu.module.SideInventoryContainer;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/** Base logic for containers with tabs on the menu */
public class TabbedContainerMenu<TILE extends BlockEntity> extends TriggeringMultiModuleContainerMenu<TILE> {
  private static final TinkerBlockComp COMPARATOR = new TinkerBlockComp();
  public final List<Pair<BlockPos, BlockState>> stationBlocks;

  public TabbedContainerMenu(MenuType<?> containerType, int id, @Nullable Inventory inv, @Nullable TILE tile) {
    super(containerType, id, inv, tile);

    this.stationBlocks = Lists.newLinkedList();

    if (tile != null && tile.getLevel() != null) {
      this.detectStationParts(tile.getLevel(), tile.getBlockPos());
    }
  }

  /**
   * Detects the given station parts nearby the given position
   *
   * @param world the current world
   * @param start the current position of the tile entity
   */
  public void detectStationParts(Level world, BlockPos start) {
    Set<BlockPos> visited = Sets.newHashSet();

    // BFS for related blocks
    Queue<BlockPos> queue = new ArrayDeque<>();
    queue.add(start);

    while (!queue.isEmpty()) {
      BlockPos pos = queue.poll();
      // already visited between adding and call
      if (visited.contains(pos)) {
        continue;
      }

      BlockState state = world.getBlockState(pos);
      if (!(state.getBlock() instanceof ITabbedBlock)) {
        // not a valid block for us
        continue;
      }

      // found a part, add surrounding blocks that haven't been visited yet
      for (Direction direction : Direction.values()) {
        BlockPos offset = pos.relative(direction);
        if (!visited.contains(offset)) {
          queue.add(offset);
        }
      }

      // mark this block as visited to visited
      visited.add(pos);

      // save the thing
      this.stationBlocks.add(Pair.of(pos, state));

      // we only have space for 6 tabs, so stop after the first 6
      if (this.stationBlocks.size() >= 6) {
        break;
      }
    }

    // sort the found blocks by priority
    this.stationBlocks.sort(COMPARATOR);
  }

  /** Adds a side inventory to this container */
  protected void addChestSideInventory() {
    if (tile == null || inv == null) {
      return;
    }
    Level world = tile.getLevel();
    if (world != null) {
      // detect side inventory
      BlockEntity inventoryTE = null;
      Direction accessDir = null;

      BlockPos pos = tile.getBlockPos();
      horizontals:
      for (Direction dir : Direction.Plane.HORIZONTAL) {
        // skip any tables in this multiblock
        BlockPos neighbor = pos.relative(dir);
        for (Pair<BlockPos,BlockState> tinkerPos : this.stationBlocks) {
          if (tinkerPos.getLeft().equals(neighbor)) {
            continue horizontals;
          }
        }

        // fetch tile entity
        BlockEntity te = world.getBlockEntity(neighbor);
        if (te != null && isUsable(te, inv.player)) {
          // try internal access first
          if (hasItemHandler(te, null)) {
            inventoryTE = te;
            break;
          }

          // try sided access next
          Direction side = dir.getOpposite();
          if (hasItemHandler(te, side)) {
            inventoryTE = te;
            accessDir = side;
            break;
          }
        }
      }

      // if we found something, add the side inventory
      if (inventoryTE != null) {
        int invSlots = inventoryTE.getCapability(ForgeCapabilities.ITEM_HANDLER, accessDir).orElse(EmptyItemHandler.INSTANCE).getSlots();
        int columns = Mth.clamp((invSlots - 1) / 9 + 1, 3, 6);
        this.addSubContainer(new SideInventoryContainer<>(TinkerTables.craftingStationContainer.get(), containerId, inv, inventoryTE, accessDir, -6 - 18 * 6, 8, columns), false);
      }
    }
  }

  /**
   * Checks if the given tile entity is blacklisted
   * @param tileEntity  Tile to check
   * @return  True if blacklisted
   */
  @SuppressWarnings("deprecation")  // your tag utils are overkill
  private static boolean isUsable(BlockEntity tileEntity, Player player) {
    // must not be blacklisted and be usable
    return !RegistryHelper.contains(BuiltInRegistries.BLOCK_ENTITY_TYPE, TinkerTags.TileEntityTypes.CRAFTING_STATION_BLACKLIST, tileEntity.getType())
           && (!(tileEntity instanceof Container) || ((Container)tileEntity).stillValid(player));
  }

  /**
   * Checks to see if the given Tile Entity has an item handler that's compatible with the side inventory
   * The Tile Entity's item handler must be an instance of IItemHandlerModifiable
   * @param tileEntity Tile to check
   * @param direction the given direction
   * @return True if compatible.
   */
  private static boolean hasItemHandler(BlockEntity tileEntity, @Nullable Direction direction) {
    return tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction).filter(cap -> cap instanceof IItemHandlerModifiable).isPresent();
  }


  /**
   * Sends a update to the client's current screen.
   */
  public void updateScreen() {
    if (this.tile != null) {
      if (this.tile.getLevel() != null) {
        if (this.tile.getLevel().isClientSide && FMLEnvironment.dist == Dist.CLIENT) {
          ClientOnly.clientScreenUpdate();
        }
      }
    }
  }

  /**
   * Tells the client to display the LOCALIZED error message
   */
  public void error(final MutableComponent message) {
    if (this.tile != null) {
      if (this.tile.getLevel() != null) {
        if (this.tile.getLevel().isClientSide && FMLEnvironment.dist == Dist.CLIENT) {
          ClientOnly.clientError(message);
        }
      }
    }
  }

  /**
   * Tells the client to display the LOCALIZED warning message
   */
  public void warning(final MutableComponent message) {
    if (this.tile != null) {
      if (this.tile.getLevel() != null) {
        if (this.tile.getLevel().isClientSide && FMLEnvironment.dist == Dist.CLIENT) {
          ClientOnly.clientWarning(message);
        }
      }
    }
  }

  /** Logic for comparing two blocks based on position and state */
  private static class TinkerBlockComp implements Comparator<Pair<BlockPos, BlockState>> {
    @Override
    public int compare(Pair<BlockPos, BlockState> o1, Pair<BlockPos, BlockState> o2) {
      // base location: lowest overall position
      BlockPos pos1 = o1.getLeft();
      BlockPos pos2 = o2.getLeft();
      int sum1 = pos1.getX() + pos1.getY() + pos1.getZ();
      int sum2 = pos2.getX() + pos2.getY() + pos2.getZ();
      if (sum1 != sum2) {
        return Integer.compare(sum1, sum2);
      }
      // so they have the same distance from 0,0,0, prefer lower y, then x, then z
      if (pos1.getY() != pos2.getY()) {
        return Integer.compare(pos1.getY(), pos2.getY());
      }
      if (pos1.getX() != pos2.getX()) {
        return Integer.compare(pos1.getX(), pos2.getX());
      }
      return Integer.compare(pos1.getZ(), pos2.getZ());
    }
  }

  /** Methods that only work on the client side */
  private static class ClientOnly {
    /** Updates the client's screen */
    private static void clientScreenUpdate() {
      Screen screen = Minecraft.getInstance().screen;
      if (screen instanceof BaseTabbedScreen) {
        ((BaseTabbedScreen<?,?>) screen).updateDisplay();
      }
    }

    /** Sends the error message from the container to the client's screen */
    private static void clientError(MutableComponent errorMessage) {
      Screen screen = Minecraft.getInstance().screen;
      if (screen instanceof BaseTabbedScreen) {
        ((BaseTabbedScreen<?,?>) screen).error(errorMessage);
      }
    }

    /** Sends the warning message from the container to the client's screen */
    private static void clientWarning(MutableComponent warningMessage) {
      Screen screen = Minecraft.getInstance().screen;
      if (screen instanceof BaseTabbedScreen) {
        ((BaseTabbedScreen<?,?>) screen).warning(warningMessage);
      }
    }
  }
}
