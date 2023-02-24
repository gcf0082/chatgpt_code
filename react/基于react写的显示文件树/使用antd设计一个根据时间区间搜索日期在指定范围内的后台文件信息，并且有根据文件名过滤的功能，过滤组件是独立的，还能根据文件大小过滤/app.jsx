import React, { useState } from 'react';
import { DatePicker, Tag, Button, Table, Space } from 'antd';

const { RangePicker } = DatePicker;

const columns = [
  {
    title: '文件名',
    dataIndex: 'name',
    key: 'name',
    render: text => <a>{text}</a>,
  },
  {
    title: '文件大小',
    dataIndex: 'size',
    key: 'size',
  },
  {
    title: '上传时间',
    dataIndex: 'time',
    key: 'time',
  },
  {
    title: '操作',
    key: 'action',
    render: () => (
      <Space size="middle">
        <a>查看</a>
        <a>下载</a>
      </Space>
    ),
  },
];

const App = () => {
  const [filteredData, setFilteredData] = useState([]);

  const handleSearch = async () => {
    // 获取时间区间和文件名过滤条件
    const [startTime, endTime] = dateRange;
    const fileNames = filterText.trim().split(/\s+/);

    // 发送Fetch请求获取文件信息
    const response = await fetch('/api/files', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ startTime, endTime, fileNames }),
    });

    if (response.ok) {
      const data = await response.json();
      setFilteredData(data);
    } else {
      console.error(response.statusText);
    }
  };

  const [dateRange, setDateRange] = useState([]);
  const handleDateChange = (dates, dateStrings) => {
    setDateRange(dates);
  };

  const [filterText, setFilterText] = useState('');
  const handleFilterChange = event => {
    setFilterText(event.target.value);
  };

  const [fileNames, setFileNames] = useState([]);
  const handleTagClose = removedTag => {
    const remainingTags = fileNames.filter(tag => tag !== removedTag);
    setFileNames(remainingTags);
  };
  const handleTagInputConfirm = inputValue => {
    if (inputValue && !fileNames.includes(inputValue)) {
      setFileNames([...fileNames, inputValue]);
    }
    setFilterText('');
  };

  return (
    <>
      <div style={{ marginBottom: 16 }}>
        <RangePicker onChange={handleDateChange} />
        <Tag
          closable
          onClose={handleTagClose}
          onInputKeyDown={event => {
            if (event.keyCode === 13) {
              handleTagInputConfirm(event.target.value);
            }
          }}
          style={{ margin: '0 16px' }}
        >
          {fileNames.map(tag => (
            <Tag key={tag} closable onClose={() => handleTagClose(tag)}>
              {tag}
            </Tag>
          ))}
          <Input
            type="text"
            value={filterText}
            onChange={handleFilterChange}
            onPressEnter={event => {
              event.preventDefault();
              handleTagInputConfirm(event.target.value);
            }}
            style={{ width: 78 }}
          />
        </Tag>
        <Button type="primary" onClick={handleSearch}>
          搜索
        </Button>
      </div>
      <Table columns={columns} dataSource={filtered
