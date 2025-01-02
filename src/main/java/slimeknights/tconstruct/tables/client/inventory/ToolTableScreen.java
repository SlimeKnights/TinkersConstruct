package slimeknights.tconstruct.tables.client.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag.Default;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.ITinkerStationDisplay;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.TinkerTooltipFlags;
import slimeknights.tconstruct.tables.client.inventory.module.InfoPanelScreen;
import slimeknights.tconstruct.tables.menu.TabbedContainerMenu;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Shared logic for the tinker station and the modifier worktable */
public abstract class ToolTableScreen<T extends BlockEntity, C extends TabbedContainerMenu<T>> extends BaseTabbedScreen<T,C> {
  private static final Component MODIFIERS_TEXT = TConstruct.makeTranslation("gui", "tinker_station.modifiers");
  private static final Component UPGRADES_TEXT = TConstruct.makeTranslation("gui", "tinker_station.upgrades");
  private static final Component TRAITS_TEXT = TConstruct.makeTranslation("gui", "tinker_station.traits");

  /** Side panels, for tools and modifiers */
  protected final InfoPanelScreen<ToolTableScreen<T,C>,C> tinkerInfo;
  protected final InfoPanelScreen<ToolTableScreen<T,C>,C> modifierInfo;

  protected final Player player;
  @Nullable
  protected ArmorStand armorStandPreview;
  protected boolean enableArmorStandPreview = true;

  public ToolTableScreen(C c, Inventory playerInventory, Component title) {
    super(c, playerInventory, title);
    this.player = playerInventory.player;

    this.tinkerInfo = new InfoPanelScreen<>(this, c, playerInventory, title);
    this.tinkerInfo.setTextScale(8/9f);
    this.addModule(this.tinkerInfo);

    this.modifierInfo = new InfoPanelScreen<>(this, c, playerInventory, title);
    this.modifierInfo.setTextScale(7/9f);
    this.addModule(this.modifierInfo);
  }

  @Override
  protected void init() {
    super.init();
    if (enableArmorStandPreview) {
      assert this.minecraft != null;
      assert this.minecraft.level != null;
      this.armorStandPreview = new ArmorStand(this.minecraft.level, 0.0D, 0.0D, 0.0D);
      this.armorStandPreview.setNoBasePlate(true);
      this.armorStandPreview.setShowArms(true);
      this.armorStandPreview.yBodyRot = 210.0F;
      this.armorStandPreview.setXRot(25.0F);
      this.armorStandPreview.yHeadRot = this.armorStandPreview.getYRot();
      this.armorStandPreview.yHeadRotO = this.armorStandPreview.getYRot();
    }
  }

  /**
   * Renders the armor stand
   * @param graphics  Graphics instance
   * @param x         Stand X position
   * @param y         Stand Y position
   * @param scale     Stand size
   */
  protected void renderArmorStand(GuiGraphics graphics, int x, int y, int scale) {
    if (this.armorStandPreview != null) {
      InventoryScreen.renderEntityInInventory(graphics, this.cornerX + x, this.cornerY + y, scale, SmithingScreen.ARMOR_STAND_ANGLE, null, this.armorStandPreview);
    }
  }

  /** Updates the item displayed on the armor stand */
  protected void updateArmorStandPreview(ItemStack stack) {
    if (this.armorStandPreview != null) {
      for(EquipmentSlot equipmentslot : EquipmentSlot.values()) {
        this.armorStandPreview.setItemSlot(equipmentslot, ItemStack.EMPTY);
      }

      if (!stack.isEmpty()) {
        ItemStack copy = stack.copy();
        Item item = stack.getItem();
        if (item instanceof ArmorItem armor) {
          this.armorStandPreview.setItemSlot(armor.getEquipmentSlot(), copy);
        } else {
          this.armorStandPreview.setItemSlot(EquipmentSlot.OFFHAND, copy);
        }
      }

    }
  }

  /** Updates the tool panel area */
  protected void updateToolPanel(LazyToolStack lazyToolStack) {
    ToolStack tool = lazyToolStack.getTool();
    if (tool.getItem() instanceof ITinkerStationDisplay display) {
      tinkerInfo.setCaption(display.getLocalizedName());
      tinkerInfo.setText(display.getStatInformation(tool, Minecraft.getInstance().player, new ArrayList<>(), SafeClientAccess.getTooltipKey(), TinkerTooltipFlags.TINKER_STATION));
    }
    else {
      ItemStack result = lazyToolStack.getStack();
      tinkerInfo.setCaption(result.getHoverName());
      List<Component> list = new ArrayList<>();
      result.getItem().appendHoverText(result, Minecraft.getInstance().level, list, Default.NORMAL);
      tinkerInfo.setText(list);
    }
  }

  /** Updates the modifier panel with relevant info */
  protected void updateModifierPanel(ToolStack tool) {
    RegistryAccess access = player.level().registryAccess();
    List<Component> modifierNames = new ArrayList<>();
    List<Component> modifierTooltip = new ArrayList<>();
    Component title;
    // control displays just traits, bit trickier to do
    if (hasControlDown()) {
      title = TRAITS_TEXT;
      Map<Modifier,Integer> upgrades = tool.getUpgrades().getModifiers().stream()
                                           .collect(Collectors.toMap(ModifierEntry::getModifier, ModifierEntry::getLevel));
      for (ModifierEntry entry : tool.getModifierList()) {
        Modifier mod = entry.getModifier();
        if (mod.shouldDisplay(true)) {
          int level = entry.getLevel() - upgrades.getOrDefault(mod, 0);
          if (level > 0) {
            ModifierEntry trait = new ModifierEntry(entry.getModifier(), level);
            modifierNames.add(mod.getDisplayName(tool, trait, access));
            modifierTooltip.add(mod.getDescription(tool, trait));
          }
        }
      }
    } else {
      // shift is just upgrades/abilities, otherwise all
      List<ModifierEntry> modifiers;
      if (hasShiftDown()) {
        modifiers = tool.getUpgrades().getModifiers();
        title = UPGRADES_TEXT;
      } else {
        modifiers = tool.getModifierList();
        title = MODIFIERS_TEXT;
      }
      for (ModifierEntry entry : modifiers) {
        Modifier mod = entry.getModifier();
        if (mod.shouldDisplay(true)) {
          modifierNames.add(mod.getDisplayName(tool, entry, access));
          modifierTooltip.add(mod.getDescription(tool, entry));
        }
      }
    }

    modifierInfo.setCaption(title);
    modifierInfo.setText(modifierNames, modifierTooltip);
  }
}
