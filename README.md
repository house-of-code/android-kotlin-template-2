![android-kotlin-template-2](app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

# android-kotlin-template-2

Architecture template for Android using Kotlin.
This template is a renewed version of the previous Clean Architecture template, and is created to provide a transparent architecture for small to medium projects with a relatively low complexity.


- - -

## Starter guide

### Step 1: Clone project
Clone the project into a new folder:
```
git clone git@bitbucket.org:houseofcode/android-kotlin-template-2.git my-new-project
```

### Step 2: Application id and package name
The application id (used to identify app) can simply be changed in `app/build.gradle` (see `applicationId` in `android.defaultConfig`).

To change the package naming used in the application open the manifest and and refactor (`Refactor` > `Rename..`) the package name on the manifest root element.

### Step 3: Launcher icon

All launcher icons (adaptive and legacy icons) can be generated from a square background and foreground SVG (ideally in the same size). Make sure to have these two resources provided, so we can generate adaptive icons and not only use legacy icons for our new apps.

An example of adaptive and legacy icons have been added to this project for each build types; debug and release.

### Step 4: Run through code

This template contains a lot of code examples, that might not benefit your project, so make sure to remove unnecessary files and modify the rest to your needs. You can always come back to this repository to find the code examples again, if you need them during further development.

The files you most certainly want to modify or replace are:
- Retrofit service: `ItemService`, located at `io.houseofcode.template2.data.ItemService`, is a simple example of a Retrofit service for network requests.
- Use cases: Located at `io.houseofcode.template2.domain.usecase` is the example use cases for encapsulating the business logic when accessing data. Don't forget to modify the use case Android tests as well.
- UI and presenters: Activities (and fragments should) are located at `io.houseofcode.template2.presentation.ui`, and the contract and presenter is found at `io.houseofcode.template2.presentation.feature`.


- - -

## Distribution

### Google Play Store

This is what is required to be able to publish the application to Google Play Store:

- Title (max 50 characters)
- Short description (max 80 characters)
- Full description (max 4000 characters)
- App icon (512 x 512 px)
- Phone and tablet screenshots (minimum 2)
- Feature graphic (1024 x 500 px)
- Application type and category
- Tags (max 5)
- Contact email
- Privacy policy

As default the texts and graphics should be created in English, but more languages can be added.

See more in the links below:

https://support.google.com/googleplay/android-developer/answer/1078870

https://support.google.com/googleplay/android-developer/answer/113475


- - -

## Layers

The application is structured into a multi-tier architecture to separate UI, logic and data. The segregated layers are described below and can easily be changed to fit the scope of your project.

A open layer architecture is used, which means that no actual restriction is set up to prevent communication between the presentation layer and the data layer, though a true three-tier architecture would prohibit this. To keep a clear architecture we should always limit the direct communication between these layers.


### Presentation
Top-most layer of the application is the user interface. The main function of the interface is to translate tasks and results to something the user can understand.

No actual business logic should exist in this layers presenters, and the view model and use case is used to transfer the necessary processing to the domain layer.

### Domain
Also known as the logic tier or business logic layer.

Coordinates the application, processes commands, makes logical decisions and evaluations, and performs calculations. It also moves and processes data between the two surrounding layers.

### Data
Information is stored, retrieved and passed to the domain layer for processing.

This layer is responsible for accessing all data whether it is retrieved from a remote data source or a caching component.


- - -

## Features

### App signing

Google can manage keystore and signing of the application, which also enabled us to benefit from features like Android App Bundle ([new publishing format](https://developer.android.com/platform/technology/app-bundle) for reduced app size and more efficient releases) and Google Play's Dynamic Delivery ([deliver optimized apps](https://developer.android.com/studio/projects/dynamic-delivery) for users configuration).

The new app signing uses an _upload key_ to sign app before being uploaded to Google Play Store, after which the app is signed with a key handled by Google so keystores can no longer be lost or compromised.

To get started using Googles app signing and App Bundle you'll have to generate your upload key. Generate upload keystore and sign your App Bundle from Android Studio:

1. Go to: `Build` > `Generate Signed Bundle` and choose `Android App Bundle`
2. Create a new keystore and place it in your repository
3. Make sure to export encryption private key (`Export encryption key for enrolling published apps in Google Play App Signing`) and place the `.pepk` in your repository as well
4. When you create and upload your first release you'll enroll into App Signing and the final App Bundle is signed with a keystore handled by Google Play Store


See more in the links below:

https://developer.android.com/studio/publish/app-signing

https://support.google.com/googleplay/android-developer/answer/7384423


### Adaptive icons

Adaptive launcher icons was introduced in Android 8 (Oreo) as a way to handle different icons for all OEM devices by splitting the icon into a background and foreground vector image. Some home screen launchers even support animated visual effects on 

With Android Studio we can generate the legacy launcher icons, used on all other previous Android versions, from the background and foreground drawables:

1. Right-click the `res` folder and select: `New` > `Image Asset`
2. Choose to generate adaptive and legacy icons from a foreground and background drawable
3. Make sure to choose the correct `res` folder for your build type and the adaptive and legacy launcher icons will be generated


See more in the links below:

https://developer.android.com/guide/practices/ui_guidelines/icon_design_adaptive

https://developer.android.com/studio/write/image-asset-studio


### Coroutines

Kotlin Coroutines handles asynchronous and non-blocking executing with light-weight threading.

A dispatcher context determines what thread pool the code is executed in, and the ability to change dispatcher context within one coroutine gives us the ability to safely jump between threads.

We can call a [suspending function](https://kotlinlang.org/docs/reference/coroutines/composing-suspending-functions.html) like this:
```

suspend fun get(url: String): String {
    return withContext(Dispatchers.IO) {
        /* perform network IO here */
        remoteService.getUrl(url)
    }
}
```

The suspended function, which is executed on an IO thread, needs to be called from a coroutine:
```
CoroutineScope(Dispatchers.Main).launch {
    val value = get("https://houseofcode.io/")
}
```

See more in the links below:

https://kotlinlang.org/docs/reference/coroutines-overview.html

https://developer.android.com/kotlin/coroutines


### LiveData / ViewModel

A [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) manages UI-related data within the lifecycle of the view.

[LiveData](https://developer.android.com/topic/libraries/architecture/livedata) is an observable data class that ensures safe and automatic data communication.

The two classes can be used in combination to provide data from a repository (real-time or request-response) to UI without having to deal with the lifecycle of the UI:

```
class UserViewModel : ViewModel() {

    private lateinit var remoteService: RemoteService

    fun getUsers(): LiveData<Resource<List<User>>> {
        val data = MutableLiveData<Resource<List<User>>>()

        CoroutineScope(Dispatchers.Main).launch {
            data.value = getResource(remoteService.getUsers())
        }

        return data
    }

    private suspend fun <T> getResource(request: Deferred<Response<T>>): Resource<T> {
        return withContext(Dispatchers.IO) {
            val response = request.await()
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    Resource.success(responseBody)
                } else {
                    Resource.error("Request did not return any body")
                }
            } else {
                Resource.error("Request was unsuccessful")
            }
        }
    }
}
```

See more in the links below:

https://developer.android.com/topic/libraries/architecture/livedata

https://developer.android.com/topic/libraries/architecture/viewmodel


### Glide

A `AppGlideModule` is used to apply a global configuration to the image loading. A configuration is already added in this project to cache two screens of decoded images: `TemplateAppGlideModule`.

You can find much more about applying options to the configuration here: https://bumptech.github.io/glide/doc/configuration.html#application-options


See more in the links below: 

https://bumptech.github.io/glide/

https://bumptech.github.io/glide/doc/configuration.html#module-classes-and-annotations


### Caching with Room

Two repositories are exposed in the application class; One is a "regular" remote repository, that fetches content from a remote API, and the second is a cached repository, which fetches content from the same remote API but also caches responses to limit the amount of network requests.

The cached responses are stored with [Android Room](https://developer.android.com/jetpack/androidx/releases/room) alongside a cache entry, which registers when a response was last cached based on a cache key.


#### Debugging database
To debug the caching database, check LogCat when the application starts for an entry like:

```
D/DebugDB: Open http://xxx.xxx.x.xxx:8080 in your browser
```

If you are using an emulator, run `adb forward tcp:8080 tcp:8080` and open http://localhost:8080/.

- - -

## Testing

This template aims to make testing of the vital business logic in the domain layers use cases easy and transparent. For that reason it it important to keep _all_ business logic encapsulated in use cases in the domain layer, so testing of this logic is centralised and convenient.

The `CoroutineTest` class is created to make testing of LiveData and Coroutines easy in the use cases; Simply inherit from this class and use the extensions methods in `TestingExtensions` to be able to test LiveData with Coroutines.

To run the Android tests in the terminal use this command:

```
./gradlew connectedAndroidTest
```

To run the Android tests from Android Studio go to: `Run` > `Edit Configurations...`, add a new `Android Instrumented Tests` and select the `domain` as your module.