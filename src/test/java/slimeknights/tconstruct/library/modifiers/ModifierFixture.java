package slimeknights.tconstruct.library.modifiers;

public class ModifierFixture {
  public static final ModifierId TEST_1 = new ModifierId("test", "modifier_1");
  public static final ModifierId TEST_2 = new ModifierId("test", "modifier_2");

  public static final Modifier TEST_MODIFIER_1 = new Modifier();
  public static final Modifier TEST_MODIFIER_2 = new Modifier();

  private static boolean init = false;

  public static void init() {
    if (init) {
      return;
    }
    init = true;
    TEST_MODIFIER_1.setId(TEST_1);
    TEST_MODIFIER_2.setId(TEST_2);
    ModifierManager.INSTANCE.staticModifiers.put(TEST_1, TEST_MODIFIER_1);
    ModifierManager.INSTANCE.staticModifiers.put(TEST_2, TEST_MODIFIER_2);
    ModifierManager.INSTANCE.dynamicModifiersLoaded = true;
  }
}
