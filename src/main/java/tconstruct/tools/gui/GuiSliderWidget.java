package tconstruct.tools.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

/*
 * Taken from Mantle 1.12-1.3.3.49 under the MIT License
 *
 * The MIT License (MIT) Copyright (c) 2013-2014 Slime Knights (mDiyo, fuj1n, Sunstrike, progwml6, pillbox, alexbegt)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

// a vertical slider!
@SideOnly(Side.CLIENT)
public class GuiSliderWidget {
    // gui info
    public final GuiElementDuex slider;
    public final GuiElementDuex sliderHighlighted;
    public final GuiElementDuex sliderDisabled;
    public final GuiElementDuex slideBarTop;
    public final GuiElementDuex slideBarBottom;
    public final GuiElementScalable slideBar;
    public int xPos;
    public int yPos;
    public int height;
    public int width;
    public int sliderOffset; // x-offset of the slider to the left edge of the slideBar
    // slider info
    protected int minValue;
    protected int maxValue;
    protected int increment;
    // positioning info
    protected int currentValue;
    protected boolean enabled;
    protected boolean hidden;

    protected boolean isScrolling;
    protected boolean isHighlighted;
    // where the slider was clicked on the slider itself (not on the bar, on the thing that slides)
    private int clickX;
    private int clickY;
    private boolean clickedBar; // if the bar has already been clicked and not released

    public GuiSliderWidget(GuiElementDuex slider, GuiElementDuex sliderHighlighted, GuiElementDuex sliderDisabled, GuiElementDuex slideBarTop, GuiElementDuex slideBarBottom, GuiElementScalable slideBar) {
        this.slider = slider;
        this.sliderHighlighted = sliderHighlighted;
        this.sliderDisabled = sliderDisabled;
        this.slideBar = slideBar;
        this.slideBarTop = slideBarTop;
        this.slideBarBottom = slideBarBottom;

        height = slideBar.h;
        width = slideBar.w;
        currentValue = minValue = 0;
        maxValue = slideBar.h;
        increment = 1;

        sliderOffset = Math.abs(slideBar.w - slider.w) / 2;

        isScrolling = false;
        isHighlighted = false;

        enabled = true;
        hidden = false;
    }

    /**
     * Where the part will be drawn. Upper left corner.
     */
    public void setPosition(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the height of the whole slider and slidebar
     */
    public void setSize(int height) {
        this.height = height;
    }

    /**
     * specifies the values that the slider represents
     */
    public void setSliderParameters(int min, int max, int stepsize) {
        this.minValue = min;
        this.maxValue = max;
        this.increment = stepsize;

        // just in case
        setSliderValue(currentValue);
    }

    public int setSliderValue(int val) {
        if (val > maxValue) {
            val = maxValue;
        } else if (val < minValue) {
            val = minValue;
        }

        currentValue = val;
        return currentValue;
    }

    public int getValue() {
        if (isHidden()) {
            return 0;
        }
        return Math.min(maxValue, Math.max(minValue, currentValue));
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void hide() {
        this.hidden = true;
    }

    public void show() {
        this.hidden = false;
    }

    public void draw() {
        if (hidden) {
            return;
        }

        // slidebar background
        slideBarTop.draw(xPos, yPos);
        slideBar.drawScaledY(xPos, yPos + slideBarTop.h, getUsableSlidebarHeight());
        slideBarBottom.draw(xPos, yPos + height - slideBarBottom.h);

        int x = xPos + sliderOffset;
        int y = yPos + getSliderTop();

        // the slider depending on state
        if (enabled) {
            if (isScrolling) {
                sliderDisabled.draw(x, y);
            } else if (isHighlighted) {
                sliderHighlighted.draw(x, y);
            } else {
                slider.draw(x, y);
            }
        } else {
            sliderDisabled.draw(x, y);
        }
    }

    private int getUsableSlidebarHeight() {
        return height - slideBarTop.h - slideBarBottom.h;
    }

    private int getSliderTop() {
        float d = maxValue - minValue;
        d = (float) (currentValue - minValue) / d;
        d *= getUsableSlidebarHeight() - slider.h;

        return (int) d + slideBarTop.h;
    }

    public void update(int mouseX, int mouseY, boolean useMouseWheel) {
        if (!enabled || hidden) {
            return;
        }

        boolean mouseDown = Mouse.isButtonDown(0); // left mouse button
        int wheel = Mouse.getDWheel();

        if (useMouseWheel) {
            if (wheel > 0) {
                decrement();
                return;
            } else if (wheel < 0) {
                increment();
                return;
            }
        }

        // relative position inside the widget
        int x = mouseX - xPos;
        int y = mouseY - yPos;

        // reset click data
        if (!mouseDown && clickedBar) {
            clickedBar = false;
        }

        // button not pressed and scrolling -> stop scrolling
        if (!mouseDown && isScrolling) {
            isScrolling = false;
        }
        // button pressed and scrolling -> update position of slider
        else if (isScrolling) {
            float d = maxValue - minValue;
            float val = (float) (y - clickY) / (float) (getUsableSlidebarHeight() - slider.h);
            val *= d;

            if (val < (float) increment / 2f) {
                // < 1/2 increment
                setSliderValue(minValue);
            } else if (val > maxValue - ((float) increment / 2f)) {
                // > max-1/2 increment
                setSliderValue(maxValue);
            } else {
                // in between
                setSliderValue((int) (minValue + (float) increment * Math.round(val)));
            }
        }
        // not scrolling yet but possibly inside the slider
        else if (x >= 0 && y >= getSliderTop() &&
            x - sliderOffset <= slider.w && y <= getSliderTop() + slider.h) {
            isHighlighted = true;
            if (mouseDown) {
                isScrolling = true;
                clickX = x - sliderOffset;
                clickY = y - getSliderTop();
            }
        }
        // not on the slider but clicked on the bar
        else if (mouseDown && !clickedBar &&
            x >= 0 && y >= 0 &&
            x <= slideBar.w && y <= height) {
            if (y < getSliderTop()) {
                decrement();
            } else {
                increment();
            }

            clickedBar = true;
        } else {
            isHighlighted = false;
        }
    }

    public int decrement() {
        setSliderValue(currentValue - increment);
        return currentValue;
    }

    public int increment() {
        setSliderValue(currentValue + increment);
        return currentValue;
    }
}