{pkgs ? import <nixpkgs> {}}:
pkgs.mkShell {
  buildInputs = [
    pkgs.openjdk21
    pkgs.maven
    pkgs.gradle
    pkgs.vscode
];
  # Using openjdk17, maven, and gradle
  # so you can build and run your Java project in this shell.
}
