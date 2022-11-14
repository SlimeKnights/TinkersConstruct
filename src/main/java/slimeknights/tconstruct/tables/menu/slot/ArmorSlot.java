package slimeknights.tconstruct.tables.menu.slot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/** Slot for accessing player armor */
public class ArmorSlot extends Slot {
  private static final ResourceLocation[] ARMOR_SLOT_BACKGROUNDS = new ResourceLocation[] {
    InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
    InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
    InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
    InventoryMenu.EMPTY_ARMOR_SLOT_HELMET
  };

  private final Player player;
  private final EquipmentSlot slotType;

  public ArmorSlot(Inventory inv, EquipmentSlot slotType, int xPosition, int yPosition) {
    super(inv, 36 + slotType.getIndex(), xPosition, yPosition);
    this.player = inv.player;
    this.slotType = slotType;
    setBackground(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_BACKGROUNDS[slotType.getIndex()]);
  }

  @Override
  public int getMaxStackSize() {
    return 1;
  }

  @Override
  public boolean mayPlace(ItemStack stack) {
    return stack.canEquip(slotType, player);
  }

  @Override
  public boolean mayPickup(Player player) {
    ItemStack stack = this.getItem();
    return stack.isEmpty() || player.isCreative() || !EnchantmentHelper.hasBindingCurse(stack);
  }
}
