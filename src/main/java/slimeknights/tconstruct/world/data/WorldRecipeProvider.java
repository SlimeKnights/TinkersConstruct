package slimeknights.tconstruct.world.data;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.library.data.recipe.ICommonRecipeHelper;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class WorldRecipeProvider extends BaseRecipeProvider implements ICommonRecipeHelper {
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
    // only earth slime recipe we need here slime
    ShapedRecipeBuilder.shapedRecipe(TinkerWorld.congealedSlime.get(SlimeType.EARTH))
                       .key('#', SlimeType.EARTH.getSlimeballTag())
                       .patternLine("##")
                       .patternLine("##")
                       .addCriterion("has_item", hasItem(SlimeType.EARTH.getSlimeballTag()))
                       .setGroup("tconstruct:congealed_slime")
                       .build(consumer, modResource("common/slime/earth/congealed"));

    // does not need green as its the fallback
    for (SlimeType slimeType : SlimeType.TINKER) {
      ResourceLocation name = modResource("common/slime/" + slimeType.getString() + "/congealed");
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.congealedSlime.get(slimeType))
                         .key('#', slimeType.getSlimeballTag())
                         .patternLine("##")
                         .patternLine("##")
                         .addCriterion("has_item", hasItem(slimeType.getSlimeballTag()))
                         .setGroup("tconstruct:congealed_slime")
                         .build(consumer, name);
      ResourceLocation blockName = modResource("common/slime/" + slimeType.getString() + "/slimeblock");
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.slime.get(slimeType))
                         .key('#', slimeType.getSlimeballTag())
                         .patternLine("###")
                         .patternLine("###")
                         .patternLine("###")
                         .addCriterion("has_item", hasItem(slimeType.getSlimeballTag()))
                         .setGroup("slime_blocks")
                         .build(consumer, blockName);
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

    // craft other slime based items, forge does not automatically add recipes using the tag anymore
    Consumer<IFinishedRecipe> slimeConsumer = withCondition(consumer, ConfigEnabledCondition.SLIME_RECIPE_FIX);
    ShapedRecipeBuilder.shapedRecipe(Blocks.STICKY_PISTON)
                       .patternLine("#")
                       .patternLine("P")
                       .key('#', Tags.Items.SLIMEBALLS)
                       .key('P', Blocks.PISTON)
                       .addCriterion("has_slime_ball", hasItem(Tags.Items.SLIMEBALLS))
                       .build(slimeConsumer, modResource("common/slime/sticky_piston"));
    ShapedRecipeBuilder.shapedRecipe(Items.LEAD, 2)
                       .key('~', Items.STRING)
                       .key('O', Tags.Items.SLIMEBALLS)
                       .patternLine("~~ ")
                       .patternLine("~O ")
                       .patternLine("  ~")
                       .addCriterion("has_slime_ball", hasItem(Tags.Items.SLIMEBALLS))
                       .build(slimeConsumer, modResource("common/slime/lead"));
    ShapelessRecipeBuilder.shapelessRecipe(Items.MAGMA_CREAM)
                          .addIngredient(Items.BLAZE_POWDER)
                          .addIngredient(Tags.Items.SLIMEBALLS)
                          .addCriterion("has_blaze_powder", hasItem(Items.BLAZE_POWDER))
                          .build(slimeConsumer, modResource("common/slime/magma_cream"));

    // wood
    String woodFolder = "world/wood/";
    woodCrafting(consumer, TinkerWorld.greenheart, woodFolder + "greenheart/");
    woodCrafting(consumer, TinkerWorld.skyroot, woodFolder + "skyroot/");
    woodCrafting(consumer, TinkerWorld.bloodshroom, woodFolder + "bloodshroom/");
  }
}
