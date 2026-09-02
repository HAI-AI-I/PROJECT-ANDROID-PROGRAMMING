package com.group_7.library_management.ui.qrscan

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScanResultState {
    object Idle : ScanResultState
    object Processing : ScanResultState
    data class Success(val rawCode: String) : ScanResultState
    data class Error(val message: String) : ScanResultState
}

data class ScanUiState(
    val isFlashlightOn:Boolean=false,
    val isFlashlightAvailable:Boolean=true,
    val scanState: ScanResultState = ScanResultState.Idle
)

@HiltViewModel
class ScanViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    // 1. Nhận kết quả quét trực tiếp từ Camera (Google Code Scanner)
    fun onLiveCameraScanned(rawCode: String) {
        processScannedCode(rawCode)
    }

    // 2. Nhận URI ảnh từ thư viện và giải mã bằng ML Kit
    fun scanFromGalleryUri(context: Context, imageUri: Uri) {
        _uiState.update { it.copy(scanState = ScanResultState.Processing) }

        viewModelScope.launch {
            try {
                val inputImage = InputImage.fromFilePath(context, imageUri)
                val scanner = BarcodeScanning.getClient()

                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        val code = barcodes.firstOrNull()?.rawValue
                        if (code != null) {
                            processScannedCode(code)
                        } else {
                            _uiState.update {
                                it.copy(scanState = ScanResultState.Error("Không tìm thấy mã QR trong ảnh này."))
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        _uiState.update {
                            it.copy(scanState = ScanResultState.Error("Lỗi quét ảnh: ${exception.localizedMessage}"))
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(scanState = ScanResultState.Error("Không thể đọc tệp ảnh."))
                }
            }
        }
    }

    // Hàm dùng chung để kiểm tra và xử lý mã sau khi đọc được
    private fun processScannedCode(code: String) {
        _uiState.update { it.copy(scanState = ScanResultState.Success(code)) }
    }

    // Reset lại trạng thái để người dùng quét lại nếu muốn
    fun resetScanState() {
        _uiState.update { it.copy(scanState = ScanResultState.Idle) }
    }

    fun toggleFlashlight() {
        _uiState.update { it.copy(isFlashlightOn = !it.isFlashlightOn) }
    }
}