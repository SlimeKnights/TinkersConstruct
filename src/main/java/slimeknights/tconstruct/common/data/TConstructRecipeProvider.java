package slimeknights.tconstruct.common.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.blocks.DecorativeBlocks;
import slimeknights.tconstruct.blocks.GadgetBlocks;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.common.Tags;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.common.conditions.PulseLoadedCondition;
import slimeknights.tconstruct.items.CommonItems;
import slimeknights.tconstruct.items.GadgetItems;
import slimeknights.tconstruct.library.TinkerPulseIds;

import java.util.function.Consumer;
import java.util.stream.Stream;

public class TConstructRecipeProvider extends RecipeProvider implements IConditionBuilder {

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

    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.book)
      .addIngredient(Items.BOOK)
      .addIngredient(Blocks.GRAVEL)
      .addCriterion("has_gravel", this.hasItem(Blocks.GRAVEL))
      .build(consumer, "tconstruct:common/book");

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
  }

  private void addWorld(Consumer<IFinishedRecipe> consumer) {

  }

  private void addGadgets(Consumer<IFinishedRecipe> consumer) {
    this.addSlimeBootsRecipes(consumer);
    this.addSlimeSlingRecipes(consumer);
    this.addWoodenRailRecipes(consumer);
    this.addStoneRecipes(consumer);

    ResourceLocation eflnBallId = new ResourceLocation(TConstruct.modID, "gadgets/throwball/efln");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addCondition(new TagEmptyCondition("forge", "dusts/sulfur"))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(GadgetItems.efln_ball)
        .addIngredient(Items.FLINT)
        .addIngredient(Items.GUNPOWDER)
        .addCriterion("has_item", this.hasItem(net.minecraftforge.common.Tags.Items.DUSTS_GLOWSTONE))::build)
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addCondition(not(new TagEmptyCondition("forge", "dusts/sulfur")))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(GadgetItems.efln_ball)
        .addIngredient(Tags.Items.DUSTS_SULFUR)
        .addIngredient(Ingredient.fromItemListStream(Stream.of(
          new Ingredient.TagList(Tags.Items.DUSTS_SULFUR),
          new Ingredient.SingleItemList(new ItemStack(Items.GUNPOWDER)))
        ))
        .addCriterion("has_item", this.hasItem(Items.GUNPOWDER))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/throwball/efln"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(eflnBallId))
          .withCriterion("has_item", hasItem(Items.GUNPOWDER))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(eflnBallId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, eflnBallId);

    ResourceLocation glowBallId = new ResourceLocation(TConstruct.modID, "gadgets/throwball/glowball");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.glow_ball, 8)
        .key('#', Items.SNOWBALL)
        .key('X', net.minecraftforge.common.Tags.Items.DUSTS_GLOWSTONE)
        .patternLine("###")
        .patternLine("#X#")
        .patternLine("###")
        .addCriterion("has_item", this.hasItem(net.minecraftforge.common.Tags.Items.DUSTS_GLOWSTONE))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/throwball/glowball"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(glowBallId))
          .withCriterion("has_item", hasItem(net.minecraftforge.common.Tags.Items.DUSTS_GLOWSTONE))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(glowBallId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, glowBallId);

    ResourceLocation piggyBackpackId = new ResourceLocation(TConstruct.modID, "gadgets/piggy_backpack");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.piggy_backpack)
        .key('#', net.minecraftforge.common.Tags.Items.RODS_WOODEN)
        .key('X', net.minecraftforge.common.Tags.Items.LEATHER)
        .patternLine(" X ")
        .patternLine("# #")
        .patternLine(" X ")
        .addCriterion("has_item", this.hasItem(net.minecraftforge.common.Tags.Items.RODS_WOODEN))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/piggy_backpack"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(piggyBackpackId))
          .withCriterion("has_item", hasItem(net.minecraftforge.common.Tags.Items.RODS_WOODEN))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(piggyBackpackId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, piggyBackpackId);

    ResourceLocation punjiSticksId = new ResourceLocation(TConstruct.modID, "gadgets/punji_sticks");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.punji, 3)
        .key('#', Items.SUGAR_CANE)
        .patternLine("# #")
        .patternLine(" # ")
        .patternLine("# #")
        .addCriterion("has_item", this.hasItem(Items.SUGAR_CANE))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/punji_sticks"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(punjiSticksId))
          .withCriterion("has_item", hasItem(Items.SUGAR_CANE))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(punjiSticksId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, punjiSticksId);
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

  private void addStoneRecipes(Consumer<IFinishedRecipe> consumer) {
    ResourceLocation jackOLanternId = new ResourceLocation(TConstruct.modID, "gadgets/stone/jack_o_lantern");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(Blocks.JACK_O_LANTERN)
        .key('#', Blocks.CARVED_PUMPKIN)
        .key('X', GadgetBlocks.stone_torch)
        .patternLine("#")
        .patternLine("X")
        .addCriterion("has_item", this.hasItem(Blocks.CARVED_PUMPKIN))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/stone/jack_o_lantern"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(jackOLanternId))
          .withCriterion("has_item", hasItem(Blocks.CARVED_PUMPKIN))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(jackOLanternId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, jackOLanternId);

    ResourceLocation stoneLadderId = new ResourceLocation(TConstruct.modID, "gadgets/stone/stone_ladder");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.stone_ladder, 3)
        .key('#', Tags.Items.RODS_STONE)
        .patternLine("# #")
        .patternLine("###")
        .patternLine("# #")
        .addCriterion("has_item", this.hasItem(Tags.Items.RODS_STONE))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/stone/stone_ladder"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(stoneLadderId))
          .withCriterion("has_item", hasItem(Tags.Items.RODS_STONE))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(stoneLadderId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, stoneLadderId);

    ResourceLocation stoneRodId = new ResourceLocation(TConstruct.modID, "gadgets/stone/stone_rod");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.stone_stick, 4)
        .key('#', Ingredient.fromItemListStream(Stream.of(
          new Ingredient.TagList(net.minecraftforge.common.Tags.Items.STONE),
          new Ingredient.TagList(net.minecraftforge.common.Tags.Items.COBBLESTONE))
        ))
        .patternLine("#")
        .patternLine("#")
        .addCriterion("has_item", this.hasItem(net.minecraftforge.common.Tags.Items.STONE))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/stone/stone_rod"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(stoneRodId))
          .withCriterion("has_item", hasItem(net.minecraftforge.common.Tags.Items.STONE))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(stoneRodId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, stoneRodId);

    ResourceLocation stoneTorchId = new ResourceLocation(TConstruct.modID, "gadgets/stone/stone_torch");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.stone_torch, 4)
        .key('#', Ingredient.fromItemListStream(Stream.of(
          new Ingredient.SingleItemList(new ItemStack(Items.COAL)),
          new Ingredient.SingleItemList(new ItemStack(Items.CHARCOAL))
        )))
        .key('X', Tags.Items.RODS_STONE)
        .patternLine("#")
        .patternLine("X")
        .addCriterion("has_item", this.hasItem(Tags.Items.RODS_STONE))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/stone/stone_torch"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(stoneTorchId))
          .withCriterion("has_item", hasItem(Tags.Items.RODS_STONE))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(stoneTorchId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, stoneTorchId);
  }

  private void addSlimeSlingRecipes(Consumer<IFinishedRecipe> consumer) {
    ResourceLocation bloodSlimeSlingId = new ResourceLocation(TConstruct.modID, "gadgets/slimesling/blood");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_sling_blood)
        .setGroup("tconstruct:slimesling")
        .key('#', Items.STRING)
        .key('X', WorldBlocks.congealed_blood_slime)
        .key('L', Tags.Items.BLOOD_SLIMEBALL)
        .patternLine("#X#")
        .patternLine("L L")
        .patternLine(" L ")
        .addCriterion("has_item", this.hasItem(Items.STRING))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/slimesling/blood"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(bloodSlimeSlingId))
          .withCriterion("has_item", hasItem(Items.STRING))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(bloodSlimeSlingId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, bloodSlimeSlingId);


    ResourceLocation blueSlimeSlingId = new ResourceLocation(TConstruct.modID, "gadgets/slimesling/blue");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_sling_blue)
        .setGroup("tconstruct:slimesling")
        .key('#', Items.STRING)
        .key('X', WorldBlocks.congealed_blue_slime)
        .key('L', Tags.Items.BLUE_SLIMEBALL)
        .patternLine("#X#")
        .patternLine("L L")
        .patternLine(" L ")
        .addCriterion("has_item", this.hasItem(Items.STRING))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/slimesling/blue"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(blueSlimeSlingId))
          .withCriterion("has_item", hasItem(Items.STRING))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(blueSlimeSlingId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, blueSlimeSlingId);

    ResourceLocation greenSlimeSlingId = new ResourceLocation(TConstruct.modID, "gadgets/slimesling/green");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_sling_green)
        .setGroup("tconstruct:slimesling")
        .key('#', Items.STRING)
        .key('X', WorldBlocks.congealed_green_slime)
        .key('L', Tags.Items.GREEN_SLIMEBALL)
        .patternLine("#X#")
        .patternLine("L L")
        .patternLine(" L ")
        .addCriterion("has_item", this.hasItem(Items.STRING))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/slimesling/green"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(greenSlimeSlingId))
          .withCriterion("has_item", hasItem(Items.STRING))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(greenSlimeSlingId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, greenSlimeSlingId);

    ResourceLocation magmaSlimeSlingId = new ResourceLocation(TConstruct.modID, "gadgets/slimesling/magma");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_sling_magma)
        .setGroup("tconstruct:slimesling")
        .key('#', Items.STRING)
        .key('X', WorldBlocks.congealed_magma_slime)
        .key('L', Tags.Items.MAGMA_SLIMEBALL)
        .patternLine("#X#")
        .patternLine("L L")
        .patternLine(" L ")
        .addCriterion("has_item", this.hasItem(Items.STRING))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/slimesling/magma"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(magmaSlimeSlingId))
          .withCriterion("has_item", hasItem(Items.STRING))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(magmaSlimeSlingId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, magmaSlimeSlingId);

    ResourceLocation purpleSlimeSlingId = new ResourceLocation(TConstruct.modID, "gadgets/slimesling/purple");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_sling_purple)
        .setGroup("tconstruct:slimesling")
        .key('#', Items.STRING)
        .key('X', WorldBlocks.congealed_purple_slime)
        .key('L', Tags.Items.PURPLE_SLIMEBALL)
        .patternLine("#X#")
        .patternLine("L L")
        .patternLine(" L ")
        .addCriterion("has_item", this.hasItem(Items.STRING))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/slimesling/purple"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(purpleSlimeSlingId))
          .withCriterion("has_item", hasItem(Items.STRING))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(purpleSlimeSlingId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, purpleSlimeSlingId);
  }

  private void addSlimeBootsRecipes(Consumer<IFinishedRecipe> consumer) {
    ResourceLocation bloodSlimeBootsId = new ResourceLocation(TConstruct.modID, "gadgets/slimeboots/blood");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_boots_blood)
        .setGroup("tconstruct:slime_boots")
        .key('#', WorldBlocks.congealed_blood_slime)
        .key('X', Tags.Items.BLOOD_SLIMEBALL)
        .patternLine("X X")
        .patternLine("# #")
        .addCriterion("has_item", this.hasItem(Items.SLIME_BALL))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/slimeboots/blood"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(bloodSlimeBootsId))
          .withCriterion("has_item", hasItem(Items.SLIME_BALL))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(bloodSlimeBootsId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, bloodSlimeBootsId);

    ResourceLocation blueSlimeBootsId = new ResourceLocation(TConstruct.modID, "gadgets/slimeboots/blue");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_boots_blue)
        .setGroup("tconstruct:slime_boots")
        .key('#', WorldBlocks.congealed_blue_slime)
        .key('X', Tags.Items.BLUE_SLIMEBALL)
        .patternLine("X X")
        .patternLine("# #")
        .addCriterion("has_item", this.hasItem(Items.SLIME_BALL))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/slimeboots/blue"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(blueSlimeBootsId))
          .withCriterion("has_item", hasItem(Items.SLIME_BALL))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(blueSlimeBootsId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, blueSlimeBootsId);

    ResourceLocation greenSlimeBootsId = new ResourceLocation(TConstruct.modID, "gadgets/slimeboots/green");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_boots_green)
        .setGroup("tconstruct:slime_boots")
        .key('#', WorldBlocks.congealed_green_slime)
        .key('X', Tags.Items.GREEN_SLIMEBALL)
        .patternLine("X X")
        .patternLine("# #")
        .addCriterion("has_item", this.hasItem(Items.SLIME_BALL))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/slimeboots/green"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(greenSlimeBootsId))
          .withCriterion("has_item", hasItem(Items.SLIME_BALL))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(greenSlimeBootsId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, greenSlimeBootsId);

    ResourceLocation magmaSlimeBootsId = new ResourceLocation(TConstruct.modID, "gadgets/slimeboots/magma");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_boots_magma)
        .setGroup("tconstruct:slime_boots")
        .key('#', WorldBlocks.congealed_magma_slime)
        .key('X', Tags.Items.MAGMA_SLIMEBALL)
        .patternLine("X X")
        .patternLine("# #")
        .addCriterion("has_item", this.hasItem(Items.SLIME_BALL))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/slimeboots/magma"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(magmaSlimeBootsId))
          .withCriterion("has_item", hasItem(Items.SLIME_BALL))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(magmaSlimeBootsId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, magmaSlimeBootsId);

    ResourceLocation purpleSlimeBootsId = new ResourceLocation(TConstruct.modID, "gadgets/slimeboots/purple");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_boots_purple)
        .setGroup("tconstruct:slime_boots")
        .key('#', WorldBlocks.congealed_purple_slime)
        .key('X', Tags.Items.PURPLE_SLIMEBALL)
        .patternLine("X X")
        .patternLine("# #")
        .addCriterion("has_item", this.hasItem(Items.SLIME_BALL))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/slimeboots/purple"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(purpleSlimeBootsId))
          .withCriterion("has_item", hasItem(Items.SLIME_BALL))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(purpleSlimeBootsId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, purpleSlimeBootsId);
  }

  private void addWoodenRailRecipes(Consumer<IFinishedRecipe> consumer) {
    ResourceLocation woodenRailId = new ResourceLocation(TConstruct.modID, "gadgets/rail/wooden_rail");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.wooden_rail, 4)
        .key('#', ItemTags.PLANKS)
        .key('X', net.minecraftforge.common.Tags.Items.RODS_WOODEN)
        .patternLine("# #")
        .patternLine("#X#")
        .patternLine("# #")
        .addCriterion("has_item", this.hasItem(ItemTags.PLANKS))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/rail/wooden_rail"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(woodenRailId))
          .withCriterion("has_item", hasItem(ItemTags.PLANKS))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(woodenRailId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, woodenRailId);

    ResourceLocation woodenDropperRailId = new ResourceLocation(TConstruct.modID, "gadgets/rail/wooden_dropper_rail");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.wooden_dropper_rail, 4)
        .key('#', ItemTags.PLANKS)
        .key('X', ItemTags.WOODEN_TRAPDOORS)
        .patternLine("# #")
        .patternLine("#X#")
        .patternLine("# #")
        .addCriterion("has_item", this.hasItem(ItemTags.PLANKS))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/rail/wooden_dropper_rail"), ConditionalAdvancement.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(woodenDropperRailId))
          .withCriterion("has_item", hasItem(ItemTags.PLANKS))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(woodenDropperRailId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, woodenDropperRailId);
  }
}
