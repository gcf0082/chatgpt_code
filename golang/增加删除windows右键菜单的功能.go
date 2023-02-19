package main

import (
	"fmt"
	"log"
	"os"
	"syscall"
	"unsafe"
)

var (
	moduser32      = syscall.NewLazyDLL("user32.dll")
	modkernel32    = syscall.NewLazyDLL("kernel32.dll")
	procMessageBox = moduser32.NewProc("MessageBoxW")
	procRegOpenKey = modkernel32.NewProc("RegOpenKeyExW")
	procRegDelete  = modkernel32.NewProc("RegDeleteValueW")
)

func main() {
	// 获取 HKEY_CLASSES_ROOT 键的句柄
	var hKey syscall.Handle
	if _, _, err := procRegOpenKey.Call(
		uintptr(syscall.HKEY_CLASSES_ROOT),
		uintptr(unsafe.Pointer(syscall.StringToUTF16Ptr(`*\shell\Open with Notepad`))),
		0,
		syscall.KEY_SET_VALUE); err != nil {
		log.Fatal(err)
	} else {
		hKey = syscall.Handle(int32(ret))
	}
	defer syscall.RegCloseKey(hKey)

	// 删除命令值
	if _, _, err := procRegDelete.Call(
		uintptr(hKey),
		uintptr(unsafe.Pointer(syscall.StringToUTF16Ptr("command")))); err != nil {
		log.Fatal(err)
	}

	// 显示消息框
	title := "成功"
	message := "已删除右键菜单项！"
	procMessageBox.Call(0,
		uintptr(unsafe.Pointer(syscall.StringToUTF16Ptr(message))),
		uintptr(unsafe.Pointer(syscall.StringToUTF16Ptr(title))),
		0)
}
