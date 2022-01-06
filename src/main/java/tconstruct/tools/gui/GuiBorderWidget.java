package tconstruct.tools.gui;

/*
 * Taken from Tinker's Construct 1.12 under the MIT License
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

public class GuiBorderWidget {
    // all elements based on generic gui
    public GuiElementDuex cornerTopLeft = new GuiElementDuex(0, 0, 7, 7, 64, 64);
    public GuiElementDuex cornerTopRight = new GuiElementDuex(64 - 7, 0, 7, 7, 64, 64);
    public GuiElementDuex cornerBottomLeft = new GuiElementDuex(0, 64 - 7, 7, 7, 64, 64);
    public GuiElementDuex cornerBottomRight = new GuiElementDuex(64 - 7, 64 - 7, 7, 7, 64, 64);

    public GuiElementScalable borderTop = new GuiElementScalable(7, 0, 64 - 7 - 7, 7, 64, 64);
    public GuiElementScalable borderBottom = new GuiElementScalable(7, 64 - 7, 64 - 7 - 7, 7, 64, 64);
    public GuiElementScalable borderLeft = new GuiElementScalable(0, 7, 7, 64 - 7 - 7, 64, 64);
    public GuiElementScalable borderRight = new GuiElementScalable(64 - 7, 7, 7, 64 - 7 - 7, 64, 64);

    public int xPos;
    public int yPos;
    public int height;
    public int width;
    public int w = borderLeft.w;
    public int h = borderTop.h;

    public void setPosition(int x, int y) {
        this.xPos = x;
        this.yPos = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    /** Sets the size so that the given point is the upper left corner of the inside */
    public void setPosInner(int x, int y) {
        setPosition(x - cornerTopLeft.w, y - cornerTopLeft.h);
    }

    /** Sets the size so that it surrounds the given area */
    public void sedSizeInner(int width, int height) {
        setSize(width + borderLeft.w + borderRight.w, height + borderTop.h + borderBottom.h);
    }

    public int getWidthWithBorder(int width) {
        return width + borderRight.w + borderLeft.w;
    }

    public int getHeightWithBorder(int height) {
        return height + borderTop.h + borderBottom.h;
    }

    public void draw() {
        int x = xPos;
        int y = yPos;
        int midW = width - borderLeft.w - borderRight.w;
        int midH = height - borderTop.h - borderBottom.h;

        // top row
        x += cornerTopLeft.draw(x, y);
        x += borderTop.drawScaledX(x, y, midW);
        cornerTopRight.draw(x, y);

        // center row
        x = xPos;
        y += borderTop.h;
        x += borderLeft.drawScaledY(x, y, midH);
        x += midW;
        borderRight.drawScaledY(x, y, midH);

        // bottom row
        x = xPos;
        y += midH;
        x += cornerBottomLeft.draw(x, y);
        x += borderBottom.drawScaledX(x, y, midW);
        cornerBottomRight.draw(x, y);
    }
}
