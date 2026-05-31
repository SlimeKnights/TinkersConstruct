package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import slimeknights.mantle.compat.neoforged.neoforge.common.crafting.IIngredientSerializer;
import net.neoforged.neoforge.common.crafting.IngredientType;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/** Item ingredient matching items with a block form in the given tag */
@RequiredArgsConstructor
public class BlockTagIngredient implements ICustomIngredient {
  private final TagKey<Block> tag;
  @Nullable
  private Set<Item> matchingItems;
  @Nullable
  private ItemStack[] items;

  public static Ingredient of(TagKey<Block> tag) {
    return new BlockTagIngredient(tag).toVanilla();
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && getMatchingItems().contains(stack.getItem());
  }

  @Override
  public boolean isSimple() {
    return true;
  }

  /** Gets the ordered matching items set */
  private Set<Item> getMatchingItems() {
    if (matchingItems == null) {
      matchingItems = RegistryHelper.getTagValueStream(BuiltInRegistries.BLOCK, tag)
                                    .map(Block::asItem)
                                    .filter(item -> item != Items.AIR)
                                    .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }
    return matchingItems;
  }

  @Override
  public Stream<ItemStack> getItems() {
    if (items == null) {
      items = getMatchingItems().stream().map(ItemStack::new).toArray(ItemStack[]::new);
      if (items.length == 0) {
        ItemStack barrier = new ItemStack(Blocks.BARRIER);
        barrier.set(DataComponents.CUSTOM_NAME, Component.literal("Empty Tag: " + tag.location()));
        items = new ItemStack[] { barrier };
      }
    }
    return Stream.of(items);
  }

  @Override
  public IngredientType<?> getType() {
    return TinkerCommons.blockTagIngredient.get();
  }

  public JsonElement toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("type", Serializer.ID.toString());
    json.add("tag", Loadables.BLOCK_TAG.serialize(tag));
    return json;
  }

  @Override
  public boolean equals(Object object) {
    return this == object || object instanceof BlockTagIngredient that && tag.equals(that.tag);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tag);
  }

  /** Serializer instance */
  public enum Serializer implements IIngredientSerializer<BlockTagIngredient> {
    INSTANCE;

    public static final ResourceLocation ID = TConstruct.getResource("block_tag");

    @Override
    public BlockTagIngredient parse(JsonObject json) {
      return new BlockTagIngredient(Loadables.BLOCK_TAG.getIfPresent(json, "tag"));
    }

    @Override
    public void write(FriendlyByteBuf buffer, BlockTagIngredient ingredient) {
      Loadables.BLOCK_TAG.encode(buffer, ingredient.tag);
    }

    @Override
    public BlockTagIngredient parse(FriendlyByteBuf buffer) {
      return new BlockTagIngredient(Loadables.BLOCK_TAG.decode(buffer));
    }
  }
}
