package pokeraidbot.jda;

@SuppressWarnings("UnusedReturnValue")
public abstract class MenuBuilder<T> {
    protected String placeholderMessage;
    protected Control[] controls;
    protected T parent;

    public MenuBuilder(T parent) {
        setParent(parent);
    }

    public abstract void build();

    protected MenuBuilder setControls(Control... controls) {
        this.controls = controls;
        return this;
    }

    public MenuBuilder setPlaceholderMessage(String placeholderMessage) {
        this.placeholderMessage = placeholderMessage;
        return this;
    }

    private MenuBuilder setParent(T parent) {
        this.parent = parent;
        return this;
    }
}