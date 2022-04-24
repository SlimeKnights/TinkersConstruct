package slimeknights.tconstruct.library.data.tinkering;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.fluid.transfer.EmptyFluidContainerTransfer;
import slimeknights.tconstruct.library.fluid.transfer.FillFluidContainerTransfer;
import slimeknights.tconstruct.library.fluid.transfer.FluidContainerTransferManager;
import slimeknights.tconstruct.library.fluid.transfer.IFluidContainerTransfer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Datagen for fluid transfer logic */
public abstract class AbstractFluidContainerTransferProvider extends GenericDataProvider {
  private final Map<ResourceLocation,IFluidContainerTransfer> allTransfers = new HashMap<>();
  private final String modId;

  public AbstractFluidContainerTransferProvider(DataGenerator generator, String modId) {
    super(generator, PackType.SERVER_DATA, FluidContainerTransferManager.FOLDER, FluidContainerTransferManager.GSON);
    this.modId = modId;
  }

  /** Function to add all relevant transfers */
  protected abstract void addTransfers();

  /** Adds a transfer to be saved */
  protected void addTransfer(ResourceLocation id, IFluidContainerTransfer transfer) {
    IFluidContainerTransfer previous = allTransfers.putIfAbsent(id, transfer);
    if (previous != null) {
      throw new IllegalArgumentException("Duplicate fluid container transfer " + id);
    }
  }

  /** Adds a transfer to be saved */
  protected void addTransfer(String name, IFluidContainerTransfer transfer) {
    addTransfer(new ResourceLocation(modId, name), transfer);
  }

  /** Adds generic fill and empty for a container */
  protected void addFillEmpty(String prefix, ItemLike item, ItemLike container, Fluid fluid, TagKey<Fluid> tag, int amount) {
    addTransfer(prefix + "empty",  new EmptyFluidContainerTransfer(Ingredient.of(item), ItemOutput.fromItem(container), new FluidStack(fluid, amount)));
    addTransfer(prefix + "fill", new FillFluidContainerTransfer(Ingredient.of(container), ItemOutput.fromItem(item), FluidIngredient.of(tag, amount)));
  }

  @Override
  public void run(HashCache cache) throws IOException {
    addTransfers();
    allTransfers.forEach((id, data) -> saveThing(cache, id, data));
  }
}
