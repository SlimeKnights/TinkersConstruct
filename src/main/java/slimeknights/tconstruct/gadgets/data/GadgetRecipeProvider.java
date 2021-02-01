package slimeknights.tconstruct.gadgets.data;

import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import slimeknights.mantle.recipe.crafting.ShapedFallbackRecipeBuilder;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.StickySlimeBlock.SlimeType;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class GadgetRecipeProvider extends BaseRecipeProvider {
  public GadgetRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    // slime
    String folder = "gadgets/slimeboots/";
    ShapedFallbackRecipeBuilder slimeBoots = ShapedFallbackRecipeBuilder.fallback(
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeBoots.get(SlimeType.GREEN))
                         .setGroup("tconstruct:slime_boots")
                         .key('#', TinkerTags.Items.CONGEALED_SLIME)
                         .key('X', Tags.Items.SLIMEBALLS)
                         .patternLine("X X")
                         .patternLine("# #")
                         .addCriterion("has_item", hasItem(Tags.Items.SLIMEBALLS)));
    for (SlimeType slime : SlimeType.TINKER) {
      ResourceLocation name = location(folder + slime.getString());
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeBoots.get(slime))
                         .setGroup("tconstruct:slime_boots")
                         .key('#', TinkerWorld.congealedSlime.get(slime))
                         .key('X', slime.getSlimeBallTag())
                         .patternLine("X X")
                         .patternLine("# #")
                         .addCriterion("has_item", hasItem(slime.getSlimeBallTag()))
                         .build(consumer, name);
      slimeBoots.addAlternative(name);
    }
    slimeBoots.build(consumer, location(folder + "green"));

    folder = "gadgets/slimesling/";
    ShapedFallbackRecipeBuilder slimeSling = ShapedFallbackRecipeBuilder.fallback(
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeSling.get(SlimeType.GREEN))
                         .setGroup("tconstruct:slimesling")
                         .key('#', Tags.Items.STRING)
                         .key('X', TinkerTags.Items.CONGEALED_SLIME)
                         .key('L', Tags.Items.SLIMEBALLS)
                         .patternLine("#X#")
                         .patternLine("L L")
                         .patternLine(" L ")
                         .addCriterion("has_item", hasItem(Tags.Items.SLIMEBALLS)));
    for (SlimeType slime : SlimeType.TINKER) {
      ResourceLocation name = location(folder + slime.getString());
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeSling.get(slime))
                         .setGroup("tconstruct:slimesling")
                         .key('#', Items.STRING)
                         .key('X', TinkerWorld.congealedSlime.get(slime))
                         .key('L', slime.getSlimeBallTag())
                         .patternLine("#X#")
                         .patternLine("L L")
                         .patternLine(" L ")
                         .addCriterion("has_item", hasItem(slime.getSlimeBallTag()))
                         .build(consumer, name);
      slimeSling.addAlternative(name);
    }
    slimeSling.build(consumer, location(folder + "green"));

    // rails
    folder = "gadgets/rail/";
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.woodenRail, 4)
                       .key('#', ItemTags.PLANKS)
                       .key('X', Tags.Items.RODS_WOODEN)
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

    // stone
    folder = "gadgets/stone/";
    ShapedRecipeBuilder.shapedRecipe(Blocks.JACK_O_LANTERN)
                       .key('#', Blocks.CARVED_PUMPKIN)
                       .key('X', TinkerGadgets.stoneTorch.get())
                       .patternLine("#")
                       .patternLine("X")
                       .addCriterion("has_item", hasItem(Blocks.CARVED_PUMPKIN))
                       .build(consumer, location(folder + "jack_o_lantern"));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneLadder.get(), 3)
                       .key('#', TinkerTags.Items.RODS_STONE)
                       .patternLine("# #")
                       .patternLine("###")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerTags.Items.RODS_STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneLadder, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneStick.get(), 4)
                       .key('#', Ingredient.fromItemListStream(Stream.of(
                         new Ingredient.TagList(Tags.Items.STONE),
                         new Ingredient.TagList(Tags.Items.COBBLESTONE))
                                                              ))
                       .patternLine("#")
                       .patternLine("#")
                       .addCriterion("has_item", hasItem(Tags.Items.STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneStick, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneTorch.get(), 4)
                       .key('#', Ingredient.fromItemListStream(Stream.of(
                         new Ingredient.SingleItemList(new ItemStack(Items.COAL)),
                         new Ingredient.SingleItemList(new ItemStack(Items.CHARCOAL))
                                                                        )))
                       .key('X', TinkerTags.Items.RODS_STONE)
                       .patternLine("#")
                       .patternLine("X")
                       .addCriterion("has_item", hasItem(TinkerTags.Items.RODS_STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneTorch, folder));

    // throw balls
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.efln.get())
                       .key('#', Tags.Items.GUNPOWDER)
                       .key('X', Items.FLINT)
                       .patternLine(" # ")
                       .patternLine("#X#")
                       .patternLine(" # ")
                       .addCriterion("has_item", hasItem(Tags.Items.DUSTS_GLOWSTONE))
                       .build(consumer, prefix(TinkerGadgets.efln, "gadgets/throwball/"));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.glowBall.get(), 8)
                       .key('#', Items.SNOWBALL)
                       .key('X', Tags.Items.DUSTS_GLOWSTONE)
                       .patternLine("###")
                       .patternLine("#X#")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(Tags.Items.DUSTS_GLOWSTONE))
                       .build(consumer, prefix(TinkerGadgets.glowBall, "gadgets/throwball/"));

    // Shurikens
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.flintShuriken.get(), 4)
                        .key('X', Items.FLINT)
                        .patternLine(" X ")
                        .patternLine("X X")
                        .patternLine(" X ")
                        .addCriterion("has_item", hasItem(Items.FLINT))
                        .build(consumer, prefix(TinkerGadgets.flintShuriken, "gadgets/shuriken/"));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.quartzShuriken.get(), 4)
                        .key('X', Items.QUARTZ)
                        .patternLine(" X ")
                        .patternLine("X X")
                        .patternLine(" X ")
                        .addCriterion("has_item", hasItem(Items.QUARTZ))
                        .build(consumer, prefix(TinkerGadgets.quartzShuriken, "gadgets/shuriken/"));

    // piggybackpack
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.piggyBackpack.get())
                       .key('#', Tags.Items.RODS_WOODEN)
                       .key('X', Tags.Items.LEATHER)
                       .patternLine(" X ")
                       .patternLine("# #")
                       .patternLine(" X ")
                       .addCriterion("has_item", hasItem(Tags.Items.RODS_WOODEN))
                       .build(consumer, prefix(TinkerGadgets.piggyBackpack, "gadgets/"));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.punji.get(), 3)
                       .key('#', Items.SUGAR_CANE)
                       .patternLine("# #")
                       .patternLine(" # ")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(Items.SUGAR_CANE))
                       .build(consumer, prefix(TinkerGadgets.punji, "gadgets/"));

    // frames
    registerFrameRecipes(consumer, TinkerModifiers.silkyCloth, FrameType.JEWEL);
    registerFrameRecipes(consumer, TinkerMaterials.cobalt.getNugget(), FrameType.COBALT);
    registerFrameRecipes(consumer, TinkerMaterials.ardite.getNugget(), FrameType.ARDITE);
    registerFrameRecipes(consumer, TinkerMaterials.manyullyn.getNugget(), FrameType.MANYULLYN);
    registerFrameRecipes(consumer, Items.GOLD_NUGGET, FrameType.GOLD);
    Item clearFrame = TinkerGadgets.itemFrame.get(FrameType.CLEAR);
    ShapedRecipeBuilder.shapedRecipe(clearFrame)
                       .key('e', Tags.Items.GLASS_PANES_COLORLESS)
                       .key('M', Tags.Items.GLASS_COLORLESS)
                       .patternLine(" e ")
                       .patternLine("eMe")
                       .patternLine(" e ")
                       .addCriterion("has_item", hasItem(Tags.Items.GLASS_PANES_COLORLESS))
                       .setGroup(locationString("fancy_item_frame"))
                       .build(consumer, prefix(clearFrame, "gadgets/fancy_frame/"));

    // dried clay
    folder = "building/";
    ShapedRecipeBuilder.shapedRecipe(TinkerCommons.driedClayBricks)
                       .key('b', TinkerCommons.driedBrick)
                       .patternLine("bb")
                       .patternLine("bb")
                       .addCriterion("has_item", hasItem(TinkerCommons.driedClay))
                       .build(consumer, prefix(TinkerCommons.driedClayBricks, folder));
    registerSlabStair(consumer, TinkerCommons.driedClay, folder, true);
    registerSlabStair(consumer, TinkerCommons.driedClayBricks, folder, true);

    // FIXME: temporary dried clay recipes
    addCampfireCooking(consumer, Blocks.CLAY, TinkerCommons.driedClay, 0.3f, folder);
    addCampfireCooking(consumer, Items.CLAY_BALL, TinkerCommons.driedBrick, 0.3f, folder);

    // FIXME: temporary jerky recipes
    folder = "foods/";
    addFoodCooking(consumer, Items.COOKED_BEEF, TinkerGadgets.beefJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.COOKED_CHICKEN, TinkerGadgets.chickenJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.COOKED_PORKCHOP, TinkerGadgets.porkJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.COOKED_MUTTON, TinkerGadgets.muttonJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.COOKED_RABBIT, TinkerGadgets.rabbitJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.COOKED_COD, TinkerGadgets.fishJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.COOKED_SALMON, TinkerGadgets.salmonJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.ROTTEN_FLESH, TinkerGadgets.monsterJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.TROPICAL_FISH, TinkerGadgets.clownfishJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.PUFFERFISH, TinkerGadgets.pufferfishJerky, 0.35f, folder);
    // slime drops
    for (SlimeType slime : SlimeType.values()) {
      addFoodCooking(consumer, TinkerCommons.slimeball.get(slime), TinkerGadgets.slimeDrop.get(slime), 0.35f, folder);
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
  private void addCampfireCooking(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider output, float experience, String folder) {
    CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(input), output, experience, 600, IRecipeSerializer.CAMPFIRE_COOKING)
                        .addCriterion("has_item", hasItem(input))
                        .build(consumer, wrap(output, folder, "_campfire"));
  }

  /**
   * Adds a recipe to the campfire, furnace, and smoker
   * @param consumer    Recipe consumer
   * @param input       Recipe input
   * @param output      Recipe output
   * @param experience  Experience for the recipe
   * @param folder      Folder to store the recipe
   */
  private void addFoodCooking(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider output, float experience, String folder) {
    addCampfireCooking(consumer, input, output, experience, folder);
    // furnace is 200 ticks
    ICriterionInstance criteria = hasItem(input);
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, experience, 200)
                        .addCriterion("has_item", criteria)
                        .build(consumer, wrap(output, folder, "_furnace"));
    // smoker 100 ticks
    CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(input), output, experience, 100, IRecipeSerializer.SMOKING)
                        .addCriterion("has_item", criteria)
                        .build(consumer, wrap(output, folder, "_smoker"));
  }

  /**
   * Adds a recipe for an item frame type
   * @param consumer  Recipe consumer
   * @param edges     Edge item
   * @param type      Frame type
   */
  private void registerFrameRecipes(Consumer<IFinishedRecipe> consumer, IItemProvider edges, FrameType type) {
    Item frame = TinkerGadgets.itemFrame.get(type);
    ShapedRecipeBuilder.shapedRecipe(frame)
                       .key('e', edges)
                       .key('M', Items.OBSIDIAN)
                       .patternLine(" e ")
                       .patternLine("eMe")
                       .patternLine(" e ")
                       .addCriterion("has_item", hasItem(edges))
                       .setGroup(locationString("fancy_item_frame"))
                       .build(consumer, prefix(frame, "gadgets/fancy_frame/"));

  }
}
