package slimeknights.tconstruct.gadgets.data;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class GadgetRecipeProvider extends BaseRecipeProvider {
  public GadgetRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Gadget Recipes";
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    // slime
    String folder = "gadgets/slimesling/";
    for (SlimeType slime : SlimeType.TRUE_SLIME) {
      ResourceLocation name = modResource(folder + slime.getString());
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeSling.get(slime))
                         .setGroup("tconstruct:slimesling")
                         .key('#', Items.STRING)
                         .key('X', TinkerWorld.congealedSlime.get(slime))
                         .key('L', slime.getSlimeballTag())
                         .patternLine("#X#")
                         .patternLine("L L")
                         .patternLine(" L ")
                         .addCriterion("has_item", hasItem(slime.getSlimeballTag()))
                         .build(consumer, name);
    }

    // throw balls
    folder = "gadgets/throwball/";
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.efln.get())
                       .key('#', Tags.Items.GUNPOWDER)
                       .key('X', Items.FLINT)
                       .patternLine(" # ")
                       .patternLine("#X#")
                       .patternLine(" # ")
                       .addCriterion("has_item", hasItem(Tags.Items.DUSTS_GLOWSTONE))
                       .build(consumer, prefix(TinkerGadgets.efln, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.glowBall.get(), 8)
                       .key('#', Items.SNOWBALL)
                       .key('X', Tags.Items.DUSTS_GLOWSTONE)
                       .patternLine("###")
                       .patternLine("#X#")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(Tags.Items.DUSTS_GLOWSTONE))
                       .build(consumer, prefix(TinkerGadgets.glowBall, folder));

    // Shurikens
    folder = "gadgets/shuriken/";
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.flintShuriken.get(), 4)
                        .key('X', Items.FLINT)
                        .patternLine(" X ")
                        .patternLine("X X")
                        .patternLine(" X ")
                        .addCriterion("has_item", hasItem(Items.FLINT))
                        .build(consumer, prefix(TinkerGadgets.flintShuriken, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.quartzShuriken.get(), 4)
                        .key('X', Items.QUARTZ)
                        .patternLine(" X ")
                        .patternLine("X X")
                        .patternLine(" X ")
                        .addCriterion("has_item", hasItem(Items.QUARTZ))
                        .build(consumer, prefix(TinkerGadgets.quartzShuriken, folder));

    // piggybackpack
    folder = "gadgets/";
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.piggyBackpack.get())
                       .key('P', TinkerMaterials.pigIron.getIngotTag())
                       .key('S', Items.SADDLE)
                       .patternLine("P")
                       .patternLine("S")
                       .addCriterion("has_item", hasItem(Items.SADDLE))
                       .build(consumer, prefix(TinkerGadgets.piggyBackpack, folder));

    // frames
    folder = "gadgets/fancy_frame/";
    frameCrafting(consumer, Tags.Items.NUGGETS_GOLD, FrameType.GOLD);
    frameCrafting(consumer, TinkerMaterials.manyullyn.getNuggetTag(), FrameType.MANYULLYN);
    frameCrafting(consumer, TinkerTags.Items.NUGGETS_NETHERITE, FrameType.NETHERITE);
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.itemFrame.get(FrameType.DIAMOND))
                       .key('e', TinkerCommons.obsidianPane)
                       .key('M', Tags.Items.GEMS_DIAMOND)
                       .patternLine(" e ")
                       .patternLine("eMe")
                       .patternLine(" e ")
                       .addCriterion("has_item", hasItem(Tags.Items.GEMS_DIAMOND))
                       .setGroup(modPrefix("fancy_item_frame"))
                       .build(consumer, modResource("gadgets/frame/" + FrameType.DIAMOND.getString()));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.itemFrame.get(FrameType.CLEAR))
                       .key('e', Tags.Items.GLASS_PANES_COLORLESS)
                       .key('M', Tags.Items.GLASS_COLORLESS)
                       .patternLine(" e ")
                       .patternLine("eMe")
                       .patternLine(" e ")
                       .addCriterion("has_item", hasItem(Tags.Items.GLASS_PANES_COLORLESS))
                       .setGroup(modPrefix("fancy_item_frame"))
                       .build(consumer, modResource(folder + FrameType.CLEAR.getString()));
    Item goldFrame = TinkerGadgets.itemFrame.get(FrameType.GOLD);
    Item reversedFrame = TinkerGadgets.itemFrame.get(FrameType.REVERSED_GOLD);
    ShapelessRecipeBuilder.shapelessRecipe(reversedFrame)
                          .addIngredient(goldFrame)
                          .addIngredient(Items.REDSTONE_TORCH)
                          .addCriterion("has_item", hasItem(goldFrame))
                          .setGroup(modPrefix("reverse_fancy_item_frame"))
                          .build(consumer, modResource(folder + FrameType.REVERSED_GOLD.getString()));
    ShapelessRecipeBuilder.shapelessRecipe(goldFrame)
                          .addIngredient(reversedFrame)
                          .addIngredient(Items.REDSTONE_TORCH)
                          .addCriterion("has_item", hasItem(reversedFrame))
                          .setGroup(modPrefix("reverse_fancy_item_frame"))
                          .build(consumer, modResource(folder + "reversed_reversed_gold"));

    String cakeFolder = "gadgets/cake/";
    TinkerGadgets.cake.forEach((slime, cake) -> {
      Item bucket = TinkerFluids.slime.get(slime).asItem();
      ShapedRecipeBuilder.shapedRecipe(cake)
                         .key('M', bucket)
                         .key('S', slime == SlimeType.BLOOD ? Ingredient.fromTag(Tags.Items.DUSTS_GLOWSTONE) : Ingredient.fromItems(Items.SUGAR))
                         .key('E', Items.EGG)
                         .key('W', TinkerWorld.slimeTallGrass.get(slime))
                         .patternLine("MMM").patternLine("SES").patternLine("WWW")
                         .addCriterion("has_slime", hasItem(bucket))
                         .build(consumer, modResource(cakeFolder + slime.getString()));
    });
    Item bucket = TinkerFluids.magma.asItem();
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.magmaCake)
                       .key('M', bucket)
                       .key('S', Ingredient.fromTag(Tags.Items.DUSTS_GLOWSTONE))
                       .key('E', Items.EGG)
                       .key('W', Blocks.CRIMSON_ROOTS)
                       .patternLine("MMM").patternLine("SES").patternLine("WWW")
                       .addCriterion("has_slime", hasItem(bucket))
                       .build(consumer, modResource(cakeFolder + "magma"));
  }


  /* Helpers */

  /**
   * Adds a campfire cooking recipe
   * @param consumer    Recipe consumer
   * @param input       Recipe input
   * @param output      Recipe output
   * @param experience  Experience for the recipe
   * @param folder      Folder to store the recipe
   */
  private void campfireCooking(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider output, float experience, String folder) {
    CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(input), output, experience, 600, IRecipeSerializer.CAMPFIRE_COOKING)
                        .addCriterion("has_item", hasItem(input))
                        .build(consumer, wrap(output.asItem(), folder, "_campfire"));
  }

  /**
   * Adds a recipe to the campfire, furnace, and smoker
   * @param consumer    Recipe consumer
   * @param input       Recipe input
   * @param output      Recipe output
   * @param experience  Experience for the recipe
   * @param folder      Folder to store the recipe
   */
  private void foodCooking(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider output, float experience, String folder) {
    campfireCooking(consumer, input, output, experience, folder);
    // furnace is 200 ticks
    ICriterionInstance criteria = hasItem(input);
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, experience, 200)
                        .addCriterion("has_item", criteria)
                        .build(consumer, wrap(output.asItem(), folder, "_furnace"));
    // smoker 100 ticks
    CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(input), output, experience, 100, IRecipeSerializer.SMOKING)
                        .addCriterion("has_item", criteria)
                        .build(consumer, wrap(output.asItem(), folder, "_smoker"));
  }

  /**
   * Adds a recipe for an item frame type
   * @param consumer  Recipe consumer
   * @param edges     Edge item
   * @param type      Frame type
   */
  private void frameCrafting(Consumer<IFinishedRecipe> consumer, ITag<Item> edges, FrameType type) {
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.itemFrame.get(type))
                       .key('e', edges)
                       .key('M', TinkerCommons.obsidianPane)
                       .patternLine(" e ")
                       .patternLine("eMe")
                       .patternLine(" e ")
                       .addCriterion("has_item", hasItem(edges))
                       .setGroup(modPrefix("fancy_item_frame"))
                       .build(consumer, modResource("gadgets/frame/" + type.getString()));
  }
}
