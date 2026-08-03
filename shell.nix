{ pkgs ? import <nixpkgs> {
  config.android_sdk.accept_license = true;
  config.allowUnfree = true;
} }:

let
  jdk = pkgs.openjdk21;
  build_tools_version = "37.0.0";

  android = pkgs.androidenv.composeAndroidPackages {
    buildToolsVersions = [ build_tools_version ];
    platformVersions = [ "36" ];
    abiVersions = [ "arm64-v8a" ];
  };

  emulators = let
    mk_emulator = { platformVersion, device ? "pixel_6", abiVersion ? "x86_64", systemImageType ? "default" }:
      pkgs.androidenv.emulateApp rec {
        name = "emulator_api${platformVersion}";
        inherit platformVersion abiVersion systemImageType;
        androidAvdFlags = "--device ${device}";
      };
    # Allow to install several emulators in the same environment
    link_emulator = version_name: args: {
      name = "bin/emulate_android_${version_name}";
      path = "${mk_emulator args}/bin/run-test-emulator";
    };
  in pkgs.linkFarm "emulator" [
    (link_emulator "5" { platformVersion = "21"; })
    # (link_emulator "14" { platformVersion = "34"; })
    # There's no 'default' image for Android 15
    (link_emulator "15" { platformVersion = "35"; systemImageType = "google_apis"; })
  ];

  ANDROID_SDK_ROOT = "${android.androidsdk}/libexec/android-sdk";

in pkgs.mkShell {
  buildInputs = [
    pkgs.findutils
    pkgs.fontforge
    jdk
    android.androidsdk
    (pkgs.gradle.override { java = jdk; })
    emulators
  ];
  JAVA_HOME = jdk.home;
  inherit ANDROID_SDK_ROOT;
  GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${ANDROID_SDK_ROOT}/build-tools/${build_tools_version}/aapt2";
}
