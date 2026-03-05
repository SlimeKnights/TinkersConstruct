package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import java.util.BitSet;
import java.util.function.Consumer;

/** Builder for {@link FixedMaterialSwappingRecipe} and {@link PartSwappingOverrideRecipe}. */
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "tools")
public class MaterialSwappingRecipeBuilder extends AbstractRecipeBuilder<MaterialSwappingRecipeBuilder> {
  /** Tools that support this recipe */
  private final Ingredient tools;
  @Setter
  private int maxStackSize = 16;
  /** List of indices swapped by this recipe */
  private final BitSet indices = new BitSet();

  /** Part to swap, used by part override */
  @Setter
  private IToolPart part = null;

  /** Ingredient for the input part, used by fixed */
  private SizedIngredient ingredient = SizedIngredient.EMPTY;
  /** Material to swap to, used by fixed */
  private MaterialVariantId material = IMaterial.UNKNOWN_ID;
  /** Repair value on swapping, used by fixed */
  @Setter
  private int repairValue = 0;

  /** Creates a builder for the given tool */
  public static MaterialSwappingRecipeBuilder tool(ItemLike tool) {
    return tools(Ingredient.of(tool));
  }

  /** Creates a builder for the given tool */
  public static MaterialSwappingRecipeBuilder tools(TagKey<Item> tag) {
    return tools(Ingredient.of(tag));
  }

  /** Adds the given index to the recipe */
  public MaterialSwappingRecipeBuilder index(int index) {
    indices.set(index);
    return this;
  }

  /** Sets the material for this builder */
  public MaterialSwappingRecipeBuilder material(MaterialVariantId material, SizedIngredient ingredient) {
    this.material = material;
    this.ingredient = ingredient;
    return this;
  }

  /** Sets the material for this builder */
  public MaterialSwappingRecipeBuilder material(MaterialVariantId material, ItemLike item) {
    return material(material, SizedIngredient.fromItems(item));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, Loadables.ITEM.getKey(tools.getItems()[0].getItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    int[] indices = this.indices.stream().toArray();
    if (indices.length == 0) {
      throw new IllegalStateException("Must set index");
    }
    if (part != null) {
      if (ingredient != SizedIngredient.EMPTY) {
        throw new IllegalStateException("Cannot set both part and ingredient");
      }
      consumer.accept(new LoadableFinishedRecipe<>(new PartSwappingOverrideRecipe(id, tools, maxStackSize, part, indices), PartSwappingOverrideRecipe.LOADER, null));
    } else {
      consumer.accept(new LoadableFinishedRecipe<>(new FixedMaterialSwappingRecipe(id, tools, maxStackSize, ingredient, material, indices, repairValue), FixedMaterialSwappingRecipe.LOADER, null));
    }
  }
}
