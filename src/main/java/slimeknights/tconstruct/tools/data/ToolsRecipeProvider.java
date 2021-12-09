package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.SpecializedRepairRecipeBuilder;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Collections;
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

    // plate armor
    ShapedRecipeBuilder.shapedRecipe(TinkerTools.plateArmor.get(ArmorSlotType.HELMET))
                       .patternLine("mmm")
                       .patternLine("ccc")
                       .key('m', TinkerMaterials.manyullyn.getIngotTag())
                       .key('c', Items.CHAIN)
                       .addCriterion("has_item", hasItem(TinkerMaterials.manyullyn.getIngotTag()))
                       .build(consumer, modResource(armorFolder + "plate_helmet"));
    ShapedRecipeBuilder.shapedRecipe(TinkerTools.plateArmor.get(ArmorSlotType.CHESTPLATE))
                       .patternLine("m m")
                       .patternLine("mmm")
                       .patternLine("cmc")
                       .key('m', TinkerMaterials.manyullyn.getIngotTag())
                       .key('c', Items.CHAIN)
                       .addCriterion("has_item", hasItem(TinkerMaterials.manyullyn.getIngotTag()))
                       .build(consumer, modResource(armorFolder + "plate_chestplate"));
    ShapedRecipeBuilder.shapedRecipe(TinkerTools.plateArmor.get(ArmorSlotType.LEGGINGS))
                       .patternLine("mmm")
                       .patternLine("m m")
                       .patternLine("c c")
                       .key('m', TinkerMaterials.manyullyn.getIngotTag())
                       .key('c', Items.CHAIN)
                       .addCriterion("has_item", hasItem(TinkerMaterials.manyullyn.getIngotTag()))
                       .build(consumer, modResource(armorFolder + "plate_leggings"));
    ShapedRecipeBuilder.shapedRecipe(TinkerTools.plateArmor.get(ArmorSlotType.BOOTS))
                       .patternLine("m m")
                       .patternLine("m m")
                       .key('m', TinkerMaterials.manyullyn.getIngotTag())
                       .addCriterion("has_item", hasItem(TinkerMaterials.manyullyn.getIngotTag()))
                       .build(consumer, modResource(armorFolder + "plate_boots"));
    SpecializedRepairRecipeBuilder.repair(Ingredient.fromStacks(TinkerTools.plateArmor.values().stream().map(ItemStack::new)), MaterialIds.manyullyn)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "plate_repair_kit"))
                                  .build(consumer, modResource(armorRepairFolder + "plate_station"));

    // slimeskull
    slimeskullCasting(consumer, MaterialIds.gunpowder,    Items.CREEPER_HEAD,          armorFolder);
    slimeskullCasting(consumer, MaterialIds.bone,         Items.SKELETON_SKULL,        armorFolder);
    slimeskullCasting(consumer, MaterialIds.necroticBone, Items.WITHER_SKELETON_SKULL, armorFolder);
    slimeskullCasting(consumer, MaterialIds.rottenFlesh,  Items.ZOMBIE_HEAD,           armorFolder);
    slimeskullCasting(consumer, MaterialIds.enderPearl,  TinkerWorld.heads.get(TinkerHeadType.ENDERMAN),    armorFolder);
    slimeskullCasting(consumer, MaterialIds.bloodbone,   TinkerWorld.heads.get(TinkerHeadType.STRAY),       armorFolder);
    slimeskullCasting(consumer, MaterialIds.spider,      TinkerWorld.heads.get(TinkerHeadType.SPIDER),      armorFolder);
    slimeskullCasting(consumer, MaterialIds.venom,       TinkerWorld.heads.get(TinkerHeadType.CAVE_SPIDER), armorFolder);
    slimeskullCasting(consumer, MaterialIds.potato,      TinkerWorld.heads.get(TinkerHeadType.HUSK),        armorFolder);
    slimeskullCasting(consumer, MaterialIds.fish,        TinkerWorld.heads.get(TinkerHeadType.DROWNED),     armorFolder);
    slimeskullCasting(consumer, MaterialIds.blazingBone, TinkerWorld.heads.get(TinkerHeadType.BLAZE),       armorFolder);

    // slimelytra
    ItemCastingRecipeBuilder.basinRecipe(TinkerTools.slimesuit.get(ArmorSlotType.CHESTPLATE))
                            .setCast(Items.ELYTRA, true)
                            .setFluidAndTime(TinkerFluids.enderSlime, FluidValues.SLIME_CONGEALED * 8)
                            .build(consumer, modResource(armorFolder + "slimelytra"));
    SpecializedRepairRecipeBuilder.repair(Ingredient.fromItems(TinkerTools.slimesuit.get(ArmorSlotType.CHESTPLATE)), MaterialIds.phantom)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "slimelytra_repair_kit"))
                                  .build(consumer, modResource(armorRepairFolder + "slimelytra_station"));

    // slimeshell
    ItemCastingRecipeBuilder.basinRecipe(TinkerTools.slimesuit.get(ArmorSlotType.LEGGINGS))
                            .setCast(Items.SHULKER_SHELL, true)
                            .setFluidAndTime(TinkerFluids.enderSlime, FluidValues.SLIME_CONGEALED * 7)
                            .build(consumer, modResource(armorFolder + "slimeshell"));
    SpecializedRepairRecipeBuilder.repair(Ingredient.fromItems(TinkerTools.slimesuit.get(ArmorSlotType.LEGGINGS)), MaterialIds.chorus)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "slimeshell_repair_kit"))
                                  .build(consumer, modResource(armorRepairFolder + "slimeshell_station"));

    // boots
    ItemCastingRecipeBuilder.basinRecipe(TinkerTools.slimesuit.get(ArmorSlotType.BOOTS))
                            .setCast(Items.RABBIT_FOOT, true)
                            .setFluidAndTime(TinkerFluids.enderSlime, FluidValues.SLIME_CONGEALED * 4)
                            .build(consumer, modResource(armorFolder + "slime_boots"));
    SpecializedRepairRecipeBuilder.repair(Ingredient.fromItems(TinkerTools.slimesuit.get(ArmorSlotType.BOOTS)), MaterialIds.rabbit)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "slime_boots_repair_kit"))
                                  .build(consumer, modResource(armorRepairFolder + "slime_boots_station"));
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

  /** Helper to create a casting recipe for a slimeskull variant */
  private void slimeskullCasting(Consumer<IFinishedRecipe> consumer, MaterialId material, IItemProvider skull, String folder) {
    MaterialIdNBT nbt = new MaterialIdNBT(Collections.singletonList(material));
    ItemCastingRecipeBuilder.basinRecipe(ItemOutput.fromStack(nbt.updateStack(new ItemStack(TinkerTools.slimesuit.get(ArmorSlotType.HELMET)))))
                            .setCast(skull, true)
                            .setFluidAndTime(TinkerFluids.enderSlime, FluidValues.SLIME_CONGEALED * 5)
                            .build(consumer, modResource(folder + "slime_skull/" + material.getPath()));
  }
}
