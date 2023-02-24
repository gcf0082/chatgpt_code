@RestController
public class AuthController {
    
    @PostMapping("/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            UsernamePasswordToken token = new UsernamePasswordToken(
                request.getParameter("username"),
                request.getParameter("password")
            );
            try {
                subject.login(token);
            } catch (AuthenticationException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid username or password");
            }
        }
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SecurityUtils.getSubject().logout();
        response.sendRedirect(request.getContextPath() + "/login");
    }

}
