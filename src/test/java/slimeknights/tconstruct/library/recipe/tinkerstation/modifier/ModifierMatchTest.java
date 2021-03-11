package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.ModifierFixture;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.test.BaseMcTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class ModifierMatchTest extends BaseMcTest {
  @BeforeAll
  static void beforeAll() {
    ModifierFixture.init();
  }

  private static void testSingle(Function<ModifierMatch, ModifierMatch> encodeDecode) {
    ModifierMatch match = ModifierMatch.entry(ModifierFixture.TEST_MODIFIER_1, 2);

    ModifierMatch decoded = encodeDecode.apply(match);

    assertThat(decoded instanceof ModifierMatch.EntryMatch).isTrue();
    ModifierMatch.EntryMatch casted = (ModifierMatch.EntryMatch) decoded;
    assertThat(casted.entry.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(casted.entry.getLevel()).isEqualTo(2);
  }

  @Test
  void bufferEncodeDecode_single() {
    testSingle(match -> {
      PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
      match.write(buffer);
      return ModifierMatch.read(buffer);
    });
  }

  @Test
  void jsonReadWrite_single() {
    testSingle(match -> {
      JsonObject json = match.serialize();
      return ModifierMatch.deserialize(json);
    });
  }

  private static void testList(Function<ModifierMatch, ModifierMatch> encodeDecode) {
    ModifierMatch match = ModifierMatch.list(2, ModifierMatch.entry(ModifierFixture.TEST_MODIFIER_1), ModifierMatch.entry(ModifierFixture.TEST_MODIFIER_2));

    ModifierMatch decoded = encodeDecode.apply(match);

    assertThat(decoded instanceof ModifierMatch.ListMatch).isTrue();
    ModifierMatch.ListMatch list = (ModifierMatch.ListMatch) decoded;
    assertThat(list.options).hasSize(2);
    assertThat(list.required).isEqualTo(2);

    assertThat(list.options.get(0) instanceof ModifierMatch.EntryMatch).isTrue();
    ModifierMatch.EntryMatch entry = (ModifierMatch.EntryMatch) list.options.get(0);
    assertThat(entry.entry.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(entry.entry.getLevel()).isEqualTo(1);

    assertThat(list.options.get(1) instanceof ModifierMatch.EntryMatch).isTrue();
    entry = (ModifierMatch.EntryMatch) list.options.get(1);
    assertThat(entry.entry.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(entry.entry.getLevel()).isEqualTo(1);
  }

  @Test
  void bufferEncodeDecode_list() {
    testList(match -> {
      PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
      match.write(buffer);
      return ModifierMatch.read(buffer);
    });
  }

  @Test
  void jsonReadWrite_list() {
    testList(match -> {
      JsonObject json = match.serialize();
      return ModifierMatch.deserialize(json);
    });
  }

  @Test
  void single_matchSimple() {
    ModifierMatch match = ModifierMatch.entry(ModifierFixture.TEST_MODIFIER_1, 1);

    // matches right one
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_1, 1))).isTrue();
    // does not match wrong one
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_2, 1))).isFalse();
  }

  @Test
  void single_matchLevel() {
    ModifierMatch match = ModifierMatch.entry(ModifierFixture.TEST_MODIFIER_1, 2);

    // too low
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_1, 1))).isFalse();
    // exact
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_1, 2))).isTrue();
    // above
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_1, 3))).isTrue();
    // above, but wrong type
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_2, 3))).isFalse();
  }

  @Test
  void list_matchAlways() {
    ModifierMatch match = ModifierMatch.ALWAYS;

    // a few random tests
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_1, 1))).isTrue();
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_1, 2))).isTrue();
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_2, 3))).isTrue();
  }

  @Test
  void list_matchOne() {
    ModifierMatch match = ModifierMatch.list(1, ModifierMatch.entry(ModifierFixture.TEST_MODIFIER_1), ModifierMatch.entry(ModifierFixture.TEST_MODIFIER_2));

    // match if either
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_1, 1))).isTrue();
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_2, 1))).isTrue();
    // not in list
    assertThat(match.test(entryList(ModifierFixture.EMPTY, 1))).isFalse();
    // matches if both
    assertThat(match.test(Arrays.asList(new ModifierEntry(ModifierFixture.TEST_MODIFIER_1, 1), new ModifierEntry(ModifierFixture.TEST_MODIFIER_2, 1)))).isTrue();
    // matches with 3
    assertThat(match.test(Arrays.asList(new ModifierEntry(ModifierFixture.TEST_MODIFIER_1, 1), new ModifierEntry(ModifierFixture.TEST_MODIFIER_2, 1), new ModifierEntry(ModifierFixture.EMPTY, 1)))).isTrue();
  }

  @Test
  void list_matchAll() {
    ModifierMatch match = ModifierMatch.list(2, ModifierMatch.entry(ModifierFixture.TEST_MODIFIER_1), ModifierMatch.entry(ModifierFixture.TEST_MODIFIER_2));

    // neither alone is enough
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_1, 1))).isFalse();
    assertThat(match.test(entryList(ModifierFixture.TEST_MODIFIER_2, 1))).isFalse();
    // matches if both
    assertThat(match.test(Arrays.asList(new ModifierEntry(ModifierFixture.TEST_MODIFIER_1, 1), new ModifierEntry(ModifierFixture.TEST_MODIFIER_2, 1)))).isTrue();
    // matches with 3
    assertThat(match.test(Arrays.asList(new ModifierEntry(ModifierFixture.TEST_MODIFIER_1, 1), new ModifierEntry(ModifierFixture.TEST_MODIFIER_2, 1), new ModifierEntry(ModifierFixture.EMPTY, 1)))).isTrue();
  }

  /** Creates a list for matching from the given entry */
  private static List<ModifierEntry> entryList(Modifier modifier, int level) {
    return Collections.singletonList(new ModifierEntry(modifier, level));
  }
}
