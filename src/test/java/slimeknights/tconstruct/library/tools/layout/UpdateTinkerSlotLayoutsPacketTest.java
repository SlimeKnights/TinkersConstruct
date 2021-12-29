package slimeknights.tconstruct.library.tools.layout;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.test.BaseMcTest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateTinkerSlotLayoutsPacketTest extends BaseMcTest {
  @Test
  void packetReadWrite() {
    StationSlotLayout layout = StationSlotLayout
      .builder()
      .translationKey("slot_name")
      .icon(new Pattern("test:pattern"))
      .sortIndex(3)
      .toolSlot(1, 2)
      .addInputSlot(null, 3, 4)
      .addInputSlot(null, 5, 6)
      .build();
    layout.setName(new ResourceLocation("test:main_layout"));
    UpdateTinkerSlotLayoutsPacket packetToEncode = new UpdateTinkerSlotLayoutsPacket(Arrays.asList(StationSlotLayout.EMPTY, layout));
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    packetToEncode.encode(buffer);

    UpdateTinkerSlotLayoutsPacket decoded = new UpdateTinkerSlotLayoutsPacket(buffer);
    Collection<StationSlotLayout> layouts = decoded.getLayouts();
    assertThat(layouts).hasSize(2);

    // first should be empty
    Iterator<StationSlotLayout> iterator = layouts.iterator();
    StationSlotLayout next = iterator.next();
    assertThat(next.getName().toString()).isEqualTo("tconstruct:empty");
    assertThat(next.getTranslationKey()).isEqualTo("");
    assertThat(next.getIcon()).isEqualTo(LayoutIcon.EMPTY);
    assertThat(next.getSortIndex()).isEqualTo(255);
    assertThat(next.isMain()).isTrue();
    LayoutSlot slot = next.getToolSlot();
    assertThat(slot.isHidden()).isTrue();
    assertThat(next.getInputSlots()).isEmpty();

    // next is filled with the above data
    next = iterator.next();
    assertThat(next.getName().toString()).isEqualTo("test:main_layout");
    assertThat(next.getTranslationKey()).isEqualTo("slot_name");
    Pattern pattern = next.getIcon().getValue(Pattern.class);
    assertThat(pattern).isNotNull();
    assertThat(pattern.toString()).isEqualTo("test:pattern");
    assertThat(next.getSortIndex()).isEqualTo(3);
    assertThat(next.isMain()).isFalse();
    // slots
    slot = next.getToolSlot();
    assertThat(slot.getX()).isEqualTo(1);
    assertThat(slot.getY()).isEqualTo(2);
    assertThat(next.getInputSlots()).hasSize(2);
    slot = next.getInputSlots().get(0);
    assertThat(slot.getX()).isEqualTo(3);
    assertThat(slot.getY()).isEqualTo(4);
    slot = next.getInputSlots().get(1);
    assertThat(slot.getX()).isEqualTo(5);
    assertThat(slot.getY()).isEqualTo(6);
  }
}
