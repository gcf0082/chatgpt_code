@Repository
public class UserDao {
    
    private static final Map<String, User> users = new HashMap<>();
    
    static {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("admin");
        user1.setPassword("6f0483b2b8c750d15af24f41167b52348552a8098d408e08a9c9a04f1e8dd79c"); // sha256("admin123")
        users.put(user1.getUsername(), user1);
        
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user");
        user2.setPassword("78650bdefe9eeed9da90a0fa4d4a08a12a29de4e423ad7c4682cecf27b7db9eb"); // sha256("user123")
        users.put(user2.getUsername(), user2);
    }
    
    public User findUserByUsername(String username) {
        return users.get(username);
    }
    
}
