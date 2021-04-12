package slimeknights.tconstruct.library.tools.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

@AllArgsConstructor
@Getter
public abstract class TinkerToolEvent extends Event {
  private final ItemStack stack;
  private final ToolStack tool;
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
    @Nullable
    private final PlayerEntity player;
    public ToolHarvestEvent(ItemStack stack, ToolStack tool, ItemUseContext context, ServerWorld world, BlockState state, BlockPos pos, @Nullable PlayerEntity player) {
      super(stack, tool);
      this.context = context;
      this.world = world;
      this.state = state;
      this.pos = pos;
      this.player = player;
    }

    /** Fires this event and posts the result */
    public Result fire() {
      MinecraftForge.EVENT_BUS.post(this);
      return this.getResult();
    }
  }
}
