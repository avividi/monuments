package avividi.com.controller.util;

import java.security.SecureRandom;
import java.util.Random;

public class RandomUtil {
  private static SecureRandom secureRandom = new SecureRandom();
  public static Random get() {
    return secureRandom;
  }
}
