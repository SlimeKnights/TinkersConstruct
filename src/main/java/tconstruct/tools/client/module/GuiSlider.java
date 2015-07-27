package tconstruct.tools.client.module;

import net.minecraft.util.MathHelper;

import org.lwjgl.input.Mouse;

import tconstruct.common.client.gui.GuiElement;
import tconstruct.common.client.gui.GuiElementScalable;

// a vertical slider!
public class GuiSlider {

  // gui info
  public final GuiElement slider;
  public final GuiElement sliderHighlighted;
  public final GuiElement sliderDisabled;
  public final GuiElement slideBarTop;
  public final GuiElement slideBarBottom;
  public final GuiElementScalable slideBar;

  // slider info
  protected int minValue;
  protected int maxValue;
  protected int increment;

  // positioning info
  protected int currentValue;
  public int xPos;
  public int yPos;
  public int height;
  public int sliderOffset; // x-offset of the slider to the left edge of the slideBar
  protected boolean enabled;

  protected boolean isScrolling;
  protected boolean isHighlighted;
  // where the slider was clicked on the slider itself (not on the bar, on the thing that slides)
  private int clickX;
  private int clickY;
  private boolean clickedBar; // if the bar has already been clicked and not released

  public GuiSlider(GuiElement slider, GuiElement sliderHighlighted, GuiElement sliderDisabled, GuiElement slideBarTop, GuiElement slideBarBottom, GuiElementScalable slideBar) {
    this.slider = slider;
    this.sliderHighlighted = sliderHighlighted;
    this.sliderDisabled = sliderDisabled;
    this.slideBar = slideBar;
    this.slideBarTop = slideBarTop;
    this.slideBarBottom = slideBarBottom;

    height = slideBar.h;
    currentValue = minValue = 0;
    maxValue = slideBar.h;
    increment = 1;

    sliderOffset = MathHelper.abs_int(slideBar.w - slider.w) / 2;

    isScrolling = false;
    isHighlighted = false;
  }

  /** Sets the height of the whole slider and slidebar */
  public void setSize(int height) {
    this.height = height;
  }

  /** specifies the values that the slider represents */
  public void setSliderParameters(int min, int max, int stepsize) {
    this.minValue = min;
    this.maxValue = max;
    this.increment = stepsize;
  }

  public int getValue() {
    return currentValue;
  }

  /** Where the slider will be drawn. Upper left corner. */
  public void setPosition(int x, int y) {
    this.xPos = x;
    this.yPos = y;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void draw() {
    // slidebar background
    slideBarTop.draw(xPos, yPos);
    slideBar.drawScaledY(xPos, yPos + slideBarTop.h, getUsableSlidebarHeight());
    slideBarBottom.draw(xPos, yPos + height - slideBarBottom.h);

    int x = xPos + sliderOffset;
    int y = yPos + getSliderTop();

    // the slider depending on state
    if(enabled) {
      if(isScrolling) {
        sliderDisabled.draw(x, y);
      }
      else if(isHighlighted) {
        sliderHighlighted.draw(x, y);
      }
      else {
        slider.draw(x, y);
      }
    }
  }

  public void update(int mouseX, int mouseY) {
    boolean mouseDown = Mouse.isButtonDown(0); // left mouse button

    // relative position inside the widget
    int x = mouseX - xPos;
    int y = mouseY - yPos;

    // reset click data
    if(!mouseDown && clickedBar) {
      clickedBar = false;
    }

    // button not pressed and scrolling -> stop scrolling
    if(!mouseDown && isScrolling) {
      isScrolling = false;
    }
    // button pressed and scrolling -> update position of slider
    else if(isScrolling) {
      float d = maxValue - minValue;
      float val = (float) (y - clickY) / (float) (getUsableSlidebarHeight() - slider.h);
      val *= d;

      if(val < (float) increment / 2f) {
        // < 1/2 increment
        setSliderValue(minValue);
      }
      else if(val > maxValue - ((float) increment / 2f)) {
        // > max-1/2 increment
        setSliderValue(maxValue);
      }
      else {
        // in between
        setSliderValue((int) (minValue + (float) increment * val));
      }
    }
    // not scrolling yet but possibly inside the slider
    else if(x >= 0 && y >= getSliderTop() &&
            x - sliderOffset <= slider.w && y <= getSliderTop() + slider.h) {
      isHighlighted = true;
      if(mouseDown) {
        isScrolling = true;
        clickX = x - sliderOffset;
        clickY = y - getSliderTop();
      }
    }
    // not on the slider but clicked on the bar
    else if(mouseDown && !clickedBar &&
            x >= 0 && y >= 0 &&
            x <= slideBar.w && y <= height) {
      if(y < getSliderTop())
        decrement();
      else
        increment();

      clickedBar = true;
    }
    else {
      isHighlighted = false;
    }
  }

  public int increment() {
    setSliderValue(currentValue + increment);
    return currentValue;
  }

  public int decrement() {
    setSliderValue(currentValue - increment);
    return currentValue;
  }

  public int setSliderValue(int val) {
    if(val > maxValue) {
      val = maxValue;
    }
    else if(val < minValue) {
      val = minValue;
    }

    currentValue = val;
    return currentValue;
  }

  private int getSliderTop() {
    float d = maxValue - minValue;
    d = (float) (currentValue - minValue) / d;
    d *= getUsableSlidebarHeight() - slider.h;

    return (int) d + slideBarTop.h;
  }

  private int getUsableSlidebarHeight() {
    return height - slideBarTop.h - slideBarBottom.h;
  }
}
