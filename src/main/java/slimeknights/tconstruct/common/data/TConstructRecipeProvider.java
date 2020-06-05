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
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.blocks.DecorativeBlocks;
import slimeknights.tconstruct.blocks.GadgetBlocks;
import slimeknights.tconstruct.blocks.SmelteryBlocks;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.items.CommonItems;
import slimeknights.tconstruct.items.FoodItems;
import slimeknights.tconstruct.items.GadgetItems;
import slimeknights.tconstruct.library.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.SlimeBlock;

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
    ShapelessRecipeBuilder.shapelessRecipe(CommonBlocks.firewood.get())
      .addIngredient(Items.BLAZE_POWDER)
      .addIngredient(CommonBlocks.lavawood.get())
      .addIngredient(Items.BLAZE_POWDER)
      .addCriterion("has_lavawood", this.hasItem(CommonBlocks.lavawood.get()))
      .build(consumer, "tconstruct:common/firewood/firewood");
    registerSlabStair(consumer, CommonBlocks.firewood, "common/firewood/", false);
    registerSlabStair(consumer, CommonBlocks.lavawood, "common/firewood/", false);

    ShapelessRecipeBuilder.shapelessRecipe(CommonBlocks.graveyard_soil.get())
      .addIngredient(Blocks.DIRT)
      .addIngredient(Items.ROTTEN_FLESH)
      .addIngredient(Items.BONE_MEAL)
      .addCriterion("has_dirt", this.hasItem(Blocks.DIRT))
      .addCriterion("has_rotten_flesh", this.hasItem(Items.ROTTEN_FLESH))
      .addCriterion("has_bone_meal", this.hasItem(Items.BONE_MEAL))
      .build(consumer, "tconstruct:common/soil/graveyard_soil");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.mud_bricks.get())
      .key('#', CommonItems.mud_brick.get())
      .patternLine("##")
      .patternLine("##")
      .addCriterion("has_mud_brick", this.hasItem(CommonItems.mud_brick.get()))
      .build(consumer, "tconstruct:common/soil/mud_bricks_block");
    registerSlabStair(consumer, DecorativeBlocks.mud_bricks, "common/soil/", true);
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.mud_bricks.getSlab())
      .key('#', CommonItems.mud_brick.get())
      .patternLine("##")
      .setGroup("tconstruct:mud_brick_slab")
      .addCriterion("has_mud_brick", this.hasItem(CommonItems.mud_brick.get()))
      .build(consumer, "tconstruct:common/soil/mud_bricks_slab_item");

    // book
    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.book.get())
      .addIngredient(Items.BOOK)
      .addIngredient(Blocks.GRAVEL)
      .addCriterion("has_gravel", this.hasItem(Blocks.GRAVEL))
      .build(consumer, "tconstruct:common/book");

    // glass
    for (GlassColor color : GlassColor.values()) {
      Block block = DecorativeBlocks.clear_stained_glass.get(color);
      ShapedRecipeBuilder.shapedRecipe(block, 8)
                         .key('#', DecorativeBlocks.clear_glass)
                         .key('X', color.getDye().getTag())
                         .patternLine("###")
                         .patternLine("#X#")
                         .patternLine("###")
                         .setGroup(locationString("stained_clear_glass"))
                         .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
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

    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.GLASS.asItem()), DecorativeBlocks.clear_glass.get().asItem(), 0.1F, 200)
                        .addCriterion("has_item", this.hasItem(Blocks.GLASS))
                        .build(consumer, wrap(DecorativeBlocks.clear_glass.getRegistryName(), "common/glass/", "_from_smelting"));
  }

  private void addWorldRecipes(Consumer<IFinishedRecipe> consumer) {

  }

  private void addGadgetRecipes(Consumer<IFinishedRecipe> consumer) {
    // slime
    String folder = "gadgets/slimeboots/";
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
      ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_boots.get(slime))
                         .setGroup("tconstruct:slime_boots")
                         .key('#', WorldBlocks.congealed_slime.get(slime))
                         .key('X', slime.getSlimeBallTag())
                         .patternLine("X X")
                         .patternLine("# #")
                         .addCriterion("has_item", this.hasItem(Items.SLIME_BALL))
                         .build(consumer, location(folder + slime.getName()));
    }
    folder = "gadgets/slimesling/";
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
      ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_sling.get(slime))
                         .setGroup("tconstruct:slimesling")
                         .key('#', Items.STRING)
                         .key('X', WorldBlocks.congealed_slime.get(slime))
                         .key('L', slime.getSlimeBallTag())
                         .patternLine("#X#")
                         .patternLine("L L")
                         .patternLine(" L ")
                         .addCriterion("has_item", this.hasItem(Items.STRING))
                         .build(consumer, location(folder + slime.getName()));
    }

    // rails
    folder = "gadgets/rail/";
    ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.wooden_rail.get(), 4)
                       .key('#', ItemTags.PLANKS)
                       .key('X', Tags.Items.RODS_WOODEN)
                       .patternLine("# #")
                       .patternLine("#X#")
                       .patternLine("# #")
                       .addCriterion("has_item", this.hasItem(ItemTags.PLANKS))
                       .build(consumer, prefix(GadgetBlocks.wooden_rail.getRegistryName(), folder));

    ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.wooden_dropper_rail.get(), 4)
                       .key('#', ItemTags.PLANKS)
                       .key('X', ItemTags.WOODEN_TRAPDOORS)
                       .patternLine("# #")
                       .patternLine("#X#")
                       .patternLine("# #")
                       .addCriterion("has_item", this.hasItem(ItemTags.PLANKS))
                       .build(consumer, prefix(GadgetBlocks.wooden_dropper_rail.getRegistryName(), folder));

    // folder
    folder = "gadgets/stone/";
    ShapedRecipeBuilder.shapedRecipe(Blocks.JACK_O_LANTERN)
                       .key('#', Blocks.CARVED_PUMPKIN)
                       .key('X', GadgetBlocks.stone_torch.get())
                       .patternLine("#")
                       .patternLine("X")
                       .addCriterion("has_item", this.hasItem(Blocks.CARVED_PUMPKIN))
                       .build(consumer, location(folder + "jack_o_lantern"));
    ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.stone_ladder.get(), 3)
                       .key('#', TinkerTags.Items.RODS_STONE)
                       .patternLine("# #")
                       .patternLine("###")
                       .patternLine("# #")
                       .addCriterion("has_item", this.hasItem(TinkerTags.Items.RODS_STONE))
                       .build(consumer, prefix(GadgetBlocks.stone_ladder.getRegistryName(), folder));
    ShapedRecipeBuilder.shapedRecipe(GadgetItems.stone_stick.get(), 4)
                       .key('#', Ingredient.fromItemListStream(Stream.of(
                         new Ingredient.TagList(Tags.Items.STONE),
                         new Ingredient.TagList(Tags.Items.COBBLESTONE))
                       ))
                       .patternLine("#")
                       .patternLine("#")
                       .addCriterion("has_item", this.hasItem(Tags.Items.STONE))
                       .build(consumer, prefix(GadgetItems.stone_stick.getRegistryName(), folder));
    ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.stone_torch.get(), 4)
                       .key('#', Ingredient.fromItemListStream(Stream.of(
                         new Ingredient.SingleItemList(new ItemStack(Items.COAL)),
                         new Ingredient.SingleItemList(new ItemStack(Items.CHARCOAL))
                       )))
                       .key('X', TinkerTags.Items.RODS_STONE)
                       .patternLine("#")
                       .patternLine("X")
                       .addCriterion("has_item", this.hasItem(TinkerTags.Items.RODS_STONE))
                       .build(consumer, prefix(GadgetBlocks.stone_torch.getRegistryName(), folder));

    // throw balls
    ResourceLocation eflnBallId = new ResourceLocation(TConstruct.modID, "gadgets/throwball/efln");
    ConditionalRecipe.builder()
      .addCondition(new TagEmptyCondition("forge", "dusts/sulfur"))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(GadgetItems.efln_ball.get())
        .addIngredient(Items.FLINT)
        .addIngredient(Items.GUNPOWDER)
        .addCriterion("has_item", this.hasItem(Tags.Items.DUSTS_GLOWSTONE))::build)
      .addCondition(not(new TagEmptyCondition("forge", "dusts/sulfur")))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(GadgetItems.efln_ball.get())
        .addIngredient(TinkerTags.Items.DUSTS_SULFUR)
        .addIngredient(Ingredient.fromItemListStream(Stream.of(
          new Ingredient.TagList(TinkerTags.Items.DUSTS_SULFUR),
          new Ingredient.SingleItemList(new ItemStack(Items.GUNPOWDER)))
        ))
        .addCriterion("has_item", this.hasItem(Items.GUNPOWDER))::build)
      .build(consumer, eflnBallId);
    ShapedRecipeBuilder.shapedRecipe(GadgetItems.glow_ball.get(), 8)
      .key('#', Items.SNOWBALL)
      .key('X', Tags.Items.DUSTS_GLOWSTONE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .addCriterion("has_item", this.hasItem(Tags.Items.DUSTS_GLOWSTONE))
      .build(consumer, wrap(GadgetItems.glow_ball.getRegistryName(), "gadgets/throwball/", ""));

    // piggybackpack
    ShapedRecipeBuilder.shapedRecipe(GadgetItems.piggy_backpack.get())
      .key('#', Tags.Items.RODS_WOODEN)
      .key('X', Tags.Items.LEATHER)
      .patternLine(" X ")
      .patternLine("# #")
      .patternLine(" X ")
      .addCriterion("has_item", this.hasItem(Tags.Items.RODS_WOODEN))
      .build(consumer, prefix(GadgetItems.piggy_backpack.getRegistryName(), "gadgets/"));
    ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.punji.get(), 3)
      .key('#', Items.SUGAR_CANE)
      .patternLine("# #")
      .patternLine(" # ")
      .patternLine("# #")
      .addCriterion("has_item", this.hasItem(Items.SUGAR_CANE))
      .build(consumer, prefix(GadgetBlocks.punji.getRegistryName(), "gadgets/"));
  }

  private void addSmelteryRecipes(Consumer<IFinishedRecipe> consumer) {
    final String folder = "smeltery/seared_block/";
    // cobble -> stone
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SmelteryBlocks.seared_cobble.get()), SmelteryBlocks.seared_stone, 0.1f, 200)
                        .addCriterion("has_item", hasItem(SmelteryBlocks.seared_cobble.get()))
                        .build(consumer, wrap(SmelteryBlocks.seared_stone.getRegistryName(), folder, "_smelting"));
    // stone -> paver
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SmelteryBlocks.seared_stone.get()), SmelteryBlocks.seared_paver, 0.1f, 200)
                        .addCriterion("has_item", hasItem(SmelteryBlocks.seared_stone.get()))
                        .build(consumer, wrap(SmelteryBlocks.seared_paver.getRegistryName(), folder, "_smelting"));
    // paver -> bricks
    ShapedRecipeBuilder.shapedRecipe(SmelteryBlocks.seared_bricks, 4)
                       .key('b', SmelteryBlocks.seared_stone)
                       .patternLine("bb")
                       .patternLine("bb")
                       .addCriterion("has_item", hasItem(SmelteryBlocks.seared_stone))
                       .build(consumer, wrap(SmelteryBlocks.seared_bricks.getRegistryName(), folder, "_crafting"));
    // bricks -> cracked
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(SmelteryBlocks.seared_bricks), SmelteryBlocks.seared_cracked_bricks, 0.1f, 200)
                        .addCriterion("has_item", hasItem(SmelteryBlocks.seared_bricks))
                        .build(consumer, wrap(SmelteryBlocks.seared_cracked_bricks.getRegistryName(), folder, "_smelting"));
    // brick slabs -> chiseled
    ShapedRecipeBuilder.shapedRecipe(SmelteryBlocks.seared_square_bricks)
                       .key('s', SmelteryBlocks.seared_bricks.getSlab())
                       .patternLine("s")
                       .patternLine("s")
                       .addCriterion("has_item",  hasItem(SmelteryBlocks.seared_bricks.getSlab()))
                       .build(consumer, wrap(SmelteryBlocks.seared_square_bricks.getRegistryName(), folder, "_crafting"));

    // transform bricks
    this.addStonecutter(consumer, TinkerTags.Items.SEARED_BRICKS, SmelteryBlocks.seared_bricks, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SEARED_BRICKS, SmelteryBlocks.seared_fancy_bricks, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SEARED_BRICKS, SmelteryBlocks.seared_square_bricks, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SEARED_BRICKS, SmelteryBlocks.seared_small_bricks, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SEARED_BRICKS, SmelteryBlocks.seared_triangle_bricks, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SEARED_BRICKS, SmelteryBlocks.seared_road, folder);
    // transform smooth
    this.addStonecutter(consumer, TinkerTags.Items.SMOOTH_SEARED_BLOCKS, SmelteryBlocks.seared_paver, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SMOOTH_SEARED_BLOCKS, SmelteryBlocks.seared_creeper, folder);
    this.addStonecutter(consumer, TinkerTags.Items.SMOOTH_SEARED_BLOCKS, SmelteryBlocks.seared_tile, folder);

    // stairs and slabs
    this.registerSlabStair(consumer, SmelteryBlocks.seared_stone, folder, true);
    this.registerSlabStair(consumer, SmelteryBlocks.seared_cobble, folder, true);
    this.registerSlabStair(consumer, SmelteryBlocks.seared_paver, folder, true);
    this.registerSlabStair(consumer, SmelteryBlocks.seared_bricks, folder, true);
    this.registerSlabStair(consumer, SmelteryBlocks.seared_cracked_bricks, folder, true);
    this.registerSlabStair(consumer, SmelteryBlocks.seared_fancy_bricks, folder, true);
    this.registerSlabStair(consumer, SmelteryBlocks.seared_square_bricks, folder, true);
    this.registerSlabStair(consumer, SmelteryBlocks.seared_small_bricks, folder, true);
    this.registerSlabStair(consumer, SmelteryBlocks.seared_triangle_bricks, folder, true);
    this.registerSlabStair(consumer, SmelteryBlocks.seared_creeper, folder, true);
    this.registerSlabStair(consumer, SmelteryBlocks.seared_road, folder, true);
    this.registerSlabStair(consumer, SmelteryBlocks.seared_tile, folder, true);
  }

  private void addSlimeRecipes(Consumer<IFinishedRecipe> consumer) {
    // Add recipe for all slimeball<->congealed
    for (SlimeBlock.SlimeType slimeType : SlimeBlock.SlimeType.values()) {
      ShapedRecipeBuilder.shapedRecipe(WorldBlocks.congealed_slime.get(slimeType))
        .key('#', slimeType.getSlimeBallTag())
        .patternLine("##")
        .patternLine("##")
        .addCriterion("has_item", this.hasItem(slimeType.getSlimeBallTag()))
        .setGroup("tconstruct:congealed_slime")
        .build(consumer, "tconstruct:common/slime/" + slimeType.getName() + "/congealed");

      ShapelessRecipeBuilder.shapelessRecipe(FoodItems.slime_ball.get(slimeType), 4)
        .addIngredient(WorldBlocks.congealed_slime.get(slimeType))
        .addCriterion("has_item", this.hasItem(WorldBlocks.congealed_slime.get(slimeType)))
        .setGroup("tconstruct:slime_balls")
        .build(consumer, "tconstruct:common/slime/" + slimeType.getName() + "/slimeball_from_congealed");
    }

    // Don't re add recipe for vanilla slime_block and slime_ball
    for (SlimeBlock.SlimeType slimeType : SlimeBlock.SlimeType.TINKER) {
      ShapedRecipeBuilder.shapedRecipe(WorldBlocks.slime.get(slimeType))
        .key('#', slimeType.getSlimeBallTag())
        .patternLine("###")
        .patternLine("###")
        .patternLine("###")
        .addCriterion("has_item", this.hasItem(slimeType.getSlimeBallTag()))
        .setGroup("tconstruct:slime_blocks")
        .build(consumer, "tconstruct:common/slime/" + slimeType.getName() + "/slimeblock");

      ShapelessRecipeBuilder.shapelessRecipe(FoodItems.slime_ball.get(slimeType), 9)
        .addIngredient(WorldBlocks.slime.get(slimeType))
        .addCriterion("has_item", this.hasItem(WorldBlocks.slime.get(slimeType)))
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
