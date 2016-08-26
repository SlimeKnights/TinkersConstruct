package slimeknights.tconstruct.library.events;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import javax.annotation.Nullable;

import slimeknights.tconstruct.library.entity.EntityProjectileBase;

public class ProjectileEvent extends TinkerEvent {

  public final Entity projectileEntity;
  /** Might be null if the entity is a vanilla or other mods entity */
  @Nullable
  public final EntityProjectileBase projectile;

  public ProjectileEvent(Entity projectile) {
    this.projectileEntity = projectile;
    if(projectile instanceof EntityProjectileBase) {
      this.projectile = (EntityProjectileBase) projectile;
    }
    else {
      this.projectile = null;
    }
  }

  @Cancelable
  public static class OnLaunch extends ProjectileEvent {
    @Nullable
    public final ItemStack launcher;

    @Nullable
    public final EntityLivingBase shooter;

    public OnLaunch(Entity projectile, ItemStack launcher, EntityLivingBase shooter) {
      super(projectile);
      this.launcher = launcher;
      this.shooter = shooter;
    }

    public static boolean fireEvent(Entity projectile, ItemStack launcher, EntityLivingBase shooter) {
      return !MinecraftForge.EVENT_BUS.post(new OnLaunch(projectile, launcher, shooter));
    }
  }

  /** When a projectile hits a block */
  public static class OnHitBlock extends ProjectileEvent {
    public final float speed;
    public final BlockPos pos;
    public final IBlockState blockState;

    public OnHitBlock(EntityProjectileBase projectile, float speed, BlockPos pos, IBlockState blockState) {
      super(projectile);
      this.speed = speed;
      this.pos = pos;
      this.blockState = blockState;
    }

    public static void fireEvent(EntityProjectileBase projectile, float speed, BlockPos pos, IBlockState blockState) {
      MinecraftForge.EVENT_BUS.post(new OnHitBlock(projectile, speed, pos, blockState));
    }
  }
}
