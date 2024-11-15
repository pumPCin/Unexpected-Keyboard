{ pkgs ? import <nixpkgs> {
  config.android_sdk.accept_license = true;
  config.allowUnfree = true;
} }:

let
  jdk = pkgs.openjdk21;
  build_tools_version = "35";

  android = pkgs.androidenv.composeAndroidPackages {
    buildToolsVersions = [ build_tools_version ];
    platformVersions = [ "35" ];
    abiVersions = [ "arm64-v8a" ];
  };

  ANDROID_SDK_ROOT = "${android.androidsdk}/libexec/android-sdk";

  gradle = pkgs.gradle.override { java = jdk; };
  # Without this option, aapt2 fails to run with a permissions error.
  gradle_wrapped = pkgs.runCommandLocal "gradle-wrapped" {
    nativeBuildInputs = with pkgs; [ makeBinaryWrapper ];
  } ''
    mkdir -p $out/bin
    ln -s ${gradle}/bin/gradle $out/bin/gradle
    wrapProgram $out/bin/gradle \
    --add-flags "-Dorg.gradle.project.android.aapt2FromMavenOverride=${ANDROID_SDK_ROOT}/build-tools/${build_tools_version}/aapt2"
  '';

in pkgs.mkShell {
  buildInputs =
    [ pkgs.findutils pkgs.fontforge jdk android.androidsdk gradle_wrapped ];
  JAVA_HOME = jdk.home;
  inherit ANDROID_SDK_ROOT;
}
