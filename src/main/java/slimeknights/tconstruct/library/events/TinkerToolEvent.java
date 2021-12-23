package slimeknights.tconstruct.library.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

@AllArgsConstructor
@Getter
public abstract class TinkerToolEvent extends Event {
  private final ItemStack stack;
  private final IModifierToolStack tool;
  public TinkerToolEvent(ItemStack stack) {
    this.stack = stack;
    this.tool = ToolStack.from(stack);
  }

  /**
   * Event fired when a kama tries to harvest a crop. Set result to {@link Result#ALLOW} if you handled the harvest yourself. Set the result to {@link Result#DENY} if the block cannot be harvested.
   */
  @HasResult
  @Getter
  public static class ToolHarvestEvent extends TinkerToolEvent {
    /** Item context, note this is the original context, so some information (such as position) may not be accurate */
    private final ItemUseContext context;
    private final ServerWorld world;
    private final BlockState state;
    private final BlockPos pos;
    private final EquipmentSlotType slotType;

    public ToolHarvestEvent(IModifierToolStack tool, ItemUseContext context, ServerWorld world, BlockState state, BlockPos pos, EquipmentSlotType slotType) {
      super(getItem(context, slotType), tool);
      this.context = context;
      this.world = world;
      this.state = state;
      this.pos = pos;
      this.slotType = slotType;
    }

    /** Gets the item for the event */
    private static ItemStack getItem(ItemUseContext context, EquipmentSlotType slotType) {
      PlayerEntity player = context.getPlayer();
      if (player != null) {
        return player.getItemStackFromSlot(slotType);
      }
      return context.getItem();
    }

    @Nullable
    public PlayerEntity getPlayer() {
      return context.getPlayer();
    }

    /** Fires this event and posts the result */
    public Result fire() {
      MinecraftForge.EVENT_BUS.post(this);
      return this.getResult();
    }
  }

  /**
   * Event fired when a kama or scythe tries to shear an entity
   */
  @HasResult
  @Getter
  public static class ToolShearEvent extends TinkerToolEvent {
    private final World world;
    private final PlayerEntity player;
    private final Entity target;
    private final int fortune;
    public ToolShearEvent(ItemStack stack, IModifierToolStack tool, World world, PlayerEntity player, Entity target, int fortune) {
      super(stack, tool);
      this.world = world;
      this.player = player;
      this.target = target;
      this.fortune = fortune;
    }

    /** Fires this event and posts the result */
    public Result fire() {
      MinecraftForge.EVENT_BUS.post(this);
      return this.getResult();
    }

  }
}
