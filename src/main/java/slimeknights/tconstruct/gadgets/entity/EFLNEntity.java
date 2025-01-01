package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkHooks;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

import javax.annotation.Nonnull;

public class EFLNEntity extends ThrowableItemProjectile implements IEntityAdditionalSpawnData {
  public EFLNEntity(EntityType<? extends EFLNEntity> type, Level level) {
    super(type, level);
  }

  public EFLNEntity(Level level, LivingEntity thrower) {
    super(TinkerGadgets.eflnEntity.get(), thrower, level);
  }

  public EFLNEntity(Level worldIn, double x, double y, double z) {
    super(TinkerGadgets.eflnEntity.get(), x, y, z, worldIn);
  }

  @Override
  protected Item getDefaultItem() {
    return TinkerGadgets.efln.get();
  }

  @Override
  protected void onHit(HitResult result) {
    Level level = level();
    if (!level.isClientSide) {
      // based on ServerLevel#explode
      EFLNExplosion explosion = new EFLNExplosion(level, this, null, null, this.getX(), this.getY(), this.getZ(), 4f, false, BlockInteraction.DESTROY);
      if (!ForgeEventFactory.onExplosionStart(level, explosion)) {
        explosion.explode();
        explosion.finalizeExplosion(false);
        if (level instanceof ServerLevel server) {
          for (ServerPlayer player : server.players()) {
            if (player.distanceToSqr(this) < 4096.0D) {
              player.connection.send(new ClientboundExplodePacket(getX(), getY(), getZ(), 6, explosion.getToBlow(), explosion.getHitPlayers().get(player)));
            }
          }
        }
      }
      this.discard();
    }
  }

  @Override
  public void writeSpawnData(FriendlyByteBuf buffer) {
    buffer.writeItem(this.getItemRaw());
  }

  @Override
  public void readSpawnData(FriendlyByteBuf additionalData) {
    this.setItem(additionalData.readItem());
  }

  @Nonnull
  @Override
  public Packet<ClientGamePacketListener> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
