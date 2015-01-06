
package exterminatorJeff.undergroundBiomes.api;

import Zeno410Utils.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Zeno410
 */
public final class UndergroundBiomesSettings extends Settings {

    public static Streamer<UndergroundBiomesSettings> streamer(final UndergroundBiomesSettings setting) {
        return new Streamer<UndergroundBiomesSettings>() {

            @Override
            public UndergroundBiomesSettings readFrom(DataInput input) throws IOException {
                setting.readFrom(input);
                return setting;
            }

            @Override
            public void writeTo(UndergroundBiomesSettings written, DataOutput output) throws IOException {
                written.writeTo(output);
            }

        };
    }
    
    private final Category blockCategory = category("block");
    private final Category itemCategory = category("item");

    public final Mutable<Boolean> addOreDictRecipes = this.general().booleanSetting(
            "oreDictifyStone", true, "Modify all recipes to include Underground Biomes blocks");
    
    public final Mutable<Boolean> vanillaStoneBiomes = this.general().booleanSetting(
                "vanillaStoneBiomes", false, "Will cause sharp biome transitions if changed while playing the same world");

    public final Mutable<Boolean> buttonsOn = this.general().booleanSetting(
            "UndergroundBiomesButtons", true, "Provide Buttons for non-brick Underground Biomes blocks");

    public final Mutable<Boolean> stairsOn = this.general().booleanSetting(
            "UndergroundBiomesStairs", true, "Provide Stairs for Underground Biomes blocks");

    public final Mutable<Boolean> wallsOn= this.general().booleanSetting(
            "UndergroundBiomesWalls", true, "Provide Walls for Underground Biomes blocks");

    public final Mutable<Boolean> harmoniousStrata= this.general().booleanSetting(
            "HarmoniousStrata", false, "Avoid jarring strata transitions");

    public final Mutable<Boolean> replaceCobblestone= this.general().booleanSetting(
            "replaceCobblestone", true, "Swap vanilla cobble and slabs with Underground Biomes where appropriate, plus some village changes");

    public final Mutable<Boolean> imposeUBStone = this.general().booleanSetting(
            "ImposeUBStone", "Impose UB stone on other mods specially programmed for (currently Highlands)", true);

    public final Mutable<Boolean> replaceVillageGravel= this.general().booleanSetting(
            "ReplaceVillageGravel", false, "Replace village gravel with brick");

    public final Mutable<Boolean> crashOnProblems= this.general().booleanSetting(
            "CrashOnProblems", false, "Crash rather than try to get by when encountering problems");

    public final Mutable<Boolean> clearVarsForRecursiveGeneration = this.general().booleanSetting(
            "clearVarsForRecursiveGeneration", false, "Clear the world var in BiomeGenBase for recursive generation");

    public final Mutable<Boolean> forceConfigIds= this.general().booleanSetting(
            "ForceConfigIds", false, "(for worlds created pre-1.7) Force IDs to config values");

    public final Mutable<Boolean> ubOres = this.general().booleanSetting(
            "UBifyOres", true, "Convert ores to have Underground Biomes stone backgrounds");

    public final Mutable<Integer> biomeSize = this.general().intSetting(
            "biomeSize", 3, "Warning: exponential");

    public final Mutable<String> excludeDimensions = this.general().stringSetting(
            "excludeDimensionIDs", "-1,1", "Comma-separated list of dimension IDs, used only if include list is *");

    public final Mutable<String> includeDimensions = this.general().stringSetting(
            "includeDimensionIDs", "*", "Comma-separated list of dimension IDs, put * to use exclude list");
    
    public final Mutable<Integer>  generateHeight = this.general().intSetting(
            "generateHeight", 256, "Highest block to generated UB stone for");

    public final Mutable<Integer> vanillaStoneCrafting = this.general().intSetting(
            "vanillaStoneCrafting", 4, "0 = none; 1 = one rock; 2 = with redstone; 3 = 2x2 stone, lose 3; 4 = 2x2 stone");

    public final Mutable<Double>  hardnessModifier = this.general().doubleSetting(
            "hardnessModifier", 1.5, "Increase to make stone longer to mine. Normal is 1.5");

    public final Mutable<Double> resistanceModifier = this.general().doubleSetting(
            "resistanceModifier", 6.0, "Increase to make stone more resistant to explosions. Normal is 6.0");
        // Item read from block category to be backwards-compatible

    public final Mutable<Boolean> ubActive = this.general().booleanSetting(
            "undergroundBiomesActive", "True if Underground Biomes is supposed to replace stones", true);

