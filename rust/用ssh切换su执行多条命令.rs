use std::io::{self, Read};
use std::net::TcpStream;
use std::str;
use std::time::Duration;

use ssh2::Session;

const USER: &str = "user";
const PASSWORD: &str = "password";
const NEW_PASSWORD: &str = "newPassword";
const COMMANDS: &str = "commands";

fn main() -> Result<(), Box<dyn std::error::Error>> {
    // 从命令行参数中读取连接参数
    let host = std::env::args().nth(1).unwrap();
    let user = std::env::args().nth(2).unwrap();
    let password = std::env::args().nth(3).unwrap();
    let new_password = std::env::args().nth(4).unwrap();
    let commands = std::env::args().nth(5).unwrap();

    let tcp = TcpStream::connect(&host)?;
    let mut session = Session::new()?;
    session.set_tcp_stream(tcp);
    session.handshake()?;

    // 认证用户
    session.userauth_password(&user, &password)?;

    // 切换用户
    let mut channel = session.channel_session()?;
    channel.exec("su -")?;
    let mut out = String::new();
    let mut err = String::new();
    let mut buf = [0; 1024];
    let mut in_buf = [0; 1024];
    channel.read(&mut buf)?;
    channel.write_all((new_password.to_owned() + "\n").as_bytes())?;
    channel.read(&mut in_buf)?;
    channel.read(&mut buf)?;
    out.push_str(str::from_utf8(&buf).unwrap());
    err.push_str(str::from_utf8(&in_buf).unwrap());

    // 执行多个命令
    let cmds = commands.split(';');
    for cmd in cmds {
        let mut channel = session.channel_session()?;
        channel.exec(cmd)?;
        let mut out = String::new();
        let mut err = String::new();
        let mut buf = [0; 1024];
        let mut in_buf = [0; 1024];
        while channel.eof() == false {
            channel.read(&mut buf)?;
            out.push_str(str::from_utf8(&buf).unwrap());
            channel.stderr().read(&mut in_buf)?;
            err.push_str(str::from_utf8(&in_buf).unwrap());
        }
        println!("Command output:\n{}", out);
    }

    // 关闭连接
    session.disconnect(None)?;

    Ok(())
}
