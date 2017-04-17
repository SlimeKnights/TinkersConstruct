package slimeknights.tconstruct.tools.ranged;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.ranged.item.BoltCore;

public class BoltCoreCastingRecipe implements ICastingRecipe {

  public static final BoltCoreCastingRecipe INSTANCE = new BoltCoreCastingRecipe();
  public static final int boltCoreAmount = Material.VALUE_Ingot * 2;

  protected BoltCoreCastingRecipe() {}

  @Override
  public ItemStack getResult(ItemStack cast, Fluid fluid) {
    Material shaftMaterial = TinkerTools.arrowShaft.getMaterial(cast);
    Material headMaterial = getMaterialForFluid(fluid);

    return BoltCore.getItemstackWithMaterials(shaftMaterial, headMaterial);
  }

  private Material getMaterialForFluid(Fluid fluid) {
    return TinkerRegistry.getAllMaterials().stream()
                         .filter(mat -> fluid.equals(mat.getFluid()))
                         .findFirst()
                         .orElse(Material.UNKNOWN);
  }

  @Override
  public boolean matches(ItemStack cast, Fluid fluid) {
    // cast needs to be a shaft with shaft material, fluid needs to belong to a material with head stats
    return cast.getItem() == TinkerTools.arrowShaft &&
           TinkerTools.arrowShaft.getMaterial(cast).hasStats(MaterialTypes.SHAFT) &&
           isFluidWithHeadMaterial(fluid);
  }

  private boolean isFluidWithHeadMaterial(Fluid fluid) {
    return TinkerRegistry.getAllMaterials().stream()
                         .filter(mat -> mat.hasStats(MaterialTypes.HEAD))
                         .map(Material::getFluid)
                         .anyMatch(fluid::equals);
  }

  @Override
  public boolean switchOutputs() {
    return false;
  }

  @Override
  public boolean consumesCast() {
    return true;
  }

  @Override
  public int getTime() {
    return 120;
  }

  @Override
  public int getFluidAmount() {
    return boltCoreAmount;
  }
}
