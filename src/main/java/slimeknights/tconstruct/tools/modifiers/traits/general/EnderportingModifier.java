package slimeknights.tconstruct.tools.modifiers.traits.general;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket.RelativeArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.events.teleport.EnderportingTeleportEvent;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Set;

public class EnderportingModifier extends SingleUseModifier {
  private static final Set<RelativeArgument> PACKET_FLAGS = ImmutableSet.of(RelativeArgument.X, RelativeArgument.Y, RelativeArgument.Z);

  @Override
  public int getPriority() {
    return 75;
  }

  /** Attempts to teleport to the given location */
  private static boolean tryTeleport(LivingEntity living, double x, double y, double z) {
    Level world = living.getCommandSenderWorld();
    // should never happen with the hooks, but just in case
    if (world.isClientSide) {
      return false;
    }
    // this logic is cloned from suffocation damage logic
    float scaledWidth = living.getBbWidth() * 0.8F;
    float eyeHeight = living.getEyeHeight();
    AABB aabb = AABB.ofSize(new Vec3(x, y + (eyeHeight / 2), z), scaledWidth, eyeHeight, scaledWidth);

    boolean didCollide = world.getBlockCollisions(living, aabb).iterator().hasNext();

    // if we collided, try again 1 block down, means mining the top of 2 blocks is valid
    if (didCollide && living.getBbHeight() > 1) {
      // try again 1 block down
      aabb = aabb.move(0, -1, 0);
      didCollide = world.getBlockCollisions(living, aabb).iterator().hasNext();
      y -= 1;
    }

    // as long as no collision now, we can teleport
    if (!didCollide) {
      // actual teleport
      EnderportingTeleportEvent event = new EnderportingTeleportEvent(living, x, y, z);
      MinecraftForge.EVENT_BUS.post(event);
      if (!event.isCanceled()) {
        // this logic only runs serverside, so need to use the server controller logic to move the player
        if (living instanceof ServerPlayer playerMP) {
          playerMP.connection.teleport(x, y, z, playerMP.getYRot(), playerMP.getXRot(), PACKET_FLAGS);
        } else {
          living.setPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        }
        // particles must be sent on a server
        if (world instanceof ServerLevel serverWorld) {
          for (int i = 0; i < 32; ++i) {
            serverWorld.sendParticles(ParticleTypes.PORTAL, living.getX(), living.getY() + world.random.nextDouble() * 2.0D, living.getZ(), 1, world.random.nextGaussian(), 0.0D, world.random.nextGaussian(), 0);
          }
        }
        world.playSound(null, living.getX(), living.getY(), living.getZ(), Sounds.ENDERPORTING.getSound(),  living.getSoundSource(), 1f, 1f);
        return true;
      }
    }
    return false;
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    if (!context.isExtraAttack()) {
      LivingEntity target = context.getLivingTarget();
      // if the entity is dead now
      if (target != null && target.getHealth() == 0) {
        Vec3 pos = target.position();
        if (tryTeleport(context.getAttacker(), pos.x(), pos.y(), pos.z())) {
          return 2;
        }
      }
    }
    return 0;
  }

  @Override
  public void finishBreakingBlocks(IToolStackView tool, int level, ToolHarvestContext context) {
    if (context.canHarvest()) {
      BlockPos pos = context.getPos();
      LivingEntity living = context.getLiving();
      if (tryTeleport(living, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f)) {
        ToolDamageUtil.damageAnimated(tool, 2, living);
      }
    }
  }
}
