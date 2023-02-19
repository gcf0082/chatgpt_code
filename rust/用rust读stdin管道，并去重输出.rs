use std::io::{self, BufRead};

fn main() {
    let stdin = io::stdin();
    let mut lines = stdin.lock().lines().map(|line| line.unwrap());

    let mut seen = std::collections::HashSet::new();
    while let Some(line) = lines.next() {
        if seen.contains(&line) {
            continue;
        }
        seen.insert(line.clone());
        println!("{}", line);
    }
}
