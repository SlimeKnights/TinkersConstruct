package slimeknights.tconstruct.common.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.blocks.DecorativeBlocks;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.common.Tags;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.common.conditions.PulseLoadedCondition;
import slimeknights.tconstruct.items.CommonItems;
import slimeknights.tconstruct.library.TinkerPulseIds;

import java.util.function.Consumer;

public class TConstructRecipeProvider extends RecipeProvider {

  public TConstructRecipeProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.addCommon(consumer);
    this.addGlassRecipes(consumer);
    this.addSlimeRecipes(consumer);

    if (TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_WORLD_PULSE_ID)) {
      this.addWorld(consumer);
    }

    if (TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_GADGETS_PULSE_ID)) {
      this.addGadgets(consumer);
    }
  }

  private void addCommon(Consumer<IFinishedRecipe> consumer) {
    ShapelessRecipeBuilder.shapelessRecipe(CommonBlocks.firewood)
      .addIngredient(Items.BLAZE_POWDER)
      .addIngredient(CommonBlocks.lavawood)
      .addIngredient(Items.BLAZE_POWDER)
      .addCriterion("has_lavawood", this.hasItem(CommonBlocks.lavawood))
      .build(consumer, "tconstruct:common/firewood/firewood");
    ShapedRecipeBuilder.shapedRecipe(CommonBlocks.firewood_slab, 6)
      .key('#', CommonBlocks.firewood)
      .patternLine("###")
      .addCriterion("has_firewood", this.hasItem(CommonBlocks.firewood))
      .build(consumer, "tconstruct:common/firewood/firewood_slab");
    ShapedRecipeBuilder.shapedRecipe(CommonBlocks.firewood_stairs, 4)
      .key('#', CommonBlocks.firewood)
      .patternLine("#  ")
      .patternLine("## ")
      .patternLine("###")
      .addCriterion("has_firewood", this.hasItem(CommonBlocks.firewood))
      .build(consumer, "tconstruct:common/firewood/firewood_stairs");
    ShapedRecipeBuilder.shapedRecipe(CommonBlocks.lavawood_slab, 6)
      .key('#', CommonBlocks.firewood)
      .patternLine("###")
      .addCriterion("has_firewood", this.hasItem(CommonBlocks.firewood))
      .build(consumer, "tconstruct:common/firewood/lavawood_slab");
    ShapedRecipeBuilder.shapedRecipe(CommonBlocks.lavawood_stairs, 4)
      .key('#', CommonBlocks.firewood)
      .patternLine("#  ")
      .patternLine("## ")
      .patternLine("###")
      .addCriterion("has_firewood", this.hasItem(CommonBlocks.firewood))
      .build(consumer, "tconstruct:common/firewood/lavawood_stairs");

    ShapelessRecipeBuilder.shapelessRecipe(CommonBlocks.graveyard_soil)
      .addIngredient(Blocks.DIRT)
      .addIngredient(Items.ROTTEN_FLESH)
      .addIngredient(Items.BONE_MEAL)
      .addCriterion("has_dirt", this.hasItem(Blocks.DIRT))
      .addCriterion("has_rotten_flesh", this.hasItem(Items.ROTTEN_FLESH))
      .addCriterion("has_bone_meal", this.hasItem(Items.BONE_MEAL))
      .build(consumer, "tconstruct:common/soil/graveyard_soil");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.mud_bricks)
      .key('#', CommonItems.mud_brick)
      .patternLine("##")
      .patternLine("##")
      .addCriterion("has_mud_brick", this.hasItem(CommonItems.mud_brick))
      .build(consumer, "tconstruct:common/soil/mud_bricks_block");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.mud_bricks_slab, 6)
      .key('#', DecorativeBlocks.mud_bricks)
      .patternLine("###")
      .setGroup("tconstruct:mud_brick_slab")
      .addCriterion("has_mud_bricks", this.hasItem(DecorativeBlocks.mud_bricks))
      .build(consumer, "tconstruct:common/soil/mud_bricks_slab_block");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.mud_bricks_slab)
      .key('#', CommonItems.mud_brick)
      .patternLine("##")
      .setGroup("tconstruct:mud_brick_slab")
      .addCriterion("has_mud_brick", this.hasItem(CommonItems.mud_brick))
      .build(consumer, "tconstruct:common/soil/mud_bricks_slab");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.mud_bricks_stairs, 4)
      .key('#', DecorativeBlocks.mud_bricks)
      .patternLine("#  ")
      .patternLine("## ")
      .patternLine("###")
      .addCriterion("has_mud_bricks", this.hasItem(DecorativeBlocks.mud_bricks))
      .build(consumer, "tconstruct:common/soil/mud_bricks_stairs");

    ResourceLocation bookId = new ResourceLocation(TConstruct.modID, "common/book");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_TOOLS_PULSE_ID))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(CommonItems.book)
        .addIngredient(Items.BOOK)
        .addIngredient(Blocks.GRAVEL)
        .addCriterion("has_item", this.hasItem(Items.BOOK))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tconstruct/book"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_TOOLS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(bookId))
          .withCriterion("has_item", hasItem(Items.BOOK))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(bookId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, bookId);

    ResourceLocation flintId = new ResourceLocation(TConstruct.modID, "common/flint");
    ConditionalRecipe.builder()
      .addCondition(new ConfigOptionEnabledCondition("addGravelToFlintRecipe"))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(Items.FLINT)
        .addIngredient(Blocks.GRAVEL)
        .addIngredient(Blocks.GRAVEL)
        .addIngredient(Blocks.GRAVEL)
        .addCriterion("has_item", this.hasItem(Blocks.GRAVEL))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tconstruct/flint"), ConditionalAdvancement.builder()
        .addCondition(new ConfigOptionEnabledCondition("addGravelToFlintRecipe"))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(flintId))
          .withCriterion("has_item", hasItem(Blocks.GRAVEL))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(flintId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, flintId);
  }

  private void addWorld(Consumer<IFinishedRecipe> consumer) {

  }

  private void addGadgets(Consumer<IFinishedRecipe> consumer) {

  }

  private void addSlimeRecipes(Consumer<IFinishedRecipe> consumer) {
    //BLOOD
    ShapedRecipeBuilder.shapedRecipe(WorldBlocks.congealed_blood_slime)
      .key('#', Tags.Items.BLOOD_SLIMEBALL)
      .patternLine("##")
      .patternLine("##")
      .addCriterion("has_item", this.hasItem(Tags.Items.BLOOD_SLIMEBALL))
      .setGroup("tconstruct:congealed_slime")
      .build(consumer, "tconstruct:common/slime/blood/congealed");

    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.blood_slime_ball, 9)
      .addIngredient(WorldBlocks.blood_slime)
      .addCriterion("has_item", this.hasItem(WorldBlocks.blood_slime))
      .setGroup("tconstruct:slime_balls")
      .build(consumer, "tconstruct:common/slime/blood/slimeball_from_block");

    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.blood_slime_ball, 4)
      .addIngredient(WorldBlocks.congealed_blood_slime)
      .addCriterion("has_item", this.hasItem(WorldBlocks.congealed_blood_slime))
      .setGroup("tconstruct:slime_balls")
      .build(consumer, "tconstruct:common/slime/blood/slimeball_from_congealed");

    ShapedRecipeBuilder.shapedRecipe(WorldBlocks.blood_slime)
      .key('#', Tags.Items.BLOOD_SLIMEBALL)
      .patternLine("###")
      .patternLine("###")
      .patternLine("###")
      .addCriterion("has_item", this.hasItem(Tags.Items.BLOOD_SLIMEBALL))
      .setGroup("tconstruct:slime_blocks")
      .build(consumer, "tconstruct:common/slime/blood/slimeblock");

    //BLUE
    ShapedRecipeBuilder.shapedRecipe(WorldBlocks.congealed_blue_slime)
      .key('#', Tags.Items.BLUE_SLIMEBALL)
      .patternLine("##")
      .patternLine("##")
      .addCriterion("has_item", this.hasItem(Tags.Items.BLUE_SLIMEBALL))
      .setGroup("tconstruct:congealed_slime")
      .build(consumer, "tconstruct:common/slime/blue/congealed");

    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.blue_slime_ball, 9)
      .addIngredient(WorldBlocks.blue_slime)
      .addCriterion("has_item", this.hasItem(WorldBlocks.blue_slime))
      .setGroup("tconstruct:slime_balls")
      .build(consumer, "tconstruct:common/slime/blue/slimeball_from_block");

    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.blue_slime_ball, 4)
      .addIngredient(WorldBlocks.congealed_blue_slime)
      .addCriterion("has_item", this.hasItem(WorldBlocks.congealed_blue_slime))
      .setGroup("tconstruct:slime_balls")
      .build(consumer, "tconstruct:common/slime/blue/slimeball_from_congealed");

    ShapedRecipeBuilder.shapedRecipe(WorldBlocks.blue_slime)
      .key('#', Tags.Items.BLUE_SLIMEBALL)
      .patternLine("###")
      .patternLine("###")
      .patternLine("###")
      .addCriterion("has_item", this.hasItem(Tags.Items.BLUE_SLIMEBALL))
      .setGroup("tconstruct:slime_blocks")
      .build(consumer, "tconstruct:common/slime/blue/slimeblock");

    //GREEN
    ShapedRecipeBuilder.shapedRecipe(WorldBlocks.congealed_green_slime)
      .key('#', Tags.Items.GREEN_SLIMEBALL)
      .patternLine("##")
      .patternLine("##")
      .addCriterion("has_item", this.hasItem(Tags.Items.GREEN_SLIMEBALL))
      .setGroup("tconstruct:congealed_slime")
      .build(consumer, "tconstruct:common/slime/green/congealed");

    ShapelessRecipeBuilder.shapelessRecipe(Items.SLIME_BALL, 4)
      .addIngredient(WorldBlocks.congealed_green_slime)
      .addCriterion("has_item", this.hasItem(WorldBlocks.congealed_green_slime))
      .setGroup("tconstruct:slime_balls")
      .build(consumer, "tconstruct:common/slime/green/slimeball_from_congealed");

    /*
    TODO GREEN
    ShapedRecipeBuilder.shapedRecipe(WorldBlocks.green_slime)
      .key('#', Tags.Items.GREEN_SLIMEBALL)
      .patternLine("###")
      .patternLine("###")
      .patternLine("###")
      .addCriterion("has_item", this.hasItem(Tags.Items.GREEN_SLIMEBALL))
      .setGroup("tconstruct:slime_blocks")
      .build(consumer, "tconstruct:common/slime/green/slimeblock");*/

    //MAGMA
    ShapedRecipeBuilder.shapedRecipe(WorldBlocks.congealed_magma_slime)
      .key('#', Tags.Items.MAGMA_SLIMEBALL)
      .patternLine("##")
      .patternLine("##")
      .addCriterion("has_item", this.hasItem(Tags.Items.MAGMA_SLIMEBALL))
      .setGroup("tconstruct:congealed_slime")
      .build(consumer, "tconstruct:common/slime/magma/congealed");

    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.magma_slime_ball, 9)
      .addIngredient(WorldBlocks.magma_slime)
      .addCriterion("has_item", this.hasItem(WorldBlocks.magma_slime))
      .setGroup("tconstruct:slime_balls")
      .build(consumer, "tconstruct:common/slime/magma/slimeball_from_block");

    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.magma_slime_ball, 4)
      .addIngredient(WorldBlocks.congealed_magma_slime)
      .addCriterion("has_item", this.hasItem(WorldBlocks.congealed_magma_slime))
      .setGroup("tconstruct:slime_balls")
      .build(consumer, "tconstruct:common/slime/magma/slimeball_from_congealed");

    ShapedRecipeBuilder.shapedRecipe(WorldBlocks.magma_slime)
      .key('#', Tags.Items.MAGMA_SLIMEBALL)
      .patternLine("###")
      .patternLine("###")
      .patternLine("###")
      .addCriterion("has_item", this.hasItem(Tags.Items.MAGMA_SLIMEBALL))
      .setGroup("tconstruct:slime_blocks")
      .build(consumer, "tconstruct:common/slime/magma/slimeblock");

    /*
    TODO PINK
    //PINK
    ShapedRecipeBuilder.shapedRecipe(WorldBlocks.congealed_pink_slime)
      .key('#', Tags.Items.PINK_SLIMEBALL)
      .patternLine("##")
      .patternLine("##")
      .addCriterion("has_item", this.hasItem(Tags.Items.PINK_SLIMEBALL))
      .setGroup("tconstruct:congealed_slime")
      .build(consumer, "tconstruct:common/slime/pink/congealed");

    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.pink_slime_ball, 9)
      .addIngredient(WorldBlocks.pink_slime)
      .addCriterion("has_item", this.hasItem(WorldBlocks.pink_slime))
      .setGroup("tconstruct:slime_balls")
      .build(consumer, "tconstruct:common/slime/pink/slimeball_from_block");

    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.pink_slime_ball, 4)
      .addIngredient(WorldBlocks.congealed_pink_slime)
      .addCriterion("has_item", this.hasItem(WorldBlocks.congealed_pink_slime))
      .setGroup("tconstruct:slime_balls")
      .build(consumer, "tconstruct:common/slime/blue/slimeball_from_congealed");

    ShapedRecipeBuilder.shapedRecipe(WorldBlocks.pink_slime)
      .key('#', Tags.Items.PINK_SLIMEBALL)
      .patternLine("###")
      .patternLine("###")
      .patternLine("###")
      .addCriterion("has_item", this.hasItem(Tags.Items.PINK_SLIMEBALL))
      .setGroup("tconstruct:slime_blocks")
      .build(consumer, "tconstruct:common/slime/pink/slimeblock");
    */

    //PURPLE
    ShapedRecipeBuilder.shapedRecipe(WorldBlocks.congealed_purple_slime)
      .key('#', Tags.Items.PURPLE_SLIMEBALL)
      .patternLine("##")
      .patternLine("##")
      .addCriterion("has_item", this.hasItem(Tags.Items.PURPLE_SLIMEBALL))
      .setGroup("tconstruct:congealed_slime")
      .build(consumer, "tconstruct:common/slime/purple/congealed");

    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.purple_slime_ball, 9)
      .addIngredient(WorldBlocks.purple_slime)
      .addCriterion("has_item", this.hasItem(WorldBlocks.purple_slime))
      .setGroup("tconstruct:slime_balls")
      .build(consumer, "tconstruct:common/slime/purple/slimeball_from_block");

    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.purple_slime_ball, 4)
      .addIngredient(WorldBlocks.congealed_purple_slime)
      .addCriterion("has_item", this.hasItem(WorldBlocks.congealed_purple_slime))
      .setGroup("tconstruct:slime_balls")
      .build(consumer, "tconstruct:common/slime/purple/slimeball_from_congealed");

    ShapedRecipeBuilder.shapedRecipe(WorldBlocks.purple_slime)
      .key('#', Tags.Items.PURPLE_SLIMEBALL)
      .patternLine("###")
      .patternLine("###")
      .patternLine("###")
      .addCriterion("has_item", this.hasItem(Tags.Items.PURPLE_SLIMEBALL))
      .setGroup("tconstruct:slime_blocks")
      .build(consumer, "tconstruct:common/slime/purple/slimeblock");
  }

  private void addGlassRecipes(Consumer<IFinishedRecipe> consumer) {
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.black_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_BLACK)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/black_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.blue_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_BLUE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/blue_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.brown_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_BROWN)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/brown_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.cyan_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_CYAN)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/cyan_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.gray_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_GRAY)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/gray_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.green_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_GREEN)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/green_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.light_blue_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_LIGHT_BLUE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/light_blue_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.light_gray_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_LIGHT_GRAY)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/light_gray_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.lime_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_LIME)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/lime_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.magenta_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_MAGENTA)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/magenta_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.orange_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_ORANGE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/orange_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.pink_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_PINK)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/pink_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.purple_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_PURPLE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/purple_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.red_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_RED)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/red_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.white_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_WHITE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/white_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.yellow_clear_stained_glass, 8)
      .key('#', DecorativeBlocks.clear_glass)
      .key('X', net.minecraftforge.common.Tags.Items.DYES_YELLOW)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass))
      .build(consumer, "tconstruct:common/glass/yellow_clear_stained_glass");
  }
}
