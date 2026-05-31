package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import slimeknights.tconstruct.compat.neoforged.neoforge.entity.IEntityAdditionalSpawnData;
import slimeknights.tconstruct.compat.neoforged.neoforge.network.NetworkHooks;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

import javax.annotation.Nonnull;

/** @deprecated use {@link slimeknights.tconstruct.tools.entity.ThrownShuriken} */
@Deprecated
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
      new EFLNExplosion(level, position(), 4f, this, 8f, null, 1, false, BlockInteraction.DESTROY).handleServer();
      this.discard();
    }
  }

  @Override
  public void writeSpawnData(FriendlyByteBuf buffer) {
    ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf)buffer, this.getItem());
  }

  @Override
  public void readSpawnData(FriendlyByteBuf additionalData) {
    this.setItem(ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf)additionalData));
  }

  @Nonnull
  @Override
  public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}
