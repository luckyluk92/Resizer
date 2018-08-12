package me.echodev.resizer.util;

public class Size {

    private int width;
    private int height;

    Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    Size(Size size) {
        width = size.getWidth();
        height = size.getHeight();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float sizeRatio(Size size) {
        return width / (float) size.getWidth();
    }

    public Size swap() {
        int tmp = width;
        width = height;
        height = tmp;
        return this;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Size) {
            Size other = (Size) obj;
            return getWidth() == other.getWidth() && getHeight() == other.getHeight();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = (width ^ (width >>> 32));
        result = 31 * result + (height ^ (height >>> 32));
        return result;
    }
}
