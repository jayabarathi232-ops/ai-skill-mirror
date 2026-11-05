package com.simats.aiskillmirror.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.aiskillmirror.data.network.ApiClient
import com.simats.aiskillmirror.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

object AvatarCacheManager {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8
    private val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    fun getFromMemory(key: String): Bitmap? = memoryCache.get(key)
    fun putInMemory(key: String, bitmap: Bitmap) {
        memoryCache.put(key, bitmap)
    }
    fun clearKey(key: String) {
        memoryCache.remove(key)
    }

    fun getDiskFile(context: Context, key: String): File {
        val dir = File(context.cacheDir, "avatars").apply { if (!exists()) mkdirs() }
        val hash = hashKey(key)
        return File(dir, "$hash.jpg")
    }

    private fun hashKey(key: String): String {
        return try {
            val digest = MessageDigest.getInstance("MD5")
            val bytes = digest.digest(key.toByteArray())
            bytes.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            key.hashCode().toString()
        }
    }
}

@Composable
fun ProfileAvatar(
    imageUrl: String?,
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    shape: Shape = CircleShape,
    previewBytes: ByteArray? = null,
    previewBitmap: Bitmap? = null,
    showEditBadge: Boolean = false,
    fontSize: Int = 26,
    onEditClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var loadedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // If local preview is provided, prefer that immediately
    val activePreviewBitmap = remember(previewBytes, previewBitmap) {
        when {
            previewBitmap != null -> previewBitmap
            previewBytes != null && previewBytes.isNotEmpty() -> {
                try {
                    BitmapFactory.decodeByteArray(previewBytes, 0, previewBytes.size)
                } catch (_: Exception) { null }
            }
            else -> null
        }
    }

    val resolvedUrl = remember(imageUrl) {
        ApiClient.resolveImageUrl(imageUrl)
    }

    LaunchedEffect(resolvedUrl, activePreviewBitmap) {
        if (activePreviewBitmap != null) {
            loadedBitmap = activePreviewBitmap
            isLoading = false
            return@LaunchedEffect
        }

        if (resolvedUrl.isBlank()) {
            loadedBitmap = null
            isLoading = false
            return@LaunchedEffect
        }

        // 1. Check memory cache
        val memCached = AvatarCacheManager.getFromMemory(resolvedUrl)
        if (memCached != null) {
            loadedBitmap = memCached
            isLoading = false
            return@LaunchedEffect
        }

        // 2. Check disk cache
        val diskFile = AvatarCacheManager.getDiskFile(context, resolvedUrl)
        if (diskFile.exists() && diskFile.length() > 0) {
            val diskBitmap = withContext(Dispatchers.IO) {
                try {
                    BitmapFactory.decodeFile(diskFile.absolutePath)
                } catch (_: Exception) { null }
            }
            if (diskBitmap != null) {
                AvatarCacheManager.putInMemory(resolvedUrl, diskBitmap)
                loadedBitmap = diskBitmap
                isLoading = false
                return@LaunchedEffect
            }
        }

        // 3. Download from network
        isLoading = true
        val downloadedBitmap = withContext(Dispatchers.IO) {
            try {
                val conn = (URL(resolvedUrl).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 10000
                    readTimeout = 10000
                    doInput = true
                    connect()
                }
                if (conn.responseCode in 200..299) {
                    val bytes = conn.inputStream.use { it.readBytes() }
                    conn.disconnect()
                    if (bytes.isNotEmpty()) {
                        try {
                            FileOutputStream(diskFile).use { it.write(bytes) }
                        } catch (_: Exception) {}
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    } else null
                } else {
                    conn.disconnect()
                    null
                }
            } catch (_: Exception) {
                null
            }
        }

        if (downloadedBitmap != null) {
            AvatarCacheManager.putInMemory(resolvedUrl, downloadedBitmap)
            loadedBitmap = downloadedBitmap
        }
        isLoading = false
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Main Avatar Circle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(8.dp, shape, spotColor = CenterCardGradientStart)
                .clip(shape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(CenterCardGradientStart, CenterCardGradientEnd)
                    )
                )
                .border(2.dp, CardBorder, shape),
            contentAlignment = Alignment.Center
        ) {
            val bitmapToDraw = activePreviewBitmap ?: loadedBitmap

            if (bitmapToDraw != null) {
                Image(
                    bitmap = bitmapToDraw.asImageBitmap(),
                    contentDescription = "Profile Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Default Profile Initials
                val safeInitials = if (initials.isNotBlank()) initials.take(2).uppercase() else "SM"
                Text(
                    text = safeInitials,
                    color = Color.White,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Loading overlay
            if (isLoading && bitmapToDraw == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33000000)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size * 0.35f),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        // Camera / Edit Badge Button
        if (showEditBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(size * 0.36f)
                    .shadow(4.dp, CircleShape, spotColor = PrimaryGradientStart)
                    .clip(CircleShape)
                    .background(PrimaryGradientStart)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable { onEditClick?.invoke() },
                contentAlignment = Alignment.Center
            ) {
                CameraBadgeIcon(size = size * 0.18f, color = Color.White)
            }
        }
    }
}

@Composable
fun CameraBadgeIcon(size: Dp, color: Color) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = 1.6.dp.toPx()

        // Camera body
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.15f, h * 0.28f),
            size = Size(w * 0.70f, h * 0.56f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
            style = Stroke(width = stroke)
        )

        // Camera lens
        drawCircle(
            color = color,
            radius = w * 0.16f,
            center = Offset(w * 0.50f, h * 0.56f),
            style = Stroke(width = stroke)
        )

        // Flash notch
        drawRect(
            color = color,
            topLeft = Offset(w * 0.35f, h * 0.16f),
            size = Size(w * 0.30f, h * 0.14f)
        )
    }
}