    public final Mutable<Boolean>  dimensionSpecificSeeds = this.general().booleanSetting(
            "DimensionSpecificSeeds", false,"Use different seeds in different dimensions");

    public final Mutable<Boolean>  inChunkGeneration = this.general().booleanSetting(
            "InChunkGeneration", true,"Change stones during chunk generation");

    public final Mutable<String> inChunkGenerationExclude = this.general().stringSetting(
            "inChunkDimensionExclusions", "-1,1", "Comma-separated list of dimension to only use old decoration-phase generation, used only if inclusion list is *");

    public final Mutable<String> inChunkGenerationInclude = this.general().stringSetting(
            "inChunkDimensionInclusions", "0", "Comma-separated list of dimension IDs to allow new chunk-phase decoration, put * to use exclusion list");

    public final Mutable<Integer> ligniteCoalID = this.blockCategory.intSetting("Lignite Item ID:", 5500);
    public final Mutable<Integer> fossilPieceID = this.itemCategory.intSetting("fossilPiece", 5501);

    public final Mutable<Integer> igneousStoneID = this.blockCategory.intSetting("Igneous Stone ID:", 209);
    public final Mutable<Integer> metamorphicStoneID = this.blockCategory.intSetting("Metamorphic Stone ID:", 210);
    public final Mutable<Integer> sedimentaryStoneID = this.blockCategory.intSetting("Sedimentary Stone ID:", 211);

    public final Mutable<Integer>  igneousCobblestoneID = this.blockCategory.intSetting("Igneous Cobblestone ID:", 200);
    public final Mutable<Integer> metamorphicCobblestoneID = this.blockCategory.intSetting("Metamorphic Cobblestone ID:", 201);

    public final Mutable<Integer>  igneousStoneBrickID = this.blockCategory.intSetting("Igneous Brick ID:", 202);
    public final Mutable<Integer> metamorphicStoneBrickID = this.blockCategory.intSetting("Metamorphic Brick ID:", 203);

    public final Mutable<Integer> igneousBrickSlabHalfID = this.blockCategory.intSetting("Igneous Stone Brick Slab ID (half):", 205);
    public final Mutable<Integer> igneousBrickSlabFullID = this.blockCategory.intSetting("Igneous Stone Brick Slab ID (full):", 206);

    public final Mutable<Integer> metamorphicBrickSlabHalfID = this.blockCategory.intSetting("Metamorphic Stone Brick Slab ID (half):", 207);
    public final Mutable<Integer> metamorphicBrickSlabFullID = this.blockCategory.intSetting("Metamorphic Stone Brick Slab ID (full):", 208);

    public final Mutable<Integer> igneousStoneSlabHalfID = this.blockCategory.intSetting("Igneous Stone Slab ID (half):", 215);
    public final Mutable<Integer> igneousStoneSlabFullID = this.blockCategory.intSetting("Igneous Stone Slab ID (full):", 216);

    public final Mutable<Integer> metamorphicStoneSlabHalfID = this.blockCategory.intSetting("Metamorphic Stone Slab ID (half):", 217);
            // ID 2012 - 2014 used by constructs
    public final Mutable<Integer> metamorphicStoneSlabFullID = this.blockCategory.intSetting("Metamorphic Stone Slab ID (full):", 218);

    public final Mutable<Integer> igneousCobblestoneSlabHalfID = this.blockCategory.intSetting("Igneous Stone Cobblestone Slab ID (half):", 219);
    public final Mutable<Integer> igneousCobblestoneSlabFullID = this.blockCategory.intSetting("Igneous Stone Cobblestone Slab ID (full):", 220);

    public final Mutable<Integer> metamorphicCobblestoneSlabHalfID = this.blockCategory.intSetting("Metamorphic Stone Cobblestone Slab ID (half):", 221);
    public final Mutable<Integer> metamorphicCobblestoneSlabFullID = this.blockCategory.intSetting("Metamorphic Stone Cobblestone Slab ID (full):", 222);

    public final Mutable<Integer> sedimentaryStoneSlabHalfID = this.blockCategory.intSetting("Sedimentary Stone Slab ID (half):", 223);
    public final Mutable<Integer> sedimentaryStoneSlabFullID = this.blockCategory.intSetting("Sedimentary Stone Slab ID (full):", 224);

    public final Mutable<Integer> stoneStairID = this.blockCategory.intSetting("Universal Biomes Stairs ID:", 212);
    public final Mutable<Integer> stoneWallID = this.blockCategory.intSetting("Universal Biomes Wall ID:", 213);
    public final Mutable<Integer> stoneButtonID = this.blockCategory.intSetting("Universal Biomes Button ID:", 214);
}
