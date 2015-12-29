package slimeknights.tconstruct.world.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

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
    int i = this.getSlimeSize();
    for (int j = 0; j < i * 8; ++j)
    {
      float f = this.rand.nextFloat() * (float)Math.PI * 2.0F;
      float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
      float f2 = MathHelper.sin(f) * (float)i * 0.5F * f1;
      float f3 = MathHelper.cos(f) * (float)i * 0.5F * f1;
      World world = this.worldObj;
      EnumParticleTypes enumparticletypes = EnumParticleTypes.VILLAGER_HAPPY;
      double d0 = this.posX + (double)f2;
      double d1 = this.posZ + (double)f3;
      world.spawnParticle(enumparticletypes, d0, this.getEntityBoundingBox().minY, d1, 0.0D, 0.0D, 0.0D, new int[0]);
    }
    return true;
  }
}
