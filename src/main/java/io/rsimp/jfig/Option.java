package io.rsimp.jfig;

import java.util.Objects;

//using separate class for java 1.7 compatibility, but similar to 1.8's Optional class
public class Option<T> {
    private final T value;
    private final boolean isPresent;
    private static final Option<?> EMPTY = new Option<>(null);

    public T get(){ return value; }
    public boolean isPresent(){ return isPresent;}

    private Option(T value){
        this.value = value;
        this.isPresent = value != null;
    }

    public static <T> Option<T> empty(){ return (Option<T>)EMPTY; }
    public static <T> Option<T> of(T value){ return new Option<>(Objects.requireNonNull(value));}
}
