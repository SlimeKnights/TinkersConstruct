package slimeknights.tconstruct.common.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
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
import slimeknights.tconstruct.items.FoodItems;
import slimeknights.tconstruct.items.GadgetItems;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;

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
    ShapelessRecipeBuilder.shapelessRecipe(CommonBlocks.firewood.get())
      .addIngredient(Items.BLAZE_POWDER)
      .addIngredient(CommonBlocks.lavawood.get())
      .addIngredient(Items.BLAZE_POWDER)
      .addCriterion("has_lavawood", this.hasItem(CommonBlocks.lavawood.get()))
      .build(consumer, "tconstruct:common/firewood/firewood");
    ShapedRecipeBuilder.shapedRecipe(CommonBlocks.firewood.getSlab(), 6)
      .key('#', CommonBlocks.firewood.get())
      .patternLine("###")
      .addCriterion("has_firewood", this.hasItem(CommonBlocks.firewood.get()))
      .build(consumer, "tconstruct:common/firewood/firewood_slab");
    ShapedRecipeBuilder.shapedRecipe(CommonBlocks.firewood.getStairs(), 4)
      .key('#', CommonBlocks.firewood.get())
      .patternLine("#  ")
      .patternLine("## ")
      .patternLine("###")
      .addCriterion("has_firewood", this.hasItem(CommonBlocks.firewood.get()))
      .build(consumer, "tconstruct:common/firewood/firewood_stairs");
    ShapedRecipeBuilder.shapedRecipe(CommonBlocks.lavawood.getSlab(), 6)
      .key('#', CommonBlocks.firewood.get())
      .patternLine("###")
      .addCriterion("has_firewood", this.hasItem(CommonBlocks.firewood.get()))
      .build(consumer, "tconstruct:common/firewood/lavawood_slab");
    ShapedRecipeBuilder.shapedRecipe(CommonBlocks.lavawood.getStairs(), 4)
      .key('#', CommonBlocks.firewood.get())
      .patternLine("#  ")
      .patternLine("## ")
      .patternLine("###")
      .addCriterion("has_firewood", this.hasItem(CommonBlocks.firewood.get()))
      .build(consumer, "tconstruct:common/firewood/lavawood_stairs");

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
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.mud_bricks.getSlab(), 6)
      .key('#', DecorativeBlocks.mud_bricks.get())
      .patternLine("###")
      .setGroup("tconstruct:mud_brick_slab")
      .addCriterion("has_mud_bricks", this.hasItem(DecorativeBlocks.mud_bricks.get()))
      .build(consumer, "tconstruct:common/soil/mud_bricks_slab_block");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.mud_bricks.getSlab())
      .key('#', CommonItems.mud_brick.get())
      .patternLine("##")
      .setGroup("tconstruct:mud_brick_slab")
      .addCriterion("has_mud_brick", this.hasItem(CommonItems.mud_brick.get()))
      .build(consumer, "tconstruct:common/soil/mud_bricks_slab");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.mud_bricks.getStairs(), 4)
      .key('#', DecorativeBlocks.mud_bricks.get())
      .patternLine("#  ")
      .patternLine("## ")
      .patternLine("###")
      .addCriterion("has_mud_bricks", this.hasItem(DecorativeBlocks.mud_bricks.get()))
      .build(consumer, "tconstruct:common/soil/mud_bricks_stairs");

    ShapelessRecipeBuilder.shapelessRecipe(CommonItems.book.get())
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

    ResourceLocation clearGlassSmeltingId = new ResourceLocation(TConstruct.modID, "common/glass/clear_glass_from_smelting");
    ConditionalRecipe.builder()
      .addCondition(not(new PulseLoadedCondition(TinkerPulseIds.TINKER_SMELTERY_PULSE_ID)))
      .addRecipe(CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.GLASS.asItem()), DecorativeBlocks.clear_glass.get().asItem(), 0.1F, 200)
        .addCriterion("has_item", this.hasItem(Blocks.GLASS))::build)
      .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_general/common/glass/clear_glass_from_smelting"), ConditionalAdvancement.builder()
        .addCondition(not(new PulseLoadedCondition(TinkerPulseIds.TINKER_SMELTERY_PULSE_ID)))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(clearGlassSmeltingId))
          .withCriterion("has_item", hasItem(Blocks.GLASS))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(clearGlassSmeltingId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
      ).build(consumer, clearGlassSmeltingId);
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
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(GadgetItems.efln_ball.get())
        .addIngredient(Items.FLINT)
        .addIngredient(Items.GUNPOWDER)
        .addCriterion("has_item", this.hasItem(net.minecraftforge.common.Tags.Items.DUSTS_GLOWSTONE))::build)
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addCondition(not(new TagEmptyCondition("forge", "dusts/sulfur")))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(GadgetItems.efln_ball.get())
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
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.glow_ball.get(), 8)
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
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.piggy_backpack.get())
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
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.punji.get(), 3)
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

  private void addGlassRecipes(Consumer<IFinishedRecipe> consumer) {
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.BLACK), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_BLACK)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/black_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.BLUE), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_BLUE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/blue_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.BROWN), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_BROWN)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/brown_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.CYAN), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_CYAN)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/cyan_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.GRAY), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_GRAY)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/gray_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.GREEN), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_GREEN)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/green_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.LIGHT_BLUE), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_LIGHT_BLUE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/light_blue_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.LIGHT_GRAY), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_LIGHT_GRAY)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/light_gray_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.LIME), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_LIME)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/lime_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.MAGENTA), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_MAGENTA)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/magenta_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.ORANGE), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_ORANGE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/orange_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.PINK), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_PINK)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/pink_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.PURPLE), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_PURPLE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/purple_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.RED), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_RED)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/red_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.WHITE), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_WHITE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/white_clear_stained_glass");
    ShapedRecipeBuilder.shapedRecipe(DecorativeBlocks.clear_stained_glass.get(ClearStainedGlassBlock.GlassColor.YELLOW), 8)
      .key('#', DecorativeBlocks.clear_glass.get())
      .key('X', net.minecraftforge.common.Tags.Items.DYES_YELLOW)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .setGroup("tconstruct:stained_clear_glass")
      .addCriterion("has_clear_glass", this.hasItem(DecorativeBlocks.clear_glass.get()))
      .build(consumer, "tconstruct:common/glass/yellow_clear_stained_glass");
  }

  private void addStoneRecipes(Consumer<IFinishedRecipe> consumer) {
    ResourceLocation jackOLanternId = new ResourceLocation(TConstruct.modID, "gadgets/stone/jack_o_lantern");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(Blocks.JACK_O_LANTERN)
        .key('#', Blocks.CARVED_PUMPKIN)
        .key('X', GadgetBlocks.stone_torch.get())
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
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.stone_ladder.get(), 3)
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
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.stone_stick.get(), 4)
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
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.stone_torch.get(), 4)
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
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
      ResourceLocation slimeSlingId = new ResourceLocation(TConstruct.modID, "gadgets/slimesling/" + slime.getName());
      ConditionalRecipe.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_sling.get(slime))
          .setGroup("tconstruct:slimesling")
          .key('#', Items.STRING)
          .key('X', WorldBlocks.congealed_slime.get(slime))
          .key('L', slime.getSlimeBallTag())
          .patternLine("#X#")
          .patternLine("L L")
          .patternLine(" L ")
          .addCriterion("has_item", this.hasItem(Items.STRING))::build)
        .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/slimesling/" + slime.getName()), ConditionalAdvancement.builder()
          .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
          .addAdvancement(Advancement.Builder.builder()
            .withParentId(new ResourceLocation("recipes/root"))
            .withRewards(AdvancementRewards.Builder.recipe(slimeSlingId))
            .withCriterion("has_item", hasItem(Items.STRING))
            .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(slimeSlingId))
            .withRequirementsStrategy(IRequirementsStrategy.OR))
        ).build(consumer, slimeSlingId);
    }
  }

  private void addSlimeBootsRecipes(Consumer<IFinishedRecipe> consumer) {
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
      ResourceLocation slimeBootsId = new ResourceLocation(TConstruct.modID, "gadgets/slimeboots/" + slime.getName());
      ConditionalRecipe.builder()
        .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
        .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetItems.slime_boots.get(slime))
          .setGroup("tconstruct:slime_boots")
          .key('#', WorldBlocks.congealed_slime.get(slime))
          .key('X', slime.getSlimeBallTag())
          .patternLine("X X")
          .patternLine("# #")
          .addCriterion("has_item", this.hasItem(Items.SLIME_BALL))::build)
        .setAdvancement(new ResourceLocation(TConstruct.modID, "recipes/tinkers_gadgets/slimeboots/" + slime.getName()), ConditionalAdvancement.builder()
          .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
          .addAdvancement(Advancement.Builder.builder()
            .withParentId(new ResourceLocation("recipes/root"))
            .withRewards(AdvancementRewards.Builder.recipe(slimeBootsId))
            .withCriterion("has_item", hasItem(Items.SLIME_BALL))
            .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(slimeBootsId))
            .withRequirementsStrategy(IRequirementsStrategy.OR))
        ).build(consumer, slimeBootsId);
    }
  }

  private void addWoodenRailRecipes(Consumer<IFinishedRecipe> consumer) {
    ResourceLocation woodenRailId = new ResourceLocation(TConstruct.modID, "gadgets/rail/wooden_rail");
    ConditionalRecipe.builder()
      .addCondition(new PulseLoadedCondition(TinkerPulseIds.TINKER_GADGETS_PULSE_ID))
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.wooden_rail.get(), 4)
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
      .addRecipe(ShapedRecipeBuilder.shapedRecipe(GadgetBlocks.wooden_dropper_rail.get(), 4)
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
