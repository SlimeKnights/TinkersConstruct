package slimeknights.tconstruct.tools.modifiers.upgrades;

import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class LightspeedModifier extends IncrementalModifier {
  public LightspeedModifier() {
    super(0xFFBC5E);
  }

  @Override
  public int getPriority() {
    return 125; // run before trait boosts such as dwarven
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, PlayerEntity player, boolean isEffective, float miningSpeedModifier) {
    if (!isEffective) {
      return;
    }
    World world = player.getEntityWorld();
    BlockHitResult blockTrace = ToolCore.blockRayTrace(world, player, RaycastContext.FluidHandling.ANY);
    BlockPos pos = player.getBlockPos();
    if (blockTrace.getType() == HitResult.Type.BLOCK && pos == null || pos.equals(blockTrace.getPos())) {
      int light = player.getEntityWorld().getLightLevel(LightType.BLOCK, blockTrace.getBlockPos().offset(blockTrace.getSide()));
      // bonus is +9 mining speed at light level 15, +3 at light level 10, +1 at light level 5
      float boost = (float)(level * Math.pow(3, (light - 5) / 5f) * tool.getDefinition().getBaseStatDefinition().getMiningSpeedModifier() * miningSpeedModifier);
      throw new RuntimeException("CRAB");
      //event.setNewSpeed(event.getNewSpeed() + boost);
    }
  }
}
