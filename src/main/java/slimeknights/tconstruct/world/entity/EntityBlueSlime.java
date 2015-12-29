package slimeknights.tconstruct.world.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.particle.EntitySlimeFx;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerFluids;
import slimeknights.tconstruct.world.TinkerWorld;

public class EntityBlueSlime extends EntitySlime {

  public EntityBlueSlime(World worldIn) {
    super(worldIn);
  }

  // we're using this instead of getDropItem because we need the metadata
  @Override
  public EntityItem dropItemWithOffset(Item itemIn, int size, float offsetY) {
    ItemStack stack = TinkerCommons.matSlimeBallBlue.copy();
    stack.stackSize = size;
    return this.entityDropItem(stack, offsetY);
  }

  @Override
  protected EntitySlime createInstance() {
    return new EntityBlueSlime(this.worldObj);
  }

  @Override
  public boolean getCanSpawnHere() {
    if(this.worldObj.getBlockState(this.getPosition()).getBlock() == TinkerFluids.blockBlueslime) {
      return true;
    }
    if(this.worldObj.getBlockState(this.getPosition().down()).getBlock() == TinkerWorld.slimeGrass) {
      return true;
    }
    return false;
  }

  @Override
  protected boolean spawnCustomParticles() {
    if(this.worldObj.isRemote) {
      int i = this.getSlimeSize();
      for(int j = 0; j < i * 8; ++j) {
        float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
        float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
        float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
        float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;
        double d0 = this.posX + (double) f2;
        double d1 = this.posZ + (double) f3;
        double d2 = this.getEntityBoundingBox().minY;
        TinkerWorld.proxy.spawnSlimeParticle(this.worldObj, d0, d2, d1);
      }
    }
    return true;
  }
}
