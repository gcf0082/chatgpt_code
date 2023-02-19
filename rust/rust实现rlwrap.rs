use std::io::{BufRead, BufReader, Write};
use std::process::{Command, Stdio};

fn main() {
    let mut args = std::env::args().skip(1);

    if let Some(command) = args.next() {
        let mut cmd = Command::new(&command);
        cmd.args(args).stdin(Stdio::piped()).stdout(Stdio::inherit());

        let mut child = cmd.spawn().unwrap();
        let stdin = child.stdin.as_mut().unwrap();

        let stdin_reader = BufReader::new(std::io::stdin());
        for line in stdin_reader.lines() {
            let line = line.unwrap();

            if stdin.write_all(line.as_bytes()).is_err() {
                break;
            }
            if stdin.write_all(b"\n").is_err() {
                break;
            }

            if let Err(e) = stdin.flush() {
                eprintln!("Error: {}", e);
                break;
            }

            if let Err(e) = child.wait() {
                eprintln!("Error: {}", e);
                break;
            }
        }
    } else {
        eprintln!("Usage: rlwrap command [arguments]");
        std::process::exit(1);
    }
}
