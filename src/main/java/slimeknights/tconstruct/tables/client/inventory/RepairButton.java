package slimeknights.tconstruct.tables.client.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.tables.client.ToolSlotInformationLoader;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotInformation;

public class RepairButton extends ButtonItem<SlotInformation> {

  public RepairButton(int buttonId, int x, int y, IPressable onPress) {
    super(buttonId, x, y, new TranslationTextComponent("gui.tconstruct.repair").getFormattedText(), ToolSlotInformationLoader.get(ToolSlotInformationLoader.REPAIR_NAME), onPress);
  }

  @Override
  protected void drawIcon(Minecraft minecraft) {
    minecraft.getTextureManager().bindTexture(Icons.ICONS);
    Icons.ANVIL.draw(this.x, this.y);
  }
}
