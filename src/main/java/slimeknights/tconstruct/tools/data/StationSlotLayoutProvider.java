package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractStationSlotLayoutProvider;
import slimeknights.tconstruct.library.tools.layout.Patterns;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.function.Consumer;

public class StationSlotLayoutProvider extends AbstractStationSlotLayoutProvider {
  public StationSlotLayoutProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void addLayouts() {
    // stations
    Ingredient modifiable = Ingredient.fromTag(TinkerTags.Items.MODIFIABLE);
    define(TinkerTables.tinkerStation)
      .translationKey(TConstruct.makeTranslationKey("gui", "tinker_station.repair_limited"))
      .icon(Patterns.REPAIR)
      .toolSlot(53, 41, modifiable)
      .addInputSlot(Patterns.QUARTZ, 11, 41)
      .addInputSlot(Patterns.DUST,   31, 30)
      .addInputSlot(Patterns.LAPIS,  31, 50)
      .build();
    Consumer<IItemProvider> addAnvil = item ->
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
      .addInputItem(TinkerToolParts.pickaxeHead, 53, 22)
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
      .addInputItem(TinkerToolParts.hammerHead,  44, 29)
      .addInputItem(TinkerToolParts.toughHandle, 21, 52)
      .addInputItem(TinkerToolParts.pickaxeHead, 50, 48)
      .addInputItem(TinkerToolParts.largePlate,  25, 20)
      .build();

    // shovels
    defineModifiable(TinkerTools.mattock)
      .sortIndex(SORT_HARVEST)
      .addInputItem(TinkerToolParts.smallAxeHead, 31, 22)
      .addInputItem(TinkerToolParts.toolHandle,   22, 53)
      .addInputItem(TinkerToolParts.pickaxeHead,  51, 34)
      .build();
    defineModifiable(TinkerTools.excavator)
      .sortIndex(SORT_HARVEST + SORT_LARGE)
      .addInputItem(TinkerToolParts.largePlate,  45, 26)
      .addInputItem(TinkerToolParts.toughHandle, 25, 46)
      .addInputItem(TinkerToolParts.largePlate,  25, 26)
      .addInputItem(TinkerToolParts.toughHandle,  7, 62)
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
      .addInputItem(TinkerToolParts.pickaxeHead,  50, 48)
      .addInputItem(TinkerToolParts.toolBinding,  44, 29)
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
      .addInputItem(TinkerToolParts.broadBlade,  35, 20)
      .addInputItem(TinkerToolParts.toughHandle, 12, 55)
      .addInputItem(TinkerToolParts.toolBinding, 50, 40)
      .addInputItem(TinkerToolParts.toughHandle, 30, 40)
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
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Tinker Station Slot Layouts";
  }
}
