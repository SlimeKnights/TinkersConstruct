package slimeknights.tconstruct.common.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.data.SingleItemRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TConstructRecipeProvider extends RecipeProvider implements IConditionBuilder {

  public TConstructRecipeProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.addCommonRecipes(consumer);
    this.addSlimeRecipes(consumer);
    this.addWorldRecipes(consumer);
    this.addSmelteryRecipes(consumer);
    this.addGadgetRecipes(consumer);
  }

  private void addCommonRecipes(Consumer<IFinishedRecipe> consumer) {
    // firewood and lavawood
    ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.firewood.get())
      .addIngredient(Items.BLAZE_POWDER)
      .addIngredient(TinkerCommons.lavawood.get())
      .addIngredient(Items.BLAZE_POWDER)
      .addCriterion("has_lavawood", this.hasItem(TinkerCommons.lavawood.get()))
      .build(consumer, "tconstruct:common/firewood/firewood");
    registerSlabStair(consumer, TinkerCommons.firewood, "common/firewood/", false);
    registerSlabStair(consumer, TinkerCommons.lavawood, "common/firewood/", false);

    ShapelessRecipeBuilder.shapelessRecipe(TinkerModifiers.graveyardSoil.get())
      .addIngredient(Blocks.DIRT)
      .addIngredient(Items.ROTTEN_FLESH)
      .addIngredient(Items.BONE_MEAL)
      .addCriterion("has_dirt", this.hasItem(Blocks.DIRT))
      .addCriterion("has_rotten_flesh", this.hasItem(Items.ROTTEN_FLESH))
      .addCriterion("has_bone_meal", this.hasItem(Items.BONE_MEAL))
      .build(consumer, "tconstruct:common/soil/graveyard_soil");
    ShapedRecipeBuilder.shapedRecipe(TinkerCommons.mudBricks.get())
      .key('#', TinkerCommons.mudBrick.get())
      .patternLine("##")
      .patternLine("##")
      .addCriterion("has_mud_brick", this.hasItem(TinkerCommons.mudBrick.get()))
      .build(consumer, "tconstruct:common/soil/mud_bricks_block");
    registerSlabStair(consumer, TinkerCommons.mudBricks, "common/soil/", true);
    ShapedRecipeBuilder.shapedRecipe(TinkerCommons.mudBricks.getSlab())
      .key('#', TinkerCommons.mudBrick.get())
      .patternLine("##")
      .setGroup("tconstruct:mud_brick_slab")
      .addCriterion("has_mud_brick", this.hasItem(TinkerCommons.mudBrick.get()))
      .build(consumer, "tconstruct:common/soil/mud_bricks_slab_item");

    // book
    ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.book.get())
      .addIngredient(Items.BOOK)
      .addIngredient(Blocks.GRAVEL)
      .addCriterion("has_gravel", this.hasItem(Blocks.GRAVEL))
      .build(consumer, "tconstruct:common/book");

    // glass
    for (GlassColor color : GlassColor.values()) {
      Block block = TinkerCommons.clearStainedGlass.get(color);
      ShapedRecipeBuilder.shapedRecipe(block, 8)
                         .key('#', TinkerCommons.clearGlass)
                         .key('X', color.getDye().getTag())
                         .patternLine("###")
                         .patternLine("#X#")
                         .patternLine("###")
                         .setGroup(locationString("stained_clear_glass"))
                         .addCriterion("has_clear_glass", this.hasItem(TinkerCommons.clearGlass.get()))
                         .build(consumer, wrap(block.getRegistryName(), "common/glass/", ""));
    }

    // vanilla recipes
    ResourceLocation flintId = new ResourceLocation(TConstruct.modID, "common/flint");
    ConditionalRecipe.builder()
      .addCondition(new ConfigOptionEnabledCondition("addGravelToFlintRecipe"))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(Items.FLINT)
        .addIngredient(Blocks.GRAVEL)
        .addIngredient(Blocks.GRAVEL)
        .addIngredient(Blocks.GRAVEL)
        .addCriterion("has_item", this.hasItem(Blocks.GRAVEL))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_general/common/flint"), ConditionalAdvancement.builder()
        .addCondition(new ConfigOptionEnabledCondition("addGravelToFlintRecipe"))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(flintId))
          .withCriterion("has_item", hasItem(Blocks.GRAVEL))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(flintId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
       ).build(consumer, flintId);

    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.GLASS.asItem()), TinkerCommons.clearGlass.get().asItem(), 0.1F, 200)
                        .addCriterion("has_item", this.hasItem(Blocks.GLASS))
                        .build(consumer, wrap(TinkerCommons.clearGlass.getRegistryName(), "common/glass/", "_from_smelting"));
  }

  private void addWorldRecipes(Consumer<IFinishedRecipe> consumer) {

  }

  private void addGadgetRecipes(Consumer<IFinishedRecipe> consumer) {
    // slime
    String folder = "gadgets/slimeboots/";
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeBoots.get(slime))
                         .setGroup("tconstruct:slime_boots")
                         .key('#', TinkerWorld.congealedSlime.get(slime))
                         .key('X', slime.getSlimeBallTag())
                         .patternLine("X X")
                         .patternLine("# #")
                         .addCriterion("has_item", this.hasItem(Items.SLIME_BALL))
                         .build(consumer, location(folder + slime.getName()));
    }
    folder = "gadgets/slimesling/";
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeSling.get(slime))
                         .setGroup("tconstruct:slimesling")
                         .key('#', Items.STRING)
                         .key('X', TinkerWorld.congealedSlime.get(slime))
                         .key('L', slime.getSlimeBallTag())
                         .patternLine("#X#")
                         .patternLine("L L")
                         .patternLine(" L ")
                         .addCriterion("has_item", this.hasItem(Items.STRING))
                         .build(consumer, location(folder + slime.getName()));
    }

    // rails
    folder = "gadgets/rail/";
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.woodenRail.get(), 4)
                       .key('#', ItemTags.PLANKS)
                       .key('X', Tags.Items.RODS_WOODEN)
                       .patternLine("# #")
                       .patternLine("#X#")
                       .patternLine("# #")
                       .addCriterion("has_item", this.hasItem(ItemTags.PLANKS))
                       .build(consumer, prefix(TinkerGadgets.woodenRail.getRegistryName(), folder));

    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.woodenDropperRail.get(), 4)
                       .key('#', ItemTags.PLANKS)
                       .key('X', ItemTags.WOODEN_TRAPDOORS)
                       .patternLine("# #")
                       .patternLine("#X#")
                       .patternLine("# #")
                       .addCriterion("has_item", this.hasItem(ItemTags.PLANKS))
                       .build(consumer, prefix(TinkerGadgets.woodenDropperRail.getRegistryName(), folder));

    // folder
    folder = "gadgets/stone/";
    ShapedRecipeBuilder.shapedRecipe(Blocks.JACK_O_LANTERN)
                       .key('#', Blocks.CARVED_PUMPKIN)
                       .key('X', TinkerGadgets.stoneTorch.get())
                       .patternLine("#")
                       .patternLine("X")
                       .addCriterion("has_item", this.hasItem(Blocks.CARVED_PUMPKIN))
                       .build(consumer, location(folder + "jack_o_lantern"));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneLadder.get(), 3)
                       .key('#', TinkerTags.Items.RODS_STONE)
                       .patternLine("# #")
                       .patternLine("###")
                       .patternLine("# #")
                       .addCriterion("has_item", this.hasItem(TinkerTags.Items.RODS_STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneLadder.getRegistryName(), folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneStick.get(), 4)
                       .key('#', Ingredient.fromItemListStream(Stream.of(
                         new Ingredient.TagList(Tags.Items.STONE),
                         new Ingredient.TagList(Tags.Items.COBBLESTONE))
                       ))
                       .patternLine("#")
                       .patternLine("#")
                       .addCriterion("has_item", this.hasItem(Tags.Items.STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneStick.getRegistryName(), folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneTorch.get(), 4)
                       .key('#', Ingredient.fromItemListStream(Stream.of(
                         new Ingredient.SingleItemList(new ItemStack(Items.COAL)),
                         new Ingredient.SingleItemList(new ItemStack(Items.CHARCOAL))
                       )))
                       .key('X', TinkerTags.Items.RODS_STONE)
                       .patternLine("#")
                       .patternLine("X")
                       .addCriterion("has_item", this.hasItem(TinkerTags.Items.RODS_STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneTorch.getRegistryName(), folder));

    // throw balls
    ResourceLocation eflnBallId = new ResourceLocation(TConstruct.modID, "gadgets/throwball/efln");
    ConditionalRecipe.builder()
      .addCondition(new TagEmptyCondition("forge", "dusts/sulfur"))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(TinkerGadgets.efln.get())
        .addIngredient(Items.FLINT)
        .addIngredient(Items.GUNPOWDER)
        .addCriterion("has_item", this.hasItem(Tags.Items.DUSTS_GLOWSTONE))::build)
      .addCondition(not(new TagEmptyCondition("forge", "dusts/sulfur")))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(TinkerGadgets.efln.get())
        .addIngredient(TinkerTags.Items.DUSTS_SULFUR)
        .addIngredient(Ingredient.fromItemListStream(Stream.of(
          new Ingredient.TagList(TinkerTags.Items.DUSTS_SULFUR),
          new Ingredient.SingleItemList(new ItemStack(Items.GUNPOWDER)))
        ))
        .addCriterion("has_item", this.hasItem(Items.GUNPOWDER))::build)
      .build(consumer, eflnBallId);
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.glowBall.get(), 8)
      .key('#', Items.SNOWBALL)
      .key('X', Tags.Items.DUSTS_GLOWSTONE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .addCriterion("has_item", this.hasItem(Tags.Items.DUSTS_GLOWSTONE))
      .build(consumer, wrap(TinkerGadgets.glowBall.getRegistryName(), "gadgets/throwball/", ""));

    // piggybackpack
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.piggyBackpack.get())
      .key('#', Tags.Items.RODS_WOODEN)
      .key('X', Tags.Items.LEATHER)
      .patternLine(" X ")
      .patternLine("# #")
      .patternLine(" X ")
      .addCriterion("has_item", this.hasItem(Tags.Items.RODS_WOODEN))
      .build(consumer, prefix(TinkerGadgets.piggyBackpack.getRegistryName(), "gadgets/"));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.punji.get(), 3)
      .key('#', Items.SUGAR_CANE)
      .patternLine("# #")
      .patternLine(" # ")
      .patternLine("# #")
      .addCriterion("has_item", this.hasItem(Items.SUGAR_CANE))
      .build(consumer, prefix(TinkerGadgets.punji.getRegistryName(), "gadgets/"));
  }

  private void addSmelteryRecipes(Consumer<IFinishedRecipe> consumer) {
    final String folder = "smeltery/seared_block/";
    // cobble -> stone
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerSmeltery.searedCobble.get()), TinkerSmeltery.searedStone, 0.1f, 200)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.searedCobble.get()))
                        .build(consumer, wrap(TinkerSmeltery.searedStone.getRegistryName(), folder, "_smelting"));
    // stone -> paver
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerSmeltery.searedStone.get()), TinkerSmeltery.searedPaver, 0.1f, 200)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.searedStone.get()))
                        .build(consumer, wrap(TinkerSmeltery.searedPaver.getRegistryName(), folder, "_smelting"));
    // paver -> bricks
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedBricks, 4)
                       .key('b', TinkerSmeltery.searedStone)
                       .patternLine("bb")
                       .patternLine("bb")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedStone))
                       .build(consumer, wrap(TinkerSmeltery.searedBricks.getRegistryName(), folder, "_crafting"));
    // bricks -> cracked
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerSmeltery.searedBricks), TinkerSmeltery.searedCrackedBricks, 0.1f, 200)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.searedBricks))
                        .build(consumer, wrap(TinkerSmeltery.searedCrackedBricks.getRegistryName(), folder, "_smelting"));
    // brick slabs -> chiseled
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedSquareBricks)
                       .key('s', TinkerSmeltery.searedBricks.getSlab())
                       .patternLine("s")
                       .patternLine("s")
                       .addCriterion("has_item",  hasItem(TinkerSmeltery.searedBricks.getSlab()))
                       .build(consumer, wrap(TinkerSmeltery.searedSquareBricks.getRegistryName(), folder, "_crafting"));

    // transform bricks
    this.addStonecutter(consumer, TinkerTags.Items.SEARED_BRICKS, TinkerSmeltery.searedBricks, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SEARED_BRICKS, TinkerSmeltery.searedFancyBricks, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SEARED_BRICKS, TinkerSmeltery.searedSquareBricks, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SEARED_BRICKS, TinkerSmeltery.searedSmallBricks, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SEARED_BRICKS, TinkerSmeltery.searedTriangleBricks, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SEARED_BRICKS, TinkerSmeltery.searedRoad, folder);
    // transform smooth
    this.addStonecutter(consumer, TinkerTags.Items.SMOOTH_SEARED_BLOCKS, TinkerSmeltery.searedPaver, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SMOOTH_SEARED_BLOCKS, TinkerSmeltery.searedCreeper, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SMOOTH_SEARED_BLOCKS, TinkerSmeltery.searedTile, folder);

    // stairs and slabs
    this.registerSlabStair(consumer, TinkerSmeltery.searedStone, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedCobble, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedPaver, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedCrackedBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedFancyBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedSquareBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedSmallBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedTriangleBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedCreeper, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedRoad, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedTile, folder, true);
  }

  private void addSlimeRecipes(Consumer<IFinishedRecipe> consumer) {
    // Add recipe for all slimeball<->congealed
    for (SlimeBlock.SlimeType slimeType : SlimeBlock.SlimeType.values()) {
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.congealedSlime.get(slimeType))
        .key('#', slimeType.getSlimeBallTag())
        .patternLine("##")
        .patternLine("##")
        .addCriterion("has_item", this.hasItem(slimeType.getSlimeBallTag()))
        .setGroup("tconstruct:congealed_slime")
        .build(consumer, "tconstruct:common/slime/" + slimeType.getName() + "/congealed");

      ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.slimeball.get(slimeType), 4)
        .addIngredient(TinkerWorld.congealedSlime.get(slimeType))
        .addCriterion("has_item", this.hasItem(TinkerWorld.congealedSlime.get(slimeType)))
        .setGroup("tconstruct:slime_balls")
        .build(consumer, "tconstruct:common/slime/" + slimeType.getName() + "/slimeball_from_congealed");
    }

    // Don't re add recipe for vanilla slime_block and slime_ball
    for (SlimeBlock.SlimeType slimeType : SlimeBlock.SlimeType.TINKER) {
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.slime.get(slimeType))
        .key('#', slimeType.getSlimeBallTag())
        .patternLine("###")
        .patternLine("###")
        .patternLine("###")
        .addCriterion("has_item", this.hasItem(slimeType.getSlimeBallTag()))
        .setGroup("tconstruct:slime_blocks")
        .build(consumer, "tconstruct:common/slime/" + slimeType.getName() + "/slimeblock");

      ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.slimeball.get(slimeType), 9)
        .addIngredient(TinkerWorld.slime.get(slimeType))
        .addCriterion("has_item", this.hasItem(TinkerWorld.slime.get(slimeType)))
        .setGroup("tconstruct:slime_balls")
        .build(consumer, "tconstruct:common/slime/" + slimeType.getName() + "/slimeball_from_block");
    }
  }

  /* String helpers */
  /**
   * Gets a resource location for Tinkers
   * @param id  Location path
   * @return  Location for Tinkers
   */
  private static ResourceLocation location(String id) {
    return new ResourceLocation(TConstruct.modID, id);
  }
  /**
   * Gets a resource location string for Tinkers
   * @param id  Location path
   * @return  Location for Tinkers
   */
  private static String locationString(String id) {
    return TConstruct.modID + ":" + id;
  }

  /**
   * Prefixes the resource location path with the given value
   * @param loc     Location to prefix
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  private static ResourceLocation wrap(ResourceLocation loc, String prefix, String suffix) {
    return new ResourceLocation(loc.getNamespace(), prefix + loc.getPath() + suffix);
  }

  /**
   * Prefixes the resource location path with the given value
   * @param loc     Location to prefix
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  private static ResourceLocation prefix(ResourceLocation loc, String prefix) {
    return new ResourceLocation(loc.getNamespace(), prefix + loc.getPath());
  }


  /* Helpers */

  /**
   * Adds a stonecutting recipe with automatic name and criteria
   * @param consumer  Recipe consumer
   * @param input     Recipe input
   * @param output    Recipe output
   * @param folder    Recipe folder path
   */
  private void addStonecutter(@Nonnull Consumer<IFinishedRecipe> consumer, Tag<Item> input, IItemProvider output, String folder) {
    SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromTag(input), output, 1)
                           .addCriterion("has_item", hasItem(input))
                           .build(consumer, wrap(output.asItem().getRegistryName(), folder, "_stonecutting"));
  }

  /**
   * Registers generic building block recipes
   * @param consumer  Recipe consumer
   * @param building  Building object instance
   */
  private void registerSlabStair(@Nonnull Consumer<IFinishedRecipe> consumer, BuildingBlockObject building, String folder, boolean addStonecutter) {
    Item item = building.asItem();
    ResourceLocation location = item.getRegistryName();
    ICriterionInstance hasBlock = hasItem(item);
    Ingredient ingredient = Ingredient.fromItems(item);
    // slab
    Item slab = building.getSlabItem();
    ShapedRecipeBuilder.shapedRecipe(slab, 6)
                       .key('B', item)
                       .patternLine("BBB")
                       .addCriterion("has_item", hasBlock)
                       .setGroup(slab.getRegistryName().toString())
                       .build(consumer, wrap(location, folder, "_slab"));
    // stairs
    Item stairs = building.getStairsItem();
    ShapedRecipeBuilder.shapedRecipe(stairs, 4)
                       .key('B', item)
                       .patternLine("B  ")
                       .patternLine("BB ")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasBlock)
                       .setGroup(stairs.getRegistryName().toString())
                       .build(consumer, wrap(location, folder, "_stairs"));

    // only add stonecutter if relevant
    if (addStonecutter) {
      SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, slab, 2)
                             .addCriterion("has_item", hasBlock)
                             .build(consumer, wrap(location, folder, "_slab_stonecutter"));
      SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, stairs)
                             .addCriterion("has_item", hasBlock)
                             .build(consumer, wrap(location, folder, "_stairs_stonecutter"));
    }
  }
}
