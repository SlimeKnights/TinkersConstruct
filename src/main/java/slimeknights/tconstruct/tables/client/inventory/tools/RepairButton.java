package slimeknights.tconstruct.tables.client.inventory.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.tables.client.inventory.ButtonItem;
import slimeknights.tconstruct.tables.client.inventory.library.ToolBuildScreenInfo;

public class RepairButton extends ButtonItem<ToolBuildScreenInfo> {

  public static final ToolBuildScreenInfo info;

  public RepairButton(int buttonId, int x, int y, IPressable onPress) {
    super(buttonId, x, y, new TranslationTextComponent("gui.tconstruct.repair").getFormattedText(), info, onPress);

  }

  @Override
  protected void drawIcon(Minecraft minecraft) {
    minecraft.getTextureManager().bindTexture(Icons.ICONS);
    Icons.ANVIL.draw(this.x, this.y);
  }

  static {
    int x = 7 + 80 / 2 - 8 - 6;
    int y = 18 + 64 / 2 - 8;

    info = new ToolBuildScreenInfo();

    info.addSlotPosition(x, y);

    info.addSlotPosition(x - 18, y + 20); // -20,+20
    info.addSlotPosition(x - 22, y - 5); // -22, -7
    info.addSlotPosition(x, y - 23); // +-0, -21
    info.addSlotPosition(x + 22, y - 5); // +22, -7
    info.addSlotPosition(x + 18, y + 20); // +20,+20
  }
}
