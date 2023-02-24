const [page, setPage] = useState(1);
const [pageSize, setPageSize] = useState(10);

const handleTableChange = (pagination, filters, sorter) => {
  setPage(pagination.current);
  setPageSize(pagination.pageSize);
};

<Table
  dataSource={data}
  columns={columns}
  loading={loading}
  rowKey="id"
  pagination={{
    current: page,
    pageSize: pageSize,
    showSizeChanger: true,
    showQuickJumper: true,
    total: total,
  }}
  onChange={handleTableChange}
/>
