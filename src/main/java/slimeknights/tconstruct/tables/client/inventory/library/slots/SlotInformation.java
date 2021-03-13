package slimeknights.tconstruct.tables.client.inventory.library.slots;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.JSONUtils;
import slimeknights.tconstruct.library.tools.item.ToolCore;

import java.util.Collections;
import java.util.List;

/** Slot information to show in the tool station */
@RequiredArgsConstructor
public class SlotInformation {
  public static final SlotInformation EMPTY = new SlotInformation(Collections.emptyList(), SlotPosition.EMPTY, Items.AIR, -1);

  @Getter
  private final List<SlotPosition> points;
  @Getter
  private final SlotPosition toolSlot;
  @Getter
  private final Item item;
  @Getter
  private final int sortIndex;

  /** Cache of the tool rendering stack */
  private ItemStack toolForRendering;

  /**
   * Creates a new instance of SlotInformation from a json
   *
   * @param json the json object
   * @return a instance of SlotInformation that contains all the points, sort index and tool
   */
  public static SlotInformation fromJson(JsonObject json) {
    List<SlotPosition> slots = SlotPosition.listFromJson(json, "slots");
    Item item = Items.AIR;

    if (json.has("item")) {
      item = JSONUtils.getItem(json, "item");
    }

    SlotPosition slotPosition = new SlotPosition(-1, -1);

    if (json.has("tool")) {
      slotPosition = SlotPosition.fromJson(json.get("tool").getAsJsonObject());
    }

    int sortIndex = JSONUtils.getInt(json, "sortIndex");

    return new SlotInformation(slots, slotPosition, item, sortIndex);
  }

  /**
   * Gets the item to use for rendering in the client's screen
   *
   * @return the itemstack to use for rendering
   */
  public ItemStack getToolForRendering() {
    if (this.toolForRendering == null || this.toolForRendering.isEmpty()) {
      if (this.item instanceof ToolCore) {
        this.toolForRendering = ((ToolCore) this.item).buildToolForRendering();
      }
      else {
        this.toolForRendering = new ItemStack(this.item);
      }
    }

    return this.toolForRendering;
  }

  /** Checks if this slot information is the repair button */
  public boolean isRepair() {
    return item == Items.AIR;
  }
}
