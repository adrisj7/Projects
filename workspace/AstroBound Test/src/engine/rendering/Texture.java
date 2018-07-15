package engine.rendering;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class Texture {

    private int id;
    private int width;
    private int height;

    public Texture(String fname) {
        BufferedImage img;
        try {
            img = ImageIO.read(new File(fname));
        } catch(IOException e) {
            System.err.println("Image at directory \"" + fname + "\" failed to load");
            e.printStackTrace();
            return;
        }

        width = img.getWidth();
        height = img.getHeight();

        int[] pixels_raw = new int[width * height * 4]; // TODO: maybe just int[] without multiplying 4? Bit shifting?
        img.getRGB(0, 0, width, height, pixels_raw, 0, width);
        // Weird how we're allocating w*h*4 ints before, and now we're doing w*h*4 bytes...
        ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);

        for(int yy = 0; yy < height; yy++) {
            for(int xx = 0; xx < width; xx++) {
                int pixel = pixels_raw[yy*width + xx];
                pixels.put((byte) ((pixel >> 16) & 0x0FF)); // RED
                pixels.put((byte) ((pixel >> 8 ) & 0x0FF)); // GREEN
                pixels.put((byte) ((pixel      ) & 0x0FF)); // BLUE
                pixels.put((byte) ((pixel >> 24) & 0x0FF)); // ALPHA
//                System.out.println((pixel >> 24) & 0x0FF);
            }
        }

        // OpenGL wants flipped buffers.
        pixels.flip();

        id = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

        // You can change the import settings here I think. Nearest, or linear or bicubic ect...
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
    }

    /**
     * Cleans up the texture when we don't need it anymore.
     * @throws Throwable 
     */
    @Override
    protected void finalize() throws Throwable {
        GL11.glDeleteTextures(id);
        super.finalize();
    }

    /**
     * Binds the texture. <br>
     * <br>
     * 
     * Basically, puts the texture frontward and says "Hey OPENGL! Use this texture
     * now!"
     */
    public void bind(int sampler) {
        if (sampler >= 0 && sampler <= 31) { // OPENGL offers 32 texture samplers total
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + sampler); // Binds texture to a sampler
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        }
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    
}
