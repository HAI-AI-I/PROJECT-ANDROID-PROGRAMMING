package com.group_7.library_management.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.group_7.library_management.components.QrDisplayCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberQrScreen(
    onBack: () -> Unit,
    viewModel: MemberQrViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mã QR thành viên", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.errorMessage != null -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(requireNotNull(state.errorMessage), color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = viewModel::loadMember) { Text("Thử lại") }
                }
                state.user != null -> {
                    val user = requireNotNull(state.user)
                    val memberCode = "UTH-MEMBER-${user.id.toString().padStart(8, '0')}"
                    QrDisplayCard(
                        qrContent = memberCode,
                        code = memberCode,
                        title = user.name,
                        description = "Xuất trình mã này cho thủ thư để nhận diện tài khoản thành viên.",
                        modifier = Modifier.fillMaxSize().padding(24.dp).wrapContentHeight(Alignment.CenterVertically)
                    ) {
                        Text(user.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (user.phone.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(user.phone, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
