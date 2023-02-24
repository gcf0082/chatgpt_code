@Override
protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
    // 判断用户是否已经登录
    Subject subject = getSubject(request, response);
    if (!subject.isAuthenticated()) {
        return false;
    }
    
    // 判断用户是否具有访问文件的权限
    String filePath = (String) mappedValue;
    boolean hasPermission = checkPermission(subject, filePath);
    return hasPermission;
}

private boolean checkPermission(Subject subject, String filePath) {
    // 这里假设只有 admin 用户具有访问文件的权限
    return subject.hasRole("admin");
}
