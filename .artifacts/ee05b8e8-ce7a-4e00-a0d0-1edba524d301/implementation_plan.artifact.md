# Build HomeScreen.kt

Implement the logic and data flow for the Home Screen in the library management application. This includes creating data models, a ViewModel for state management, and refactoring the existing UI to be dynamic.

## User Review Required

> [!IMPORTANT]
> The current `HomeScreen.kt` is purely static. I will introduce a `HomeViewModel` and data models (`Book`, `User`) to make it dynamic.

## Proposed Changes

### [Models]

#### [NEW] [Book.kt](file:///C:/Users/Hiep/AndroidStudioProject/PROJECT-ANDROID-PROGRAMMING/Code/app/src/main/java/com/group_7/library_management/models/Book.kt)
Define the `Book` data model with properties like title, author, availability, and remaining days for borrowed books.

#### [NEW] [User.kt](file:///C:/Users/Hiep/AndroidStudioProject/PROJECT-ANDROID-PROGRAMMING/Code/app/src/main/java/com/group_7/library_management/models/User.kt)
Define the `User` data model with name, student ID, and QR code data.

### [UI Logic]

#### [NEW] [HomeViewModel.kt](file:///C:/Users/Hiep/AndroidStudioProject/PROJECT-ANDROID-PROGRAMMING/Code/app/src/main/java/com/group_7/library_management/ui/home/HomeViewModel.kt)
Create a ViewModel to provide data for the Home screen, including:
- Current user info.
- List of borrowed books.
- List of popular books.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/Hiep/AndroidStudioProject/PROJECT-ANDROID-PROGRAMMING/Code/app/src/main/java/com/group_7/library_management/ui/home/HomeScreen.kt)
Refactor the `HomeScreen` composable to:
- Accept `HomeViewModel` as a parameter.
- Observe state from the ViewModel.
- Replace hardcoded lists and strings with data from the state.

## Verification Plan

### Automated Tests
- N/A for UI-only refactoring at this stage, but can add unit tests for `HomeViewModel` later.

### Manual Verification
- Verify that the Home screen displays the mock data provided by the ViewModel.
- Ensure the "Borrowed Books" and "Popular Books" horizontal lists work correctly.
- Check that the Student Card displays the user's name and ID correctly.
