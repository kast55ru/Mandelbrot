import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

public class Mandelbrot extends JPanel {
    private int w, h;
    private DataBuffer dataBuffer;

    private void setPixel(int x, int y, int color) {
        dataBuffer.setElem(x + w * y, color);
    }

    private static int hsv2rgb(float h, float s, float v) {
        if (h >= 360.f) {
            h = h - 360.f * (int)(h / 360.f);
        }

        float R = 0.f, G = 0.f, B = 0.f, H, S, V, f, p, q, t;
        int i;

        H = Math.max(0.f, Math.min(360.f, h));
        S = Math.max(0.f, Math.min(255.f, s)) / 255.f;
        V = Math.max(0.f, Math.min(255.f, v));

        if(H >= 360.f) {
            H = 0.f;
        }

        H = H / 60;
        i = (int)H;
        f = H - i;

        p = V * (1.f - S);
        q = V * (1.f - S * f);
        t = V * (1.f - (1.f - f) * S);

        switch (i) {
            case 0: R = V; G = t; B = p; break;
            case 1: R = q; G = V; B = p; break;
            case 2: R = p; G = V; B = t; break;
            case 3: R = p; G = q; B = V; break;
            case 4: R = t; G = p; B = V; break;
            case 5: R = V; G = p; B = q; break;
        }

        return Integer.parseInt(String.format("%02X%02X%02X", (int)R, (int)G, (int)B), 16);
    }

    private void render() {
        double zoom = Math.pow(2, 0);
        double width = 1 / (w * zoom);
        int iterations = 600;
        double posRe = -0.56267837374;
        double posIm = 0.65679461735;

        double complexRe, complexIm, approxRe, approxIm;
        double p, theta, pc;
        double newRe, newIm;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                approxRe = approxIm = 0;
                complexRe = posRe + (x - w*0.5) * width;
                complexIm = posIm + (y - h*0.5) * width;

                p = Math.sqrt(Math.pow(complexRe - 0.25, 2) + Math.pow(complexIm, 2));
                theta = Math.atan2(complexIm, complexRe - 0.25);
                pc = 0.5 - 0.5 * Math.cos(theta);

                if (p > pc) {
                    for (int i = 0; i < iterations; i++) {
                        newRe = Math.pow(approxRe, 2) - Math.pow(approxIm, 2) + complexRe;
                        newIm = 2 * approxRe * approxIm + complexIm;
                        approxRe = newRe;
                        approxIm = newIm;

                        if (Math.pow(approxRe, 2) + Math.pow(approxIm, 2) > 4) {
                            setPixel(x, y, hsv2rgb(188 + 360 * i / (float)iterations, 255, 255));
                            break;
                        }
                    }
                }
            }
        }
    }

    public void paint(Graphics g) {
        w = 1200;
        h = 800;
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        dataBuffer = bufferedImage.getRaster().getDataBuffer();

        render();

        g.drawImage(bufferedImage, 2, 2, this);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new Mandelbrot());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1220, 845);
        frame.setVisible(true);
    }
}
