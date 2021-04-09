package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeSerializer;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recipe type that converts a material item from one material to another using a fluid
 */
public abstract class CompositeCastingRecipe implements ICastingRecipe, IMultiRecipe<IDisplayableCastingRecipe> {
  @Getter
  protected final RecipeType<?> type;
  @Getter
  private final Identifier id;
  /** Material type to check from the input stack */
  protected final MaterialId inputId;
  /** Fluid required to make the upgrade, size determines cost per ingot */
  protected final FluidIngredient fluid;
  /** Material type to check fron the output stack */
  protected final MaterialId outputId;
  /** Temperature used in calculating cooling time */
  protected final int coolingTemperature;
  /** Lazy loaded materials */
  private final Lazy<IMaterial> inputMaterial, outputMaterial;
  /** Cached list of recipes used for display in JEI */
  private List<IDisplayableCastingRecipe> multiRecipes;
  protected CompositeCastingRecipe(RecipeType<?> type, Identifier id, MaterialId inputId, FluidIngredient fluid, MaterialId outputId, int coolingTemperature) {
    this.type = type;
    this.id = id;
    this.inputId = inputId;
    this.fluid = fluid;
    this.outputId = outputId;
    this.coolingTemperature = coolingTemperature;
    this.inputMaterial = Lazy.of(() -> MaterialRegistry.getMaterial(this.inputId));
    this.outputMaterial = Lazy.of(() -> MaterialRegistry.getMaterial(this.outputId));
  }

  /**
   * Gets the cost in mb for the given material item
   * @param item  Item to check
   * @return  Cost, or 0 if no valid recipe exists
   */
  protected abstract int getMaterialItemCost(IMaterialItem item);

  @Override
  public boolean matches(ICastingInventory inv, World worldIn) {
    if (!fluid.test(inv.getFluid())) {
      return false;
    }

    ItemStack stack = inv.getStack();
    Item item = stack.getItem();
    if (item instanceof IMaterialItem) {
      IMaterialItem part = (IMaterialItem) item;
      return getMaterialItemCost(part) > 0 && inputId.equals(part.getMaterial(stack).getIdentifier());
    }
    return false;
  }

  @Override
  public int getFluidAmount(ICastingInventory inv) {
    Item item = inv.getStack().getItem();
    if (item instanceof IMaterialItem) {
      return fluid.getAmount(inv.getFluid()) * getMaterialItemCost((IMaterialItem) item);
    }
    return 0;
  }

  @Override
  public int getCoolingTime(ICastingInventory inv) {
    return ICastingRecipe.calcCoolingTime(coolingTemperature, getFluidAmount(inv));
  }

  @Override
  public ItemStack getOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean isConsumed() {
    return true;
  }

  @Override
  public boolean switchSlots() {
    return false;
  }

  @Override
  public ItemStack getCraftingResult(ICastingInventory inv) {
    Item item = inv.getStack().getItem();
    if (item instanceof IMaterialItem) {
      return ((IMaterialItem)item).getItemstackWithMaterial(outputMaterial.get());
    }
    return ItemStack.EMPTY;
  }

  /**
   * Base logic to get and cache a list of recipes for the given parts list
   * @return  Display recipe list
   */
  @SuppressWarnings("WeakerAccess")
  protected List<IDisplayableCastingRecipe> getRecipes(Collection<Entry<IMaterialItem>> parts) {
    if (multiRecipes == null) {
      List<FluidVolume> fluids = this.fluid.getFluids();
      if (fluids.isEmpty()) {
        multiRecipes = Collections.emptyList();
      } else {
        multiRecipes = parts.stream()
                            .filter(entry -> entry.getIntValue() > 0)
                            .map(entry -> {
                              IMaterialItem part = entry.getKey();
                              List<FluidVolume> recipeFluids = fluids;
                              int partCost = entry.getIntValue();
                              if (partCost != 1) {
                                recipeFluids = recipeFluids.stream()
                                                           .map(fluid -> new FluidVolume(fluid, fluid.getAmount() * partCost))
                                                           .collect(Collectors.toList());
                              }
                              return new DisplayCastingRecipe(getType(), Collections.singletonList(part.getItemstackWithMaterial(inputMaterial.get())), recipeFluids,
                                                              part.getItemstackWithMaterial(outputMaterial.get()), ICastingRecipe.calcCoolingTime(coolingTemperature, recipeFluids.get(0).getAmount()), true);
                          })
                          .collect(Collectors.toList());
      }
    }
    return multiRecipes;
  }

  /** Implementaiton for casting basins */
  public static class Basin extends CompositeCastingRecipe {
    public Basin(Identifier id, MaterialId inputId, FluidIngredient fluid, MaterialId outputId, int coolingTemperature) {
      super(RecipeTypes.CASTING_BASIN, id, inputId, fluid, outputId, coolingTemperature);
    }

    @Override
    protected int getMaterialItemCost(IMaterialItem item) {
      return MaterialItemCostLookup.getBasinCost(item);
    }

    @Override
    public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinCompositeSerializer.get();
    }

    @Override
    public List<IDisplayableCastingRecipe> getRecipes() {
      return getRecipes(MaterialItemCostLookup.getAllBasinParts());
    }
  }

  /** Implementaiton for casting tables */
  public static class Table extends CompositeCastingRecipe {
    public Table(Identifier id, MaterialId inputId, FluidIngredient fluid, MaterialId outputId, int coolingTemperature) {
      super(RecipeTypes.CASTING_TABLE, id, inputId, fluid, outputId, coolingTemperature);
    }

    @Override
    protected int getMaterialItemCost(IMaterialItem item) {
      return MaterialItemCostLookup.getTableCost(item);
    }

    @Override
    public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableCompositeSerializer.get();
    }

    @Override
    public List<IDisplayableCastingRecipe> getRecipes() {
      return getRecipes(MaterialItemCostLookup.getAllTableParts());
    }
  }

  /** Constructor factory for this casting recipe */
  public interface IFactory<T extends CompositeCastingRecipe> {
    T create(Identifier id, MaterialId input, FluidIngredient fluid, MaterialId output, int coolingTemperature);
  }

  /** Serializer for casting tables and basins */
  @RequiredArgsConstructor
  public static class Serializer<T extends CompositeCastingRecipe> extends RecipeSerializer<T> {
    private final IFactory<T> factory;

    @Override
    public T read(Identifier id, JsonObject json) {
      MaterialId input = MaterialRecipeSerializer.getMaterial(json, "input");
      FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
      MaterialId output = MaterialRecipeSerializer.getMaterial(json, "result");
      int coolingTemperature = JsonHelper.getInt(json, "temperature");
      return factory.create(id, input, fluid, output, coolingTemperature);
    }

    @Override
    public T read(Identifier id, PacketByteBuf buffer) {
      MaterialId input = new MaterialId(buffer.readString(Short.MAX_VALUE));
      FluidIngredient fluid = FluidIngredient.read(buffer);
      MaterialId output = new MaterialId(buffer.readString(Short.MAX_VALUE));
      int coolingTemperature = buffer.readVarInt();
      return factory.create(id, input, fluid, output, coolingTemperature);
    }

    @Override
    public void write(PacketByteBuf buffer, T recipe) {
      buffer.writeString(recipe.inputId.toString());
      recipe.fluid.write(buffer);
      buffer.writeString(recipe.outputId.toString());
      buffer.writeVarInt(recipe.coolingTemperature);
    }
  }
}
