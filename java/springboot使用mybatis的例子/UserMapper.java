public interface UserMapper {

    User findById(Long id);

    int insert(User user);

}
