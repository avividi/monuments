package avividi.com.monuments.gui.lwjgl.text;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static avividi.com.monuments.gui.util.IOUtil.ioResourceToByteBuffer;
import static java.awt.Font.MONOSPACED;
import static java.awt.Font.PLAIN;
import static java.lang.Math.round;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.stb.STBTruetype.stbtt_ScaleForPixelHeight;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Font {

  private final ByteBuffer ttf;

  private final STBTTFontinfo info;

  private final int ascent;
  private final int descent;
  private final int lineGap;
  private STBTTBakedChar.Buffer cdata;

  private int textId;
  private ByteBuffer textureBitMap;

  private int BITMAP_W = round(512 * getContentScaleX());
  private int BITMAP_H = round(512 * getContentScaleY());

  private final int fontSize;

  public Font(int fontSize) {
    this.fontSize = fontSize;
    try {
      ttf = ioResourceToByteBuffer("FiraSans-Regular.ttf", 512 * 1024);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    info = STBTTFontinfo.create();
    if (!stbtt_InitFont(info, ttf)) {
      throw new IllegalStateException("Failed to initialize font information.");
    }

    try (MemoryStack stack = stackPush()) {
      IntBuffer pAscent  = stack.mallocInt(1);
      IntBuffer pDescent = stack.mallocInt(1);
      IntBuffer pLineGap = stack.mallocInt(1);

      stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

      ascent = pAscent.get(0);
      descent = pDescent.get(0);
      lineGap = pLineGap.get(0);
    }

    cdata = init(BITMAP_W, BITMAP_H);
  }

  public void renderText(String text, float posX, float posY) {

    bindTexture();

    glTranslatef(0, getFontHeight() * 0.5f - getLineOffset() * getFontHeight(), 0f);
    glTranslatef(posX, posY, 0f);

    float scale = stbtt_ScaleForPixelHeight(info, getFontHeight());

    try (MemoryStack stack = stackPush()) {
      IntBuffer pCodePoint = stack.mallocInt(1);

      FloatBuffer bufferX = stack.floats(0.0f);
      FloatBuffer bufferY = stack.floats(0.0f);

      STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

      int lineStart = 0;

      float factorX = 1.0f / getContentScaleX();
      float factorY = 1.0f / getContentScaleY();

      float lineY = 0.0f;

      glBegin(GL_QUADS);
      for (int i = 0, to = text.length(); i < to; ) {
        i += getCP(text, to, i, pCodePoint);

        int cp = pCodePoint.get(0);
        if (cp == '\n') {
          if (isLineBBEnabled()) {
            glEnd();
            renderLineBB(text, lineStart, i - 1, bufferY.get(0), scale);
            glBegin(GL_QUADS);
          }

          bufferY.put(0, lineY = bufferY.get(0) + (ascent - descent + lineGap) * scale);
          bufferX.put(0, 0.0f);

          lineStart = i;
          continue;
        } else if (cp < 32 || 128 <= cp) {
          continue;
        }

        float cpX = bufferX.get(0);
        stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, cp - 32, bufferX, bufferY, q, true);
        bufferX.put(0, scale(cpX, bufferX.get(0), factorX));
        if (isKerningEnabled() && i < to) {
          getCP(text, to, i, pCodePoint);
          bufferX.put(0, bufferX.get(0) + stbtt_GetCodepointKernAdvance(info, cp, pCodePoint.get(0)) * scale);
        }

        float
            x0 = scale(cpX, q.x0(), factorX),
            x1 = scale(cpX, q.x1(), factorX),
            y0 = scale(lineY, q.y0(), factorY),
            y1 = scale(lineY, q.y1(), factorY);

        glTexCoord2f(q.s0(), q.t0());
        glVertex2f(x0, y0);

        glTexCoord2f(q.s1(), q.t0());
        glVertex2f(x1, y0);

        glTexCoord2f(q.s1(), q.t1());
        glVertex2f(x1, y1);

        glTexCoord2f(q.s0(), q.t1());
        glVertex2f(x0, y1);
      }
      glEnd();
      if (isLineBBEnabled()) {
        renderLineBB(text, lineStart, text.length(), lineY, scale);
      }
    }
  }


  private static float scale(float center, float offset, float factor) {
    return (offset - center) * factor + center;
  }

  private void renderLineBB(String text, int from, int to, float y, float scale) {
//    glDisable(GL_TEXTURE_2D);
//    glPolygonMode(GL_FRONT, GL_LINE);
//    glColor3f(1.0f, 1.0f, 0.0f);

    float width = getStringWidth(info, text, from, to, getFontHeight());
    y -= descent * scale;

    glBegin(GL_QUADS);
    glVertex2f(0.0f, y);
    glVertex2f(width, y);
    glVertex2f(width, y - getFontHeight());
    glVertex2f(0.0f, y - getFontHeight());
    glEnd();

  }

  private float getStringWidth(STBTTFontinfo info, String text, int from, int to, int fontHeight) {
    int width = 0;

    try (MemoryStack stack = stackPush()) {
      IntBuffer pCodePoint       = stack.mallocInt(1);
      IntBuffer pAdvancedWidth   = stack.mallocInt(1);
      IntBuffer pLeftSideBearing = stack.mallocInt(1);

      int i = from;
      while (i < to) {
        i += getCP(text, to, i, pCodePoint);
        int cp = pCodePoint.get(0);

        stbtt_GetCodepointHMetrics(info, cp, pAdvancedWidth, pLeftSideBearing);
        width += pAdvancedWidth.get(0);

        if (isKerningEnabled() && i < to) {
          getCP(text, to, i, pCodePoint);
          width += stbtt_GetCodepointKernAdvance(info, cp, pCodePoint.get(0));
        }
      }
    }

    return width * stbtt_ScaleForPixelHeight(info, fontHeight);
  }

  private static int getCP(String text, int to, int i, IntBuffer cpOut) {
    char c1 = text.charAt(i);
    if (Character.isHighSurrogate(c1) && i + 1 < to) {
      char c2 = text.charAt(i + 1);
      if (Character.isLowSurrogate(c2)) {
        cpOut.put(0, Character.toCodePoint(c1, c2));
        return 2;
      }
    }
    cpOut.put(0, c1);
    return 1;
  }

  public boolean isKerningEnabled() {
    return true;
  }

  public int getFontHeight() {
    return fontSize;
  }

  public boolean isLineBBEnabled() {
    return false;
  }


  public float getContentScaleX() {
    return 1.0f;
  }

  public float getContentScaleY() {
    return 1.0f;
  }

  private STBTTBakedChar.Buffer init(int BITMAP_W, int BITMAP_H) {
    textId = glGenTextures();
    STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);

    textureBitMap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
    stbtt_BakeFontBitmap(ttf, getFontHeight() * getContentScaleY(), textureBitMap, BITMAP_W, BITMAP_H, 32, cdata);


    glEnable(GL_TEXTURE_2D);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    return cdata;
  }

  private void bindTexture() {
    glBindTexture(GL_TEXTURE_2D, textId);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, textureBitMap);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

//    glClearColor(43f / 255f, 43f / 255f, 43f / 255f, 0f); // BG color
    glColor3f(169f / 255f, 183f / 255f, 198f / 255f); // Text color

  }


  public int getLineOffset() {
    return 0;
  }
}
