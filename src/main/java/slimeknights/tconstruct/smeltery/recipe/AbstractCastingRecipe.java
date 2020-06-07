package slimeknights.tconstruct.smeltery.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractCastingRecipe implements IRecipe<IInventory> {
  protected final IRecipeType<?> type;
  protected final ResourceLocation id;
  protected final String group;
  private final Ingredient cast;
  private final FluidStack fluid;
  private final ItemStack result;
  protected final int coolingTime;
  private final boolean consumed;
  private final boolean switchSlots;

  public AbstractCastingRecipe(IRecipeType<?> typeIn, ResourceLocation idIn, String groupIn,
                               @Nullable Ingredient ingredient, @Nonnull FluidStack fluidIn, ItemStack result,
                               int coolingTime, boolean consumed, boolean switchSlots) {
    this.type = typeIn;
    this.id = idIn;
    this.group = groupIn;
    this.cast = ingredient;
    this.fluid = fluidIn;
    this.result = result;
    this.coolingTime = coolingTime;
    this.consumed = consumed;
    this.switchSlots = switchSlots;
  }

  @Override
  public boolean matches(IInventory inv, World worldIn) {
    return this.cast.test(inv.getStackInSlot(0));
  }

  public boolean matches(Fluid fluid, IInventory inv, World world) {
    return (this.fluid.getFluid() == fluid || fluid == Fluids.EMPTY) && this.matches(inv, world);
  }

  @Override
  public ItemStack getCraftingResult(IInventory inv) {
    return result;
  }

  @Override
  public boolean canFit(int width, int height) {
    return true;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.result;
  }

  @Override
  public ResourceLocation getId() {
    return this.id;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return null;
  }

  @Override
  public IRecipeType<?> getType() {
    return type;
  }

  public int getCoolingTime() {
    return this.coolingTime;
  }

  public int getFluidAmount() {
    return this.fluid.getAmount();
  }

  public FluidStack getFluidStack() {
    return this.fluid;
  }

  public Fluid getFluid() {
    return this.fluid.getFluid();
  }

  public Ingredient getCast() {
    return cast;
  }

  /**
   * Does this recipe consume/destroy the cast ingredient.
   * @return  Whether to destroy cast input.
   */
  public boolean consumesCast() {
    return this.consumed;
  }

  /**
   * Does this recipe's output get put into the input slot
   * @return  Whether to switch to input after casting
   */
  public boolean switchSlots() {
    return this.switchSlots;
  }

  /**
   * Keeps casting recipes out of the recipe book.
   */
  @Override
  public boolean isDynamic() {
    return true;
  }

  public static class Serializer<T extends AbstractCastingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>>
    implements IRecipeSerializer<AbstractCastingRecipe> {
    private final Serializer.IFactory<T> factory;

    public Serializer(Serializer.IFactory<T> factoryIn) {
      this.factory = factoryIn;
    }

    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {
      Ingredient cast = Ingredient.EMPTY;
      String s = JSONUtils.getString(json, "group", "");
      boolean consumed = false;
      boolean switchSlots = JSONUtils.getBoolean(json, "switchslots", false);
      if (json.has("cast")) {
        JsonElement jsonelement = JSONUtils.getJsonObject(json, "cast");
        cast = Ingredient.deserialize(jsonelement);
        consumed = JSONUtils.getBoolean(json, "castconsumed", false);
      }

      if (!json.has("fluidstack"))
        throw new JsonSyntaxException("Missing fluid input definition!");
      JsonObject jsonFluid = JSONUtils.getJsonObject(json, "fluidstack");
      Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(JSONUtils.getString(jsonFluid, "fluid")));
      int amount = JSONUtils.getInt(jsonFluid, "amount");
      FluidStack fluidStack = new FluidStack(fluid, amount);
      ItemStack item = new ItemStack(JSONUtils.getItem(json, "result"));
      int coolingtime;
      if (!json.has("coolingtime")) {
        int time = 24;
        int temperature = fluidStack.getFluid().getAttributes().getTemperature() - 300;
        coolingtime = time + (temperature * fluidStack.getAmount()) / 1600;
      }
      else {
        coolingtime = JSONUtils.getInt(json, "coolingtime");
      }
      return this.factory.create(recipeId, s, cast, fluidStack, item, coolingtime, consumed, switchSlots);
    }

    @Nullable
    @Override
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
      // TODO: Magic number go away
      String s = buffer.readString(32767);
      Ingredient cast = Ingredient.read(buffer);
      FluidStack fluidStack = FluidStack.readFromPacket(buffer);
      ItemStack output = buffer.readItemStack();
      int coolingtime = buffer.readInt();
      boolean consumed = buffer.readBoolean();
      boolean switchSlots = buffer.readBoolean();
      return this.factory.create(recipeId, s, cast, fluidStack, output, coolingtime, consumed, switchSlots);
    }

    @Override
    public void write(PacketBuffer buffer, AbstractCastingRecipe recipe) {
      buffer.writeString(recipe.group);
      recipe.cast.write(buffer);
      recipe.fluid.writeToPacket(buffer);
      buffer.writeItemStack(recipe.result);
      buffer.writeInt(recipe.coolingTime);
      buffer.writeBoolean(recipe.consumed);
      buffer.writeBoolean(recipe.switchSlots);
    }

    public interface IFactory<T> {
      T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, @Nonnull FluidStack fluidIn,
               ItemStack result, int coolingTime, boolean consumed, boolean switchSlots);
    }
  }
}
