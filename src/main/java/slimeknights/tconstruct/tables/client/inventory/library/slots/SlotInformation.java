package slimeknights.tconstruct.tables.client.inventory.library.slots;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import slimeknights.tconstruct.library.tools.ToolCore;

import java.util.Collections;
import java.util.List;

public class SlotInformation {

  public static final SlotInformation EMPTY = new SlotInformation(Collections.emptyList(), ItemStack.EMPTY);
  @Getter
  private final List<SlotPosition> points;
  @Getter
  private final ItemStack itemStack;
  public ItemStack toolForRendering;

  public SlotInformation(List<SlotPosition> points, ItemStack itemStack) {
    this.points = points;
    this.itemStack = itemStack;
  }

  public static SlotInformation fromJson(JsonObject json) {
    List<SlotPosition> slots = SlotPosition.listFromJson(json, "slots");
    ItemStack stack = ItemStack.EMPTY;

    if (json.has("item")) {
      stack = new ItemStack(JSONUtils.getItem(json, "item"));
    }

    return new SlotInformation(slots, stack);
  }

  public ItemStack getToolForRendering() {
    if (this.toolForRendering == null || this.toolForRendering.isEmpty()) {
      if (this.itemStack.getItem() instanceof ToolCore) {
        this.toolForRendering = ((ToolCore) this.itemStack.getItem()).buildToolForRendering();
      }
      else {
        this.toolForRendering = this.itemStack;
      }
    }

    return this.toolForRendering;
  }
}
