@Override
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    UsernamePasswordToken upToken = (UsernamePasswordToken) token;
    String username = upToken.getUsername();
    String password = new String(upToken.getPassword());
    
    // 根据用户名从数据库或其他数据源中获取用户信息
    User user = userDao.findUserByUsername(username);
    if (user == null) {
        throw new UnknownAccountException("用户名或密码错误");
    }
    
    // 验证密码
    String hashedPassword = DigestUtils.sha256Hex(password);
    if (!hashedPassword.equals(user.getPassword())) {
        throw new IncorrectCredentialsException("用户名或密码错误");
    }
    
    // 返回验证结果
    return new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), getName());
}
