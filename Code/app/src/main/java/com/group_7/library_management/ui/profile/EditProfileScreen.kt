package com.group_7.library_management.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.group_7.library_management.components.CustomTextField
import com.group_7.library_management.ui.theme.LibrarySpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile

    var name by remember(profile?.id) { mutableStateOf(profile?.name ?: "") }
    var email by remember(profile?.id) { mutableStateOf(profile?.email ?: "") }
    var phone by remember(profile?.id) { mutableStateOf(profile?.phone ?: "") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { viewModel.onAvatarChange(it.toString()) }
        }
    )

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Cập nhật hồ sơ thành công")
            viewModel.consumeSaveSuccess()
            onSaved()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa hồ sơ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (profile == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(LibrarySpacing.Medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

            // Avatar Picker
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (profile.avatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = profile.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Chọn ảnh",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(LibrarySpacing.ExtraLarge))

            CustomTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                label = "Họ và tên",
                isError = nameError != null,
                supportingText = nameError
            )

            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

            CustomTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = "Email",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError != null,
                supportingText = emailError
            )

            Spacer(modifier = Modifier.height(LibrarySpacing.Medium))

            CustomTextField(
                value = phone,
                onValueChange = { phone = it; phoneError = null },
                label = "Số điện thoại",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phoneError != null,
                supportingText = phoneError
            )

            Spacer(modifier = Modifier.height(LibrarySpacing.ExtraLarge))

            Button(
                onClick = {
                    val errors = viewModel.updateUserProfile(name, email, phone)
                    nameError = errors.nameError
                    emailError = errors.emailError
                    phoneError = errors.phoneError
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !uiState.isSaving,
                shape = MaterialTheme.shapes.medium
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Lưu thay đổi", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(LibrarySpacing.Small))

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = !uiState.isSaving
            ) {
                Text("Hủy")
            }
        }
    }
}
