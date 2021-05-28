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
import slimeknights.tconstruct.common.registration.WoodBlockObject;
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
    // only earth slime recipe we need here slime
    ShapedRecipeBuilder.shapedRecipe(TinkerWorld.congealedSlime.get(SlimeType.EARTH))
                       .key('#', SlimeType.EARTH.getSlimeBallTag())
                       .patternLine("##")
                       .patternLine("##")
                       .addCriterion("has_item", hasItem(SlimeType.EARTH.getSlimeBallTag()))
                       .setGroup("tconstruct:congealed_slime")
                       .build(consumer, location("common/slime/earth/congealed"));

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
      ResourceLocation blockName = location("common/slime/" + slimeType.getString() + "/slimeblock");
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.slime.get(slimeType))
                         .key('#', slimeType.getSlimeBallTag())
                         .patternLine("###")
                         .patternLine("###")
                         .patternLine("###")
                         .addCriterion("has_item", hasItem(slimeType.getSlimeBallTag()))
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
    ShapedRecipeBuilder.shapedRecipe(Blocks.STICKY_PISTON)
                       .patternLine("#")
                       .patternLine("P")
                       .key('#', Tags.Items.SLIMEBALLS)
                       .key('P', Blocks.PISTON)
                       .addCriterion("has_slime_ball", hasItem(Tags.Items.SLIMEBALLS))
                       .build(consumer, location("common/slime/sticky_piston"));
    ShapedRecipeBuilder.shapedRecipe(Items.LEAD, 2)
                       .key('~', Items.STRING)
                       .key('O', Tags.Items.SLIMEBALLS)
                       .patternLine("~~ ")
                       .patternLine("~O ")
                       .patternLine("  ~")
                       .addCriterion("has_slime_ball", hasItem(Tags.Items.SLIMEBALLS))
                       .build(consumer, location("common/slime/lead"));
    ShapelessRecipeBuilder.shapelessRecipe(Items.MAGMA_CREAM)
                          .addIngredient(Items.BLAZE_POWDER)
                          .addIngredient(Tags.Items.SLIMEBALLS)
                          .addCriterion("has_blaze_powder", hasItem(Items.BLAZE_POWDER))
                          .build(consumer, location("common/slime/magma_cream"));

    // wood
    String woodFolder = "world/wood/";
    registerWoodRecipes(consumer, TinkerWorld.greenheart,  woodFolder + "greenheart/");
    registerWoodRecipes(consumer, TinkerWorld.skyroot,     woodFolder + "skyroot/");
    registerWoodRecipes(consumer, TinkerWorld.bloodshroom, woodFolder + "bloodshroom/");
  }

  /**
   * Registers recipes relevant to wood
   * @param consumer  Recipe consumer
   * @param wood      Wood types
   * @param folder    Wood folder
   */
  private void registerWoodRecipes(Consumer<IFinishedRecipe> consumer, WoodBlockObject wood, String folder) {
    // planks
    ShapelessRecipeBuilder.shapelessRecipe(wood, 4).addIngredient(wood.getLogItemTag())
                          .setGroup("planks")
                          .addCriterion("has_log", hasItem(wood.getLogItemTag()))
                          .build(consumer, location(folder + "planks"));
    ShapedRecipeBuilder.shapedRecipe(wood.getSlab(), 6)
                       .key('#', wood)
                       .patternLine("###")
                       .setGroup("wooden_slab")
                       .addCriterion("has_planks", hasItem(wood))
                       .build(consumer, location(folder + "slab"));
    ShapedRecipeBuilder.shapedRecipe(wood.getStairs(), 4)
                       .key('#', wood)
                       .patternLine("#  ").patternLine("## ").patternLine("###")
                       .setGroup("wooden_stairs").addCriterion("has_planks", hasItem(wood))
                       .build(consumer, location(folder + "stairs"));
    // log to stripped
    ShapedRecipeBuilder.shapedRecipe(wood.getWood(), 3)
                       .key('#', wood.getLog())
                       .patternLine("##").patternLine("##")
                       .setGroup("bark")
                       .addCriterion("has_log", hasItem(wood.getLog()))
                       .build(consumer, location(folder + "log_to_wood"));
    ShapedRecipeBuilder.shapedRecipe(wood.getStrippedWood(), 3)
                       .key('#', wood.getStrippedLog())
                       .patternLine("##").patternLine("##")
                       .setGroup("bark")
                       .addCriterion("has_log", hasItem(wood.getStrippedLog()))
                       .build(consumer, location(folder + "stripped_log_to_wood"));
    // doors
    ShapedRecipeBuilder.shapedRecipe(wood.getFence(), 3)
                       .key('#', Tags.Items.RODS_WOODEN).key('W', wood)
                       .patternLine("W#W").patternLine("W#W")
                       .setGroup("wooden_fence")
                       .addCriterion("has_planks", hasItem(wood))
                       .build(consumer, location(folder + "fence"));
    ShapedRecipeBuilder.shapedRecipe(wood.getFenceGate())
                       .key('#', Items.STICK).key('W', wood)
                       .patternLine("#W#").patternLine("#W#")
                       .setGroup("wooden_fence_gate")
                       .addCriterion("has_planks", hasItem(wood))
                       .build(consumer, location(folder + "fence_gate"));
    ShapedRecipeBuilder.shapedRecipe(wood.getDoor(), 3)
                       .key('#', wood)
                       .patternLine("##").patternLine("##").patternLine("##")
                       .setGroup("wooden_door")
                       .addCriterion("has_planks", hasItem(wood))
                       .build(consumer, location(folder + "door"));
    ShapedRecipeBuilder.shapedRecipe(wood.getTrapdoor(), 2)
                       .key('#', wood)
                       .patternLine("###").patternLine("###")
                       .setGroup("wooden_trapdoor")
                       .addCriterion("has_planks", hasItem(wood))
                       .build(consumer, location(folder + "trapdoor"));
    // buttons
    ShapelessRecipeBuilder.shapelessRecipe(wood.getButton())
                          .addIngredient(wood)
                          .setGroup("wooden_button")
                          .addCriterion("has_planks", hasItem(wood))
                          .build(consumer, location(folder + "button"));
    ShapedRecipeBuilder.shapedRecipe(wood.getPressurePlate())
                       .key('#', wood)
                       .patternLine("##")
                       .setGroup("wooden_pressure_plate")
                       .addCriterion("has_planks", hasItem(wood))
                       .build(consumer, location(folder + "pressure_plate"));
  }
}
