package slimeknights.tconstruct.library.recipe.melting;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for a melting recipe that melts a {@link IMaterialItem} into the proper fluid
 */
@AllArgsConstructor(staticName = "melting")
public class MaterialMeltingRecipeBuilder extends AbstractRecipeBuilder<MaterialMeltingRecipeBuilder> {
  private final IMaterialItem item;
  private final int cost;

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, Objects.requireNonNull(item.asItem().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "melting");
    consumer.accept(new Result(id, advancementId));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serialize(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }
      json.addProperty("item", Objects.requireNonNull(item.asItem().getRegistryName()).toString());
      json.addProperty("item_cost", cost);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.materialMeltingSerializer.get();
    }
  }
}
