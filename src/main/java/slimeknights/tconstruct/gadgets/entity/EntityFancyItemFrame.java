package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

// exists solely to be distinguishable from the vanilla itemframe
// also because network handling requires us to recreate what vanilla does.. meh
public class EntityFancyItemFrame extends EntityItemFrame implements IEntityAdditionalSpawnData {

  private FrameType type;

  public EntityFancyItemFrame(World worldIn, BlockPos p_i45852_2_, EnumFacing p_i45852_3_, int meta) {
    this(worldIn, p_i45852_2_, p_i45852_3_, FrameType.fromMeta(meta));
  }

  public EntityFancyItemFrame(World worldIn, BlockPos p_i45852_2_, EnumFacing p_i45852_3_, FrameType type) {
    super(worldIn, p_i45852_2_, p_i45852_3_);
    this.type = type;
  }

  public EntityFancyItemFrame(World worldIn) {
    super(worldIn);
  }

  @Override
  public void dropItemOrSelf(Entity entity, boolean dropFrame) {
    if(this.getEntityWorld().getGameRules().getBoolean("doEntityDrops")) {
      ItemStack itemstack = this.getDisplayedItem();

      if(entity instanceof EntityPlayer) {
        EntityPlayer entityplayer = (EntityPlayer) entity;

        if(entityplayer.capabilities.isCreativeMode) {
          this.removeFrameFromMap(itemstack);
          return;
        }
      }

      // drop frame
      if(dropFrame) {
        this.entityDropItem(new ItemStack(TinkerGadgets.fancyFrame, 1, type.ordinal()), 0.0F);
      }

      // drop item in frame
      if(!itemstack.isEmpty()) {
        itemstack = itemstack.copy();
        this.removeFrameFromMap(itemstack);
        this.entityDropItem(itemstack, 0.0F);
      }
    }
  }

  @Nonnull
  @Override
  public String getName() {
    if(this.hasCustomName()) {
      return this.getCustomNameTag();
    }

    ItemStack foo = new ItemStack(TinkerGadgets.fancyFrame, 1, type.ordinal());
    return foo.getDisplayName();
  }

  @Override
  public void writeEntityToNBT(NBTTagCompound tagCompound) {
    int nr = type != null ? type.ordinal() : 0;
    tagCompound.setInteger("frame", nr);
    super.writeEntityToNBT(tagCompound);
  }

  @Override
  public void readEntityFromNBT(@Nonnull NBTTagCompound tagCompund) {
    int nr = tagCompund.getInteger("frame");
    type = FrameType.values()[nr % FrameType.values().length];

    super.readEntityFromNBT(tagCompund);
  }

  @Override
  public void writeSpawnData(ByteBuf buffer) {
    assert this.facingDirection != null;
    buffer.writeShort(this.facingDirection.getHorizontalIndex());
    buffer.writeShort(type != null ? this.type.ordinal() : 0);
  }

  @Override
  public void readSpawnData(ByteBuf additionalData) {
    EnumFacing facing = EnumFacing.getHorizontal(additionalData.readShort());
    updateFacingWithBoundingBox(facing);
    this.type = FrameType.values()[additionalData.readShort()];
  }

  public FrameType getType() {
    if(type == null) {
      return FrameType.JEWEL;
    }
    return type;
  }

  public enum FrameType {
    JEWEL,
    ALUBRASS,
    COBALT,
    ARDITE,
    MANYULLYN,
    GOLD,
    CLEAR;

    public static FrameType fromMeta(int meta) {
      return FrameType.values()[meta % FrameType.values().length];
    }
  }
}
