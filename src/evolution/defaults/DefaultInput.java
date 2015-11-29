package evolution.defaults;

import evolution.interfaces.Input;

public class DefaultInput<T> implements Input<T> {
    private T value;

    public DefaultInput(T input){
        this.value = input;
    }

    public T value(){
        return value;
    }
}
