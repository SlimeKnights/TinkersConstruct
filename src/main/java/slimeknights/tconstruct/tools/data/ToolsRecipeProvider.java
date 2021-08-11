package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.SpecializedRepairRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.item.ArmorSlotType;

import java.util.function.Consumer;

public class ToolsRecipeProvider extends BaseRecipeProvider implements IMaterialRecipeHelper, IToolRecipeHelper {
  public ToolsRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Tool Recipes";
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.addToolBuildingRecipes(consumer);
    this.addPartRecipes(consumer);
  }

  private void addToolBuildingRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/building/";
    String repairFolder = "tools/repair/";
    String armorFolder = "armor/building/";
    String armorRepairFolder = "armor/repair/";
    // stone
    toolBuilding(consumer, TinkerTools.pickaxe, folder);
    toolBuilding(consumer, TinkerTools.sledgeHammer, folder);
    toolBuilding(consumer, TinkerTools.veinHammer, folder);
    // dirt
    toolBuilding(consumer, TinkerTools.mattock, folder);
    toolBuilding(consumer, TinkerTools.excavator, folder);
    // wood
    toolBuilding(consumer, TinkerTools.handAxe, folder);
    toolBuilding(consumer, TinkerTools.broadAxe, folder);
    // plants
    toolBuilding(consumer, TinkerTools.kama, folder);
    toolBuilding(consumer, TinkerTools.scythe, folder);
    // sword
    toolBuilding(consumer, TinkerTools.dagger, folder);
    toolBuilding(consumer, TinkerTools.sword, folder);
    toolBuilding(consumer, TinkerTools.cleaver, folder);

    // specialized
    ShapelessRecipeBuilder.shapelessRecipe(TinkerTools.flintAndBronze)
                          .addIngredient(Items.FLINT)
                          .addIngredient(TinkerMaterials.tinkersBronze.getIngotTag())
                          .addCriterion("has_bronze", hasItem(TinkerMaterials.tinkersBronze.getIngotTag()))
                          .build(consumer, prefix(TinkerTools.flintAndBronze, folder));
    SpecializedRepairRecipeBuilder.repair(TinkerTools.flintAndBronze, MaterialIds.tinkersBronze)
                                  .buildRepairKit(consumer, wrap(TinkerTools.flintAndBronze, repairFolder, "_repair_kit"))
                                  .build(consumer, wrap(TinkerTools.flintAndBronze, repairFolder, "_station"));

    // travelers gear
    ShapedRecipeBuilder.shapedRecipe(TinkerTools.travelersGear.get(ArmorSlotType.HELMET))
                       .patternLine("l l")
                       .patternLine("glg")
                       .patternLine("c c")
                       .key('c', TinkerMaterials.copper.getIngotTag())
                       .key('l', Tags.Items.LEATHER)
                       .key('g', Tags.Items.GLASS_PANES_COLORLESS)
                       .addCriterion("has_item", hasItem(TinkerMaterials.copper.getIngotTag()))
                       .build(consumer, modResource(armorFolder + "travelers_goggles"));
    ShapedRecipeBuilder.shapedRecipe(TinkerTools.travelersGear.get(ArmorSlotType.CHESTPLATE))
                       .patternLine("l l")
                       .patternLine("lcl")
                       .patternLine("lcl")
                       .key('c', TinkerMaterials.copper.getIngotTag())
                       .key('l', Tags.Items.LEATHER)
                       .addCriterion("has_item", hasItem(TinkerMaterials.copper.getIngotTag()))
                       .build(consumer, modResource(armorFolder + "travelers_chestplate"));
    ShapedRecipeBuilder.shapedRecipe(TinkerTools.travelersGear.get(ArmorSlotType.LEGGINGS))
                       .patternLine("lll")
                       .patternLine("c c")
                       .patternLine("l l")
                       .key('c', TinkerMaterials.copper.getIngotTag())
                       .key('l', Tags.Items.LEATHER)
                       .addCriterion("has_item", hasItem(TinkerMaterials.copper.getIngotTag()))
                       .build(consumer, modResource(armorFolder + "travelers_pants"));
    ShapedRecipeBuilder.shapedRecipe(TinkerTools.travelersGear.get(ArmorSlotType.BOOTS))
                       .patternLine("c c")
                       .patternLine("l l")
                       .key('c', TinkerMaterials.copper.getIngotTag())
                       .key('l', Tags.Items.LEATHER)
                       .addCriterion("has_item", hasItem(TinkerMaterials.copper.getIngotTag()))
                       .build(consumer, modResource(armorFolder + "travelers_boots"));
    SpecializedRepairRecipeBuilder.repair(Ingredient.fromStacks(TinkerTools.travelersGear.values().stream().map(ItemStack::new)), MaterialIds.copper)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "travelers_repair_kit"))
                                  .build(consumer, modResource(armorRepairFolder + "travelers_station"));
  }

  private void addPartRecipes(Consumer<IFinishedRecipe> consumer) {
    String partFolder = "tools/parts/";
    String castFolder = "smeltery/casts/";
    partRecipes(consumer, TinkerToolParts.repairKit, TinkerSmeltery.repairKitCast, 2, partFolder, castFolder);
    // head
    partRecipes(consumer, TinkerToolParts.pickaxeHead,  TinkerSmeltery.pickaxeHeadCast,  2, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.hammerHead,   TinkerSmeltery.hammerHeadCast,   8, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.smallAxeHead, TinkerSmeltery.smallAxeHeadCast, 2, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.broadAxeHead, TinkerSmeltery.broadAxeHeadCast, 8, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.smallBlade,   TinkerSmeltery.smallBladeCast,   2, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.broadBlade,   TinkerSmeltery.broadBladeCast,   8, partFolder, castFolder);
    // other parts
    partRecipes(consumer, TinkerToolParts.toolBinding, TinkerSmeltery.toolBindingCast, 1, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.largePlate,  TinkerSmeltery.largePlateCast,  4, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.toolHandle,  TinkerSmeltery.toolHandleCast,  1, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.toughHandle, TinkerSmeltery.toughHandleCast, 3, partFolder, castFolder);
  }
}
