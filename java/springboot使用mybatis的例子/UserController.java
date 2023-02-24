@RestController

@RequestMapping("/users")

public class UserController {

    @Autowired

    private UserMapper userMapper;

    @GetMapping("/{id}")

    public User getUserById(@PathVariable Long id) {

        return userMapper.findById(id);

    }

    @PostMapping

    public Long createUser(@RequestBody User user) {

        userMapper.insert(user);

        return user.getId();

    }

}
