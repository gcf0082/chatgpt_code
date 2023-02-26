public class Plugin extends AbstractPlugin {

    @Override
    public List<Setting<?>> getSettings() {
        // ...
    }

    @Override
    public Collection<Class<? extends SearchStrategy>> getSearchStrategies() {
        List<Class<? extends SearchStrategy>> strategies = new ArrayList<>();
        strategies.add(FileSearchStrategy.class);
        return strategies;
    }
}
