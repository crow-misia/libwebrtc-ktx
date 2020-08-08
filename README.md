# libwebrtc kotlin extensions + utilities

[![Android CI](https://github.com/crow-misia/libwebrtc-ktx/workflows/Android%20CI/badge.svg)](https://github.com/crow-misia/libwebrtc-ktx/actions)
[![License](https://img.shields.io/github/license/crow-misia/libwebrtc-ktx)](LICENSE)

Useful classes for developing apps using libwebrtc.

## Get Started

### Gradle

Add dependencies (you can also add other modules that you need):

`${latest.version}` is [![Download](https://api.bintray.com/packages/zncmn/maven/libwebrtc-ktx/images/download.svg)](https://bintray.com/zncmn/maven/libwebrtc-ktx/_latestVersion)

```groovy
dependencies {
    implementation "io.github.zncmn.webrtc:libwebrtc-ktx:${latest.version}"
}
```

Make sure that you have either `jcenter()` and `maven { url 'https://jitpack.io' }` in the list of repositories:

```
repository {
    jcenter()
    maven { url 'https://jitpack.io' }
}
```

## Dependencies

* [libwebrtc-bin](https://github.com/crow-misia/libwebrtc-bin)

## License

```
Copyright 2020, Zenichi Amano.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
