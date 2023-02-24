@Component
public class BookResolver implements GraphQLQueryResolver, GraphQLMutationResolver {

    private List<Book> books = new ArrayList<>();

    public BookResolver() {
        books.add(new Book("1", "Book A", "Author A"));
        books.add(new Book("2", "Book B", "Author B"));
        books.add(new Book("3", "Book C", "Author C"));
    }

    public List<Book> getBooks() {
        return books;
    }

    public Book getBook(String id) {
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Book createBook(String title, String author) {
        Book book = new Book(UUID.randomUUID().toString(), title, author);
        books.add(book);
        return book;
    }

    public Book updateBook(String id, String title, String author) {
        Book book = getBook(id);
        if (book != null) {
            if (title != null) {
                book.setTitle(title);
            }
            if (author != null) {
                book.setAuthor(author);
            }
        }
        return book;
    }

    public boolean deleteBook(String id) {
        return books.removeIf(book -> book.getId().equals(id));
    }
}
