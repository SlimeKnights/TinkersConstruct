package slimeknights.tconstruct.library.utils;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

// TODO: move to mantle
public class SupplierItemGroup extends ItemGroup {
  private final Supplier<ItemStack> supplier;
  public SupplierItemGroup(String modId, String name, Supplier<ItemStack> supplier) {
    super(String.format("%s.%s", modId, name));
    this.setTabPath(String.format("%s/%s", modId, name));
    this.supplier = supplier;
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public ItemStack createIcon() {
    return supplier.get();
  }
}
