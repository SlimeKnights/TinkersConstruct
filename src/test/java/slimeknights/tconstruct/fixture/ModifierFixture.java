package slimeknights.tconstruct.fixture;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ModifierFixture {
  public static final ModifierId EMPTY_ID = new ModifierId("test", "empty");
  public static final ModifierId TEST_1 = new ModifierId("test", "modifier_1");
  public static final ModifierId TEST_2 = new ModifierId("test", "modifier_2");


  protected static final IForgeRegistry<Modifier> MODIFIER_REGISTRY = createRegistry(new RegistryBuilder<Modifier>().setType(Modifier.class).setName(TConstruct.getResource("modifiers")).setDefaultKey(EMPTY_ID));

  public static final Modifier EMPTY = new Modifier().setRegistryName(EMPTY_ID);
  public static final Modifier TEST_MODIFIER_1 = new Modifier().setRegistryName(TEST_1);
  public static final Modifier TEST_MODIFIER_2 = new Modifier().setRegistryName(TEST_2);

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

  /** Forge went and made all the registry creation methods package private, and we are planning to move to a different system for modifiers in the near future, so good enough temp solution */
  @SuppressWarnings("unchecked")
  private static IForgeRegistry<Modifier> createRegistry(RegistryBuilder<Modifier> registry) {
    try {
      Method method = RegistryManager.class.getDeclaredMethod("createRegistry", ResourceLocation.class, RegistryBuilder.class);
      method.setAccessible(true);
      return (IForgeRegistry<Modifier>)method.invoke(RegistryManager.ACTIVE, TinkerRegistries.MODIFIER_REGISTRY.location(), registry);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
