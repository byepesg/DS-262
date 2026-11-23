package unal.ds.textureeditor.model;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Objects;

public class ColorSpaceUtils {
    public ColorSpaceUtils(){

    }

    private static final int HUE = 0;
    private static final int SATURATION = 1;
    private static final int BRIGHTNESS = 2;

    public static void hueShift(
            BufferedImage image,
            Collection<Point> pixels,
            float degrees) {

        shiftComponent(image, pixels, degrees, HUE);
    }

    public static void saturationShift(
            BufferedImage image,
            Collection<Point> pixels,
            float amount) {

        shiftComponent(image, pixels, amount, SATURATION);
    }

    public static void brightnessShift(
            BufferedImage image,
            Collection<Point> pixels,
            float amount) {

        shiftComponent(image, pixels, amount, BRIGHTNESS);
    }

    private static void shiftComponent(
            BufferedImage image,
            Collection<Point> pixels,
            float amount,
            int component) {

        Objects.requireNonNull(image);
        Objects.requireNonNull(pixels);

        if (!Float.isFinite(amount)) {
            throw new IllegalArgumentException("Amount must be finite.");
        }

        for (Point p : pixels) {

            int x = p.x;
            int y = p.y;

            if (x < 0 || x >= image.getWidth() ||
                    y < 0 || y >= image.getHeight()) {
                throw new IndexOutOfBoundsException(
                        "Pixel outside image boundaries: " + p
                );
            }

            int argb = image.getRGB(x, y);

            int alpha = (argb >>> 24) & 0xFF;
            int red   = (argb >>> 16) & 0xFF;
            int green = (argb >>> 8) & 0xFF;
            int blue  = argb & 0xFF;

            // RGB -> HSB
            float[] hsb = Color.RGBtoHSB(
                    red, green, blue, null
            );

            if (component == HUE) {

                // Hue wraps around.
                hsb[HUE] = (hsb[HUE] + amount / 360f) % 1f;

                if (hsb[HUE] < 0) {
                    hsb[HUE] += 1f;
                }

            } else {

                // Saturation and brightness are clamped.
                hsb[component] = Math.max(0f, Math.min(1f, hsb[component] + amount));
            }

            // HSB -> RGB
            int newRgb = Color.HSBtoRGB(
                    hsb[HUE], hsb[SATURATION], hsb[BRIGHTNESS]
            );

            // Restore original transparency.
            int newArgb = (alpha << 24) | (newRgb & 0x00FFFFFF);

            image.setRGB(x, y, newArgb);
        }
    }
    //Como llamar la funcion

    /*
    BufferedImage img = state.block.image(face);

    // +30 grados de hue con hue C [-180,180].
    ColorSpaceUtils.shiftHue(img, selectedPixels, 30f);

    // +20% saturacion.
    ColorSpaceUtils.shiftSaturation(img, selectedPixels, 0.2f);

    // -10% brillo
    ColorSpaceUtils.shiftBrightness(img, selectedPixels, -0.1f);

    Pasarle un arreglo de pixeles seleccionados.
     */



    public void tintShift(){

    }
    public void contrastShift(){

    }
    public void generateShades(){

    }

}
