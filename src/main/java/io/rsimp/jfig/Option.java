package io.rsimp.jfig;

//using separate class for java 1.7 compatibility, but similar to 1.8's Optional class
public class Option<T> {
    private final T value;
    private final boolean isPresent;

    public T get(){ return value; }
    public boolean isPresent(){ return isPresent;}

    private Option(T value){
        this.value = value;
        this.isPresent = value != null;
    }

    public static <T> Option<T> empty(){ return new Option<>(null); }
    public static <T> Option<T> of(T value) { return new Option<>(value);}
}
