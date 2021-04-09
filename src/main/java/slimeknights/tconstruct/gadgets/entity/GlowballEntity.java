package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.shared.TinkerCommons;

public class GlowballEntity extends ThrownItemEntity {//implements IEntityAdditionalSpawnData {

  public GlowballEntity(EntityType<? extends GlowballEntity> entityType, World world) {
    super(entityType, world);
  }

  public GlowballEntity(World worldIn, LivingEntity throwerIn) {
    super(TinkerGadgets.glowBallEntity, throwerIn, worldIn);
  }

  public GlowballEntity(World worldIn, double x, double y, double z) {
    super(TinkerGadgets.glowBallEntity, x, y, z, worldIn);
  }

  @Override
  protected Item getDefaultItem() {
    return TinkerGadgets.glowBall.get();
  }

  @Override
  protected void onCollision(HitResult result) {
    if (!this.world.isClient) {
      BlockPos position = null;
      Direction direction = Direction.DOWN;

      if (result.getType() == HitResult.Type.ENTITY) {
        position = ((EntityHitResult) result).getEntity().getBlockPos();
      }

      if (result.getType() == HitResult.Type.BLOCK) {
        BlockHitResult blockraytraceresult = (BlockHitResult) result;
        position = blockraytraceresult.getBlockPos().offset(blockraytraceresult.getSide());
        direction = blockraytraceresult.getSide().getOpposite();
      }

      if (position != null) {
        TinkerCommons.glow.addGlow(this.world, position, direction);
      }
    }

    if (!this.world.isClient) {
      this.world.sendEntityStatus(this, (byte) 3);
      this.remove();
    }
  }

//  @Override
//  public void writeSpawnData(PacketByteBuf buffer) {
//    buffer.writeItemStack(this.getItem());
//  }
//
//  @Override
//  public void readSpawnData(PacketByteBuf additionalData) {
//    this.setItem(additionalData.readItemStack());
//  }

  @NotNull
  @Override
  public Packet<?> createSpawnPacket() {
    return super.createSpawnPacket();
  }
}
