package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;

public class GreenSlimeSlingItem extends BaseSlimeSlingItem {

  public GreenSlimeSlingItem(Item.Properties props) {
    super(props);
  }

  /**
   * Called when the player stops using an Item (stops holding the right mouse button).
   */
  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!(entityLiving instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entityLiving;
    if (player.isAirBorne) {
      return;
    }

    int i = this.getUseDuration(stack) - timeLeft;
    float f = getForce(i);

    // check if player was targeting a block
    BlockRayTraceResult mop = rayTrace(worldIn, player, RayTraceContext.FluidMode.NONE);
    if (mop.getType() == RayTraceResult.Type.BLOCK) {
      // we fling the inverted player look vector
      Vector3d vec = player.getLookVec().normalize();
      player.addVelocity(vec.x * -f,
        vec.y * -f / 3f,
        vec.z * -f);

      if (player instanceof ServerPlayerEntity) {
        ServerPlayerEntity playerMP = (ServerPlayerEntity) player;
        TinkerNetwork.getInstance().sendTo(new EntityMovementChangePacket(player), playerMP);
      }

      player.playSound(Sounds.SLIME_SLING.getSound(), 1f, 1f);
      SlimeBounceHandler.addBounceHandler(player);
    }
  }

}
