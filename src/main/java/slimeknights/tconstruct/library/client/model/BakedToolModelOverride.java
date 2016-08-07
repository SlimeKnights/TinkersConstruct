package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

import javax.annotation.Nullable;

// Basically ItemOverride but with models instead of locations as output
@SideOnly(Side.CLIENT)
public class BakedToolModelOverride {

  public final ImmutableMap<ResourceLocation, Float> predicates;
  public final BakedToolModel bakedToolModel;

  public BakedToolModelOverride(ImmutableMap<ResourceLocation, Float> predicates, BakedToolModel bakedToolModel) {
    this.predicates = predicates;
    this.bakedToolModel = bakedToolModel;
  }

  public boolean matches(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
    Item item = stack.getItem();

    for(Map.Entry<ResourceLocation, Float> entry : predicates.entrySet()) {
      IItemPropertyGetter iitempropertygetter = item.getPropertyGetter(entry.getKey());

      if(iitempropertygetter == null || iitempropertygetter.apply(stack, worldIn, entityIn) < entry.getValue()) {
        return false;
      }
    }

    return true;
  }
}
