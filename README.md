# Mochi — a live pet widget for your Samsung home screen

Mochi is a real Android **home-screen widget** (not an app window, not a wallpaper, not an overlay).
Once installed, Mochi lives directly on your home screen, has moods, gets hungry, sleeps, wakes up,
and reacts when you tap, feed, play with, or pet it — all without ever opening an app.

This guide assumes you have **no developer experience** and will do everything from your
Samsung Galaxy A14. You never need Android Studio or a computer. GitHub's free cloud servers
build the installable file (the APK) for you.

---

## What you are about to do, in plain terms

1. Create a free GitHub account (skip if you already have one).
2. Upload this project to a new GitHub repository, from your phone's browser.
3. Let GitHub's robot (GitHub Actions) compile the app in the cloud.
4. Download the finished file it produces.
5. Install it like any other app.
6. Add the Mochi widget to your home screen.

Total time: about 10–15 minutes, most of it waiting for step 3.

---

## Step 1 — Create a free GitHub account

1. Open your browser (Chrome or Samsung Internet) and go to **github.com**.
2. Tap **Sign up**.
3. Enter an email address, a password, and a username.
4. Verify your email when GitHub asks you to.

If you already have a GitHub account, just log in and skip to Step 2.

---

## Step 2 — Upload this project to a new repository

1. On github.com, tap the **+** icon (top right) → **New repository**.
2. Give it any name, for example `mochi-pet-widget`.
3. Leave it **Public** (required for the free build minutes to apply easily) and tap **Create repository**.
4. On the new, empty repository page, tap **uploading an existing file** (GitHub shows this link on an empty repo).
5. From the ZIP you downloaded from this chat, extract it first if your phone did not do it automatically:
   - Open your **Files** app (My Files on Samsung).
   - Find the downloaded `MochiPetWidget.zip`.
   - Tap it, then tap **Extract** (Samsung's file manager can unzip directly).
6. Go back to the GitHub upload page and tap **choose your files**.
7. Select **all files and folders** from the extracted `MochiPetWidget` folder (select-all, including the hidden
   `.github` folder — if your file picker hides it, see the note below) and upload them.
8. Scroll down and tap **Commit changes**.

**Note on the hidden `.github` folder:** phone file pickers sometimes hide folders starting with a dot.
If `.github/workflows/android-build.yml` does not appear after upload, use GitHub's **Add file → Create new file**
button instead, type the path exactly as `.github/workflows/android-build.yml` in the name field, and paste in
the contents of that file (open it in your file manager's text viewer first to copy it). Everything else can be
uploaded normally through drag-and-drop/choose-files.

---

## Step 3 — Run the cloud build

1. In your new repository, tap the **Actions** tab.
2. GitHub should already show a workflow called **Android Build** (because of the file you uploaded in
   `.github/workflows/android-build.yml`).
3. Tap **Android Build**, then tap **Run workflow** → **Run workflow** again to confirm.
   (If you don't see this button, it also runs automatically a few seconds after your upload, since the
   workflow is configured to build on every push to the `main` branch.)
4. Wait. Tap the running job (a yellow dot) to watch its progress. A first build typically takes
   **5–10 minutes** because it downloads the Android build tools.
5. When the dot turns green, the build succeeded.

If the dot turns red, tap into the failed step to read the error — the most common cause is an
incomplete upload (a file got skipped). Re-upload any missing file and re-run the workflow.

---

## Step 4 — Download the APK

1. Still on the finished (green) workflow run page, scroll down to **Artifacts**.
2. Tap **VirtualPetWidget-APK** to download it. GitHub packages it as a `.zip` containing the `.apk` file.
3. Extract that zip the same way as in Step 2 (tap it in Files → Extract) to get `app-debug.apk`.

---

## Step 5 — Allow installing from your browser/Files app

Samsung blocks installing apps from outside the Play Store by default. The first time you try to
install, Android will ask for permission:

1. Tap the `app-debug.apk` file in **My Files**.
2. If Android shows **"For your security, your phone is not allowed to install unknown apps from
   this source"**, tap **Settings** on that same dialog.
3. Turn on **Allow from this source** for the Files app (or your browser, if you install directly
   from a download notification).
4. Go back and tap the APK file again.

---

## Step 6 — Install

1. Tap **Install** on the install screen.
2. Tap **Install anyway** if Samsung shows a generic warning about an unrecognized app (this is
   expected for any app installed outside the Play Store — it is not specific to Mochi).
3. Tap **Open** once installation finishes, or find **Mochi** in your app drawer.

---

## Step 7 — Add Mochi to your home screen

You have two ways to do this:

**Easiest way:**
1. Open the Mochi app once.
2. Tap the button **"Add widget to home screen"**.
3. Confirm the system prompt that appears.

**Manual way (works even if the button above doesn't appear on your One UI version):**
1. Long-press any empty spot on your home screen.
2. Tap **Widgets**.
3. Scroll or search for **Mochi**.
4. Long-press the Mochi widget preview and drag it onto your home screen, or tap it and tap **Add**.

---

## Step 8 — Resize it (optional)

1. Long-press the widget on your home screen until it's selected (you'll see a frame with handles).
2. Drag the handles to make it bigger or smaller.
3. A larger size reveals Mochi's mood/energy/hunger bars and the feed/play/pet buttons. The smallest
   size shows just Mochi's face, which you can still tap to interact.

---

## How Mochi behaves

- Tap Mochi → a quick reaction.
- Tap the heart button → petting (raises affection and mood).
- Tap the bone button → feeding (lowers hunger).
- Tap the star button → playing (raises mood, uses energy).
- Leave it alone for a while → it gets sleepy, falls asleep, and wakes up later on its own.
- Mochi's stats and mood are saved on your device and survive closing any app, restarting your
  phone, or not touching your phone for hours — it recalculates what should have happened while
  you were away the next time its widget refreshes.

---

## Was the APK actually compiled already?

**No — not by me.** I do not have internet access or an Android build environment in this chat, so
I could not run a real Gradle build myself. Everything above — every Kotlin file, every XML
resource, the manifest, the Gradle configuration, and the GitHub Actions workflow — has been
written and cross-checked (every resource reference, class reference, and manifest registration
was verified to resolve correctly), but it has **not been run through an actual compiler**.

The GitHub Actions workflow in `.github/workflows/android-build.yml` performs the real compilation,
on GitHub's own cloud servers, once you push this project there. That is the "cloud build" this
whole setup is designed around — it is the step that turns source code into an actual installable
APK, and it's free, requires no signing certificate, and needs nothing from your phone except a
browser.

If the cloud build fails, it means a real compiler found a real problem (most likely a missed file
during upload, or a changed/renamed GitHub Actions version). It does not mean the project is fake —
you'll be able to see the exact error in the Actions log and tell me, and I can fix the specific
file involved.

---

## Project structure reference

```
MochiPetWidget/
├── .github/workflows/android-build.yml   cloud build definition
├── app/
│   ├── build.gradle.kts                  app module build config
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/mochipet/widget/     Kotlin source (data, domain, widget, worker, receiver)
│       └── res/                          layouts, drawables (14 mood illustrations), strings, widget info
├── build.gradle.kts                      root build config
├── settings.gradle.kts
└── gradle.properties
```

No Gradle wrapper binary (`gradle-wrapper.jar`) is checked into this project, by design — the cloud
workflow installs Gradle directly on the runner instead of relying on a committed wrapper file, so
there is nothing binary for you to have to trust from a chat download. If you ever do open this
project in Android Studio on an actual computer, Android Studio will offer to generate the wrapper
for you automatically on first sync — no action needed from you either way.

