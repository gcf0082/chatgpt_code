import React, { useState, useEffect } from 'react';
import { DatePicker, Input, Button, Table, Tag } from 'antd';
import moment from 'moment';
import 'moment/locale/zh-cn';
import locale from 'antd/lib/date-picker/locale/zh_CN';
import './App.css';

const { RangePicker } = DatePicker;

const columns = [
  {
    title: '文件名',
    dataIndex: 'name',
    key: 'name',
    render: text => <a href="/">{text}</a>,
  },
  {
    title: '大小',
    dataIndex: 'size',
    key: 'size',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
  },
];

const App = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [dateRange, setDateRange] = useState([]);
  const [tags, setTags] = useState([]);
  const [filter, setFilter] = useState('');

  const handleDateChange = (dates, dateStrings) => {
    setDateRange(dates);
  };

  const handleTagClose = removedTag => {
    const newTags = tags.filter(tag => tag !== removedTag);
    setTags(newTags);
    if (newTags.length === 0) {
      handleSearch();
    }
  };

  const handleTagInputChange = e => {
    setFilter(e.target.value);
  };

  const handleTagInputConfirm = () => {
    const newTags = [...tags, filter.trim()];
    setTags(newTags);
    setFilter('');
    handleSearch();
  };

  const handleSearch = () => {
    setLoading(true);
    fetch('/api/files', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        dateRange,
        tags,
      }),
    })
      .then(response => response.json())
      .then(data => setData(data))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    handleSearch();
  }, []);

  return (
    <>
      <div className="search-bar">
        <RangePicker
          locale={locale}
          ranges={{
            Today: [moment(), moment()],
            'This Week': [moment().startOf('week'), moment().endOf('week')],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
          }}
          showTime
          format="YYYY-MM-DD HH:mm:ss"
          onChange={handleDateChange}
        />
        <div className="tag-filter">
          {tags.map(tag => (
            <Tag key={tag} closable onClose={() => handleTagClose(tag)}>
              {tag}
            </Tag>
          ))}
          <Input
            type="text"
            placeholder="输入文件名进行过滤"
            value={filter}
            onChange={handleTagInputChange}
            onPressEnter={handleTagInputConfirm}
            style={{ width: 200 }}
          />
          <Button type="primary" onClick={handleTagInputConfirm}>
            添加
          </Button>
        </div>
        <Button type="primary" onClick={handleSearch}>
          搜索
        </Button>
      </div>
      <Table
       
dataSource={data}
columns={columns}
loading={loading}
rowKey="id"
/>
</>
);
};

export default App;
