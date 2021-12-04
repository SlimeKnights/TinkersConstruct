package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

/** Ingredient that matches a container of fluid */
public class FluidContainerIngredient extends Ingredient {
  public static final ResourceLocation ID = TConstruct.getResource("fluid_container");
  public static final Serializer SERIALIZER = new Serializer();

  /** Ingredient to use for matching */
  private final FluidIngredient fluidIngredient;
  /** Internal ingredient to display the ingredient recipe viewers */
  @Nullable
  private final Ingredient display;
  private ItemStack[] displayStacks;
  protected FluidContainerIngredient(FluidIngredient fluidIngredient, @Nullable Ingredient display) {
    super(Stream.of());
    this.fluidIngredient = fluidIngredient;
    this.display = display;
  }

  /** Creates an instance from a fluid ingredient with a display container */
  public static FluidContainerIngredient fromIngredient(FluidIngredient ingredient, Ingredient display) {
    return new FluidContainerIngredient(ingredient, display);
  }

  /** Creates an instance from a fluid ingredient with no display, not recommended */
  public static FluidContainerIngredient fromIngredient(FluidIngredient ingredient) {
    return new FluidContainerIngredient(ingredient, null);
  }

  /** Creates an instance from a fluid ingredient with a display container */
  public static FluidContainerIngredient fromFluid(FluidObject<?> fluid, boolean forgeTag) {
    return fromIngredient(FluidIngredient.of(forgeTag ? fluid.getForgeTag() : fluid.getLocalTag(), FluidAttributes.BUCKET_VOLUME), Ingredient.fromItems(fluid));
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    // first, must have a fluid capability
    return stack != null && !stack.isEmpty() && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve().flatMap(cap -> {
      // second, must contain enough fluid
      if (cap.getTanks() == 1) {
        FluidStack contained = cap.getFluidInTank(0);
        if (fluidIngredient.getAmount(contained.getFluid()) == contained.getAmount() && fluidIngredient.test(contained.getFluid())) {
          // so far so good, from this point on we are forced to make copies as we need to try draining, so copy and fetch the copy's cap
          ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
          return copy.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
        }
      }
      return Optional.empty();
    }).filter(cap -> {
      // alright, we know it has the fluid, the question is just whether draining the fluid will give us the desired result
      Fluid fluid = cap.getFluidInTank(0).getFluid();
      int amount = fluidIngredient.getAmount(fluid);
      FluidStack drained = cap.drain(amount, FluidAction.EXECUTE);
      // we need an exact match, and we need the resulting container item to be the same as the item stack's container item
      return drained.getFluid() == fluid && drained.getAmount() == amount && ItemStack.areItemStacksEqual(stack.getContainerItem(), cap.getContainer());
    }).isPresent();
  }

  @Override
  public ItemStack[] getMatchingStacks() {
    if (displayStacks == null) {
      // no container? unfortunately hard to display this recipe so show nothing
      if (display == null) {
        displayStacks = new ItemStack[0];
      } else {
        displayStacks = display.getMatchingStacks();
      }
    }
    return displayStacks;
  }

  @Override
  public JsonElement serialize() {
    JsonElement element = fluidIngredient.serialize();
    JsonObject json;
    if (element.isJsonObject()) {
      json = element.getAsJsonObject();
    } else {
      json = new JsonObject();
      json.add("fluid", element);
    }
    json.addProperty("type", ID.toString());
    if (display != null) {
      json.add("display", display.serialize());
    }
    return json;
  }

  @Override
  protected void invalidate() {
    super.invalidate();
    this.displayStacks = null;
  }

  @Override
  public boolean isSimple() {
    return false;
  }

  @Override
  public IIngredientSerializer<? extends Ingredient> getSerializer() {
    return SERIALIZER;
  }

  /** Serializer logic */
  private static class Serializer implements IIngredientSerializer<FluidContainerIngredient> {
    @Override
    public FluidContainerIngredient parse(JsonObject json) {
      FluidIngredient fluidIngredient;
      // if we have fluid, its a nested ingredient. Otherwise this object itself is the ingredient
      if (json.has("fluid")) {
        fluidIngredient = FluidIngredient.deserialize(json, "fluid");
      } else {
        fluidIngredient = FluidIngredient.deserialize((JsonElement) json, "fluid");
      }
      Ingredient display = null;
      if (json.has("display")) {
        display = Ingredient.deserialize(JsonHelper.getElement(json, "display"));
      }
      return new FluidContainerIngredient(fluidIngredient, display);
    }

    @Override
    public FluidContainerIngredient parse(PacketBuffer buffer) {
      FluidIngredient fluidIngredient = FluidIngredient.read(buffer);
      Ingredient display = null;
      if (buffer.readBoolean()) {
        display = Ingredient.read(buffer);
      }
      return new FluidContainerIngredient(fluidIngredient, display);
    }

    @Override
    public void write(PacketBuffer buffer, FluidContainerIngredient ingredient) {
      ingredient.fluidIngredient.write(buffer);
      if (ingredient.display != null) {
        buffer.writeBoolean(true);
        ingredient.display.write(buffer);
      } else {
        buffer.writeBoolean(false);
      }
    }
  }
}
