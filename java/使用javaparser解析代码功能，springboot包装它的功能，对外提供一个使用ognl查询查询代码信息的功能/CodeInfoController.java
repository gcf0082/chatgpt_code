@RestController

public class CodeInfoController {

    @PostMapping("/codeinfo")

    public List<String> getCodeInfo(@RequestBody String code, @RequestParam String query) throws ParseException, OgnlException {

        CompilationUnit compilationUnit = JavaParser.parse(code);

        OgnlContext context = new OgnlContext();

        context.setRoot(compilationUnit);

        return (List<String>) Ognl.getValue(query, context, context.getRoot());

    }

}
