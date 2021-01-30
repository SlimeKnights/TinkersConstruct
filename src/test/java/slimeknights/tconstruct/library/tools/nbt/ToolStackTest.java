package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

class ToolStackTest extends BaseMcTest {

  @Test
  void serializeNBT() {
    ToolStack stack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundNBT());
    stack.setDamage(1);
    stack.setBroken(true);
    stack.setMaterialsRaw(MaterialNBT.EMPTY);
    stack.setStats(StatsNBT.EMPTY);

    CompoundNBT nbt = stack.createStack().getTag();
    assertThat(nbt).isNotNull();
    assertThat(nbt.contains(ToolStack.TAG_BROKEN)).isTrue();
    assertThat(nbt.getTagId(ToolStack.TAG_BROKEN)).isEqualTo((byte) Constants.NBT.TAG_BYTE);
    assertThat(nbt.contains(ToolStack.TAG_DAMAGE)).isTrue();
    assertThat(nbt.getTagId(ToolStack.TAG_DAMAGE)).isEqualTo((byte) Constants.NBT.TAG_INT);
    assertThat(nbt.contains(ToolStack.TAG_MATERIALS)).isTrue();
    assertThat(nbt.getTagId(ToolStack.TAG_MATERIALS)).isEqualTo((byte) Constants.NBT.TAG_LIST);
    assertThat(nbt.contains(ToolStack.TAG_STATS)).isTrue();
    assertThat(nbt.getTagId(ToolStack.TAG_STATS)).isEqualTo((byte) Constants.NBT.TAG_COMPOUND);
  }

  @Test
  void serializeLocalNBT() {
    ToolStack stack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinitionFixture.getStandardToolDefinition(), new CompoundNBT());
    stack.setStats(new StatsNBT(100, 0, 0, 0, 0));
    stack.setDamage(1);
    stack.setBroken(true);

    CompoundNBT nbt = stack.createStack().getTag();
    assertThat(nbt).isNotNull();
    assertThat(nbt.getBoolean(ToolStack.TAG_BROKEN)).isTrue();
    assertThat(nbt.getInt(ToolStack.TAG_DAMAGE)).isEqualTo(1);
  }

  @Test
  void deserializeLocalNBT() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt(ToolStack.TAG_DAMAGE, 4);
    nbt.putBoolean(ToolStack.TAG_BROKEN, true);
    ToolStack stack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, nbt);

    assertThat(stack.getDamageRaw()).isEqualTo(4);
    assertThat(stack.isBroken()).isTrue();
  }

  @Test
  void constructor_preservesItem() {
    ToolStack stack = ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, new CompoundNBT());
    assertThat(stack.getItem()).isEqualTo(stack.getItem());
  }

  @Test
  void constructor_findsDefinition() {
    ToolDefinition definition = ToolDefinitionFixture.getStandardToolDefinition();
    ItemStack stack = new ItemStack(new ToolCore(new Item.Properties(), definition) {
      @Override
      public boolean isEffective(BlockState state) {
        return false;
      }
    });
    ToolStack tool = ToolStack.from(stack);
    assertThat(tool.getDefinition()).isEqualTo(definition);
  }

  @Test
  void deserializeNBT_materials() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.put(ToolStack.TAG_MATERIALS, new ListNBT());
    ToolStack tool = fromNBT(nbt);
    assertThat(tool.getMaterials()).isNotNull();
  }

  @Test
  void deserializeNBT_stats() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.put(ToolStack.TAG_STATS, new CompoundNBT());
    ToolStack tool = fromNBT(nbt);
    assertThat(tool.getMaterials()).isNotNull();
  }

  @Test
  void deserialize_empty() {
    CompoundNBT nbt = new CompoundNBT();
    ToolStack tool = fromNBT(nbt);
    assertThat(tool.getDefinition()).isNotNull();
    assertThat(tool.getMaterials()).isNotNull();
    assertThat(tool.getStats()).isNotNull();
  }

  /**
   * Parses a tool stack from NBT
   * @param nbt  NBT
   * @return  Tool stack
   */
  private static ToolStack fromNBT(CompoundNBT nbt) {
    return ToolStack.from(Items.DIAMOND_PICKAXE, ToolDefinition.EMPTY, nbt);
  }
}
