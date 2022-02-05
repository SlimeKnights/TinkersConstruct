package slimeknights.tconstruct.world.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.common.registration.GeodeItemObject;
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
  protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    // Add recipe for all slimeball <-> congealed and slimeblock <-> slimeball
    // only earth slime recipe we need here slime
    ShapedRecipeBuilder.shaped(TinkerWorld.congealedSlime.get(SlimeType.EARTH))
                       .define('#', SlimeType.EARTH.getSlimeballTag())
                       .pattern("##")
                       .pattern("##")
                       .unlockedBy("has_item", has(SlimeType.EARTH.getSlimeballTag()))
                       .group("tconstruct:congealed_slime")
                       .save(consumer, modResource("common/slime/earth/congealed"));

    // does not need green as its the fallback
    for (SlimeType slimeType : SlimeType.TINKER) {
      ResourceLocation name = modResource("common/slime/" + slimeType.getSerializedName() + "/congealed");
      ShapedRecipeBuilder.shaped(TinkerWorld.congealedSlime.get(slimeType))
                         .define('#', slimeType.getSlimeballTag())
                         .pattern("##")
                         .pattern("##")
                         .unlockedBy("has_item", has(slimeType.getSlimeballTag()))
                         .group("tconstruct:congealed_slime")
                         .save(consumer, name);
      ResourceLocation blockName = modResource("common/slime/" + slimeType.getSerializedName() + "/slimeblock");
      ShapedRecipeBuilder.shaped(TinkerWorld.slime.get(slimeType))
                         .define('#', slimeType.getSlimeballTag())
                         .pattern("###")
                         .pattern("###")
                         .pattern("###")
                         .unlockedBy("has_item", has(slimeType.getSlimeballTag()))
                         .group("slime_blocks")
                         .save(consumer, blockName);
      // green already can craft into slime balls
      ShapelessRecipeBuilder.shapeless(TinkerCommons.slimeball.get(slimeType), 9)
                            .requires(TinkerWorld.slime.get(slimeType))
                            .unlockedBy("has_item", has(TinkerWorld.slime.get(slimeType)))
                            .group("tconstruct:slime_balls")
                            .save(consumer, "tconstruct:common/slime/" + slimeType.getSerializedName() + "/slimeball_from_block");
    }
    // all types of congealed need a recipe to a block
    for (SlimeType slimeType : SlimeType.values()) {
      ShapelessRecipeBuilder.shapeless(TinkerCommons.slimeball.get(slimeType), 4)
                            .requires(TinkerWorld.congealedSlime.get(slimeType))
                            .unlockedBy("has_item", has(TinkerWorld.congealedSlime.get(slimeType)))
                            .group("tconstruct:slime_balls")
                            .save(consumer, "tconstruct:common/slime/" + slimeType.getSerializedName() + "/slimeball_from_congealed");
    }

    // craft other slime based items, forge does not automatically add recipes using the tag anymore
    Consumer<FinishedRecipe> slimeConsumer = withCondition(consumer, ConfigEnabledCondition.SLIME_RECIPE_FIX);
    ShapedRecipeBuilder.shaped(Blocks.STICKY_PISTON)
                       .pattern("#")
                       .pattern("P")
                       .define('#', Tags.Items.SLIMEBALLS)
                       .define('P', Blocks.PISTON)
                       .unlockedBy("has_slime_ball", has(Tags.Items.SLIMEBALLS))
                       .save(slimeConsumer, modResource("common/slime/sticky_piston"));
    ShapedRecipeBuilder.shaped(Items.LEAD, 2)
                       .define('~', Items.STRING)
                       .define('O', Tags.Items.SLIMEBALLS)
                       .pattern("~~ ")
                       .pattern("~O ")
                       .pattern("  ~")
                       .unlockedBy("has_slime_ball", has(Tags.Items.SLIMEBALLS))
                       .save(slimeConsumer, modResource("common/slime/lead"));
    ShapelessRecipeBuilder.shapeless(Items.MAGMA_CREAM)
                          .requires(Items.BLAZE_POWDER)
                          .requires(Tags.Items.SLIMEBALLS)
                          .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER))
                          .save(slimeConsumer, modResource("common/slime/magma_cream"));

    // wood
    String woodFolder = "world/wood/";
    woodCrafting(consumer, TinkerWorld.greenheart, woodFolder + "greenheart/");
    woodCrafting(consumer, TinkerWorld.skyroot, woodFolder + "skyroot/");
    woodCrafting(consumer, TinkerWorld.bloodshroom, woodFolder + "bloodshroom/");

    // geodes
    geodeRecipes(consumer, TinkerWorld.earthGeode, SlimeType.EARTH, "common/slime/earth/");
    geodeRecipes(consumer, TinkerWorld.skyGeode,   SlimeType.SKY,   "common/slime/sky/");
    geodeRecipes(consumer, TinkerWorld.ichorGeode, SlimeType.ICHOR, "common/slime/ichor/");
    geodeRecipes(consumer, TinkerWorld.enderGeode, SlimeType.ENDER, "common/slime/ender/");
  }

  private void geodeRecipes(Consumer<FinishedRecipe> consumer, GeodeItemObject geode, SlimeType slime, String folder) {
    ShapedRecipeBuilder.shaped(geode.getBlock())
                       .define('#', geode.asItem())
                       .pattern("##")
                       .pattern("##")
                       .unlockedBy("has_item", has(geode.asItem()))
                       .group("tconstruct:slime_crystal_block")
                       .save(consumer, modResource(folder + "crystal_block"));
    SimpleCookingRecipeBuilder.blasting(Ingredient.of(geode), TinkerCommons.slimeball.get(slime), 0.2f, 200)
                              .unlockedBy("has_crystal", has(geode))
                              .group("tconstruct:slime_crystal")
                              .save(consumer, modResource(folder + "crystal_smelting"));
  }
}
