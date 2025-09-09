# Messenger

A simple Messenger Application built with **Jetpack Compose** for android client.

---

## ✨ Features

- Light and Dark Modes
- Send and receive messages
- Persistent server side storage

---

## Planned features (for future)

- Desktop client support

---

## How to use
- Login/Signin first.
- After that you will come to this screen 

<img src="https://raw.githubusercontent.com/Jason3859/jason-messenger/master/screenshots/chatroom.png" height="500" alt="screenshot">

- Enter any unique id of your choice. Don't forget this because this is where your messages are saved. Share this with the person you want to chat with. You can also have a group chat.
- After that you will come to this screen 

<img src="https://raw.githubusercontent.com/Jason3859/jason-messenger/master/screenshots/messaging_screen.png" height="500" alt="screenshot">

- This is where you chat.
- Stay tuned for latest versions!

---

## What does what?

- If you click the <img src="https://raw.githubusercontent.com/Jason3859/jason-messenger/master/screenshots/info.png" height="500" alt="info"> icon in this screen,

<img src="https://raw.githubusercontent.com/Jason3859/jason-messenger/master/screenshots/messaging_screen.png" height="500" alt="screenshot">

It navigates to this screen

<img src="https://raw.githubusercontent.com/Jason3859/jason-messenger/master/screenshots/info_screen.png" height="500" alt="screenshot">

In this screen, 

- `Disconnect from <your-room-id> disconnects you from the room`
- `Delete Chatroom` button deletes the messages of this chatroom permanently. Messages might still be visible to other users when connected to the room while deleting, they will not be visible after you disconnect from room. This is because they are stored locally in the `viewmodel` of their device. They are cleared after they disconnect.
- `Delete Account` button deletes your account.

---

## 📦 Download

To download the app, scroll down to the **Assets** section in the [Releases](https://github.com/Jason3859/jason-messenger/releases) tab and download the `.apk` file.

---

## 🚀 Getting Started (by cloning this repo)

### Prerequisites

- JDK 17+
- Android Studio **Giraffe** or later OR IntelliJ IDEA
- Gradle 8.x+
- Android Emulator or physical device (API 27+)

### Clone the Repository

```bash
git clone https://github.com/Jason3859/jason-messenger.git
cd jason-messenger
```