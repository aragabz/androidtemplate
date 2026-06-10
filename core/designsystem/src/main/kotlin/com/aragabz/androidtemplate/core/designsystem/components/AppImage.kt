package com.aragabz.androidtemplate.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aragabz.androidtemplate.core.designsystem.R

/**
 * A wrapper around Coil's AsyncImage to enforce consistent behavior.
 * Features:
 * - Default crossfade animation
 * - Default error and loading placeholders
 * - Enforced content description for accessibility
 *
 * @param model The data source for the image (URL, URI, File, etc.)
 * @param contentDescription Text used by accessibility services. Must not be null.
 * @param modifier Modifier to be applied to the image.
 * @param contentScale Strategy for scaling the image.
 */
@Composable
fun AppImage(
    model: Any?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(model)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        placeholder = painterResource(R.drawable.ic_placeholder),
        error = painterResource(R.drawable.ic_placeholder),
    )
}
