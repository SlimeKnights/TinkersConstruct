package slimeknights.tconstruct.tables.block;

public enum TableTypes {
  CraftingStation(50),
  TinkerStation(25),
  PartBuilder(20),
  PartChest(16),
  PatternChest(15),
  ModifierChest(14);

  private final int sortIndex;

  TableTypes(int sortIndexIn) {
    this.sortIndex = sortIndexIn;
  }

  public int getSort() {
    return this.sortIndex;
  }
}
