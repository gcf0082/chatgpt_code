<?php
if (isset($_POST['filename'])) {
  $filename = $_POST['filename'];
  $files = array();
  $dir = '/root/temp/temp1';
  if ($handle = opendir($dir)) {
    while (false !== ($entry = readdir($handle))) {
      if ($entry != "." && $entry != "..") {
        if (strpos($entry, $filename) !== false) {
          $files[] = array('name' => $entry, 'path' => $dir . '/' . $entry);
        }
      }
    }
    closedir($handle);
  }
  header('Content-Type: application/json');
  echo json_encode($files);
}
?>
