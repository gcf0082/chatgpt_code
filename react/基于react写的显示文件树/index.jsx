import React, { useState, useEffect } from 'react';
import ReactDOM from 'react-dom';
import { Tree, Card } from 'antd';
import axios from 'axios';

const { TreeNode } = Tree;

function App() {
  const [treeData, setTreeData] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);

  useEffect(() => {
    axios.get('http://localhost:3000/files').then(response => {
      setTreeData(response.data);
    });
  }, []);

  const onNodeSelect = (keys, event) => {
    const node = event.node;
    if (node.props.type === 'file') {
      axios.get('http://localhost:3000/file/' + node.props.path).then(response => {
        setSelectedFile(response.data);
      });
    } else {
      if (!node.props.children) {
        axios.get('http://localhost:3000/files?path=' + node.props.path).then(response => {
          const children = response.data.map(item => ({
            title: item.name,
            key: item.path,
            path: item.path,
            type: item.type,
            isLeaf: item.type === 'file'
          }));
          node.props.dataRef.children = children;
          setTreeData([...treeData]);
        });
      }
    }
  };

  const renderTreeNodes = (data) => {
    return data.map(item => {
      if (item.children) {
        return (
          <TreeNode title={item.name} key={item.path} path={item.path} type={item.type} dataRef={item}>
            {renderTreeNodes(item.children)}
          </TreeNode>
        );
      }
      return <TreeNode {...item} />;
    });
  };

  return (
    <div>
      <Card>
        <Tree onSelect={onNodeSelect}>
          {renderTreeNodes(treeData)}
        </Tree>
      </Card>
      <Card>
        {selectedFile && <pre>{selectedFile}</pre>}
      </Card>
    </div>
  );
}

ReactDOM.render(<App />, document.getElementById('root'));
