package slimeknights.tconstruct.gadgets.item.slimesling;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;

import java.util.List;

public class MagmaSlimeSlingItem extends BaseSlimeSlingItem {

  public MagmaSlimeSlingItem(Properties props) {
    super(props);
  }

  /** Called when the player stops using an Item (stops holding the right mouse button). */
  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
    if (!(entityLiving instanceof PlayerEntity)) {
      return;
    }

    PlayerEntity player = (PlayerEntity) entityLiving;
    float f = getForce(stack, timeLeft);

    float range = 5F;
    Vector3d start = player.getEyePosition(1F);
    Vector3d look = player.getLookVec();
    Vector3d direction = start.add(look.x * range, look.y * range, look.z * range);
    AxisAlignedBB bb = player.getBoundingBox().expand(look.x * range, look.y * range, look.z * range).expand(1, 1, 1);
    List<Entity> entitiesInArea = player.getEntityWorld().getEntitiesWithinAABBExcludingEntity(player, bb);
    double dist = range;
    Entity closestEntity = null;
    for (Entity entity : entitiesInArea) {
      if (entity.getBoundingBox().intersects(Math.min(start.x, direction.x), Math.min(start.y, direction.y), Math.min(start.z, direction.z), Math.max(start.x, direction.x), Math.max(start.y, direction.y), Math.max(start.z, direction.z))) {
        if (look.distanceTo(entity.getLookVec()) < dist) {
          dist = look.distanceTo(entity.getLookVec());
          closestEntity = entity;
        }
      }
    }
    // TODO: Ensure there isn't a block in the way
    if (closestEntity != null) {
      if (closestEntity instanceof LivingEntity) {
        ((LivingEntity) closestEntity).applyKnockback(f , -look.x, -look.z);

        // TODO: Test on other players (I wasn't able to figure out setting that up)
        if (closestEntity instanceof ServerPlayerEntity) {
          ServerPlayerEntity playerMP = (ServerPlayerEntity) closestEntity;
          TinkerNetwork.getInstance().sendTo(new EntityMovementChangePacket(closestEntity), playerMP);
        }

        player.playSound(Sounds.SLIME_SLING.getSound(), 1f, 1f);
      }
    }
  }
}
