# Dummy Json Preview

An Android client for the [DummyJSON](https://dummyjson.com) API: sign in, browse and search
products, and create, edit or delete them.

Built with Jetpack Compose, a multi module MVVM architecture and Hilt.

## Running it

```bash
./gradlew installDebug
```

Requires JDK 21 and an Android SDK with API 37. The sign in screen is prefilled with the DummyJSON
demo account (`emilys` / `emilyspass`), so it can be signed into without typing.

```bash
./gradlew test          # 192 unit tests
./gradlew lintDebug
```

## Features

- **Login** against `POST /auth/login`. The access token and the signed in user are persisted with
  DataStore, so the app opens straight to the product list on the next launch.
- **Products** with debounced search, endless pagination and pull to refresh.
- **Product detail** with the gallery, discounted price, specifications and reviews.
- **Create, edit and delete**, each confirming the outcome in a dialog. Delete asks first.
- **Profile** with pull to refresh and logout. Signing out clears the whole back stack, so back
  from the sign in screen leaves the app rather than returning to the signed in screens.
- Five tab bottom navigation, two tabs of which are placeholders.

## Architecture

Three Gradle modules, depending inwards:

```
:app  ──────►  :domain  ◄──────  :data
(Compose UI,   (models,          (Retrofit, DataStore,
 ViewModels)    repository        repository
                contracts,        implementations)
                use cases)
```

`:domain` is a plain Kotlin module and knows nothing about Android, Retrofit or Compose. `:app` and
`:data` meet only through the interfaces it declares, which is what lets every view model be tested
against a mocked use case.

### Package layout

Packages are grouped by feature inside each layer, following the shape used by
[Now in Android](https://github.com/android/nowinandroid):

```
:app     feature/{login,product/{list,detail,edit,form},addproduct,profile,comingsoon,home}
         navigation, ui/{components,theme}, util
:domain  {auth,product}/{model,repository,usecase}, error
:data    {auth,product}/{api,model,mapper,repository}, di, network
```

One class or interface per file.

### State and navigation

Each screen has a `ViewModel` exposing a single immutable state class over `StateFlow`, and a
stateless `…Content` composable that renders it. That split is what makes every state previewable,
including the failure ones.

Navigation uses two `NavHost`s: a root host holding login and the signed in graph, and an inner host
holding the five tabs. Sharing one host would make login the graph's start destination, which the
tab switching `popUpTo` targets, and back from a tab would land on login while still signed in.

Signing in and out are driven by the stored session rather than by navigation callbacks. Writing or
clearing the session moves the user, so no screen needs to know what happens next.

### Errors

Everything the data layer can throw is translated once, in `ApiErrorMapper`, into a sealed
`AppException` declared in `:domain`. Nothing above the data layer imports Retrofit or OkHttp, and
the presentation layer branches on failure kinds without knowing where they came from.

Failures are reported in proportion to what they cost:

| Situation | Treatment |
| --- | --- |
| Nothing on screen to fall back on | Full screen message with a retry |
| A refresh failed over existing content | Banner above the content, which stays usable |
| A page failed to load | Inline row with a retry, automatic paging paused |
| A write failed | Reported in place, with the typed values kept |

### Reusable components

`ui/components` holds `AppTopBar` (the toolbar, with the back arrow appearing only for pushed
screens), `StateMessage` (full screen empty, error and coming soon states), `StateBanner` (the
compact variant for when content is already on screen), `MessageDialog` and `LoadingIndicator`.
`ProductForm` is shared by creating and editing, which submit the same fields to different
endpoints, and `NumberFormat` holds the display and input parsing extensions.

## Testing

192 unit tests, written test first: 33 in `:domain`, 64 in `:data`, 95 in `:app`. UI is verified by
hand and by Compose previews rather than by instrumentation tests.

Each cycle starts from a stub that compiles but does nothing, so a test that passes before the
behaviour exists is caught and strengthened rather than trusted. Tests that assert only the absence
of something get an explicit precondition for that reason.

Coroutine tests assert in flight state, not just the final result, so a flag that is set and cleared
without ever being observable fails.

## CI

Two GitHub Actions workflows:

- **CI** runs on every push to `main` and on pull requests against it: unit tests, lint, and a debug
  build. Reports are uploaded when something fails, and the debug APK is kept as an artifact.
- **Release** runs when a `v*` tag is pushed. It runs the tests first, then builds both APKs and
  attaches them to a GitHub release named after the tag.

```bash
git tag v1.0.0 && git push origin v1.0.0
```

Releases are signed with the debug key unless a keystore is configured, because an unsigned APK
cannot be installed. To sign properly, add these repository secrets:

| Secret | Value |
| --- | --- |
| `RELEASE_KEYSTORE` | the keystore file, base64 encoded |
| `RELEASE_KEYSTORE_PASSWORD` | store password |
| `RELEASE_KEY_ALIAS` | key alias |
| `RELEASE_KEY_PASSWORD` | key password |

```bash
base64 -i release.jks | pbcopy
```

## Notable dependencies

| | |
| --- | --- |
| Compose BOM | 2026.06.01 |
| Kotlin / KSP | 2.4.10 / 2.3.11 |
| AGP | 9.2.1 |
| Hilt | 2.60.1 |
| Retrofit / OkHttp | 3.0.0 / 5.4.0 |
| DataStore | 1.2.1 |
| Coil | 2.7.0 |
| Coroutines | 1.11.0 |
| MockK | 1.14.11 |
| Chucker | 4.3.1, debug builds only |

`compileSdk` and `targetSdk` 37, `minSdk` 24.

## Known limitations

DummyJSON simulates writes rather than storing them. Creating, editing or deleting returns a
successful response, but the product list is unchanged afterwards. The app reports what the API
actually returned instead of hiding this behind a local cache.
