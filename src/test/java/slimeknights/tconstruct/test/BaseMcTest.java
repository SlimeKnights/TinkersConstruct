package slimeknights.tconstruct.test;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.loading.LoadingModList;
import slimeknights.tconstruct.compat.neoforged.neoforge.common.TierSortingRegistry;
import net.neoforged.neoforge.common.conditions.FalseCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.TrueCondition;
import net.neoforged.neoforge.registries.BaseMappedRegistry;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class BaseMcTest {

  @SuppressWarnings({"ResultOfMethodCallIgnored", "unused"})
  @BeforeAll
  static void setUpRegistries() {
    SharedConstants.setVersion(TestWorldVersion.INSTANCE);
    if (LoadingModList.get() == null) {
      LoadingModList.of(List.of(), List.of(), List.of(), List.of(), Map.of());
    }
    Bootstrap.bootStrap();
    setupSyncedVanillaRegistries();
    setupConditionCodecs();
    ModLoadingContext.get().setActiveContainer(new TestModContainer(TestModInfo.INSTANCE));
    TierSortingRegistry.getSortedTiers();
  }

  /** Mirrors the NeoForge registry sync setup needed by stream codecs in isolated tests. */
  private static void setupSyncedVanillaRegistries() {
    try {
      Method setSync = BaseMappedRegistry.class.getDeclaredMethod("setSync", boolean.class);
      setSync.setAccessible(true);
      setSync.invoke(BuiltInRegistries.ITEM, true);
      setSync.invoke(BuiltInRegistries.DATA_COMPONENT_TYPE, true);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Failed to configure synced test registries", e);
    }
  }

  /** Registers NeoForge's built-in condition codecs for JSON tests that do not run full mod loading. */
  private static void setupConditionCodecs() {
    registerConditionCodec("true", TrueCondition.CODEC);
    registerConditionCodec("false", FalseCondition.CODEC);
  }

  private static void registerConditionCodec(String name, MapCodec<? extends ICondition> codec) {
    ResourceLocation id = ResourceLocation.fromNamespaceAndPath("neoforge", name);
    if (!NeoForgeRegistries.CONDITION_SERIALIZERS.containsKey(id)) {
      Registry.register(NeoForgeRegistries.CONDITION_SERIALIZERS, id, codec);
    }
  }

  /** No need to set it up multiple times */
  private static boolean setupTiers = false;

  /** Sets up the NeoForge tier sorting registry */
  public static void setupTierSorting() {
    if (setupTiers) {
      return;
    }
    setupTiers = true;
    TierSortingRegistry.getSortedTiers();
  }
}
