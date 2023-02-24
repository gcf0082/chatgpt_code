const fetchData = () => {
  setLoading(true);
  const params = new URLSearchParams({
    start_time: startTime,
    end_time: endTime,
    file_name: tags.join(','),
    file_size: fileSize,
    page: page,
    page_size: pageSize,
  });
  fetch(`/api/files?${params}`)
    .then(response => response.json())
    .then(data => {
      setTotal(data.total);
      setData(data.items);
      setLoading(false);
    })
    .catch(error => {
      console.error('Error fetching data: ', error);
      setLoading(false);
    });
};
