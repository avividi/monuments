package avividi.com.monuments.generic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Visitor<T> {
  default void visit(T host) {
    try {
      String name = "visit" + host.getClass().getSimpleName();
      Method method =  getClass().getMethod(name, host.getClass());
      method.invoke(this, host);
    }
    catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }
}
