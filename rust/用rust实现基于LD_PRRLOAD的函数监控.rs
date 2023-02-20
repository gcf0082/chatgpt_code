//cargo install libc backtrace
//rustc -g -O -C prefer-dynamic -C relocation-model=pic -C link-arg=-shared -o libfopen_interceptor.so libfopen_interceptor.rs

use std::os::raw::{c_char, c_int};
use std::ffi::{CString, CStr};
use std::fs::OpenOptions;
use std::io::Write;

type OrigFopenFuncType = unsafe extern "C" fn(*const c_char, *const c_char) -> *mut std::ffi::c_void;
static mut ORIG_FOPEN: Option<OrigFopenFuncType> = None;

#[no_mangle]
pub unsafe extern "C" fn fopen(path: *const c_char, mode: *const c_char) -> *mut std::ffi::c_void {
    // 打印调用参数
    let path = CStr::from_ptr(path).to_str().unwrap();
    let mode = CStr::from_ptr(mode).to_str().unwrap();
    println!("fopen called with args: {}, {}", path, mode);

    // 打印调用栈
    let mut buf = [0 as *mut std::ffi::c_void; 10];
    let size = backtrace::backtrace(&mut buf);
    backtrace::resolve(&buf, |symbol| {
        if let Some(name) = symbol.name() {
            println!("{}", name);
        }
    });

    // 调用原始的fopen函数
    let orig_fopen = ORIG_FOPEN.unwrap();
    orig_fopen(path, mode)
}

#[no_mangle]
pub unsafe extern "C" fn __libc_start_main(
    main: unsafe extern "C" fn(i32, *const *const c_char) -> c_int,
    argc: c_int,
    argv: *const *const c_char,
    init: unsafe extern "C" fn() -> (),
    fini: unsafe extern "C" fn() -> (),
    rtld_fini: unsafe extern "C" fn() -> (),
    stack_end: *mut u8,
) -> c_int {
    // 加载libc中原始的fopen函数
    let orig_fopen = dlsym(RTLD_NEXT, CString::new("fopen").unwrap().as_ptr()) as *mut OrigFopenFuncType;
    unsafe { ORIG_FOPEN = Some(std::ptr::read(orig_fopen)) };

    // 调用libc_start_main函数
    let ret = libc::libc_start_main(
        main,
        argc,
        argv,
        init,
        fini,
        rtld_fini,
        stack_end,
    );

    ret
}
