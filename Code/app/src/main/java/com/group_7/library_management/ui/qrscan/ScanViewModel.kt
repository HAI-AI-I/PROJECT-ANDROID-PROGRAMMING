package com.group_7.library_management.ui.qrscan

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group_7.library_management.data.repository.ScanDestinationType
import com.group_7.library_management.data.repository.ScanRepository
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed interface ScanResultState {
    data object Idle : ScanResultState
    data object Processing : ScanResultState
    data class Success(
        val type: ScanDestinationType,
        val targetId: Long
    ) : ScanResultState
    data class Error(val message: String) : ScanResultState
}

data class ScanUiState(
    val isFlashlightOn: Boolean = false,
    val isFlashlightAvailable: Boolean = true,
    val scanState: ScanResultState = ScanResultState.Idle
)

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val scanRepository: ScanRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun onLiveCameraScanned(rawCode: String) {
        if (_uiState.value.scanState !is ScanResultState.Idle) return
        resolveScannedCode(rawCode)
    }

    fun scanFromGalleryUri(context: Context, imageUri: Uri) {
        if (_uiState.value.scanState !is ScanResultState.Idle) return
        _uiState.update { it.copy(scanState = ScanResultState.Processing) }

        try {
            val inputImage = InputImage.fromFilePath(context, imageUri)
            val scanner = BarcodeScanning.getClient()
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    val code = barcodes.firstOrNull()?.rawValue
                    if (code != null) {
                        resolveScannedCode(code)
                    } else {
                        _uiState.update {
                            it.copy(
                                scanState = ScanResultState.Error(
                                    "Không tìm thấy mã QR hoặc mã vạch trong ảnh này."
                                )
                            )
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    _uiState.update {
                        it.copy(
                            scanState = ScanResultState.Error(
                                "Lỗi quét ảnh: ${exception.localizedMessage}"
                            )
                        )
                    }
                }
                .addOnCompleteListener { scanner.close() }
        } catch (_: Exception) {
            _uiState.update {
                it.copy(scanState = ScanResultState.Error("Không thể đọc tệp ảnh."))
            }
        }
    }

    private fun resolveScannedCode(rawCode: String) {
        val code = rawCode.trim()
        if (code.isEmpty()) {
            _uiState.update { it.copy(scanState = ScanResultState.Error("Mã quét không hợp lệ.")) }
            return
        }

        _uiState.update { it.copy(scanState = ScanResultState.Processing) }
        viewModelScope.launch {
            try {
                val destination = scanRepository.resolve(code)
                _uiState.update {
                    it.copy(
                        scanState = ScanResultState.Success(
                            type = destination.type,
                            targetId = destination.targetId
                        )
                    )
                }
            } catch (error: HttpException) {
                val message = when (error.code()) {
                    404 -> "Không tìm thấy sách hoặc đơn mượn phù hợp với mã đã quét."
                    401 -> "Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại."
                    else -> "Không thể kiểm tra mã với máy chủ."
                }
                _uiState.update { it.copy(scanState = ScanResultState.Error(message)) }
            } catch (_: IOException) {
                _uiState.update {
                    it.copy(scanState = ScanResultState.Error("Không có kết nối tới máy chủ."))
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(scanState = ScanResultState.Error("Mã quét không được hệ thống hỗ trợ."))
                }
            }
        }
    }

    fun resetScanState() {
        _uiState.update { it.copy(scanState = ScanResultState.Idle) }
    }

    fun toggleFlashlight() {
        _uiState.update { it.copy(isFlashlightOn = !it.isFlashlightOn) }
    }
}
