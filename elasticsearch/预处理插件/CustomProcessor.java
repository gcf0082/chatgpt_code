public class CustomProcessor extends AbstractProcessor {

    private static final String TYPE = "custom";

    public CustomProcessor() {
        super(TYPE);
    }

    @Override
    public DocProcFactory procFactory(Settings settings) {
        return new Factory(this, settings);
    }

    public static class Factory implements DocProcFactory {

        private final CustomProcessor processor;

        public Factory(CustomProcessor processor, Settings settings) {
            this.processor = processor;
        }

        @Override
        public DocProc create(Processor.Parameters parameters) throws Exception {
            return new CustomProcessorHandler(processor, parameters);
        }
    }

    private static class CustomProcessorHandler extends AbstractProcessor {

        private CustomProcessor processor;

        public CustomProcessorHandler(CustomProcessor processor, Processor.Parameters parameters) {
            super(parameters);
            this.processor = processor;
        }

        @Override
        public void execute(IngestDocument ingestDocument) throws Exception {
            String sourceField = ingestDocument.getFieldValue("data", String.class);
            JSONObject jsonObject = new JSONObject(sourceField);

            // 对数据进行处理和清洗

            // 转换数据格式
            String targetField = jsonObject.toString();

            ingestDocument.setFieldValue("data", targetField);
        }
    }
}
