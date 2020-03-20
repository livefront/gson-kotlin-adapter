![CI-Status](https://travis-ci.com/livefront/gson-kotlin-adapter.svg?token=yms391pwqdLJQXz4xfsz&branch=master) [![Release](https://jitpack.io/v/Livefront/gson-kotlin-adapter.svg)](https://jitpack.io/#Livefront/gson-kotlin-adapter)

# Gson Kotlin Adapter (Beta)
A Kotlin library for deserializing JSON to Kotlin models using the primary constructor.

This tool is currently in beta, while any issues are worked through. Please feel free to try it out and report any bugs that you may encounter.

<a name="why-this-exists"></a>
## Why This Exists
[Gson](https://github.com/google/gson) is a great library for parsing JSON into Java classes but it does not work as flawlessly in Kotlin. The main problems stem from the fact that Gson uses reflection to instantiate empty objects and populate the properties with data afterwards. Things like [default arguments](https://kotlinlang.org/docs/reference/functions.html#default-arguments) and [delegates](https://kotlinlang.org/docs/reference/delegated-properties.html) only get initialized when the [primary constructor](https://kotlinlang.org/docs/reference/classes.html#constructors) is called and the `init` block is run. When using Gson, the primary constructor is never called and those things are never initialized. This adapter ensures that whenever a Kotlin class is being deserialized from JSON, the primary constructor is called and everything is initialized properly.

This is intended to be used as a stop-gap for teams who are using Kotlin in a legacy system that started out in Java. New projects or projects that have the ability to migrate to newer tools made for Kotlin from the ground up should do so. [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization) and [Moshi](https://github.com/square/moshi) are great examples of these tools.

<a name="install"></a>
## Install
`Gson Kotlin Adapter` can be installed via gradle:

```gradle
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.livefront:gson-kotlin-adapter:0.1.0'
}
```

<a name="setup"></a>
## Setup
Setup is easy. Simply register the `KotlinReflectiveTypeAdapterFactory` to your `GsonBuilder`:

```kotlin
val gson: Gson = GsonBuilder()
    .registerTypeAdapterFactory(KotlinReflectiveTypeAdapterFactory.create())
    .create()
```

If you already have other type adapter factories registered, be sure place `KotlinReflectiveTypeAdapterFactory` at the beginning of the list.

<a name="how-it-works"></a>
## How It Works
The `Gson Kotlin Adapter` operates on all Kotlin classes and allows Gson's default adapter to handle the rest. The actual functionality for Kotlin classes depends on whether you are reading or writing. All non-Kotlin classes will be ignored by the `Gson Kotlin Adapter`.

### Writing JSON
When writing Kotlin classes to JSON, the `Gson Kotlin Adapter` will defer to the default adapter, usually the `ReflectiveTypeAdapterFactory`. Because of this, all objects serialized to JSON with this adapter will work exactly the same as if the adapter was not present at all.

### Reading JSON
When reading JSON to a Kotlin object, the `Gson Kotlin Adapter` will ensure that the primary constructor is invoked. Any JSON properties that are not part of the constructor will be ignored and non-constructor properties will be initialized in the same way they would be if the object was constructed in code. The `@Transient` and `@SerializableName` annotations will still be honored.
* `@Transient` will still cause properties to be ignored. It is important to ensure that these properties have default values otherwise it will always fail to construct the object.
* `@SerializableName` will still be used for finding the JSON property keys; when not present the variable name is used. If a single property has multiple keys found in the JSON an `IllegalArgumentException` is thrown. This may happen as the result of bad JSON with a single duplicated key or multiple alternate keys specified in the annotation that match more than one JSON key.

<a name="license"></a>
## License
    Copyright 2020 Livefront

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
