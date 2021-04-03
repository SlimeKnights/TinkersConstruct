package slimeknights.tconstruct.common.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.Util;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Ingredient that matches everything from another ingredient, without a second
 */
public class IngredientWithout extends Ingredient {
  public static final ResourceLocation ID = Util.getResource("without");
  public static final IIngredientSerializer<IngredientWithout> SERIALIZER = new Serializer();

  private final Ingredient base;
  private final Ingredient without;
  private ItemStack[] filteredMatchingStacks;
  private IntList packedMatchingStacks;
  public IngredientWithout(Ingredient base, Ingredient without) {
    super(Stream.empty());
    this.base = base;
    this.without = without;
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return false;
    }
    return base.test(stack) && !without.test(stack);
  }

  @Override
  public ItemStack[] getMatchingStacks() {
    if (this.filteredMatchingStacks == null) {
      this.filteredMatchingStacks = Arrays.stream(base.getMatchingStacks())
                                          .filter(stack -> !without.test(stack))
                                          .toArray(ItemStack[]::new);
    }
    return filteredMatchingStacks;
  }

  @Override
  public boolean hasNoMatchingItems() {
    return getMatchingStacks().length == 0;
  }

  @Override
  public boolean isSimple() {
    return base.isSimple() && without.isSimple();
  }

  @Override
  protected void invalidate() {
    super.invalidate();
    this.filteredMatchingStacks = null;
    this.packedMatchingStacks = null;
  }

  @Override
  public IntList getValidItemStacksPacked() {
    if (this.packedMatchingStacks == null) {
      ItemStack[] matchingStacks = getMatchingStacks();
      this.packedMatchingStacks = new IntArrayList(matchingStacks.length);
      for(ItemStack stack : matchingStacks) {
        this.packedMatchingStacks.add(RecipeItemHelper.pack(stack));
      }
      this.packedMatchingStacks.sort(IntComparators.NATURAL_COMPARATOR);
    }
    return packedMatchingStacks;
  }

  @Override
  public JsonElement serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("type", ID.toString());
    json.add("base", base.serialize());
    json.add("without", without.serialize());
    return json;
  }

  @Override
  public IIngredientSerializer<IngredientWithout> getSerializer() {
    return SERIALIZER;
  }

  private static class Serializer implements IIngredientSerializer<IngredientWithout> {
    @Override
    public IngredientWithout parse(JsonObject json) {
      Ingredient base = Ingredient.deserialize(JsonHelper.getElement(json, "base"));
      Ingredient without = Ingredient.deserialize(JsonHelper.getElement(json, "without"));
      return new IngredientWithout(base, without);
    }

    @Override
    public IngredientWithout parse(PacketBuffer buffer) {
      Ingredient base = Ingredient.read(buffer);
      Ingredient without = Ingredient.read(buffer);
      return new IngredientWithout(base, without);
    }

    @Override
    public void write(PacketBuffer buffer, IngredientWithout ingredient) {
      CraftingHelper.write(buffer, ingredient.base);
      CraftingHelper.write(buffer, ingredient.without);
    }
  }
}
