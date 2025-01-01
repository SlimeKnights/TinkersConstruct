package slimeknights.tconstruct.tools.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractStationSlotLayoutProvider;
import slimeknights.tconstruct.library.tools.layout.Patterns;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.function.Consumer;

public class StationSlotLayoutProvider extends AbstractStationSlotLayoutProvider {
  public StationSlotLayoutProvider(PackOutput packOutput) {
    super(packOutput);
  }

  @Override
  protected void addLayouts() {
    // stations
    Ingredient modifiable = Ingredient.of(TinkerTags.Items.MODIFIABLE);
    define(TinkerTables.tinkerStation)
      .translationKey(TConstruct.makeTranslationKey("gui", "tinker_station.repair_limited"))
      .icon(Patterns.REPAIR)
      .toolSlot(53, 41, modifiable)
      .addInputSlot(Patterns.QUARTZ, 11, 41)
      .addInputSlot(Patterns.DUST,   31, 30)
      .addInputSlot(Patterns.LAPIS,  31, 50)
      .build();
    Consumer<ItemLike> addAnvil = item ->
      define(item)
        .translationKey(TConstruct.makeTranslationKey("gui", "tinker_station.repair"))
        .icon(Patterns.REPAIR)
        .toolSlot(33, 41, modifiable)
        .addInputSlot(Patterns.QUARTZ, 15, 62)
        .addInputSlot(Patterns.DUST, 11, 37)
        .addInputSlot(Patterns.LAPIS, 33, 19)
        .addInputSlot(Patterns.INGOT, 55, 37)
        .addInputSlot(Patterns.GEM, 51, 62)
        .build();
    addAnvil.accept(TinkerTables.tinkersAnvil);
    addAnvil.accept(TinkerTables.scorchedAnvil);

    // tools
    // pickaxes
    defineModifiable(TinkerTools.pickaxe)
      .sortIndex(SORT_HARVEST)
      .addInputItem(TinkerToolParts.pickHead,    53, 22)
      .addInputItem(TinkerToolParts.toolHandle,  15, 60)
      .addInputItem(TinkerToolParts.toolBinding, 33, 42)
      .build();
    defineModifiable(TinkerTools.sledgeHammer)
      .sortIndex(SORT_HARVEST + SORT_LARGE)
      .addInputItem(TinkerToolParts.hammerHead,  44, 29)
      .addInputItem(TinkerToolParts.toughHandle, 21, 52)
      .addInputItem(TinkerToolParts.largePlate,  50, 48)
      .addInputItem(TinkerToolParts.largePlate,  25, 20)
      .build();
    defineModifiable(TinkerTools.veinHammer)
      .sortIndex(SORT_HARVEST + SORT_LARGE)
      .addInputItem(TinkerToolParts.hammerHead,   44, 29)
      .addInputItem(TinkerToolParts.toughHandle,  21, 52)
      .addInputItem(TinkerToolParts.toughBinding, 41, 49)
      .addInputItem(TinkerToolParts.largePlate,   25, 20)
      .build();

    // shovels
    defineModifiable(TinkerTools.mattock)
      .sortIndex(SORT_HARVEST)
      .addInputItem(TinkerToolParts.smallAxeHead, 31, 22)
      .addInputItem(TinkerToolParts.toolHandle,   22, 53)
      .addInputItem(TinkerToolParts.adzeHead,     51, 34)
      .build();
    defineModifiable(TinkerTools.pickadze)
      .sortIndex(SORT_HARVEST)
      .addInputItem(TinkerToolParts.pickHead,   31, 22)
      .addInputItem(TinkerToolParts.toolHandle, 22, 53)
      .addInputItem(TinkerToolParts.adzeHead,   51, 34)
      .build();
    defineModifiable(TinkerTools.excavator)
      .sortIndex(SORT_HARVEST + SORT_LARGE)
      .addInputItem(TinkerToolParts.largePlate,   45, 26)
      .addInputItem(TinkerToolParts.toughHandle,  25, 46)
      .addInputItem(TinkerToolParts.toughBinding, 25, 26)
      .addInputItem(TinkerToolParts.toughHandle,   7, 62)
      .build();

    // axes
    defineModifiable(TinkerTools.handAxe)
      .sortIndex(SORT_HARVEST)
      .addInputItem(TinkerToolParts.smallAxeHead, 31, 22)
      .addInputItem(TinkerToolParts.toolHandle,   22, 53)
      .addInputItem(TinkerToolParts.toolBinding,  51, 34)
      .build();
    defineModifiable(TinkerTools.broadAxe)
      .sortIndex(SORT_HARVEST + SORT_LARGE)
      .addInputItem(TinkerToolParts.broadAxeHead, 25, 20)
      .addInputItem(TinkerToolParts.toughHandle,  21, 52)
      .addInputItem(TinkerToolParts.pickHead,     50, 48)
      .addInputItem(TinkerToolParts.toughBinding, 44, 29)
      .build();

    // scythes
    defineModifiable(TinkerTools.kama)
      .sortIndex(SORT_HARVEST)
      .addInputItem(TinkerToolParts.smallBlade,  31, 22)
      .addInputItem(TinkerToolParts.toolHandle,  22, 53)
      .addInputItem(TinkerToolParts.toolBinding, 51, 34)
      .build();
    defineModifiable(TinkerTools.scythe)
      .sortIndex(SORT_HARVEST + SORT_LARGE)
      .addInputItem(TinkerToolParts.broadBlade,   35, 20)
      .addInputItem(TinkerToolParts.toughHandle,  12, 55)
      .addInputItem(TinkerToolParts.toughBinding, 50, 40)
      .addInputItem(TinkerToolParts.toughHandle,  30, 40)
      .build();

    // swords
    defineModifiable(TinkerTools.dagger)
      .sortIndex(SORT_WEAPON)
      .addInputItem(TinkerToolParts.smallBlade, 39, 35)
      .addInputItem(TinkerToolParts.toolHandle, 21, 53)
      .build();
    defineModifiable(TinkerTools.sword)
      .sortIndex(SORT_WEAPON)
      .addInputItem(TinkerToolParts.smallBlade, 48, 26)
      .addInputItem(TinkerToolParts.toolHandle, 12, 62)
      .addInputItem(TinkerToolParts.toolHandle, 30, 44)
      .build();
    defineModifiable(TinkerTools.cleaver)
      .sortIndex(SORT_WEAPON + SORT_LARGE)
      .addInputItem(TinkerToolParts.broadBlade,  45, 26)
      .addInputItem(TinkerToolParts.toughHandle,  7, 62)
      .addInputItem(TinkerToolParts.toughHandle, 25, 46)
      .addInputItem(TinkerToolParts.largePlate,  45, 46)
      .build();
    defineModifiable(TinkerTools.crossbow)
      .sortIndex(SORT_RANGED)
      .addInputItem(TinkerToolParts.bowLimb,   10, 20)
      .addInputItem(TinkerToolParts.bowGrip,   46, 56)
      .addInputItem(TinkerToolParts.bowstring, 28, 38)
      .build();
    defineModifiable(TinkerTools.longbow)
      .sortIndex(SORT_RANGED + SORT_LARGE)
      .addInputItem(TinkerToolParts.bowLimb,   20, 55)
      .addInputItem(TinkerToolParts.bowLimb,   45, 30)
      .addInputItem(TinkerToolParts.bowGrip,   25, 35)
      .addInputItem(TinkerToolParts.bowstring, 45, 55)
      .build();
    // use a single button for all plate armor, its all the same recipe and a single description is suitable
    define(TConstruct.getResource("plate_armor"))
      .sortIndex(SORT_ARMOR)
      .translationKey(TConstruct.makeTranslationKey("gui", "plate_armor"))
      .icon(Patterns.PLATE_ARMOR)
      .addInputPattern(Patterns.PLATING,   33, 29, Ingredient.of(TinkerToolParts.plating.values().toArray(new Item[0])))
      .addInputItem(TinkerToolParts.maille, 33, 53)
      .build();
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Tinker Station Slot Layouts";
  }
}
