package slimeknights.tconstruct.plugin.jsonthings.item;

import dev.gigaherz.jsonthings.things.events.FlexEventHandler;
import dev.gigaherz.jsonthings.things.events.IEventRunner;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class FlexModifiableItem extends ModifiableItem implements IEventRunner {
  private final Map<String, FlexEventHandler> eventHandlers = new HashMap<>();
  private final boolean breakBlocksInCreative;
  public FlexModifiableItem(Properties properties, ToolDefinition toolDefinition, boolean breakBlocksInCreative) {
    super(properties, toolDefinition);
    this.breakBlocksInCreative = breakBlocksInCreative;
  }

  @Override
  public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player player) {
    return breakBlocksInCreative || !player.isCreative();
  }


  /* not honestly sure what events do, but trivial to support */

  @Override
  public void addEventHandler(String name, FlexEventHandler flexEventHandler) {
    this.eventHandlers.put(name, flexEventHandler);
  }

  @Nullable
  @Override
  public FlexEventHandler getEventHandler(String name) {
    return this.eventHandlers.get(name);
  }
}
