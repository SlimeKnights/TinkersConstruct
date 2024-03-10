package slimeknights.tconstruct.library.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

@AllArgsConstructor
@Getter
public abstract class TinkerToolEvent extends Event {
  private final ItemStack stack;
  private final IToolStackView tool;
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
    private final UseOnContext context;
    private final ServerLevel world;
    private final BlockState state;
    private final BlockPos pos;
    private final InteractionSource source;

    public ToolHarvestEvent(IToolStackView tool, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos, InteractionSource source) {
      super(getItem(context, source), tool);
      this.context = context;
      this.world = world;
      this.state = state;
      this.pos = pos;
      this.source = source;
    }

    /** Gets the item for the event */
    private static ItemStack getItem(UseOnContext context, InteractionSource source) {
      Player player = context.getPlayer();
      if (player != null) {
        return player.getItemBySlot(source.getSlot(context.getHand()));
      }
      return context.getItemInHand();
    }

    /** Gets the item for the event */
    private static ItemStack getItem(UseOnContext context, EquipmentSlot slotType) {
      Player player = context.getPlayer();
      if (player != null) {
        return player.getItemBySlot(slotType);
      }
      return context.getItemInHand();
    }

    @Nullable
    public Player getPlayer() {
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
    private final Level world;
    private final Player player;
    private final Entity target;
    private final int fortune;
    public ToolShearEvent(ItemStack stack, IToolStackView tool, Level world, Player player, Entity target, int fortune) {
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
