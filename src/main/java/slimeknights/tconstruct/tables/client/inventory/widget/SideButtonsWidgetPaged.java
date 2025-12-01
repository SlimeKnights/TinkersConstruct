package slimeknights.tconstruct.tables.client.inventory.widget;


import net.minecraft.network.chat.Component;

import net.minecraft.client.gui.components.Button;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import lombok.Getter;
import net.minecraft.client.gui.GuiGraphics;

/**
 * A side buttons widget with pagination
 * <p>
 * The widget will show up to {@link #MAX_ROWS} rows of buttons at a time.
 * The number of buttons shown will be the minimum of the number of buttons and {@link #MAX_ROWS} times the number of columns.
 * <p>
 * The widget will also show previous and next page buttons if there are more buttons than fit in the widget.
 */
public class SideButtonsWidgetPaged<T extends Button> extends SideButtonsWidget<T> {

    private static final int SPACING = 4;
    
    /**
     * Maximum number of rows of buttons to show at a time
     */
    private static final int MAX_ROWS = 8;

    @Getter
    private final int leftPos;
    private final int topPos;

    private final int buttonHeight;
    private final int buttonWidth;

    private final int columns;

    private int page;

    
    private Button previousPageButton;
    private Button nextPageButton;

    private int clickedButtonIndex = 0;

    public SideButtonsWidgetPaged(MultiModuleScreen<?> parent, int leftPos, int topPos, int columns, int rows,
            int buttonWidth, int buttonHeight) {
        super(parent, leftPos, topPos, columns, Math.min(MAX_ROWS, rows), buttonWidth, buttonHeight); // show up to 8 rows at a time
        this.leftPos = leftPos;
        this.topPos = topPos;
        this.columns = columns;
        this.buttonHeight = buttonHeight;
        this.buttonWidth = buttonWidth;
        this.previousPageButton = new Button.Builder(
                Component.translatableWithFallback("button.tconstruct.previous_page", "<"),
                new Button.OnPress() {
                    @Override
                    public void onPress(Button button) {
                        if (page > 0) {
                            page--;
                            setButtonPositions();
                        }
                    }
                }).pos(leftPos, topPos).size(buttonWidth, buttonHeight).build();
        this.nextPageButton = new Button.Builder(Component.translatableWithFallback("button.tconstruct.next_page", ">"),
                new Button.OnPress() {
                    @Override
                    public void onPress(Button button) {
                        if (!isMaxPage(page)) {
                            page++;
                            setButtonPositions();
                        }
                    }
                }).pos(leftPos + (buttonWidth + SPACING) * columns, topPos).size(buttonWidth, buttonHeight).build();

        this.page = 0;
    }

    /**
     * Checks if the given page is the last page
     * @param page  The page to check
     * @return  True if the page is the last page, false otherwise
     */
    private boolean isMaxPage(int page) {
        return buttons.size() <= (page + 1) * columns * MAX_ROWS;
    }

    @Override
    public boolean handleMouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0) {
            int button_num = this.buttons.size();
            if (button_num > columns * MAX_ROWS) { // Page button shows
                if (this.previousPageButton.mouseClicked(mouseX, mouseY, mouseButton)) {
                    this.clickedButtonIndex = 1;
                    return true;
                }
                if (this.nextPageButton.mouseClicked(mouseX, mouseY, mouseButton)) {
                    this.clickedButtonIndex = 2;
                    return true;
                }
            }
        }
        return super.handleMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean handleMouseReleased(double mouseX, double mouseY, int state) {
        if (this.clickedButtonIndex == 1) {
            this.previousPageButton.mouseReleased(mouseX, mouseY, state);
            this.clickedButtonIndex = 0;
            return true;
        }
        if (this.clickedButtonIndex == 2) {
            this.nextPageButton.mouseReleased(mouseX, mouseY, state);
            this.clickedButtonIndex = 0;
            return true;
        }
        return super.handleMouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void setButtonPositions() {
        int button_num = this.buttons.size();
        if (button_num <= columns * MAX_ROWS) { // Buttons are not too many to show in one page
            this.previousPageButton.visible = false;
            this.nextPageButton.visible = false;
        }
        int startIndex = page * columns * MAX_ROWS;
        int endIndex = Math.min(startIndex + (columns) * MAX_ROWS, button_num);
        this.previousPageButton.visible = page > 0;
        this.nextPageButton.visible = !isMaxPage(page);
        this.previousPageButton.setX(leftPos);
        this.nextPageButton.setX(leftPos + (buttonWidth + SPACING) * (columns - 1));
        this.previousPageButton.setY(topPos-buttonHeight);
        this.nextPageButton.setY(topPos-buttonHeight);
        for (int i = 0; i < startIndex; i++) {
            T button = this.buttons.get(i);
            button.setX(0);
            button.setY(0);
            button.visible = false;
        }
        for (int i = startIndex; i < endIndex; i++) {
            T button = this.buttons.get(i);
            int x = ((i - startIndex) % columns) * (buttonWidth + SPACING);
            int y = ((i - startIndex) / columns) * (buttonHeight + SPACING);
            button.setX(leftPos + x);
            button.setY(topPos + y);
            button.visible = true;
        }
        for (int i = endIndex; i < button_num; i++) {
            T button = this.buttons.get(i);
            button.setX(0);
            button.setY(0);
            button.visible = false;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int button_num = this.buttons.size();
        if (button_num > columns * MAX_ROWS) {
            this.previousPageButton.render(graphics, mouseX, mouseY, partialTicks);
            this.nextPageButton.render(graphics, mouseX, mouseY, partialTicks);
        }
        int startIndex = page * columns * MAX_ROWS;
        int endIndex = Math.min(startIndex + columns * MAX_ROWS, button_num);
        for (int i = startIndex; i < endIndex; i++) { //only render buttons in the current page
            T button = this.buttons.get(i);
            button.render(graphics, mouseX, mouseY, partialTicks);
        }
    }
}
