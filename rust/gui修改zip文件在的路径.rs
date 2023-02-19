use gtk::prelude::*;
use gtk::{FileChooserAction, FileChooserDialog, FileFilter, GtkWindowExt, MessageDialog, MessageType};
use std::fs::File;
use std::io::{BufReader, Cursor};
use zip::{result::ZipError, write::FileOptions, ZipArchive, ZipWriter};

fn main() -> Result<(), Box<dyn std::error::Error>> {
    // 初始化 GTK
    gtk::init()?;

    // 创建窗口和布局
    let window = gtk::Window::new(gtk::WindowType::Toplevel);
    window.set_title("ZIP File Renamer");
    window.set_default_size(400, 100);
    let vbox = gtk::Box::new(gtk::Orientation::Vertical, 0);
    window.add(&vbox);

    // 创建文件选择按钮
    let file_chooser_button = gtk::Button::with_label("Select ZIP File");
    vbox.pack_start(&file_chooser_button, false, false, 10);

    // 创建处理按钮
    let process_button = gtk::Button::with_label("Process ZIP File");
    vbox.pack_start(&process_button, false, false, 10);

    // 为文件选择按钮添加事件处理程序
    let window_clone = window.clone();
    file_chooser_button.connect_clicked(move |_| {
        let file_chooser = FileChooserDialog::new(
            Some("Select ZIP File"),
            Some(&window_clone),
            FileChooserAction::Open,
        );
        let zip_filter = FileFilter::new();
        zip_filter.set_name("ZIP files");
        zip_filter.add_pattern("*.zip");
        file_chooser.add_filter(&zip_filter);
        file_chooser.show_all();
        let response = file_chooser.run();
        if response == gtk::ResponseType::Ok {
            let filename = file_chooser.get_filename().unwrap().to_str().unwrap().to_owned();
            let result = process_zip_file(&filename);
            match result {
                Ok(_) => {
                    let dialog = MessageDialog::new(
                        Some(&window_clone),
                        gtk::DialogFlags::MODAL,
                        MessageType::Info,
                        gtk::ButtonsType::Ok,
                        "ZIP file processed successfully.",
                    );
                    dialog.run();
                    dialog.destroy();
                }
                Err(e) => {
                    let dialog = MessageDialog::new(
                        Some(&window_clone),
                        gtk::DialogFlags::MODAL,
                        MessageType::Error,
                        gtk::ButtonsType::Ok,
                        &format!("Error processing ZIP file: {}", e),
                    );
                    dialog.run();
                    dialog.destroy();
                }
            }
        }
        file_chooser.destroy();
    });

    // 为处理按钮添加事件处理程序
    process_button.connect_clicked(move |_| {
        let result = process_zip_file("/path/to/myZipFile.zip");
        match result {
            Ok(_) => {
                let dialog = MessageDialog::new(
                    Some(&window_clone),
                    gtk::DialogFlags::MODAL,
                    MessageType::Info,
                    gtk::ButtonsType::Ok,
                    "ZIP file processed successfully.",
                );
                dialog.run();
                dialog.destroy();
            }
            Err(e) => {
            let dialog = MessageDialog::new(
                Some(&window_clone),
                gtk::DialogFlags::MODAL,
                MessageType::Error,
                gtk::ButtonsType::Ok,
                &format!("Error processing ZIP file: {}", e),
            );
            dialog.run();
            dialog.destroy();
        }
    }
});

// 显示窗口
window.show_all();

// 开始 GTK 主循环
gtk::main();

Ok(())
}
fn process_zip_file(filename: &str) -> Result<(), ZipError> {
let file = File::open(filename)?;
let mut zip = ZipArchive::new(BufReader::new(file))?;
  let new_folder = "newFolder/";

let out = Cursor::new(vec![]);
let mut writer = ZipWriter::new(out);

for i in 0..zip.len() {
    let mut file = zip.by_index(i)?;
    let mut path = file.name().to_owned();
    if path.contains('/') {
        path = format!("{}{}", new_folder, path.split('/').last().unwrap());
    } else {
        path = format!("{}{}", new_folder, path);
    }
    writer.start_file(&path, FileOptions::default())?;
    std::io::copy(&mut file, &mut writer)?;
}

let mut out = writer.finish()?;
let mut file = File::create(filename)?;
std::io::copy(&mut out, &mut file)?;

Ok(())
}
