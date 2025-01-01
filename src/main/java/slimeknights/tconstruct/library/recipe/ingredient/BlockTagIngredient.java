package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Item ingredient matching items with a block form in the given tag */
@RequiredArgsConstructor
public class BlockTagIngredient extends AbstractIngredient {
  private final TagKey<Block> tag;
  @Nullable
  private Set<Item> matchingItems;
  @Nullable
  private ItemStack[] items;
  @Nullable
  private IntList stackingIds;

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && getMatchingItems().contains(stack.getItem());
  }

  @Override
  public boolean isSimple() {
    return true;
  }

  @Override
  protected void invalidate() {
    this.matchingItems = null;
    this.items = null;
    this.stackingIds = null;
  }

  /** Gets the ordered matching items set */
  private Set<Item> getMatchingItems() {
    if (matchingItems == null || checkInvalidation()) {
      markValid();
      matchingItems = RegistryHelper.getTagValueStream(BuiltInRegistries.BLOCK, tag)
                                    .map(Block::asItem)
                                    .filter(item -> item != Items.AIR)
                                    .collect(Collectors.toCollection(LinkedHashSet::new));
    }
    return matchingItems;
  }

  @Override
  public ItemStack[] getItems() {
    if (items == null || checkInvalidation()) {
      markValid();
      items = getMatchingItems().stream().map(ItemStack::new).toArray(ItemStack[]::new);
    }
    return items;
  }

  @Override
  public IntList getStackingIds() {
    if (stackingIds == null || checkInvalidation()) {
      markValid();
      Set<Item> items = getMatchingItems();
      stackingIds = new IntArrayList(items.size());
      for (Item item : items) {
        stackingIds.add(BuiltInRegistries.ITEM.getId(item));
      }
      stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
    }
    return stackingIds;
  }

  @Override
  public IIngredientSerializer<? extends Ingredient> getSerializer() {
    return Serializer.INSTANCE;
  }

  @Override
  public JsonElement toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("type", Serializer.ID.toString());
    json.add("tag", Loadables.BLOCK_TAG.serialize(tag));
    return json;
  }

  /** Serializer instance */
  public enum Serializer implements IIngredientSerializer<Ingredient> {
    INSTANCE;

    public static final ResourceLocation ID = TConstruct.getResource("block_tag");

    @Override
    public Ingredient parse(JsonObject json) {
      return new BlockTagIngredient(Loadables.BLOCK_TAG.getIfPresent(json, "tag"));
    }

    @Override
    public void write(FriendlyByteBuf buffer, Ingredient ingredient) {
      // just write the item list, will become a vanilla ingredient client side
      buffer.writeCollection(Arrays.asList(ingredient.getItems()), FriendlyByteBuf::writeItem);
    }

    @Override
    public Ingredient parse(FriendlyByteBuf buffer) {
      int size = buffer.readVarInt();
      return Ingredient.fromValues(Stream.generate(() -> new Ingredient.ItemValue(buffer.readItem())).limit(size));
    }
  }
}
