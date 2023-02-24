@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public GraphQLSchema schema() {
        return new GraphQLSchemaGenerator()
                .withOperationsFromSingleton(new BookResolver())
                .generate();
    }

    @Bean
    public GraphQL graphQL(GraphQLSchema schema) {
        return GraphQL.newGraphQL(schema).build();
    }

    @Bean
    public GraphqlController graphqlController(GraphQL graphQL) {
        return new GraphqlController(graphQL);
    }
}
