package avividi.com.monuments.generic;

import com.google.common.base.Preconditions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public class ReflectBuilder<T> implements Supplier<T> {

  private Class<T> clazz;
  private Class<?>[] constructorParamClasses;
  private Object[] constructorParams;

  public ReflectBuilder(Class<T> clazz) {
    this.clazz = clazz;
  }

  public ReflectBuilder(String className) {
    this.clazz = getClass(className);
  }

  public ReflectBuilder<T> withConstructorParamClasses (Class<?> ... constructorParamClasses) {
    this.constructorParamClasses = constructorParamClasses;
    return this;
  }

  public ReflectBuilder<T> withConstructorParams (Object ... constructorParams) {
    this.constructorParams = constructorParams;
    return this;
  }

  @SuppressWarnings("unchecked")
  private Class<T> getClass (String className) {
    try {
      return (Class<T>) Class.forName(className);
    }
    catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static Class<?> getClassByName (String className) {
    try {
      return Class.forName(className);
    }
    catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public T get() {
    Preconditions.checkNotNull(clazz);

    if (constructorParamClasses == null) {
      Preconditions.checkState(constructorParams == null);
      return getInstance(clazz);
    }
    else {
      Preconditions.checkNotNull(constructorParams);
      Constructor<T> constructor = getConstructor(clazz, constructorParamClasses);
      return getInstance(constructor, constructorParams);
    }

  }

  private static <T> T getInstance (Constructor<T> constructor, Object ... params) {
    try {
      return constructor.newInstance(params);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
      throw new IllegalStateException(e);
    }
  }

  private static <T> T getInstance (Class<T> clazz) {
    try {
      return clazz.newInstance();
    }
    catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  private static <T> Constructor<T> getConstructor (Class<T> clazz, Class<?> ... paramsClasses) {
    try {
      return clazz.getConstructor(paramsClasses);
    }
    catch (NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
  }
}
