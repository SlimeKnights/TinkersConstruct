package slimeknights.tconstruct.gadgets.data;

import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.recipe.CookingRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.misc.CommonTags;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.tools.TinkerModifiers;
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
  protected void generate(Consumer<RecipeJsonProvider> consumer) {
    // slime
    String folder = "gadgets/slimeboots/";
    for (SlimeType slime : SlimeType.values()) {
      Identifier name = location(folder + slime.asString());
      ShapedRecipeJsonFactory.create(TinkerGadgets.slimeBoots.get(slime))
                         .group("tconstruct:slime_boots")
                         .input('#', TinkerWorld.congealedSlime.get(slime))
                         .input('X', slime.getSlimeBallTag())
                         .pattern("X X")
                         .pattern("# #")
                         .criterion("has_item", conditionsFromTag(slime.getSlimeBallTag()))
                         .offerTo(consumer, name);
    }

    folder = "gadgets/slimesling/";
    for (SlimeType slime : SlimeType.TRUE_SLIME) {
      Identifier name = location(folder + slime.asString());
      ShapedRecipeJsonFactory.create(TinkerGadgets.slimeSling.get(slime))
                         .group("tconstruct:slimesling")
                         .input('#', Items.STRING)
                         .input('X', TinkerWorld.congealedSlime.get(slime))
                         .input('L', slime.getSlimeBallTag())
                         .pattern("#X#")
                         .pattern("L L")
                         .pattern(" L ")
                         .criterion("has_item", conditionsFromTag(slime.getSlimeBallTag()))
                         .offerTo(consumer, name);
    }

    // rails
    /* TODO: moving to tinkers' mechworks
    folder = "gadgets/rail/";
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.woodenRail, 4)
                       .key('#', ItemTags.PLANKS)
                       .key('X', CommonTags.RODS_WOODEN)
                       .patternLine("# #")
                       .patternLine("#X#")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(ItemTags.PLANKS))
                       .build(consumer, prefix(TinkerGadgets.woodenRail, folder));

    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.woodenDropperRail, 4)
                       .key('#', ItemTags.PLANKS)
                       .key('X', ItemTags.WOODEN_TRAPDOORS)
                       .patternLine("# #")
                       .patternLine("#X#")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(ItemTags.PLANKS))
                       .build(consumer, prefix(TinkerGadgets.woodenDropperRail, folder));
     */

    // stone
    /* TODO: moving to natura
    folder = "gadgets/stone/";
    ShapedRecipeBuilder.shapedRecipe(Blocks.JACK_O_LANTERN)
                       .key('#', Blocks.CARVED_PUMPKIN)
                       .key('X', TinkerGadgets.stoneTorch.get())
                       .patternLine("#")
                       .patternLine("X")
                       .addCriterion("has_item", hasItem(Blocks.CARVED_PUMPKIN))
                       .build(consumer, location(folder + "jack_o_lantern"));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneLadder.get(), 3)
                       .key('#', TinkerCommonTags.RODS_STONE)
                       .patternLine("# #")
                       .patternLine("###")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerCommonTags.RODS_STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneLadder, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneStick.get(), 4)
                       .key('#', Ingredient.fromItemListStream(Stream.of(
                         new Ingredient.TagList(CommonTags.STONE),
                         new Ingredient.TagList(CommonTags.COBBLESTONE))
                                                              ))
                       .patternLine("#")
                       .patternLine("#")
                       .addCriterion("has_item", hasItem(CommonTags.STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneStick, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneTorch.get(), 4)
                       .key('#', Ingredient.fromItemListStream(Stream.of(
                         new Ingredient.SingleItemList(new ItemStack(Items.COAL)),
                         new Ingredient.SingleItemList(new ItemStack(Items.CHARCOAL))
                                                                        )))
                       .key('X', TinkerCommonTags.RODS_STONE)
                       .patternLine("#")
                       .patternLine("X")
                       .addCriterion("has_item", hasItem(TinkerCommonTags.RODS_STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneTorch, folder));
    */

    // throw balls
    folder = "gadgets/throwball/";
    ShapedRecipeJsonFactory.create(TinkerGadgets.efln.get())
                       .input('#', CommonTags.GUNPOWDER)
                       .input('X', Items.FLINT)
                       .pattern(" # ")
                       .pattern("#X#")
                       .pattern(" # ")
                       .criterion("has_item", conditionsFromTag(CommonTags.DUSTS_GLOWSTONE))
                       .offerTo(consumer, prefix(TinkerGadgets.efln, folder));
    ShapedRecipeJsonFactory.create(TinkerGadgets.glowBall.get(), 8)
                       .input('#', Items.SNOWBALL)
                       .input('X', CommonTags.DUSTS_GLOWSTONE)
                       .pattern("###")
                       .pattern("#X#")
                       .pattern("###")
                       .criterion("has_item", conditionsFromTag(CommonTags.DUSTS_GLOWSTONE))
                       .offerTo(consumer, prefix(TinkerGadgets.glowBall, folder));

    // Shurikens
    folder = "gadgets/shuriken/";
    ShapedRecipeJsonFactory.create(TinkerGadgets.flintShuriken.get(), 4)
                        .input('X', Items.FLINT)
                        .pattern(" X ")
                        .pattern("X X")
                        .pattern(" X ")
                        .criterion("has_item", conditionsFromItem(Items.FLINT))
                        .offerTo(consumer, prefix(TinkerGadgets.flintShuriken, folder));
    ShapedRecipeJsonFactory.create(TinkerGadgets.quartzShuriken.get(), 4)
                        .input('X', Items.QUARTZ)
                        .pattern(" X ")
                        .pattern("X X")
                        .pattern(" X ")
                        .criterion("has_item", conditionsFromItem(Items.QUARTZ))
                        .offerTo(consumer, prefix(TinkerGadgets.quartzShuriken, folder));

    // piggybackpack
    folder = "gadgets/";
    ShapedRecipeJsonFactory.create(TinkerGadgets.piggyBackpack.get())
                       .input('#', CommonTags.RODS_WOODEN)
                       .input('X', CommonTags.LEATHER)
                       .pattern(" X ")
                       .pattern("# #")
                       .pattern(" X ")
                       .criterion("has_item", conditionsFromTag(CommonTags.RODS_WOODEN))
                       .offerTo(consumer, prefix(TinkerGadgets.piggyBackpack, folder));
    /* TODO: moving to natura
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.punji.get(), 3)
                       .key('#', Items.SUGAR_CANE)
                       .patternLine("# #")
                       .patternLine(" # ")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(Items.SUGAR_CANE))
                       .build(consumer, prefix(TinkerGadgets.punji, folder));
     */
    // frames
    folder = "gadgets/fancy_frame/";
    registerFrameRecipes(consumer, TinkerModifiers.silkyCloth, FrameType.JEWEL);
    registerFrameRecipes(consumer, TinkerMaterials.cobalt.getNugget(), FrameType.COBALT);
    registerFrameRecipes(consumer, TinkerMaterials.manyullyn.getNugget(), FrameType.MANYULLYN);
    registerFrameRecipes(consumer, Items.GOLD_NUGGET, FrameType.GOLD);
    Item clearFrame = TinkerGadgets.itemFrame.get(FrameType.CLEAR);
    ShapedRecipeJsonFactory.create(clearFrame)
                       .input('e', CommonTags.GLASS_PANES_COLORLESS)
                       .input('M', CommonTags.GLASS_COLORLESS)
                       .pattern(" e ")
                       .pattern("eMe")
                       .pattern(" e ")
                       .criterion("has_item", conditionsFromTag(CommonTags.GLASS_PANES_COLORLESS))
                       .group(locationString("fancy_item_frame"))
                       .offerTo(consumer, prefix(clearFrame, folder));

    // dried clay
    /* TODO: move to natura
    folder = "gadgets/building/";
    ShapedRecipeBuilder.shapedRecipe(TinkerCommons.driedClayBricks)
                       .key('b', TinkerCommons.driedBrick)
                       .patternLine("bb")
                       .patternLine("bb")
                       .addCriterion("has_item", hasItem(TinkerCommons.driedClay))
                       .build(consumer, prefix(TinkerCommons.driedClayBricks, folder));
    registerSlabStair(consumer, TinkerCommons.driedClay, folder, true);
    registerSlabStair(consumer, TinkerCommons.driedClayBricks, folder, true);
     */

    // TODO: natura support: use drying rack instead
    // slime drops
    folder = "gadgets/foods/";
    for (SlimeType slime : SlimeType.values()) {
      addCampfireCooking(consumer, TinkerCommons.slimeball.get(slime), TinkerGadgets.slimeDrop.get(slime), 0.35f, folder);
    }
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
  private void addCampfireCooking(Consumer<RecipeJsonProvider> consumer, ItemConvertible input, ItemConvertible output, float experience, String folder) {
    CookingRecipeJsonFactory.create(Ingredient.ofItems(input), output, experience, 600, RecipeSerializer.CAMPFIRE_COOKING)
                        .criterion("has_item", conditionsFromItem(input))
                        .offerTo(consumer, wrap(output, folder, "_campfire"));
  }

  /**
   * Adds a recipe to the campfire, furnace, and smoker
   * @param consumer    Recipe consumer
   * @param input       Recipe input
   * @param output      Recipe output
   * @param experience  Experience for the recipe
   * @param folder      Folder to store the recipe
   */
  private void addFoodCooking(Consumer<RecipeJsonProvider> consumer, ItemConvertible input, ItemConvertible output, float experience, String folder) {
    addCampfireCooking(consumer, input, output, experience, folder);
    // furnace is 200 ticks
    CriterionConditions criteria = conditionsFromItem(input);
    CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(input), output, experience, 200)
                        .criterion("has_item", criteria)
                        .offerTo(consumer, wrap(output, folder, "_furnace"));
    // smoker 100 ticks
    CookingRecipeJsonFactory.create(Ingredient.ofItems(input), output, experience, 100, RecipeSerializer.SMOKING)
                        .criterion("has_item", criteria)
                        .offerTo(consumer, wrap(output, folder, "_smoker"));
  }

  /**
   * Adds a recipe for an item frame type
   * @param consumer  Recipe consumer
   * @param edges     Edge item
   * @param type      Frame type
   */
  private void registerFrameRecipes(Consumer<RecipeJsonProvider> consumer, ItemConvertible edges, FrameType type) {
    Item frame = TinkerGadgets.itemFrame.get(type);
    ShapedRecipeJsonFactory.create(frame)
                       .input('e', edges)
                       .input('M', Items.OBSIDIAN)
                       .pattern(" e ")
                       .pattern("eMe")
                       .pattern(" e ")
                       .criterion("has_item", conditionsFromItem(edges))
                       .group(locationString("fancy_item_frame"))
                       .offerTo(consumer, prefix(frame, "gadgets/fancy_frame/"));

  }
}
