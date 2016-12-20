package slimeknights.tconstruct.library.client;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class BooleanItemPropertyGetter implements IItemPropertyGetter {

  @Override
  @SideOnly(Side.CLIENT)
  public final float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
    return applyIf(stack, worldIn, entityIn) ? 1f : 0f;
  }

  @SideOnly(Side.CLIENT)
  public abstract boolean applyIf(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn);
}
