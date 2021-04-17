package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class LightspeedModifier extends IncrementalModifier {
  public LightspeedModifier() {
    super(0xFFBC5E);
  }

  @Override
  public int getPriority() {
    return 125; // run before trait boosts such as dwarven
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, boolean isEffective, float miningSpeedModifier) {
    if (!isEffective) {
      return;
    }
    PlayerEntity player = event.getPlayer();
    World world = player.getEntityWorld();
    BlockRayTraceResult blockTrace = ToolCore.blockRayTrace(world, player, RayTraceContext.FluidMode.ANY);
    BlockPos pos = event.getPos();
    if (blockTrace.getType() == Type.BLOCK && pos == null || pos.equals(blockTrace.getPos())) {
      int light = player.getEntityWorld().getLightFor(LightType.BLOCK, blockTrace.getPos().offset(blockTrace.getFace()));
      if (light > 5) {
        // bonus is +1 mining speed for each level above 6, factors in tool mining speed modifier
        float boost = (float)(level * Math.pow(3, (light - 5) / 5f) * tool.getDefinition().getBaseStatDefinition().getMiningSpeedModifier() * miningSpeedModifier);
        event.setNewSpeed(event.getNewSpeed() + boost);
      }
    }
  }
}
