# Taskify 📱

**Taskify** is a modern Android task management application that helps you organize, prioritize, and track your daily tasks efficiently. Built with Material Design principles, Taskify provides an intuitive interface for managing your productivity.

## ✨ Features

- **📝 Task Management**: Create, edit, and delete tasks with ease
- **🎯 Priority System**: Organize tasks by High, Medium, and Low priority levels
- **📅 Due Dates & Times**: Set specific deadlines for your tasks
- **🗂️ Date Filters**: Focus on tasks scheduled for a specific day, month, or year from both the home list and stats screen
- **📊 Task Categories**: Categorize tasks for better organization
- **✅ Status Tracking**: Mark tasks as Complete, Pending, or Failed
- **🔄 Auto-Failure**: Tasks automatically mark as failed when past due
- **📈 Statistics**: View completion rates and task analytics
- **🎨 Modern UI**: Clean, Material Design interface
- **🌗 Theme Toggle**: Light / Dark / System theme switch with persistence
- **🟦 App Icon**: Adaptive launcher icon with custom Taskify logo
- **🔔 Smart Notifications**: 5-minute advance alerts with sound & vibration

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest stable recommended)
- Android SDK and platform tools
- JDK 17 (or the version configured in `gradle.properties`)
- Android device or emulator running API 21+

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/WinBlader/Android_Project.git
   cd Android_Project
   ```

2. Open the project in Android Studio
3. Let Gradle sync finish
4. Build the app:
   ```bash
   ./gradlew assembleDebug
   ```
5. Run on a device/emulator from Android Studio

## 📱 Usage

1. **Adding Tasks**: Tap the + button to create new tasks
2. **Setting Priority**: Choose High, Medium, or Low priority when creating tasks
3. **Due Dates**: Set specific dates and times for task completion
4. **Completing Tasks**: Tap "Complete" when you finish a task
5. **Deleting Tasks**: Tap "Delete" to remove unwanted tasks
6. **Viewing Stats**: Access statistics through the menu
7. **Theme Toggle**: Tap the gear icon in the top app bar to cycle:
   - Light → Dark → System (follows device)
   - Choice is saved and applied across all screens
8. **Task Reminders**: Get notified 5 minutes before task deadlines
   - Rich notifications with task details and due time
   - Sound alerts and vibration patterns
   - Automatic cancellation for completed/deleted tasks
9. **Filtering Tasks**:
   - Tap the filter chip on the home screen to view all tasks, today’s tasks, this month, this year, or choose custom ranges
   - Statistics respect the selected filter so you can compare performance across different periods

## 🏗️ Project Structure

```
app/
├── src/main/java/com/example/taskify/
│   ├── MainActivity.java          # Main activity with task list
│   ├── AddTaskActivity.java       # Task creation interface
│   ├── StatsActivity.java         # Statistics and analytics
│   ├── Task.java                  # Task data model
│   ├── TaskAdapter.java           # RecyclerView adapter
│   ├── TaskDBHelper.java          # Database operations
│   └── TaskAlarmManager.java      # Reminder management
├── src/main/res/
│   ├── layout/                    # UI layouts
│   ├── values/                    # Colors, strings, styles
│   └── drawable/                  # Icons and graphics
└── build.gradle.kts              # App module configuration
```

## 🛠️ Technical Details

- **Language**: Java
- **Database**: SQLite with custom helper
- **UI Framework**: Material Design Components
- **Theming**: Material3 DayNight with persistent theme preference
- **Architecture**: Traditional Android with Activities
- **Notifications**: AlarmManager with BroadcastReceiver for task reminders
- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: Latest Android version

## 📊 Database Schema

The app uses SQLite with the following table structure:
- **id**: Primary key (auto-increment)
- **name**: Task title
- **category**: Task category
- **priority**: High/Medium/Low
- **dueDate**: Due date (dd/MM/yyyy)
- **dueTime**: Due time (HH:mm)
- **status**: Pending/Completed/Failed
- **score**: Completion score (0 or 1)

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

For major changes, please open an issue first to discuss what you would like to change.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**WinBlader** - [GitHub Profile](https://github.com/WinBlader)

## 🙏 Acknowledgments

- Material Design Components for Android
- Android SQLite documentation
- The open-source community for inspiration and support

---

**Made with ❤️ for productivity enthusiasts**
