# `com.tomuvak.optional-type` – a multi-platform Kotlin library for the Optional type (the type itself, nothing more)
This library is licensed under the [MIT License](https://en.wikipedia.org/wiki/MIT_License);
see [LICENSE.txt](LICENSE.txt).

Note: This library contains the bare definition of the `Optional` type _and nothing more_. Users interested in that
alone are welcome to use this library alone, but most users are likely to want to use the sister library
[`com.tomuvak.optional`] – please continue there unless the only thing you're after is really just the bare type
definition. (This library was extracted out of `com.tomuvak.optional` for a very technical reason: the _almost_ circular
dependency – [`com.tomuvak.optional-test`] depending on `com.tomuvak.optional`, with the latter depending on the former
for its tests – caused IntelliJ IDEA to _unjustly_ complain that `Cannot access class 'com.tomuvak.optional.Optional'.
Check your module classpath for missing or conflicting dependencies` on call sites to functions from
`com.tomuvak.optional-test`. This extraction does give users the option to use this library on its own, but it's an
unlikely use case.)

## Table of contents
* [The Optional type](#the-optional-type)
  * [Further reading](#further-reading)
* [Rationale](#rationale)
  * [Kotlin's built-in nullable types](#kotlins-built-in-nullable-types)
  * [Java's java.util.optional](#javas-javautiloptional)
  * [Any other multi-platform Kotlin library](#any-other-multi-platform-kotlin-library)
* [Usage](#usage)
  * [Including the library in a Kotlin project](#including-the-library-in-a-kotlin-project)
  * [Using the `Optional` type](#using-the-optional-type)
    * [Basic values](#basic-values)
    * [Other functionality](#other-functionality)
    * [Testing](#testing)

## The Optional type
The `Optional` type (also known as _Option_ or _Maybe_) represents values which are optional, that is for any value of
the `Optional` type there may or may not be an actual underlying value.

More concretely, a value of type `Optional<T>` is either:
* `None` (= no value); or
* `Value(t)` – a (wrapped) value (`t`) of type `T`.

### Further reading
* [Wikipedia](https://en.wikipedia.org/wiki/Option_type)
* [nLab](https://ncatlab.org/nlab/show/maybe+monad)

## Rationale
There are countless uses for the `Optional` type, and they need not be explored here.
But this might be a reasonable place to address the issue of why use specifically `com.tomuvak.optional-type` (and its
sister library [`com.tomuvak.optional`]) rather than any other existing solution, such as:

### Kotlin's [built-in nullable types](https://kotlinlang.org/docs/null-safety.html)
Kotlin's built-in distinction between nullable and non-null types is a very good thing in and of itself (certainly when
compared to the situation in some other programming languages, where many types are nullable and there's no way to force
non-nullability statically), and the use of nullable types and values addresses many of the use cases of the `Optional`
type.
However, one place where Kotlin's built-in nullable types fall short is in their inability to be nested:
"doubly-nullable" types are indistinguishable from their "singly-nullable" counterparts, and cannot make the often
important distinction between not having a value at all and having a value which is itself `null`.

For example, when `k` is some value of type `K` and `m` is of type `Map<K, V>`, what does it mean for
[`m[k]`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/get.html) to be `null`?
If `V` is a non-null type it means `m` does not contain the key `k`, but if `V` is a nullable type then this does not
distinguish between `m` not containing the key `k` and `m` containing the key `k` with an associated value of `null`.

For another example,
[`generateSequence`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/generate-sequence.html), which uses
`null`s to signal end of sequence, cannot be used to generate sequences containing `null`s (and, in fact, is declared to
only generate sequences of non-null types).

In contrast, the `Optional` type can be nested (allowing types such as `Optional<Optional<T>>`, and, indeed,
`Optional<Optional<Optional<T>>>`, `Optional<Optional<Optional<Optional<T>>>>` etc.), allowing analogous usages to the
ones above which make use of `Optional` rather than of nullable types to be fully generic and cater also for use cases
where underlying types could potentially themselves be `Optional`.

Note: nullable types are superior to `Optional` in their performance and in their memory consumption, and they enjoy the
significant benefit of being standard and built into the language. The author of `com.tomuvak.optional-type` (and its
sister library [`com.tomuvak.optional`]) does not at all advocate against their use for specific use cases where the
underlying type could not conceivably itself be nullable, or when the distinction between having no value and having a
value of `null` is _really_ not important. But for all other cases `Optional` seems to be the better model.

Note also that it is possible to use nested types with a mix of `Optional`s and nullable types. In cases where a `T??`
type distinct from `T?` would have been nice, alternatives to consider include `Optional<Optional<T>>`, `Optional<T?>`
and `Optional<T>?`.

### Java's [java.util.Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)
Regardless of any difference or similarity in functionality or features, one major distinguishing factor between
`com.tomuvak.optional-type` (and its sister library [`com.tomuvak.optional`]) and any Java solution is that the
_tomuvak_ libraries are multi-platform and so can be used seamlessly in any Kotlin project, including on non-JVM
platforms.

### Any other multi-platform Kotlin library
The author of `com.tomuvak.optional` (and its sister library [`com.tomuvak.optional`]) has not explored any other
multi-platform Kotlin library which offers support for the `Optional` type, and makes no claim regarding any advantage
the _tomuvak_ libraries may or may not have over other such libraries.

## Usage

### Including the library in a Kotlin project
To add the library from
[GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages), a
reference to this repository's GitHub Packages
[Maven repository](https://maven.apache.org/guides/introduction/introduction-to-repositories.html) needs to be added
inside the `repositories { ... }` block in the project's `build.gradle.kts` file:

```kotlin
    maven {
        url = uri("https://maven.pkg.github.com/tomuvak/optional-type")
        credentials { // See note below
            username = "<GitHub user name>"
            password = "<GitHub personal access token>"
        }
    }
```

and the dependency should be declared for the relevant source set(s) inside the relevant `dependencies { ... }` block(s)
inside the `sourceSet { ... }` block, e.g.

```kotlin
        val commonMain by getting {
            dependencies {
                implementation("com.tomuvak.optional-type:optional-type:0.0.1")
            }
        }
```

to add it for all platforms in a multi-platform project.

For any actual work with the `Optional` type on top of its mere definition, consider adding an additional dependency on
the sister library [`com.tomuvak.optional`], e.g. `implementation("com.tomuvak.optional:optional:<version>")`
(`<version>` at the time of writing this is `0.0.4`).

And for testing utilities for the `Optional` type consider adding a dependency (supposedly for test source sets) on
[`com.tomuvak.optional-test`].

Note: it seems that a single reference to the GitHub Packages Maven repository of any one of these three libraries
suffices for Gradle to handle all of them.

Note about credentials: it seems that even though this repository is public and everyone can download this library from
GitHub Packages, one still needs to supply credentials for some reason. Any GitHub user should work, when provided with
a [personal access
token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
for the user with (at least) the `read:packages` scope.

**You might want to keep the credentials private**, for example in case the GitHub user has access to private packages
(as GitHub personal access tokens can be restricted in the type of operations they're used for, but not in the
repositories they can access), or all the more so in case the token has a wider scope (and note also that one can change
a token's scope after its creation, so it's possible that at some future point the user might inadvertently grant a
token which was meant to be restricted more rights).

See this repository's own [Gradle script](build.gradle.kts) for an example of one way this could be done (by means of
storing private information in a local file which is not source-controlled).

### Using the `Optional` type

#### Basic values
A value of type `Optional<T>` can be either:
* `Optional.None` (corresponds to `null` when using [nullable types](https://kotlinlang.org/docs/null-safety.html) to
  model optionals); or
* an instance of `Optional.Value`, with an underlying value (accessible through the `value` property) of type `T`.

(Note: when `T` is itself a nullable type an `Optional<T>` might be a `Value` and have a `value` of `null`, which is not
the same as being `None`.)

For brevity, one can `import com.tomuvak.optional.Optional.None` and `import com.tomuvak.optional.Optional.Value`, thus
allowing the use of the unqualified forms `None` and `Value`.

Just like one cannot simply use a value of type `T?` as if its type was `T`, the same is true for values of type
`Optional<T>`. One can check for there (not) being a value by comparing an `Optional` value (or its type) to `None`,
e.g. `if (optional == None)`, `if (optional != None)`, `if (optional is None)`, `if (optional !is None)`. Unlike the
situation with the built-in nullable types, where the compiler accepts code which treats a value of type `T?` as a value
of type `T` in contexts where it's established that the value is not `null`, establishing that an `Optional` value is
not `None` is not enough to permit accessing its `value` property. But establishing it is a `Value` is:

```kotlin
if (optional is Value) {
    // it is possible (and safe) to use optional.value here
}
```

One idiom for using `Optional` values is to use `when`:

```kotlin
when (optional) {
    /*is*/ None -> // what to do when there's no value
    is Value -> // it is possible (and safe) to use optional.value here
}
```

#### Other functionality
For other functionality, including more idiomatic ways to access `Optional`s' values, as well as much much more, check
out the sister library [`com.tomuvak.optional`].

#### Testing
The library [`com.tomuvak.optional-test`] provides some utilities designed to facilitate testing code which uses the
`Optional` type, specifically assertions over values of the `Optional` type.

[`com.tomuvak.optional`]: https://github.com/tomuvak/optional
[`com.tomuvak.optional-test`]: https://github.com/tomuvak/optional-test
