package slimeknights.tconstruct.fixture;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;

public class ModifierFixture {
  public static final ModifierId EMPTY_ID = new ModifierId("test", "empty");
  public static final ModifierId TEST_1 = new ModifierId("test", "modifier_1");
  public static final ModifierId TEST_2 = new ModifierId("test", "modifier_2");


  protected static final IForgeRegistry<Modifier> MODIFIER_REGISTRY = new RegistryBuilder<Modifier>()
    .setType(Modifier.class).setName(Util.getResource("modifiers")).setDefaultKey(EMPTY_ID).create();

  public static final Modifier EMPTY = new Modifier(-1).setRegistryName(EMPTY_ID);
  public static final Modifier TEST_MODIFIER_1 = new Modifier(-1).setRegistryName(TEST_1);
  public static final Modifier TEST_MODIFIER_2 = new Modifier(-1).setRegistryName(TEST_2);

  private static boolean init = false;

  public static void init() {
    if (init) {
      return;
    }
    init = true;
    MODIFIER_REGISTRY.register(EMPTY);
    MODIFIER_REGISTRY.register(TEST_MODIFIER_1);
    MODIFIER_REGISTRY.register(TEST_MODIFIER_2);
  }
}
