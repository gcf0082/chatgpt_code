@Service
public class UserService {
    
    @Autowired
    private UserDao userDao;
    
    public User findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }
    
}
