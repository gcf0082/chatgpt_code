public class ShiroRealm extends AuthorizingRealm {
    
    @Autowired
    private UserService userService;
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 授权逻辑
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 身份验证逻辑
        String username = (String) token.getPrincipal();
        User user = userService.findUserByUsername(username);
        if (user == null) {
            throw new UnknownAccountException();
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
            user.getUsername(),
            user.getPassword(),
            getName()
        );
        return authenticationInfo;
    }
    
}
