package slimeknights.tconstruct.world.data;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import slimeknights.mantle.recipe.crafting.ShapedFallbackRecipeBuilder;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class WorldRecipeProvider extends BaseRecipeProvider {
  public WorldRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct World Recipes";
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    // Add recipe for all slimeball <-> congealed and slimeblock <-> slimeball
    // fallback: green slime
    ShapedFallbackRecipeBuilder congealed = ShapedFallbackRecipeBuilder.fallback(
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.congealedSlime.get(SlimeType.EARTH))
                         .key('#', Tags.Items.SLIMEBALLS)
                         .patternLine("##")
                         .patternLine("##")
                         .addCriterion("has_item", hasItem(Tags.Items.SLIMEBALLS))
                         .setGroup("tconstruct:congealed_slime"));
    // replace vanilla recipe to prevent it from conflicting with our slime blocks
    ShapedFallbackRecipeBuilder slimeBlock = ShapedFallbackRecipeBuilder.fallback(
      ShapedRecipeBuilder.shapedRecipe(Blocks.SLIME_BLOCK)
                         .key('#', Tags.Items.SLIMEBALLS)
                         .patternLine("###")
                         .patternLine("###")
                         .patternLine("###")
                         .addCriterion("has_item", hasItem(Tags.Items.SLIMEBALLS))
                         .setGroup("slime_blocks"));
    // does not need green as its the fallback
    for (SlimeType slimeType : SlimeType.TINKER) {
      ResourceLocation name = location("common/slime/" + slimeType.getString() + "/congealed");
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.congealedSlime.get(slimeType))
                         .key('#', slimeType.getSlimeBallTag())
                         .patternLine("##")
                         .patternLine("##")
                         .addCriterion("has_item", hasItem(slimeType.getSlimeBallTag()))
                         .setGroup("tconstruct:congealed_slime")
                         .build(consumer, name);
      congealed.addAlternative(name);
      ResourceLocation blockName = location("common/slime/" + slimeType.getString() + "/slimeblock");
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.slime.get(slimeType))
                         .key('#', slimeType.getSlimeBallTag())
                         .patternLine("###")
                         .patternLine("###")
                         .patternLine("###")
                         .addCriterion("has_item", hasItem(slimeType.getSlimeBallTag()))
                         .setGroup("slime_blocks")
                         .build(consumer, blockName);
      slimeBlock.addAlternative(blockName);
      // green already can craft into slime balls
      ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.slimeball.get(slimeType), 9)
                            .addIngredient(TinkerWorld.slime.get(slimeType))
                            .addCriterion("has_item", hasItem(TinkerWorld.slime.get(slimeType)))
                            .setGroup("tconstruct:slime_balls")
                            .build(consumer, "tconstruct:common/slime/" + slimeType.getString() + "/slimeball_from_block");
    }
    // all types of congealed need a recipe to a block
    for (SlimeType slimeType : SlimeType.values()) {
      ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.slimeball.get(slimeType), 4)
                            .addIngredient(TinkerWorld.congealedSlime.get(slimeType))
                            .addCriterion("has_item", hasItem(TinkerWorld.congealedSlime.get(slimeType)))
                            .setGroup("tconstruct:slime_balls")
                            .build(consumer, "tconstruct:common/slime/" + slimeType.getString() + "/slimeball_from_congealed");
    }

    // build fallback recipes
    congealed.build(consumer, location("common/slime/green/congealed"));
    // block fallback replaces the vanilla recipe
    slimeBlock.build(consumer);
  }
}
