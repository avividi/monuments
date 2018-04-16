package avividi.com.gui.lwjgl;

import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static avividi.com.gui.util.IOUtil.ioResourceToByteBuffer;
import static java.lang.Math.round;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImageResize.*;
import static org.lwjgl.stb.STBImageResize.STBIR_COLORSPACE_SRGB;
import static org.lwjgl.stb.STBImageResize.STBIR_FILTER_MITCHELL;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

public class ImageQuad {

  private final ByteBuffer image;
  private final int textureId;

  private int w;
  private int h;
  private int comp;

  private ColorFilter colorFilter = new ColorFilter();

  public ImageQuad(String imagePath) {
    image = loadImage(imagePath);
    textureId = createTexture(image);
  }

  ImageQuad(ByteBuffer image) {
    this.image = image;
    textureId = createTexture(image);
  }

  public void draw (float x, float y) {
    glBindTexture(GL_TEXTURE_2D, textureId);
    glColor4d(colorFilter.red,colorFilter.green,colorFilter.blue,colorFilter.alpha);
    glBegin(GL_QUADS);
    {
      glTexCoord2f(0.0f, 0.0f);
      glVertex2f(x, y);

      glTexCoord2f(1.0f, 0.0f);
      glVertex2f(x + w, y);

      glTexCoord2f(1.0f, 1.0f);
      glVertex2f(x + w, y + h);

      glTexCoord2f(0.0f, 1.0f);
      glVertex2f(x, y + h);
    }
    glEnd();
  }

  public void setColorFilter(ColorFilter colorFilter) {
    this.colorFilter = colorFilter;
  }

  public void drawFlippedHorizontally (float x, float y) {
    glBindTexture(GL_TEXTURE_2D, textureId);
    glColor4d(colorFilter.red,colorFilter.green,colorFilter.blue,colorFilter.alpha);
    glBegin(GL_QUADS);
    {
      glTexCoord2f(1.0f, 0.0f);
      glVertex2f(x, y);

      glTexCoord2f(0.0f, 0.0f);
      glVertex2f(x + w, y);

      glTexCoord2f(0.0f, 1.0f);
      glVertex2f(x + w, y + h);

      glTexCoord2f(1.0f, 1.0f);
      glVertex2f(x, y + h);
    }
    glEnd();
  }


  public void drawOneEighty (float x, float y) {
    glBindTexture(GL_TEXTURE_2D, textureId);
    glColor4d(colorFilter.red,colorFilter.green,colorFilter.blue,colorFilter.alpha);
    glBegin(GL_QUADS);
    {
      glTexCoord2f(1.0f, 1.0f);
      glVertex2f(x, y);

      glTexCoord2f(0.0f, 1.0f);
      glVertex2f(x + w, y);

      glTexCoord2f(0.0f, 0.0f);
      glVertex2f(x + w, y + h);

      glTexCoord2f(1.0f, 0.0f);
      glVertex2f(x, y + h);
    }
    glEnd();
  }


  public void drawOneEightFlipped(float x, float y) {
    glBindTexture(GL_TEXTURE_2D, textureId);
    glColor4d(colorFilter.red,colorFilter.green,colorFilter.blue,colorFilter.alpha);
    glBegin(GL_QUADS);
    {
      glTexCoord2f(0.0f, 1.0f);
      glVertex2f(x, y);

      glTexCoord2f(1.0f, 1.0f);
      glVertex2f(x + w, y);

      glTexCoord2f(1.0f, 0.0f);
      glVertex2f(x + w, y + h);

      glTexCoord2f(0.0f, 0.0f);
      glVertex2f(x, y + h);
    }
    glEnd();
  }

  private ByteBuffer loadImage(String imagePath) {
    ByteBuffer image;
    ByteBuffer imageBuffer;
    try {
      imageBuffer = ioResourceToByteBuffer(imagePath, 8 * 1024);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    try (MemoryStack stack = stackPush()) {
      IntBuffer w = stack.mallocInt(1);
      IntBuffer h = stack.mallocInt(1);
      IntBuffer comp = stack.mallocInt(1);

      System.out.println("Image width: " + w.get(0));
      System.out.println("Image height: " + h.get(0));
      System.out.println("Image components: " + comp.get(0));
      System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

      // Decode the image
      image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
      if (image == null) {
        throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
      }

      this.w = w.get(0);
      this.h = h.get(0);
      this.comp = comp.get(0);
    }
    return image;
  }

  private int createTexture(ByteBuffer image) {
    int texID = glGenTextures();

    int format;
    if (comp == 3) {
      if ((w & 3) != 0) {
        glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w & 1));
      }
      format = GL_RGB;
    } else {
      premultiplyAlpha(image);

      glEnable(GL_BLEND);
      glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

      format = GL_RGBA;
    }

    glBindTexture(GL_TEXTURE_2D, texID);
    glTexImage2D(GL_TEXTURE_2D, 0, format, w, h, 0, format, GL_UNSIGNED_BYTE, image);


    ByteBuffer input_pixels = image;
    int input_w = w;
    int input_h = h;
    int mipmapLevel = 0;
    while (1 < input_w || 1 < input_h) {
      int output_w = Math.max(1, input_w >> 1);
      int output_h = Math.max(1, input_h >> 1);

      ByteBuffer output_pixels = memAlloc(output_w * output_h * comp);
      stbir_resize_uint8_generic(
          input_pixels, input_w, input_h, input_w * comp,
          output_pixels, output_w, output_h, output_w * comp,
          comp, comp == 4 ? 3 : STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
          STBIR_EDGE_CLAMP,
          STBIR_FILTER_MITCHELL,
          STBIR_COLORSPACE_SRGB
      );

      if (mipmapLevel == 0) {
        stbi_image_free(image);
      } else {
        memFree(input_pixels);
      }

      glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, output_w, output_h, 0, format, GL_UNSIGNED_BYTE, output_pixels);

      input_pixels = output_pixels;
      input_w = output_w;
      input_h = output_h;
    }
    if (mipmapLevel == 0) {
      stbi_image_free(image);
    } else {
      memFree(input_pixels);
    }

    return texID;
  }

  private void premultiplyAlpha(ByteBuffer image) {
    int stride = w * 4;
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        int i = y * stride + x * 4;

        float alpha = (image.get(i + 3) & 0xFF) / 255.0f;
        image.put(i + 0, (byte)round(((image.get(i + 0) & 0xFF) * alpha)));
        image.put(i + 1, (byte)round(((image.get(i + 1) & 0xFF) * alpha)));
        image.put(i + 2, (byte)round(((image.get(i + 2) & 0xFF) * alpha)));
      }
    }
  }

  public int getW() {
    return w;
  }

  public int getH() {
    return h;
  }

  public void deleteTexture () {
    glDeleteTextures(textureId);
  }

  public static class ColorFilter {
    double red = 1.0;
    double green = 1.0;
    double blue = 1.0;
    double alpha = 1.0;

    public ColorFilter() {
    }

    public ColorFilter(double red, double green, double blue, double alpha) {
      this.red = red;
      this.green = green;
      this.blue = blue;
      this.alpha = alpha;
    }
  }
}