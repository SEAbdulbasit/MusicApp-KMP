# Music Player App Using Compose Multiplatform KMP

This is a music player app built using Compose Multiplatform UI that works on Android, iOS, Desktop, and Web
platforms. It uses the spotify api for fetching the top 50 charts and getting the trending albums. The Google login is still in pending
and for now, you need to add the spotify token manually. You can easily hit the endpoint [here](https://developer.spotify.com/documentation/web-api/reference/get-an-album) to get the album
and then get the token and set in the app.

## Find it on official website of JetBrains
This repository has been listed as [KMP sample](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-samples.html#:~:text=Android%20and%20iOS-,Music%20App%20KMP,-An%20application%20showcasing) on official website of Jetbrains.

## Live
You can find it live [here](https://seabdulbasit.github.io/MusicApp-KMP/)

## Platforms

The app uses different media players on different platforms:

For iOS, AVKit is used
For Android, Media Player is used
For Desktop, VLC media player is used
For the Web, an HTML media player is used.

## Integration with Low-Level APIs

One of the objectives of building this app was to explore how Compose UI interacts with low-level APIs. The experience
was challenging yet fun, and the process taught me a lot.
Out of all the media players used, integrating with the Web Media Player was the easiest. I'm grateful to IceRock
Development and Aleksey Mikhailov for their demo application, which was a fantastic learning resource.

## Running the app

- Clone this repository:

```
git clone https://github.com/SEAbdulbasit/MusicApp-KMP.git
```

- Open the project in Android Studio or IntelliJ IDEA.
- Search for **_TOKEN_** file in the code and replace the placeholder with your **Spotify access token**. You can
  generate a new token from the [Spotify Developer Dashboard](https://developer.spotify.com/console/get-album-tracks/).
- Run the app on your desired platform.
  There are a few known issues with the Music Player app using Compose Multiplatform UI:

## Known Issues

- When you click "Select All" on Android, Web, and Desktop, the app will autoplay the selected tracks and continue
  playing the next track when the previous one ends.
  On iOS, there are issues with the callbacks for `onReady` and `onVideoCompleted`
  which is causing the player to not start automatically. I was unable to configure the callbacks but hopefully, will be
  fixing that soon.

## Demo

![Screenshot 2023-03-05 at 4 44 45 PM](https://user-images.githubusercontent.com/33172684/222960302-eccb34b4-d77c-4c95-96af-3d4528323c42.png)

## Repository

To explore what Compose UI can do, check out the repository for the latest
updates: https://github.com/SEAbdulbasit/MusicApp-KMP.

If you're interested in getting started with Compose Multiplatform, I have a template for you
here: https://github.com/SEAbdulbasit/KMP-Compose-Template.

If you find my work helpful, please consider giving it a ⭐ ❤️.

## Other Projects

TravelApp: https://github.com/SEAbdulbasit/TravelApp-KMP

## Technologies and Libraries Used

- Kotlin
- Compose Multiplatform UI
- AVKit Media Player
- VLC media player
- HTML media player
- [Compose Image Loader](https://github.com/qdsfdhvh/compose-imageloader)
- Decompose

TODO
-  Add google login
- Add local db to save favorite playlist
- Add implementation for recent releases


